# Proposta de migracao para PostgreSQL

## Decisao validada

O projeto `sutil-nfe` ainda esta em desenvolvimento e o servico nao esta em execucao operacional. Por isso, nao ha necessidade de preservar uma fase intermediaria com modelo achatado apenas para reduzir risco de troca em producao.

Decisao: iniciar a migracao diretamente pelo modelo normalizado da fase 2.

Essa decisao antecipa a criacao das entidades `pessoas`, `arquivos_nfe` e `erros_importacao_nfe`, mantendo `NotaFiscal` como modelo de dominio/DTO para a API. A normalizacao passa a ser parte do desenho inicial do PostgreSQL, e nao uma evolucao posterior.

## Contexto analisado

O projeto `sutil-nfe` e uma aplicacao Spring Boot que processa PDFs de NFS-e, extrai os dados para o dominio `NotaFiscal` e persiste cada nota como arquivo JSON em `NFEs/arquivos_json/{ano}`.

Classe principal analisada: `src/main/java/br/com/sutilnfe/backend/infra/filesystem/NotaFiscalJsonRepository.java`.

Fluxo atual:

- `ProcessarPdfService` le PDFs em `NFEs/naoprocessada`, extrai dados com `PdfNfeRoles`, gera `UUID` para `id`, salva JSON e move o PDF para `NFEs/processada`.
- `NotaFiscalJsonRepository` salva, lista, busca e exclui notas lendo arquivos `.json` do disco.
- `NotaFiscalController` expoe endpoints para listar por ano, listar anos disponiveis, salvar, atualizar, processar PDFs e excluir.
- A raiz do modelo atual e `NotaFiscal`, com dados da nota, emitente, tomador, valores, origem do PDF e campos editaveis pela UI (`numero`, `descricaoServico`, `splitPj`, `incremento`).

## Problemas da persistencia atual

- As buscas por `chaveAcesso` e `id` fazem leitura linear dos arquivos JSON de um ano.
- A unicidade da `chaveAcesso` depende de varrer arquivos, nao de uma restricao transacional.
- Atualizar uma nota faz delete do arquivo antigo e criacao de um novo arquivo.
- Nao ha controle de auditoria (`created_at`, `updated_at`) nem status formal de processamento.
- A contagem por ano depende da estrutura fisica das pastas.
- O nome do arquivo JSON e detalhe de infraestrutura e hoje influencia a persistencia.

## Objetivo da migracao

Migrar a persistencia das notas fiscais para PostgreSQL mantendo o comportamento atual da API:

- listar notas por ano de emissao;
- contar notas agrupadas por ano;
- buscar por `id` e ano;
- impedir duplicidade por `chaveAcesso`;
- atualizar campos editaveis;
- excluir notas;
- decidir se `arquivoOrigem` permanece na nota ou se a rastreabilidade fica somente em `arquivos_nfe`;
- registrar pessoas e arquivos processados em tabelas proprias desde o inicio.
- registrar falhas de importacao sem criar nota fiscal incompleta.

## Modelo de dados aprovado - fase 2

O modelo aprovado usa quatro tabelas principais:

- `notas_fiscais`: dados fiscais e campos editaveis da nota.
- `pessoas`: emitente e tomador normalizados por documento.
- `arquivos_nfe`: controle do PDF de origem e status de processamento.
- `erros_importacao_nfe`: falhas de leitura/extracao que impedem a criacao da nota fiscal.

`NotaFiscal` deve continuar existindo como dominio/DTO de entrada e saida da API. A infraestrutura PostgreSQL deve mapear a entidade relacional normalizada para esse modelo, preservando o contrato atual dos controllers e services.

### Entidade `pessoas`

```sql
create table pessoas (
    id uuid primary key,
    nome varchar(255) not null,
    documento varchar(30) not null,
    tipo_documento varchar(10),
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    constraint uk_pessoas_documento unique (documento)
);

create index idx_pessoas_nome
    on pessoas (nome);
```

Observacoes:

- `documento` deve armazenar CNPJ/CPF preferencialmente normalizado apenas com digitos.
- `tipo_documento` pode assumir `CNPJ`, `CPF` ou `OUTRO`, conforme normalizacao disponivel.
- Alteracoes futuras de nome devem atualizar a pessoa existente quando o documento for o mesmo.

### Entidade `notas_fiscais`

```sql
create table notas_fiscais (
    id uuid primary key,
    numero varchar(50),
    chave_acesso varchar(60) not null,
    competencia date,
    data_emissao date not null,
    ano_emissao integer generated always as (extract(year from data_emissao)::integer) stored,

    emitente_id uuid not null references pessoas(id),
    tomador_id uuid not null references pessoas(id),

    descricao_servico text,
    valor_servico numeric(15, 2) not null default 0,
    valor_liquido numeric(15, 2) not null default 0,
    codigo_tributacao varchar(120),
    municipio_prestacao varchar(120),

    arquivo_origem varchar(255), -- decisao pendente: manter aqui ou centralizar em arquivos_nfe
    split_pj integer not null default 60,
    incremento numeric(15, 2) not null default 0,

    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),

    constraint uk_notas_fiscais_chave_acesso unique (chave_acesso),
    constraint ck_notas_fiscais_split_pj check (split_pj between 0 and 100)
);

create index idx_notas_fiscais_ano_emissao
    on notas_fiscais (ano_emissao);

create index idx_notas_fiscais_data_emissao
    on notas_fiscais (data_emissao desc);

create index idx_notas_fiscais_emitente
    on notas_fiscais (emitente_id);

create index idx_notas_fiscais_tomador
    on notas_fiscais (tomador_id);
```

Observacoes:

- `id` deve continuar sendo UUID, como ja acontece em `ProcessarPdfService`.
- `chave_acesso` deve ser unica globalmente. O codigo atual checa duplicidade por ano, mas a chave identifica a nota fiscal e deve ter restricao transacional.
- `ano_emissao` substitui a pasta `{ano}` e facilita o endpoint `/api/notas/anos`.
- `numeric(15,2)` e adequado para valores monetarios atuais.
- `descricao_servico` deve ser `text`, pois as amostras reais podem possuir descricoes longas.
- `emitente_id` e `tomador_id` devem ser obrigatorios, porque o banco iniciara vazio e nao havera carga de registros incompletos.
- A permanencia de `arquivo_origem` em `notas_fiscais` fica pendente. A decisao final deve escolher entre manter esse campo por compatibilidade com a API atual ou centralizar a rastreabilidade em `arquivos_nfe`.

### Entidade `arquivos_nfe`

```sql
create table arquivos_nfe (
    id uuid primary key,
    nota_fiscal_id uuid references notas_fiscais(id) on delete set null,
    nome_arquivo varchar(255) not null,
    caminho varchar(500),
    status varchar(30) not null,
    mensagem_erro text,
    processado_em timestamptz,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create index idx_arquivos_nfe_status
    on arquivos_nfe (status);

create index idx_arquivos_nfe_nota_fiscal
    on arquivos_nfe (nota_fiscal_id);
```

Statuses sugeridos:

- `NAO_PROCESSADO`
- `PROCESSANDO`
- `PROCESSADO`
- `ERRO`

### Entidade `erros_importacao_nfe`

Quando a extracao falhar, inclusive por ausencia de `dataEmissao`, a aplicacao deve interromper o processamento da nota, nao criar registro em `notas_fiscais` e gravar o erro de importacao.

```sql
create table erros_importacao_nfe (
    id uuid primary key,
    nome_arquivo varchar(255) not null,
    caminho varchar(500),
    motivo text not null,
    ocorrido_em timestamptz not null default now(),
    created_at timestamptz not null default now()
);

create index idx_erros_importacao_nfe_ocorrido_em
    on erros_importacao_nfe (ocorrido_em desc);

create index idx_erros_importacao_nfe_nome_arquivo
    on erros_importacao_nfe (nome_arquivo);
```

## Alteracoes no projeto Java

Status: implementado.

### Dependencias Maven

Adicionado no `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>

<dependency>
    <groupId>org.liquibase</groupId>
    <artifactId>liquibase-core</artifactId>
</dependency>
```

### Configuracao

Adicionadas propriedades por ambiente:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/sutil_nfe
spring.datasource.username=sutil_nfe
spring.datasource.password=sutil_nfe
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.open-in-view=false
spring.liquibase.enabled=true
spring.liquibase.change-log=classpath:db/changelog/db.changelog-master.yaml
```

Recomendacao: usar Liquibase para criar e versionar o schema, mantendo `ddl-auto=validate`.

Padrao de migrations:

- O arquivo mestre do Liquibase deve ficar em `src/main/resources/db/changelog/db.changelog-master.yaml`.
- Cada alteracao de schema deve ficar em arquivo `.sql`.
- Os arquivos `.sql` devem usar SQL nativo do PostgreSQL, sem XML/YAML para definir tabelas, indices ou constraints.
- O YAML mestre deve apenas referenciar os arquivos SQL.

### Estrutura de codigo implementada

Entidades persistentes criadas separadas do dominio:

- `br.com.sutilnfe.backend.infra.persistence.PessoaEntity`
- `br.com.sutilnfe.backend.infra.persistence.NotaFiscalEntity`
- `br.com.sutilnfe.backend.infra.persistence.ArquivoNfeEntity`
- `br.com.sutilnfe.backend.infra.persistence.ErroImportacaoNfeEntity`
- `br.com.sutilnfe.backend.infra.persistence.PessoaJpaRepository`
- `br.com.sutilnfe.backend.infra.persistence.NotaFiscalJpaRepository`
- `br.com.sutilnfe.backend.infra.persistence.ArquivoNfeJpaRepository`
- `br.com.sutilnfe.backend.infra.persistence.ErroImportacaoNfeJpaRepository`
- `br.com.sutilnfe.backend.infra.persistence.NotaFiscalPostgresRepository`
- `br.com.sutilnfe.backend.infra.persistence.NotaFiscalMapper`

Interface de repositorio de dominio criada. `NotaFiscalService` e `ProcessarPdfService` passaram a depender dela:

```java
public interface NotaFiscalRepository {
    void salvar(NotaFiscal nota);
    List<NotaFiscal> listarTodas(int ano);
    Map<String, Long> getAnosComQuantidade();
    boolean existeNotaComChave(String chaveAcesso);
    Optional<NotaFiscal> getNotaById(String id, int ano);
    Optional<NotaFiscal> obterNotaFiscal(String chaveAcesso);
    void delete(NotaFiscal nota);
}
```

## Plano de execucao - fase 2

Status geral: executado.

1. [x] Preparar a fronteira de persistencia.
   - [x] Criar a interface `NotaFiscalRepository`.
   - [x] Ajustar `NotaFiscalService` e `ProcessarPdfService` para dependerem da interface.
   - [x] Substituir a dependencia operacional de `NotaFiscalJsonRepository` pela implementacao PostgreSQL.

2. [x] Adicionar PostgreSQL, Spring Data JPA e Liquibase.
   - [x] Incluir dependencias no `pom.xml`.
   - [x] Configurar datasource local.
   - [x] Definir `spring.jpa.hibernate.ddl-auto=validate`.
   - [x] Habilitar Liquibase.
   - [x] Criar `src/main/resources/db/changelog/db.changelog-master.yaml` apenas para orquestrar os arquivos SQL.

3. [x] Criar o schema normalizado.
   - [x] Criar `src/main/resources/db/changelog/001_create_pessoas.sql`.
   - [x] Criar `src/main/resources/db/changelog/002_create_notas_fiscais.sql`.
   - [x] Criar `src/main/resources/db/changelog/003_create_arquivos_nfe.sql`.
   - [x] Criar `src/main/resources/db/changelog/004_create_erros_importacao_nfe.sql`.
   - [x] Validar constraints, indices e tipos monetarios por compilacao/teste de contexto.

4. [x] Implementar entidades e repositories JPA.
   - [x] Mapear `PessoaEntity`, `NotaFiscalEntity`, `ArquivoNfeEntity` e `ErroImportacaoNfeEntity`.
   - [x] Implementar consultas por ano, por `id`, por `chaveAcesso` e agrupamento por ano.
   - [x] Usar transacao ao salvar nota para criar/atualizar pessoas e associar emitente/tomador.

5. [x] Implementar normalizacao de pessoas.
   - [x] Normalizar CNPJ/CPF removendo mascara.
   - [x] Resolver `emitente` por documento.
   - [x] Resolver `tomador` por documento.
   - [x] Atualizar nome quando o documento ja existir e o nome novo estiver preenchido.

6. [x] Implementar controle de arquivos e erros.
   - [x] Registrar `arquivos_nfe` ao iniciar processamento.
   - [x] Atualizar status para `PROCESSADO` quando a nota for salva.
   - [x] Quando a extracao falhar, atualizar o arquivo para `ERRO`, gravar `erros_importacao_nfe` com nome, data e motivo, lancar excecao e nao criar `notas_fiscais`.
   - [x] Tratar ausencia de `dataEmissao` como erro de importacao.
   - [x] Manter o movimento fisico dos PDFs nas pastas atuais.

7. [x] Inicializar o banco novo sem carga historica.
   - [x] Nao criar migrador dos JSONs existentes.
   - [x] Considerar PostgreSQL como fonte oficial apenas para notas processadas apos a virada.
   - [x] Manter arquivos antigos fora do fluxo de persistencia nova.

8. [~] Rodar validacao de consistencia.
   - [ ] Processar PDF valido e confirmar criacao de `notas_fiscais`, `pessoas` e `arquivos_nfe` em PostgreSQL real.
   - [ ] Processar PDF invalido e confirmar criacao de `erros_importacao_nfe`, status `ERRO` em `arquivos_nfe` e ausencia de nota fiscal em PostgreSQL real.
   - [x] Confirmar unicidade global de `chaveAcesso` no schema e repository.
   - [x] Conferir wiring dos endpoints por teste de contexto.
   - [x] Executar `mvn test` com sucesso.

9. [x] Trocar a implementacao ativa para PostgreSQL.
   - [x] Registrar `NotaFiscalPostgresRepository` como implementacao principal.
   - [x] Remover uso direto de `NotaFiscalJsonRepository` dos services.
   - [x] Manter o repositorio JSON fora do fluxo principal.

10. [~] Limpar a dependencia operacional dos JSONs.
    - [x] Parar de gravar novos JSONs como fonte primaria.
    - [x] Documentar que PostgreSQL passa a ser a origem oficial dos dados.
    - [ ] Decidir se `arquivo_origem` permanece em `notas_fiscais` ou se a rastreabilidade fica apenas em `arquivos_nfe`.

## Consultas equivalentes

### Listar por ano

```sql
select nf.*
from notas_fiscais nf
where nf.ano_emissao = :ano
order by nf.data_emissao desc, nf.created_at desc;
```

### Contar por ano

```sql
select ano_emissao::text as ano, count(*) as total
from notas_fiscais
group by ano_emissao
order by ano_emissao desc;
```

### Verificar duplicidade

```sql
select exists (
    select 1
    from notas_fiscais
    where chave_acesso = :chaveAcesso
);
```

### Buscar por id e ano

```sql
select *
from notas_fiscais
where id = :id
  and ano_emissao = :ano;
```

### Consultar notas por pessoa

```sql
select nf.*
from notas_fiscais nf
where nf.emitente_id = :pessoaId
   or nf.tomador_id = :pessoaId
order by nf.data_emissao desc;
```

## Changelogs Liquibase implementados

### `src/main/resources/db/changelog/db.changelog-master.yaml`

```yaml
databaseChangeLog:
  - include:
      file: db/changelog/001_create_pessoas.sql
  - include:
      file: db/changelog/002_create_notas_fiscais.sql
  - include:
      file: db/changelog/003_create_arquivos_nfe.sql
  - include:
      file: db/changelog/004_create_erros_importacao_nfe.sql
```

O arquivo mestre deve apenas encadear os scripts. A definicao do schema fica nos arquivos `.sql` com SQL nativo.

### `src/main/resources/db/changelog/001_create_pessoas.sql`

```sql
--liquibase formatted sql

--changeset sutil-nfe:001-create-pessoas
create table pessoas (
    id uuid primary key,
    nome varchar(255) not null,
    documento varchar(30) not null,
    tipo_documento varchar(10),
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    constraint uk_pessoas_documento unique (documento)
);

create index idx_pessoas_nome
    on pessoas (nome);
```

### `src/main/resources/db/changelog/002_create_notas_fiscais.sql`

```sql
--liquibase formatted sql

--changeset sutil-nfe:002-create-notas-fiscais
create table notas_fiscais (
    id uuid primary key,
    numero varchar(50),
    chave_acesso varchar(60) not null,
    competencia date,
    data_emissao date not null,
    ano_emissao integer generated always as (extract(year from data_emissao)::integer) stored,
    emitente_id uuid not null references pessoas(id),
    tomador_id uuid not null references pessoas(id),
    descricao_servico text,
    valor_servico numeric(15, 2) not null default 0,
    valor_liquido numeric(15, 2) not null default 0,
    codigo_tributacao varchar(120),
    municipio_prestacao varchar(120),
    arquivo_origem varchar(255), -- decisao pendente: manter aqui ou centralizar em arquivos_nfe
    split_pj integer not null default 60,
    incremento numeric(15, 2) not null default 0,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    constraint uk_notas_fiscais_chave_acesso unique (chave_acesso),
    constraint ck_notas_fiscais_split_pj check (split_pj between 0 and 100)
);

create index idx_notas_fiscais_ano_emissao
    on notas_fiscais (ano_emissao);

create index idx_notas_fiscais_data_emissao
    on notas_fiscais (data_emissao desc);

create index idx_notas_fiscais_emitente
    on notas_fiscais (emitente_id);

create index idx_notas_fiscais_tomador
    on notas_fiscais (tomador_id);
```

### `src/main/resources/db/changelog/003_create_arquivos_nfe.sql`

```sql
--liquibase formatted sql

--changeset sutil-nfe:003-create-arquivos-nfe
create table arquivos_nfe (
    id uuid primary key,
    nota_fiscal_id uuid references notas_fiscais(id) on delete set null,
    nome_arquivo varchar(255) not null,
    caminho varchar(500),
    status varchar(30) not null,
    mensagem_erro text,
    processado_em timestamptz,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create index idx_arquivos_nfe_status
    on arquivos_nfe (status);

create index idx_arquivos_nfe_nota_fiscal
    on arquivos_nfe (nota_fiscal_id);
```

### `src/main/resources/db/changelog/004_create_erros_importacao_nfe.sql`

```sql
--liquibase formatted sql

--changeset sutil-nfe:004-create-erros-importacao-nfe
create table erros_importacao_nfe (
    id uuid primary key,
    nome_arquivo varchar(255) not null,
    caminho varchar(500),
    motivo text not null,
    ocorrido_em timestamptz not null default now(),
    created_at timestamptz not null default now()
);

create index idx_erros_importacao_nfe_ocorrido_em
    on erros_importacao_nfe (ocorrido_em desc);

create index idx_erros_importacao_nfe_nome_arquivo
    on erros_importacao_nfe (nome_arquivo);
```

## Riscos e decisoes pendentes

- `dataEmissao`: decisao fechada. Se a extracao nao identificar a data de emissao, a aplicacao deve lancar excecao, nao criar nota fiscal e registrar a falha em `erros_importacao_nfe` com nome do arquivo, data do erro e motivo.
- `chaveAcesso`: decisao fechada. A unicidade sera global em `notas_fiscais`, substituindo a checagem por ano do JSON.
- `emitente_id` e `tomador_id`: decisao fechada. Os campos serao `not null`, porque o banco novo iniciara vazio e nao havera migracao de registros pendentes ou incompletos.
- `arquivoOrigem`: decisao pendente. O campo pode continuar em `notas_fiscais` por compatibilidade com a API atual, ou a rastreabilidade pode ficar somente em `arquivos_nfe`. Essa decisao deve ser tomada antes da implementacao final das entidades.
- Encoding e JSONs historicos: removido do escopo. Nao havera migracao de dados historicos; tudo sera iniciado do zero no PostgreSQL.

## Recomendacao final

Executar diretamente a fase 2. O momento e adequado porque o sistema ainda esta em desenvolvimento e a persistencia PostgreSQL pode nascer com o modelo definitivo: notas fiscais normalizadas, pessoas reaproveitaveis por documento e controle de arquivos processados no banco.
