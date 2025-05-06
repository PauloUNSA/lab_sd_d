package lab01.propuesto1;

public class Productor extends Thread {
    public CubbyHole cubbyhole;
    public int numero;
    public int delayMax;

    public Productor(CubbyHole c, int numero, int delayMax) {
        cubbyhole = c;
        this.numero = numero;
        this.delayMax = delayMax;
    }

    public void run() {
        for (int i = 0; i < 10; i++) {
            cubbyhole.put(i);
            System.out.println("Productor #" + this.numero + " pone: " + i);
            try {
                sleep((int)(Math.random() * delayMax));
            } catch (InterruptedException e) {
                System.err.println("Productor #" + numero + " interrumpido");
                Thread.currentThread().interrupt();
                return;
            }
        }
        System.out.println("Productor #" + numero + " terminó");
    }
}