# sutil-nfe

## Decisao de persistencia

O projeto deve migrar diretamente para PostgreSQL usando o modelo normalizado da fase 2 descrito em `docs/proposta-migracao-postgresql.md`.

Como o servico ainda esta em desenvolvimento e nao esta em execucao operacional, nao sera adotada uma fase intermediaria com tabela achatada. A implementacao deve partir de `notas_fiscais`, `pessoas`, `arquivos_nfe` e `erros_importacao_nfe`, mantendo `NotaFiscal` como dominio/DTO da API.

As migrations devem usar Liquibase com arquivos `.sql` e SQL nativo do PostgreSQL.
