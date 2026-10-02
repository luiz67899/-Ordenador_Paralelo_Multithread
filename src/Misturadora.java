public class Misturadora extends Thread {
    private final byte[] primeiroVetor;
    private final byte[] segundoVetor;
    private byte[] vetorMisturado;
    private OutOfMemoryError erroMemoria;

    public Misturadora(byte[] primeiroVetor, byte[] segundoVetor) {
        this.primeiroVetor = primeiroVetor;
        this.segundoVetor = segundoVetor;
    }

    @Override
    public void run() {
        try {
            vetorMisturado = new byte[primeiroVetor.length + segundoVetor.length];
            int i = 0;
            int j = 0;
            int k = 0;

            while (i < primeiroVetor.length && j < segundoVetor.length) {
                if (primeiroVetor[i] <= segundoVetor[j]) {
                    vetorMisturado[k++] = primeiroVetor[i++];
                } else {
                    vetorMisturado[k++] = segundoVetor[j++];
                }
            }

            while (i < primeiroVetor.length) {
                vetorMisturado[k++] = primeiroVetor[i++];
            }

            while (j < segundoVetor.length) {
                vetorMisturado[k++] = segundoVetor[j++];
            }
        } catch (OutOfMemoryError erro) {
            erroMemoria = erro;
        }
    }

    public byte[] getVetor() {
        if (erroMemoria != null) {
            throw erroMemoria;
        }

        return vetorMisturado;
    }
}
