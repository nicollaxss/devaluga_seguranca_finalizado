CREATE TABLE public.pessoa
(
    codigo serial NOT NULL,
    nome text,
    cpf text,
    telefone text,
    email text,
    endereco text,
    data_nascimento date,
    status text DEFAULT 'ATIVO',
    PRIMARY KEY (codigo)
);