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
