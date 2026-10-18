CREATE TABLE area (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    slug TEXT NOT NULL UNIQUE,
    numero TEXT NOT NULL UNIQUE,
    nome TEXT NOT NULL,
    descricao TEXT NOT NULL,
    como_e_trabalhar TEXT NOT NULL,
    ordem_exibicao INT NOT NULL,
    ordem_desempate INT NOT NULL UNIQUE
);

CREATE TABLE trilha (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    area_id  UUID NOT NULL REFERENCES area(id),
    slug TEXT NOT NULL UNIQUE,
    ordem_na_area INT NOT NULL,
    nome TEXT NOT NULL,
    resumo TEXT NOT NULL,
    nivel_entrada TEXT NOT NULL,
    texto_trilha TEXT NOT NULL,
    para_quem_e TEXT NOT NULL,
    horas_por_semana INT NOT NULL CHECK (horas_por_semana > 0),
    avisos_salario TEXT NOT NULL,
    conteudo_conferido_em DATE NOT NULL,
    UNIQUE (area_id, ordem_na_area)
);