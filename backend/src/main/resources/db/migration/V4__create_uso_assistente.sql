CREATE TABLE uso_assistente (
    dia       DATE PRIMARY KEY, -- dia da cota do Google, no fuso America/Los_Angeles (e não o de Brasília)
    mensagens INT  NOT NULL DEFAULT 0 CHECK (mensagens >= 0)
);