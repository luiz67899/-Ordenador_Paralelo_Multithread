import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Programa {
    public static void main(String[] args) {
        try {
            Random aleatorio = new Random();

            System.out.println("Calculando o maior vetor aproximado...");
            int limite = descobrirMaiorVetorAproximado();
            System.out.printf("Limite aproximado: %,d elementos.%n", limite);

            int tamanho;

            do {
                System.out.print("Quantos elementos deseja no vetor? ");
                tamanho = Teclado.getUmInt();

                if (tamanho <= 0 || tamanho > limite) {
                    System.out.printf("Digite um valor entre 1 e %,d.%n", limite);
                }
            } while (tamanho <= 0 || tamanho > limite);

            byte[] vetor = new byte[tamanho];

            System.out.println("1 - Digitar os elementos");
            System.out.println("2 - Gerar os elementos aleatoriamente");
            System.out.print("Escolha: ");
            int escolha = Teclado.getUmInt();

            if (escolha == 1) {
                for (int i = 0; i < vetor.length; i++) {
                    System.out.print("Elemento [" + i + "]: ");
                    vetor[i] = Teclado.getUmByte();
                }
            } else if (escolha == 2) {
                for (int i = 0; i < vetor.length; i++) {
                    vetor[i] = (byte) (aleatorio.nextInt(256) - 128);
                }
            } else {
                System.out.println("Opcao invalida.");
                return;
            }

            // Guarda uma copia fiel do vetor DESORDENADO para usar na comparacao sequencial
            byte[] vetorCopiaOriginal = vetor.clone();

            long inicio = System.currentTimeMillis();

            int processadores = Runtime.getRuntime().availableProcessors();
            int quantidadePedacos = Math.max(1, processadores - 1);
            List<byte[]> pedacos = dividirVetor(vetor, quantidadePedacos);

            List<Ordenadora> ordenadoras = new ArrayList<>();

            for (byte[] pedaco : pedacos) {
                Ordenadora ordenadora = new Ordenadora(pedaco);
                ordenadoras.add(ordenadora);
                ordenadora.start();
            }

            List<byte[]> resultados = new ArrayList<>();

            for (Ordenadora ordenadora : ordenadoras) {
                ordenadora.join();
                resultados.add(ordenadora.getVetor());
            }

            while (resultados.size() > 1) {
                List<Misturadora> misturadoras = new ArrayList<>();
                List<byte[]> proximaRodada = new ArrayList<>();

                for (int i = 0; i + 1 < resultados.size(); i += 2) {
                    Misturadora misturadora = new Misturadora(
                            resultados.get(i), resultados.get(i + 1));
                    misturadoras.add(misturadora);
                    misturadora.start();
                }

                for (Misturadora misturadora : misturadoras) {
                    misturadora.join();
                    proximaRodada.add(misturadora.getVetor());
                }

                if (resultados.size() % 2 != 0) {
                    proximaRodada.add(resultados.get(resultados.size() - 1));
                }

                resultados = proximaRodada;
            }

            byte[] vetorOrdenado = resultados.get(0);
            long fim = System.currentTimeMillis();
            long tempoParalelo = fim - inicio;

            // Chama o Menu passando o vetor ordenado, a copia original e o tempo paralelo
            mostrarMenu(vetorCopiaOriginal, vetorOrdenado, tempoParalelo);

        } catch (NegativeArraySizeException erro) {
            System.out.println("Erro: o tamanho do vetor nao pode ser negativo.");
        } catch (InterruptedException erro) {
            Thread.currentThread().interrupt();
            System.out.println("Erro: uma thread foi interrompida.");
        } catch (OutOfMemoryError erro) {
            System.out.println("Erro: nao existe memoria suficiente para esse vetor.");
        } catch (Exception erro) {
            System.out.println("Erro: " + erro.getMessage());
        }
    }

    private static int descobrirMaiorVetorAproximado() {
        int tamanho = 1_000_000;
        int ultimoBemSucedido = 0;

        while (true) {
            try {
                byte[] vetorTeste = new byte[tamanho];
                ultimoBemSucedido = tamanho;
                vetorTeste = null;
                System.gc();

                if (tamanho > Integer.MAX_VALUE / 3 * 2) {
                    break;
                }

                tamanho /= 2;
                tamanho *= 3;
            } catch (OutOfMemoryError erro) {
                System.gc();
                break;
            }
        }

        return ultimoBemSucedido;
    }

    private static List<byte[]> dividirVetor(byte[] vetor, int quantidadePedacos) {
        List<byte[]> pedacos = new ArrayList<>();
        int tamanhoBase = vetor.length / quantidadePedacos;
        int sobra = vetor.length % quantidadePedacos;
        int inicio = 0;

        for (int i = 0; i < quantidadePedacos; i++) {
            int tamanhoPedaco = tamanhoBase;

            if (i < sobra) {
                tamanhoPedaco++;
            }

            int fim = inicio + tamanhoPedaco;
            pedacos.add(Arrays.copyOfRange(vetor, inicio, fim));
            inicio = fim;
        }

        return pedacos;
    }

    // Algoritmo de QuickSort Sequencial (para comparacao)
    private static void quickSortSequencial(byte[] vetor, int inicio, int fim) {
        if (inicio < fim) {
            int pivoIndex = particionarSequencial(vetor, inicio, fim);
            quickSortSequencial(vetor, inicio, pivoIndex - 1);
            quickSortSequencial(vetor, pivoIndex + 1, fim);
        }
    }

    private static int particionarSequencial(byte[] vetor, int inicio, int fim) {
        byte pivo = vetor[fim];
        int i = inicio - 1;

        for (int j = inicio; j < fim; j++) {
            if (vetor[j] <= pivo) {
                i++;
                byte temp = vetor[i];
                vetor[i] = vetor[j];
                vetor[j] = temp;
            }
        }

        byte temp = vetor[i + 1];
        vetor[i + 1] = vetor[fim];
        vetor[fim] = temp;

        return i + 1;
    }

    private static void mostrarMenu(byte[] vetorOriginal, byte[] vetorOrdenado, long tempoParalelo) throws Exception {
        int opcao;

        do {
            System.out.println("\n================ MENU DE OPCOES ================");
            System.out.println("1 - Imprimir o vetor ordenado inteiro");
            System.out.println("2 - Imprimir uma parte do vetor ordenado");
            System.out.println("3 - Imprimir o tempo da ordenacao paralela");
            System.out.println("4 - Executar e COMPARAR com ordenacao SEQUENCIAL (Monothread)");
            System.out.println("0 - Sair");
            System.out.print("Escolha: ");
            opcao = Teclado.getUmInt();

            if (opcao == 1) {
                System.out.println(Arrays.toString(vetorOrdenado));
            } else if (opcao == 2) {
                System.out.println("Indices validos: 0 ate " + (vetorOrdenado.length - 1));
                System.out.print("Indice inicial: ");
                int indiceInicial = Teclado.getUmInt();
                System.out.print("Indice final: ");
                int indiceFinal = Teclado.getUmInt();

                if (indiceInicial < 0 || indiceFinal >= vetorOrdenado.length
                        || indiceInicial > indiceFinal) {
                    System.out.println("Intervalo invalido.");
                } else {
                    byte[] parte = Arrays.copyOfRange(
                            vetorOrdenado, indiceInicial, indiceFinal + 1);
                    System.out.println(Arrays.toString(parte));
                }
            } else if (opcao == 3) {
                System.out.println("Tempo total paralelo: " + tempoParalelo + " ms");
            } else if (opcao == 4) {
                System.out.println("\nExecutando ordenacao sequencial na copia do vetor original...");
                byte[] copiaParaSequencial = vetorOriginal.clone();

                long inicioSeq = System.currentTimeMillis();
                quickSortSequencial(copiaParaSequencial, 0, copiaParaSequencial.length - 1);
                long fimSeq = System.currentTimeMillis();
                long tempoSequencial = fimSeq - inicioSeq;

                System.out.println("\n--- COMPARATIVO DE DESEMPENHO ---");
                System.out.println("Tamanho do vetor : " + vetorOriginal.length + " elementos");
                System.out.println("Tempo Paralelo   : " + tempoParalelo + " ms");
                System.out.println("Tempo Sequencial : " + tempoSequencial + " ms");

                if (tempoParalelo > 0) {
                    double speedup = (double) tempoSequencial / tempoParalelo;
                    System.out.printf("Ganho de desempenho (Speedup): %.2fx mais rapido!%n", speedup);
                } else {
                    System.out.println("A execucao paralela foi tao rapida que o tempo registrado foi de 0 ms.");
                }
            } else if (opcao != 0) {
                System.out.println("Opcao invalida.");
            }
        } while (opcao != 0);
    }
}