# Trabalho de Programação Paralela — Busca de Arquivos em Java

## Descrição

Este projeto tem como objetivo comparar a busca de palavras em arquivos `.txt` de um diretório, utilizando duas abordagens: uma sequencial e outra paralela (com Threads). Além do desenvolvimento dos sistemas, será realizada uma análise de Speed Up e desempenho das duas soluções.

---

## Arquitetura do Projeto

O projeto está organizado para manter clareza entre as versões sequencial e paralela, favorecer reuso de componentes e facilitar a evolução do código.

```
name_search/
│
├── src/
│   ├── sequential/
│   │   ├── FileSearch.java
│   │   ├── LineSearch.java
│   │   └── SequentialSearch.java
│   │
│   ├── parallel/
│   │   ├── FileSearch.java
│   │   ├── ParallelLineSearch.java
│   │   ├── ParallelSearch.java
│   │   └── WorkerThread.java
│   │
│   ├── model/
│   │   └── Result.java
│   │
│   ├── util/
│   │   └── Timer.java
│   │
│   └── Main.java
│
├── .gitignore
└── README.md
```

### Estruturas de Diretórios e Classes

- **src/sequential/** &mdash; Implementação sequencial:
  - **SequentialSearch.java**: Controla a execução da busca simples.
  - **FileSearch.java**: Localiza todos arquivos `.txt` em uma pasta.
  - **LineSearch.java**: Realiza a busca do termo nas linhas dos arquivos.

- **src/parallel/** &mdash; Implementação paralela:
  - **ParallelSearch.java**: Controla a versão com múltiplas threads.
  - **FileSearch.java**: (Reusada) Mesma lógica para localizar arquivos.
  - **ParallelLineSearch.java**: Quem faz a busca paralelizada nas linhas.
  - **WorkerThread.java**: Representa uma thread de processamento.

- **src/model/** &mdash; Modelos de dados:
  - **Result.java**: Estrutura para registrar os achados (arquivo, linha, etc.)

- **src/util/** &mdash; Utilitários:
  - **Timer.java**: Utilitário para medir tempo de execução (para análise de Speed Up).

- **src/Main.java** &mdash; Entrada principal, ponto para executar as versões e medir resultados.

---

## Como executar

A forma mais simples de rodar este programa é utilizando o arquivo `.jar` disponível nos [Releases do GitHub](https://github.com/vinicitadin/name_search/releases):

1. Acesse a página de [Releases](https://github.com/vinicitadin/name_search/releases).
2. Baixe o arquivo `NameSearch.jar` mais recente na seção _Assets_.
3. Certifique-se de ter o [Java](https://www.java.com/) instalado (Java 8 ou superior).
4. Execute o comando abaixo no terminal, substituindo o caminho para onde você salvou o arquivo:

```bash
java -jar caminho/para/NameSearch.jar
```

---

## Observações

- O projeto foi planejado para não misturar responsabilidades e facilitar comparações claras entre as abordagens sequencial e paralela.
- O uso de pacotes distintos para cada abordagem evita confusão e favorece a manutenção do projeto.
- Componentes como modelos e utilitários podem ser reutilizados por ambas as implementações.

---
