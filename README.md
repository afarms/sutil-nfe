# sutil-nfe

## Decisao de persistencia

O projeto deve migrar diretamente para PostgreSQL usando o modelo normalizado da fase 2 descrito em `docs/proposta-migracao-postgresql.md`.

Como o servico ainda esta em desenvolvimento e nao esta em execucao operacional, nao sera adotada uma fase intermediaria com tabela achatada. A implementacao deve partir de `notas_fiscais`, `pessoas`, `arquivos_nfe` e `erros_importacao_nfe`, mantendo `NotaFiscal` como dominio/DTO da API.

As migrations devem usar Liquibase com arquivos `.sql` e SQL nativo do PostgreSQL.

## Executando localmente com Docker Compose

### Pre-requisitos

- Docker instalado.
- Docker Compose disponivel pelo comando `docker compose`.
- Porta `8080` livre para a aplicacao.
- Porta `5432` livre para o PostgreSQL, caso queira acessar o banco pelo host.

### Configurar variaveis

Copie o arquivo de exemplo:

```bash
cp .env.example .env
```

No Windows PowerShell:

```powershell
Copy-Item .env.example .env
```

As variaveis configuradas sao:

- `POSTGRES_DB`: nome do banco local.
- `POSTGRES_USER`: usuario do PostgreSQL local.
- `POSTGRES_PASSWORD`: senha local sem segredo real.
- `POSTGRES_PORT`: porta do PostgreSQL exposta no host.
- `SERVER_PORT`: porta interna da aplicacao Spring Boot.
- `APP_PORT`: porta da aplicacao exposta no host.
- `NFE_BASE_PATH`: diretorio de arquivos NFE dentro do container.
- `SPRING_DATASOURCE_URL`: URL JDBC usada pela aplicacao dentro da rede Docker.
- `SPRING_DATASOURCE_USERNAME`: usuario usado pela aplicacao para conectar no banco.
- `SPRING_DATASOURCE_PASSWORD`: senha usada pela aplicacao para conectar no banco.

Para o ambiente local do Compose, mantenha `POSTGRES_USER` alinhado com `SPRING_DATASOURCE_USERNAME` e `POSTGRES_PASSWORD` alinhado com `SPRING_DATASOURCE_PASSWORD`.

### Subir os containers

```bash
docker compose up --build
```

Para rodar em segundo plano:

```bash
docker compose up --build -d
```

O Compose sobe dois servicos:

- `sutil-nfe-db-local`: PostgreSQL Alpine com volume persistente.
- `sutil-nfe-app-local`: aplicacao Java, aguardando o healthcheck do banco.

### Verificar logs

```bash
docker compose logs -f sutil-nfe-app-local
```

Logs do banco:

```bash
docker compose logs -f sutil-nfe-db-local
```

### Acessar a aplicacao

Com os valores padrao do `.env.example`:

- Aplicacao: `http://localhost:8080`
- Status: `http://localhost:8080/api/status`

### Conexao local com PostgreSQL

Com os valores padrao:

- Host: `localhost`
- Porta: `5432`
- Database: `sutil_nfe`
- Usuario: `sutil_nfe`
- Senha: valor definido em `POSTGRES_PASSWORD` no `.env`

Dentro da rede Docker, a aplicacao acessa o banco por:

```text
jdbc:postgresql://sutil-nfe-db-local:5432/sutil_nfe
```

### Parar/remover containers

```bash
docker compose down
```

### Limpar volumes

Use apenas quando quiser apagar os dados locais do PostgreSQL e arquivos persistidos:

```bash
docker compose down -v
```
