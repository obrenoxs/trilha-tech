CREATE TABLE etapa (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    trilha_id UUID NOT NULL REFERENCES trilha(id) ON DELETE CASCADE,
    ordem INT NOT NULL CHECK (ordem > 0),
    titulo TEXT NOT NULL,
    nivel TEXT NOT NULL,
    duracao_semanas INT NOT NULL CHECK (duracao_semanas > 0),
    descricao TEXT NOT NULL,
    projeto_pratico TEXT,
    UNIQUE(trilha_id, ordem)
);

CREATE TABLE recurso (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    etapa_id UUID NOT NULL REFERENCES etapa(id) ON DELETE CASCADE,
    ordem INT NOT NULL CHECK (ordem > 0),
    titulo TEXT NOT NULL,
    url TEXT NOT NULL,
    tipo TEXT NOT NULL,
    idioma TEXT NOT NULL,
    acesso TEXT NOT NULL,
    observacao TEXT,
    verificado_em DATE NOT NULL,
    UNIQUE(etapa_id, ordem)
);

CREATE TABLE faixa_salarial (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    trilha_id UUID NOT NULL REFERENCES trilha(id) ON DELETE CASCADE,
    nivel TEXT NOT NULL,
    minimo INT NOT NULL CHECK (minimo > 0),
    maximo INT NOT NULL,
    valor_referencia INT NOT NULL,
    tipo_referencia TEXT NOT NULL,
    fonte TEXT NOT NULL,
    data_referencia DATE NOT NULL, -- Valor representa o mês inteiro, dia sempre é 1
    dados_limitados BOOLEAN NOT NULL,
    observacao TEXT NOT NULL,
    UNIQUE(trilha_id, nivel),
    CHECK (minimo <= maximo)
);