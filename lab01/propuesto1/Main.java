package lab01.propuesto1;

public class Main {
    public static void main(String[] args) {
        CubbyHole cub = new CubbyHole();

        // Configuración avanzada para pruebas
        int numProductores = 2;
        int numConsumidores = 3;
        int delayMax = 150;
        int iteraciones = 5;

        System.out.println("Iniciando prueba con " + numProductores + " productores y " +
                numConsumidores + " consumidores");

        // Crear productores
        for (int i = 1; i <= numProductores; i++) {
            Productor prod = new Productor(cub, i, delayMax) {
                @Override
                public void run() {
                    for (int j = 0; j < iteraciones; j++) {
                        cubbyhole.put(j);
                        System.out.println("Productor #" + this.numero + " pone: " + j);
                        try {
                            sleep((int)(Math.random() * delayMax));
                        } catch (InterruptedException e) {
                            System.err.println("Productor #" + numero + " interrumpido");
                            return;
                        }
                    }
                }
            };
            prod.start();
        }

        // Crear consumidores
        for (int i = 1; i <= numConsumidores; i++) {
            Consumidor cons = new Consumidor(cub, i) {
                @Override
                public void run() {
                    for (int j = 0; j < (iteraciones * numProductores / numConsumidores); j++) {
                        int value = cubbyhole.get();
                        System.out.println("Consumidor #" + this.numero + " obtiene: " + value);
                    }
                }
            };
            cons.start();
        }
    }
}