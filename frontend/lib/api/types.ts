import type { components } from "./schema";

type Schemas = components["schemas"];
export type Area = Schemas["AreaResponse"];
export type AreaDetalhe = Schemas["AreaDetalheResponse"];
export type TrilhaDetalhe = Schemas["TrilhaDetalheResponse"];
export type Pergunta = Schemas["PerguntaResponse"];
export type Alternativa = Schemas["AlternativaResponse"];
export type ResultadoQuizRequest = Schemas["ResultadoRequest"];
export type ResultadoQuiz = Schemas["ResultadoResponse"];
export type Recomendacao = Schemas["RecomendacaoResponse"];
export type ChatRequest = Schemas["ChatRequest"];
export type ChatResponse = Schemas["ChatResponse"];
export type MensagemHistoricoRequest = Schemas["MensagemHistoricoRequest"];
