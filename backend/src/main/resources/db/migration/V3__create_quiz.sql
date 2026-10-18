CREATE TABLE pergunta (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ordem INT NOT NULL UNIQUE CHECK (ordem > 0),
    texto TEXT NOT NULL
);

CREATE TABLE alternativa (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    pergunta_id UUID NOT NULL REFERENCES pergunta(id) ON DELETE CASCADE,
    ordem INT NOT NULL CHECK (ordem > 0),
    texto TEXT NOT NULL,
    area_principal_id UUID NOT NULL REFERENCES area(id),
    area_secundaria_id UUID NOT NULL REFERENCES area(id),
    UNIQUE(pergunta_id, ordem),
    CHECK(area_principal_id <> area_secundaria_id)
);