# Checklist de fidelidade ao design

Use depois de cada fase com tela, no desktop (1280) e no mobile (390). Marque cada item. O que não bater vira um pedido de correção com o modelo do arquivo `prompts-claude-code.md`.

## Como comparar

1. Abra o app local (`npm run dev`) e o frame do Figma lado a lado, no mesmo tamanho. No Figma, selecione o frame e use zoom 100%.
2. Compare por **blocos** (cabeçalho, título, cada card, rodapé), de cima para baixo, e não a página inteira de uma vez.
3. Para detalhes finos, use as ferramentas do navegador (F12): meça margens, tamanho de fonte e cores, e compare com o painel de propriedades do Figma (modo Dev).
4. **O texto e a quantidade de itens do mockup são exemplo.** Compare estilo, estrutura e espaçamento, não as palavras.
5. Se o Claude Code disser que ficou diferença conhecida, ela deve estar listada na resposta dele. Diferença escondida é falha da fase.

## 1. Estrutura e layout

- [ ] A ordem dos blocos é a mesma do frame.
- [ ] Largura máxima e margens laterais iguais (desktop) e 16 a 24 px de respiro (mobile), como no frame.
- [ ] Alinhamentos corretos (esquerda, centro, itens lado a lado ou empilhados, como no Figma).
- [ ] No mobile, a ordem e o empilhamento seguem o frame mobile, não só uma versão menor do desktop.

## 2. Tipografia

- [ ] Família da fonte certa em cada papel (títulos, corpo e rótulos em mono).
- [ ] Tamanho, peso, altura de linha e espaçamento entre letras iguais ao estilo do Figma.
- [ ] Rótulos em mono estão em caixa alta, com o espaçamento do design.
- [ ] Textos longos quebram sem estourar o layout e sem cortar palavras estranhamente.

## 3. Cores

- [ ] Fundo, texto e textos secundários usam os tokens (`bg`, `ink`, `ink-soft`).
- [ ] As cores dos cards e tickets (coral, menta, limão, lavanda) alternam por posição, como no Figma.
- [ ] Não existe nenhuma cor "parecida" fora dos tokens.

## 4. Bordas, raios e sombras

- [ ] Espessura e cor da borda iguais às do frame.
- [ ] Raios dos cantos iguais (botões, cards, painel do chat).
- [ ] Sombra dura (deslocada) no tamanho e na direção do Figma, em botões, cards e tickets.
- [ ] Os detalhes do "ticket" (linha tracejada, recortes, código de barras) estão presentes onde existem.

## 5. Componentes e estados

- [ ] Botões: padrão, hover, foco (tecla Tab) e pressionado. Se algum não existe no Figma, foi **perguntado**, não inventado.
- [ ] Botão desabilitado (ex.: "Próxima" sem resposta) é claramente diferente.
- [ ] Tickets e cards de etapa têm hover igual ao Figma.
- [ ] Ícones e logo no tamanho certo e nítidos (em telas de alta densidade também).

## 6. Dados reais

- [ ] Nada do mockup ficou fixo: nomes, resumos, números e etapas vêm da API.
- [ ] Títulos e resumos mais longos que os do mockup não quebram o layout.
- [ ] Lista com mais ou menos itens que o mockup continua bonita.

## 7. Regras de conteúdo (não negociáveis)

- [ ] Em toda trilha, a faixa salarial aparece como "R$ mín – máx", com referência e fonte.
- [ ] O aviso de salário (`avisosSalario`) está visível junto das faixas, **em toda trilha**.
- [ ] Selo "dados limitados" só aparece quando a API marca assim.
- [ ] Em nenhum lugar diz CLT ou PJ.
- [ ] Recursos mostram tipo, idioma e Grátis, Pago ou Confirmar preço.
- [ ] O quiz diz que recomenda **áreas**, não vagas.
- [ ] Nenhuma mensagem técnica de erro aparece (sem "500", sem JSON, sem caminho da API).

## 8. Responsividade

- [ ] 390 px confere com o frame mobile.
- [ ] 1280 px confere com o frame desktop.
- [ ] Larguras intermediárias (por exemplo 600, 768 e 1024) não quebram: sem rolagem horizontal, sem texto cortado, sem elementos sobrepostos.
- [ ] Acima de 1280, o conteúdo fica centralizado, com largura máxima.

## 9. Acessibilidade rápida

- [ ] Dá para usar tudo só com o teclado (Tab, Shift+Tab, Enter, Espaço, Esc).
- [ ] O foco é visível em todos os elementos clicáveis.
- [ ] Contraste do texto legível, principalmente o texto secundário.
- [ ] Imagens decorativas sem ruído para leitores de tela; imagens com sentido têm descrição.
- [ ] O painel do chat abre com o foco dentro dele, e Esc fecha.

## 10. Itens por tela

**Home** (`5:4`, `24:301`; Como funciona `121:537`, `124:715`)
- [ ] Hero com ticket inclinado e botões como no frame.
- [ ] Oito tickets de área, número, nome e "N TRILHAS" corretos.
- [ ] "Como funciona" existe e o link do cabeçalho rola até lá.

**Área** (`9:238`, `24:653`)
- [ ] Trilha de navegação, título, descrição e "N trilhas nesta área".
- [ ] Cada trilha com número colorido, nome, resumo e botão "Ver roadmap".
- [ ] O bloco "Como é trabalhar nesta área" (`comoETrabalhar`) fica entre a descrição e a contagem de trilhas, como nos frames `134:649` e `134:747`, e só aparece se o campo vier.

**Trilha** (`9:333`, `24:738`; com etapa expandida e salário completo `123:553`, `125:640`)
- [ ] Título, resumo, botão do PDF e bloco de salário iguais ao frame.
- [ ] Etapas com número colorido, título, descrição curta e nível.
- [ ] Etapa expandida mostra descrição, projeto prático, duração e recursos.
- [ ] Linha de "verificado em" no final.

**Quiz** (`8:192`, `24:538`) **e Resultado** (`8:252`, `24:595`)
- [ ] Barra de 12 segmentos, avançando a cada pergunta.
- [ ] Letras A, B, C, D e alternativas embaralhadas a cada exibição.
- [ ] "Próxima" desabilitada sem resposta; "Voltar" funciona.
- [ ] Top 3 com barras proporcionais (a primeira cheia).
- [ ] Recarregar a página mantém o resultado salvo, e "Refazer o quiz" limpa.

**Assistente** (`10:444`, `24:823`; 429 e 503 `124:571`, `125:838`, `125:860`)
- [ ] Botão flutuante no canto, em todas as páginas.
- [ ] Faixa de contexto mostra onde a pessoa está.
- [ ] Bolhas do usuário e do assistente com as cores do frame.
- [ ] Aviso de dados pessoais, contador de 500 caracteres e botão Enviar.
- [ ] A conversa continua ao trocar de página, e zera ao recarregar.
- [ ] Estados 429 (contagem) e 503 mostram mensagem amigável.

**404 e Erro** (`124:623`, `124:756`, `124:669`, `124:796`)
- [ ] Bilhete inclinado, dois botões e nenhum número de erro técnico na tela.

## Resultado da conferência

Anote por fase: **Aprovado**, ou a lista de diferenças enviada ao Claude Code. Só faça o commit depois de **Aprovado**.
