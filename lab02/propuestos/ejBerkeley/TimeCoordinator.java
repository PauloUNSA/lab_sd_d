package lab02.propuestos.ejBerkeley;

import java.util.*;

public class TimeCoordinator {
    public static void main(String[] args) {
        Random rand = new Random();
        List<Integer> clientTimes = new ArrayList<>();
        int coordinatorTime = 10000;

        System.out.println("Tiempo del coordinador: " + coordinatorTime);

        // Simular tiempos de clientes
        for (int i = 1; i <= 4; i++) {
            int clientTime = 10000 + rand.nextInt(2000) - 1000;
            clientTimes.add(clientTime);
            System.out.println("Tiempo del cliente " + i + ": " + clientTime);
        }

        // Calcular diferencias
        int totalDifference = 0;
        for (int time : clientTimes) {
            totalDifference += (time - coordinatorTime);
        }

        // Ajuste promedio
        int averageAdjustment = totalDifference / (clientTimes.size() + 1);

        System.out.println("\nAjuste promedio: " + averageAdjustment + " unidades");

        // Ajustar tiempos
        coordinatorTime += averageAdjustment;
        System.out.println("\nNuevo tiempo del coordinador: " + coordinatorTime);

        for (int i = 0; i < clientTimes.size(); i++) {
            int adjustedTime = clientTimes.get(i) + (coordinatorTime - clientTimes.get(i)) + averageAdjustment;
            System.out.println("Nuevo tiempo del cliente " + (i + 1) + ": " + adjustedTime);
        }
    }
}
