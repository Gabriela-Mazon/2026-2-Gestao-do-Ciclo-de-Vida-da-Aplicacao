# Laboratório de Refatoração — Princípios de Projeto

## Contexto

Você recebeu um pequeno sistema de pedidos que **funciona**, mas foi desenvolvido sem grande preocupação com princípios de projeto.

Seu objetivo é analisar e refatorar o código **sem alterar o comportamento essencial da aplicação**.

Os princípios trabalhados são:

1. Responsabilidade Única
2. Segregação de Interfaces
3. Inversão de Dependências / Prefira Interfaces a Classes
4. Prefira Composição à Herança
5. Princípio de Demeter
6. Aberto/Fechado
7. Substituição de Liskov

## Como executar

Pré-requisito: **JDK 17 ou superior**.

Na pasta raiz do projeto, compile:

```bash
javac -d out src/projetoprincipiosdesign/*.java
```

Depois execute:

```bash
java -cp out projetoprincipiosdesign.Main
```

## Sua missão

Para cada princípio:

- identifique pelo menos um trecho problemático;
- explique por que o trecho merece ser refatorado;
- proponha uma solução;
- implemente a alteração;
- execute novamente o projeto.

## Regras

- Não remova funcionalidades apenas para facilitar a refatoração.
- Não é necessário que sua solução fique igual à de outros colegas.
- Justifique cada decisão.
- Princípios de projeto são recomendações: evite criar complexidade sem necessidade.
- Preserve a possibilidade de cadastrar, calcular, pagar e apresentar o pedido.
