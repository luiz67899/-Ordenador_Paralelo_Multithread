# ⚡ Ordenador Paralelo Multithread em Java

Projeto desenvolvido em Java para a disciplina de Programação Concorrente / Algoritmos. O programa realiza a ordenação de grandes vetores do tipo `byte` utilizando **divisão e conquista com concorrência (Threads)** e compara o tempo de execução paralela contra uma solução **monothread (sequencial)**.

---

## 📌 Sobre o Projeto

O objetivo principal é explorar a capacidade do processamento multinúcleo (*Multi-core*) do computador para acelerar a ordenação de dados em memória.

O algoritmo divide um vetor de dados em fatias e dispara threads concorrentes para ordenar cada bloco. Em seguida, threads intercaladoras unem esses pedaços ordenados em rodadas consecutivas até obter o vetor final completamente ordenado.

---

## 🚀 Funcionalidades

* **Cálculo de Capacidade:** Identifica automaticamente o limite aproximado da memória RAM para alocação do vetor.
* **Divisão Automática por Núcleos:** O número de threads de ordenação é ajustado com base na quantidade de processadores disponíveis (`Runtime.getRuntime().availableProcessors()`).
* **Tratamento de Fatias Ímpares:** Garante que pedaços sem par avancem para a próxima rodada de mistura sem perda de dados ou exceções de índice.
* **Menu Interativo:** Permite visualizar o vetor completo, partes dele, o tempo de execução e a comparação direta de desempenho.
* **Benchmark Integrado:** Executa o algoritmo sequencial sob a mesma base de dados desordenada para calcular o ganho real de desempenho (*Speedup*).

---

## 🏗️ Arquitetura do Sistema

O projeto é estruturado nos seguintes componentes:

1. **`Programa.java`**: Ponto de entrada (`main`), orquestrador das threads de ordenação e intercalação, além de gerenciar a interface via menu.
2. **`Ordenadora.java`**: Classe baseada em `Thread` encarregada de aplicar o algoritmo de ordenação (QuickSort) na fatia atribuída a ela.
3. **`Misturadora.java`**: Classe baseada em `Thread` responsável pela intercalação (*Merge*) de duas fatias já ordenadas.
4. **`Teclado.java`**: Classe auxiliar para leitura segura de dados pelo console.

---

## 📊 Desempenho: Paralelo vs. Sequencial

A eficiência do paralelismo depende do **tamanho do vetor**:

* **Vetores Pequenos (< 50.000 elementos):** A versão **sequencial** costuma ser mais rápida devido ao *overhead* (custo de criação, contexto e sincronização de threads no SO).
* **Vetores Grandes (> 1.000.000 de elementos):** O paralelismo supera o modelo sequencial, atingindo ganhos expressivos de *Speedup* proporcional ao número de núcleos do processador.

---

## 🛠️ Como Executar

### Pré-requisitos
* Java JDK 8 ou superior instalado.