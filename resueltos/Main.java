public class Main {
    public static void main(String[] args) {
        Cliente cliente1 = new Cliente("Cliente 1", new int[] { 2, 2, 1, 5, 2, 3 });
        Cliente cliente2 = new Cliente("Cliente 2", new int[] { 1, 3, 5, 1, 1 });
        Cajera Cajera1 = new Cajera("Cajera 1");
        Cajera Cajera2 = new Cajera("Cajera 2");
// Tiempo inicial de referencia
        long initialTime = System.currentTimeMillis();
        Cajera1.procesarCompra(cliente1, initialTime);
        Cajera2.procesarCompra(cliente2, initialTime);
    }
}