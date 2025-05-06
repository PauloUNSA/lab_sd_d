package lab01.propuesto1;

public class Consumidor extends Thread {
    public CubbyHole cubbyhole;
    public int numero;

    public Consumidor(CubbyHole c, int numero) {
        cubbyhole = c;
        this.numero = numero;
    }

    public void run() {
        int value = 0;
        for (int i = 0; i < 10; i++) {
            value = cubbyhole.get();
            System.out.println("Consumidor #" + this.numero + " obtiene: " + value);
        }
        System.out.println("Consumidor #" + numero + " terminó");
    }
}