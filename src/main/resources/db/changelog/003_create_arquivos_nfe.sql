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
