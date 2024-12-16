CREATE TABLE public.aluguel
(
    codigo bigserial NOT NULL,                  -- Código único do aluguel (auto incremento)
    usuario_codigo integer,                      -- Referência à pessoa que fez o aluguel
    produto_codigo integer,
    data_inicio date,                           -- Data de início do aluguel
    data_fim date,                              -- Data de fim do aluguel (quando o produto é devolvido)
    valor_total decimal(10, 2),                     -- Valor total do aluguel
    status text DEFAULT 'ATIVO',    -- Status do aluguel ('ALUGADO', 'CONCLUÍDO', 'CANCELADO')
    CONSTRAINT fk_usuario FOREIGN KEY (usuario_codigo) REFERENCES public.usuario(codigo) ON DELETE CASCADE, -- Chave estrangeira para pessoa
    CONSTRAINT fk_produto FOREIGN KEY (produto_codigo) REFERENCES public.produto(codigo) ON DELETE CASCADE,
    PRIMARY KEY (codigo)                     -- Definindo a chave primária para o campo "codigo"
);