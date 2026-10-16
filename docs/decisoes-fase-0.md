# Decisões da Fase 0 (alinhamento)

## Áreas e trilhas (36)
01 Desenvolvimento de Software (7): Java e Spring · Python (Django/FastAPI) · Node.js (JavaScript/TypeScript) · C#/.NET · Front-end · Desenvolvimento Mobile · PHP/Laravel
02 Dados e Inteligência Artificial (5): Análise de Dados/BI · Engenharia de Dados · Ciência de Dados · Machine Learning · IA Generativa/LLMs
03 Infraestrutura, Cloud e DevOps (4): Cloud · DevOps · SRE · Linux
04 Segurança da Informação (4): Pentest · SOC/Blue Team · AppSec · GRC
05 Qualidade e Testes (4): Teste manual · Automação de testes · Testes de API · Testes de performance
06 Suporte Técnico e Redes (3): Help Desk · Redes · Administração de sistemas
07 UX/UI Design (4): UX Research · UI Design · Product Design · UX Writing
08 Gestão e Produtos de TI (5): Gestão de projetos · Scrum Master · Product Owner · Product Manager · Analista de requisitos

## Textos
- Cada área tem texto próprio (explicação e como é trabalhar nela).
- Cada trilha tem texto próprio, diferente do da área. São campos distintos no modelo de dados.

## Nível de entrada da trilha
- É um campo da trilha (não uma observação no texto): nível de entrada com os valores Iniciante, Intermediário ou Avançado.
- Serve de selo na interface, para o iniciante saber se a trilha é porta de entrada.
- Todas as 36 trilhas recebem um valor. Machine Learning e SRE entram como Avançado. Os demais são definidos trilha a trilha durante a pesquisa dos roadmaps.

## Público dos roadmaps
- Todo roadmap é escrito para um iniciante, partindo de conhecimento zero. Exemplo: a trilha Java e Spring começa por lógica de programação e algoritmos, e só depois chega a Java, orientação a objetos, Spring e o resto.
- Trilhas de nível de entrada Avançado (como Machine Learning e SRE) também começam pelos fundamentos que elas exigem (programação, matemática básica, Linux etc.). Por isso tendem a ter mais etapas ou durar mais.
- Etapas de fundamentos podem se repetir em várias trilhas. Na v1, cada trilha tem sua própria cópia.

## Etapas do roadmap
Cada trilha tem de 6 a 8 etapas (algumas trilhas passaram disso depois de decisões posteriores). Campos de cada etapa:
- ordem, título, descrição (obrigatórios)
- nível: Básico, Intermediário ou Avançado (agrupa as etapas no PDF)
- duração estimada em semanas, assumindo cerca de 8h de estudo por semana
- projeto prático: uma frase sobre o que construir (opcional)
- recursos de estudo: título, link, tipo, idioma (PT ou EN), se é gratuito e a data em que o link foi verificado ("verificado em")

## Faixa salarial
- Por trilha e por nível (Júnior, Pleno, Sênior), em faixa mínimo–máximo, reais por mês.
- Cada faixa leva fonte e data de referência. O site mostra "estimativa, fonte X, mês/ano".
- Fontes: guias e pesquisas salariais públicas, com a origem de cada número anotada.
- Trilhas com poucos dados são marcadas como "dados limitados". Não inventar número.

## Quiz
Perguntas e pontuação em [quiz.md](quiz.md). Decisões complementares:
- Desempate, em ordem: (1) mais pontos; (2) a área escolhida como principal em mais perguntas; (3) ordem fixa de desempate das áreas. A regra vale para qualquer posição do top 3, inclusive empate entre 3º e 4º lugar.
- A ordem fixa fica em um campo próprio da área (ordem de desempate), separado da ordem de exibição, para poder mudar sem afetar o resto. Valor inicial: a mesma ordem 01 a 08.
- Em simulação com respostas totalmente aleatórias, o critério 2 ainda deixa cerca de 13% de empates entre 1º e 2º e 25% entre 3º e 4º. Pessoas reais respondem de forma mais concentrada, então devem empatar menos, mas o critério 3 é necessário.
- As perguntas, alternativas e pesos ficam no banco, carregados por migração (Flyway), como o resto do conteúdo. Mudar uma pergunta exige uma nova migração e um deploy. A vantagem é manter uma única fonte de dados e deixar o caminho aberto para uma área administrativa na fase 2.
- O backend calcula o resultado a partir das respostas (ids das alternativas) e devolve o top 3. Não persiste nada: sem login, não há a quem associar o dado. O backend valida as respostas (uma por pergunta, alternativas válidas). O resultado fica só no navegador.
- Se o front-end enviar o resultado do quiz como contexto para a IA, o backend valida os identificadores das áreas e não confia no valor cegamente.

## Assistente de IA
- Provedor: Gemini. O backend depende de uma interface própria ("cliente de IA"), sem o SDK do provedor espalhado pelo código, para poder trocar de provedor.
- O uso da API por chave é separado do plano de estudante do Google: usa a camada gratuita do projeto (com limites próprios por modelo) ou cobrança por uso. Antes da Fase 6, conferir os limites reais da chave no AI Studio.
- Na camada gratuita da API, o conteúdo enviado pode ser usado pelo Google para melhorar os produtos. O chat deve avisar o usuário para não digitar dados pessoais.
- Histórico: o front-end reenvia as últimas mensagens da conversa a cada pergunta, para a IA entender referências como "e esse salário?". Estrutura:
  - Enviar só as últimas 6 mensagens (3 trocas), com papel "usuário" ou "assistente".
  - Cada mensagem do usuário tem no máximo 500 caracteres; o backend também trunca e valida, sem confiar no front-end.
  - O prompt de sistema é montado só no backend, a partir do banco e do contexto validado (rota, área, trilha, resultado do quiz). O histórico vindo do navegador entra apenas como turnos da conversa, nunca como instrução de sistema.
  - Limite de tamanho da resposta da IA.
- Limite de uso (decidido): limite por IP combinado com um teto global diário de mensagens, para proteger o custo. Quando o teto for atingido, só o assistente fica indisponível, com mensagem amigável, e o resto do site segue funcionando. Atrás do proxy da hospedagem, o IP real vem de cabeçalho encaminhado, que o backend precisa tratar com cuidado. Os valores dos limites ainda serão definidos.

## Hospedagem
- Front-end na Vercel.
- Backend na Railway.
- Banco PostgreSQL também na Railway.
- A geração de PDF deve ser pensada para o consumo de memória do servidor na Railway (Fase 5).

## Conteúdo dos 36 roadmaps
- Os roadmaps ficam em `conteudo/trilhas/` em formato estruturado e depois viram a carga inicial do banco.
- Cada link é verificado, com a data da verificação anotada no próprio recurso ("verificado em").
- A revalidação automática periódica dos links não entra na v1. Fica para a fase 2 do produto.
- Recursos grátis e pagos entram, em português ou inglês; os pagos são sempre marcados.

## PDF do roadmap
- Modelo em HTML/CSS (estilo "Bilhete"), com cores e fontes em variáveis. Os 36 PDFs já existem em `conteudo/pdfs/`.
- Como o backend entrega o PDF fica para a Fase 5.

## Em aberto
- Como entregar o PDF (arquivos prontos ou geração no servidor).
- Direção visual final (o grupo está votando).
- Valores dos limites de uso da IA (por IP e teto global diário).
- Limites reais da chave da API do Gemini (consultar no AI Studio).
