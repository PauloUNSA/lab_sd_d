import java.io.*;
import java.net.*;

public class CristianAlgorithm {

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("server")) {
            startServer();
        } else {
            startClient();
        }
    }

    private static void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("Servidor de tiempo iniciado en puerto 12345. Esperando conexiones...");

            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
                     DataInputStream in = new DataInputStream(clientSocket.getInputStream())) {

                    System.out.println("Cliente conectado: " + clientSocket.getInetAddress());
                    String request = in.readUTF();
                    if ("TIME_REQUEST".equals(request)) {
                        long serverTime = System.currentTimeMillis();
                        out.writeLong(serverTime);
                        System.out.println("Tiempo enviado al cliente: " + serverTime);
                    }
                } catch (Exception e) {
                    System.err.println("Error en conexión con cliente: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("No se pudo iniciar el servidor en puerto 12345: " + e.getMessage());
        }
    }

    private static void startClient() {
        long localTime = System.currentTimeMillis() - 120_000;
        System.out.println("Tiempo local del cliente (desfasado): " + localTime);

        try {
            System.out.println("Intentando conectar con servidor...");
            Socket socket = new Socket("localhost", 12345);
            System.out.println("Conexión establecida con servidor");

            try (DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                 DataInputStream in = new DataInputStream(socket.getInputStream())) {

                long requestTime = System.currentTimeMillis();
                out.writeUTF("TIME_REQUEST");

                long serverTime = in.readLong();
                long responseTime = System.currentTimeMillis();

                long roundTripTime = responseTime - requestTime;
                long estimatedDelay = roundTripTime / 2;
                long adjustedTime = serverTime + estimatedDelay;

                System.out.println("\n--- Resultados de sincronización ---");
                System.out.println("Tiempo del servidor recibido (T2): " + serverTime);
                System.out.println("Tiempo de solicitud (T1): " + requestTime);
                System.out.println("Tiempo de respuesta (T3): " + responseTime);
                System.out.println("Retardo de ida y vuelta: " + roundTripTime + " ms");
                System.out.println("Retardo estimado (1/2 RTT): " + estimatedDelay + " ms");
                System.out.println("\nTiempo local antes de ajuste: " + localTime);
                System.out.println("Tiempo ajustado: " + adjustedTime);
                System.out.println("Diferencia corregida: " + (adjustedTime - localTime) + " ms");
            }
        } catch (ConnectException ce) {
            System.err.println("Error: No se pudo conectar al servidor. Asegúrate de que el servidor esté ejecutándose primero.");
        } catch (Exception e) {
            System.err.println("Error en cliente: " + e.getMessage());
        }
    }
}