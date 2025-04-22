public class CubbyHole {
    public int contents;
    public boolean available = false;

    public synchronized int get() {
        while (!available) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.err.println("Consumidor interrumpido");
                Thread.currentThread().interrupt();
            }
        }
        available = false;
        notifyAll();
        return contents;
    }

    public synchronized void put(int value) {
        while (available) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.err.println("Productor interrumpido");
                Thread.currentThread().interrupt();
            }
        }
        contents = value;
        available = true;
        notifyAll();
    }
}