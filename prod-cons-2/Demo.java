
public class Demo {
    public static void main(String[] args) {
        CubbyHole cub = new CubbyHole();
        Productor prod = new Productor(cub, 1);
        Consumidor cons = new Consumidor(cub, 1);

        prod.start(); // Inicia el hilo del productor
        cons.start(); // Inicia el hilo del consumidor
    }
}
