import java.util.Arrays;

public class Ordenadora extends Thread {
    private byte[] vetor;
    private OutOfMemoryError erroMemoria;

    public Ordenadora(byte[] vetor) {
        this.vetor = vetor;
    }

    @Override
    public void run() {
        try {
            vetor = mergeSort(vetor);
        } catch (OutOfMemoryError erro) {
            erroMemoria = erro;
        }
    }

    public static byte[] mergeSort(byte[] vetor) {
        if (vetor.length <= 1) {
            return vetor;
        }

        int meio = vetor.length / 2;

        byte[] esquerda = Arrays.copyOfRange(vetor, 0, meio);
        byte[] direita = Arrays.copyOfRange(vetor, meio, vetor.length);

        esquerda = mergeSort(esquerda);
        direita = mergeSort(direita);

        return misturar(esquerda, direita);
    }

    private static byte[] misturar(byte[] esquerda, byte[] direita) {
        byte[] resultado = new byte[esquerda.length + direita.length];
        int i = 0;
        int j = 0;
        int k = 0;

        while (i < esquerda.length && j < direita.length) {
            if (esquerda[i] <= direita[j]) {
                resultado[k++] = esquerda[i++];
            } else {
                resultado[k++] = direita[j++];
            }
        }

        while (i < esquerda.length) {
            resultado[k++] = esquerda[i++];
        }

        while (j < direita.length) {
            resultado[k++] = direita[j++];
        }

        return resultado;
    }

    public byte[] getVetor() {
        if (erroMemoria != null) {
            throw erroMemoria;
        }

        return vetor;
    }
}
