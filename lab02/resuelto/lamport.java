package lab02.resuelto;


import java.util.ArrayList;
import java.util.List;

public class lamport {

    private int clock; // reloj lógico

    // constructor - inicia el reloj en 0
    public lamport() {
        this.clock = 0;
    }

    // tick(): incrementa el reloj en 1
    public synchronized int tick() {
        this.clock++;
        return this.clock;
    }

    // update(): actualiza el reloj con el tiempo recibido + 1
    public synchronized void update(int receivedTime) {
        this.clock = Math.max(this.clock, receivedTime) + 1;
    }

    // getTime(): devuelve el valor actual del reloj
    public int getTime() {
        return this.clock;
    }

    public static void main(String[] args) {
        List<Thread> threads = new ArrayList<>();
        lamport clock = new lamport();

        // Crear 5 hilos
        for (int i = 0; i < 5; i++) {
            Thread thread = new Thread(new Runnable() {
                public void run() {
                    int time = clock.tick();
                    System.out.println("Hilo " + Thread.currentThread().getId() +
                            " creo evento con tiempo Lamport " + time);

                    // espera aleatoria
                    try {
                        long espera = (long) (Math.random() * 1000);
                        System.out.println("Hilo " + Thread.currentThread().getId() +" espera = " + espera);
                        Thread.sleep(espera);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    int receivedTime = clock.tick();
                    System.out.println("Hilo " + Thread.currentThread().getId() +
                            " recibio evento con tiempo Lamport " + receivedTime);

                    clock.update(receivedTime);
                }
            });
            threads.add(thread);
            thread.start(); // inicia el hilo
        }

        // espera a que todos los hilos terminen
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // se imprime el valor final del reloj
        System.out.println("Tiempo Lamport final: " + clock.getTime());
    }
}
