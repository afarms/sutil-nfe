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
    arquivo_origem varchar(255),
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
