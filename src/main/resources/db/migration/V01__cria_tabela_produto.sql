CREATE TABLE public.produto
(
    codigo bigserial NOT NULL,
    nome text,
    descricao text,
    precodiario decimal(10,2),
    status text DEFAULT 'ATIVO',
    PRIMARY KEY (codigo)
);