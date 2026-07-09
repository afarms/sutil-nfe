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
