# Sprint 4 — Princípios de Projeto em Java

Atividade concluída localmente em 12/09/2026, conforme os dois roteiros de refatoração fornecidos na pasta Sprint 4. A solução mantém o cenário original da loja acadêmica e aplica os sete princípios solicitados.

## Conteúdo da entrega

- `src/projetoprincipiosdesign/`: implementação refatorada, com 23 arquivos Java.
- `tests/projetoprincipiosdesign/RefatoracaoTest.java`: 23 testes automatizados, executáveis apenas com o JDK.
- `RELATORIO_REFATORACAO.md`: problemas originais, soluções, justificativas dos sete princípios, comparação das estruturas e respostas às reflexões finais.
- `evidencias/`: saída e arquivo de pedidos do programa original e de cada uma das sete etapas; verificações de compilação, hashes dos fontes e resultado dos testes.
- `executar.ps1`: compilação, execução e testes no Windows.
- `scripts/verificar-etapa.ps1`: ferramenta usada para registrar a execução de cada etapa e comparar seu resultado com o original.
- `README_ALUNO.md`: enunciado original, preservado.

O arquivo original `ProjetoPrincipiosDesignJava.zip`, localizado na pasta superior, foi preservado. Não houve criação de repositório remoto, commit, push ou envio para o GitHub.

## Executar no Windows

Abra um terminal PowerShell nesta pasta:

```powershell
.\executar.ps1
```

Para executar os testes:

```powershell
.\executar.ps1 -Modo testar
```

Para apenas compilar:

```powershell
.\executar.ps1 -Modo compilar
```

O script usa um JDK informado por `-JdkDirectory`, em `JAVA_HOME`, no `PATH` ou no cache local preparado nesta máquina. É necessário JDK 17 ou superior. Para informar outro JDK:

```powershell
.\executar.ps1 -Modo testar -JdkDirectory 'C:\caminho\do\jdk-17'
```

Cada compilação usa uma pasta nova dentro de `build/`, evitando misturar classes antigas com as atuais. A execução normal acrescenta `Ana;144.0` ao `pedidos.txt` da raiz do projeto. Os testes usam arquivos temporários e não alteram esse arquivo.

## Executar diretamente com Java

Com o JDK disponível no terminal, a partir da raiz deste projeto:

```text
javac --release 17 -encoding UTF-8 -d out src/projetoprincipiosdesign/*.java
java -Dfile.encoding=UTF-8 -Duser.language=pt -Duser.country=BR -cp out projetoprincipiosdesign.Main
javac --release 17 -encoding UTF-8 -cp out -d out tests/projetoprincipiosdesign/*.java
java -Dfile.encoding=UTF-8 -cp out projetoprincipiosdesign.RefatoracaoTest
```

## Resultado validado

- Compilação com `--release 17 -encoding UTF-8 -Xlint:all -Werror`, sem avisos.
- Programa original e sete etapas executados com sucesso.
- Saída completa do `Main` e conteúdo de `pedidos.txt` idênticos ao original em todas as etapas.
- **23 testes aprovados; zero falhas.**
- Cenário principal: cliente Ana, Belo Horizonte, subtotal R$ 160,00, desconto de aluno de 10%, total R$ 144,00 e pagamento por cartão.

Ambiente utilizado: Windows x64, Microsoft Build of OpenJDK 17.0.20.1+1. O JDK portátil foi obtido da [página oficial da Microsoft](https://learn.microsoft.com/en-us/java/openjdk/download), com SHA-256 conferido, e está no cache local da máquina, fora da entrega. Os comandos e a versão estão registrados em `evidencias/`.

## Configurar os comportamentos

O serviço recebe as implementações no construtor. O `Main` usa:

```java
PedidoService servico = new PedidoService(
    new PedidoRepositoryArquivo(),
    new DescontoAluno(),
    new PagamentoCartao()
);
```

Para professor com PIX, por exemplo:

```java
PedidoService servico = new PedidoService(
    new PedidoRepositoryArquivo(),
    new DescontoProfessor(),
    new PagamentoPix()
);
```

O caso de boleto do programa original gerava a linha digitável. Para preservar exatamente essa operação, forneça a referência ao método de geração:

```java
GeraBoleto boleto = new PagamentoBoleto();
PedidoService servico = new PedidoService(
    new PedidoRepositoryArquivo(),
    new DescontoAluno(),
    boleto::gerarBoleto
);
```

Essa referência adapta `gerarBoleto(double)` ao contrato `IPagamento.pagar(double)`. A escolha fica no código que monta o serviço. `PagamentoBoleto.pagar()` continua disponível para a operação original de registrar o boleto; não confunda registro com geração da linha digitável.

## Contrato das entregas

As duas modalidades implementam `TipoEntrega` e retornam `OptionalDouble`:

- Valor presente: modalidade disponível, contendo o preço do frete.
- Valor ausente: modalidade indisponível para aquele pedido.

```java
TipoEntrega entrega = new EntregaRetiradaLoja();
entrega.calcularFrete(49.99); // OptionalDouble.empty(): retirada indisponível.
entrega.calcularFrete(50.0);  // OptionalDouble.of(0.0): retirada gratuita.
```

A entrega em domicílio mantém frete de R$ 15,00. A retirada continua exigindo pedido a partir de R$ 50,00. O `Main` original não calculava frete, e o total de sua demonstração continua sem frete.

## Escopo preservado

O exercício permanece uma aplicação de console. Pagamentos e mensagens são simulações; a persistência continua em arquivo. O contrato de desconto e a forma de selecionar pagamentos mudaram conforme o roteiro; a sinalização de retirada indisponível também mudou para corrigir LSP. Essas decisões estão detalhadas no relatório.

O ZIP de entrega contém fontes, testes, documentação e evidências. `build/`, `out/`, arquivos `.class`, o JDK e pedidos gerados por execuções locais não são necessários para entregar ou versionar o projeto.
