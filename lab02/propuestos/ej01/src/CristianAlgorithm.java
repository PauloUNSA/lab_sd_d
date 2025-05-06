package lab02.propuestos.ej01.src;

import java.io.*;
import java.net.*;

// Clase principal que implementa el algoritmo de Cristian
public class CristianAlgorithm {

    // Metodo principal. Decide si actuar como servidor o cliente dependiendo del argumento.
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("server")) {
            startServer(); // Inicia el servidor si el argumento es "server"
        } else {
            startClient(); // En caso contrario, inicia como cliente
        }
    }

    // Metodo que implementa el servidor de tiempo
    private static void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("Servidor de tiempo iniciado en puerto 12345. Esperando conexiones...");

            // Bucle infinito para aceptar múltiples conexiones de clientes
            while (true) {
                try (Socket clientSocket = serverSocket.accept(); // Acepta conexión de cliente
                     DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
                     DataInputStream in = new DataInputStream(clientSocket.getInputStream())) {

                    System.out.println("Cliente conectado: " + clientSocket.getInetAddress());

                    // Espera una solicitud del cliente
                    String request = in.readUTF();
                    if ("TIME_REQUEST".equals(request)) {
                        // Si el cliente solicita el tiempo, se envía la hora actual del servidor
                        long serverTime = System.currentTimeMillis();
                        out.writeLong(serverTime);
                        System.out.println("Tiempo enviado al cliente: " + serverTime);
                    }
                } catch (Exception e) {
                    // Muestra error si ocurre un problema con un cliente
                    System.err.println("Error en conexión con cliente: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            // Error al intentar iniciar el servidor
            System.err.println("No se pudo iniciar el servidor en puerto 12345: " + e.getMessage());
        }
    }

    // Metodo que implementa el cliente que se sincroniza con el servidor
    private static void startClient() {
        // Simula que el reloj del cliente está desfasado en 2 minutos (120000 ms)
        long localTime = System.currentTimeMillis() - 120_000;
        System.out.println("Tiempo local del cliente (desfasado): " + localTime);

        try {
            System.out.println("Intentando conectar con servidor...");
            Socket socket = new Socket("localhost", 12345); // Se conecta al servidor en localhost
            System.out.println("Conexión establecida con servidor");

            try (DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                 DataInputStream in = new DataInputStream(socket.getInputStream())) {

                long requestTime = System.currentTimeMillis(); // Tiempo justo antes de enviar la solicitud
                out.writeUTF("TIME_REQUEST"); // Solicita la hora al servidor

                long serverTime = in.readLong(); // Recibe la hora del servidor
                long responseTime = System.currentTimeMillis(); // Tiempo justo después de recibir la respuesta

                // Cálculo del tiempo de ida y vuelta (RTT)
                long roundTripTime = responseTime - requestTime;
                long estimatedDelay = roundTripTime / 2; // Estimación del retardo de ida

                // Ajuste del tiempo local con base en el tiempo del servidor + retardo estimado
                long adjustedTime = serverTime + estimatedDelay;

                // Imprime resultados de sincronización
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
            // Error si el servidor no está disponible
            System.err.println("Error: No se pudo conectar al servidor. Asegúrate de que el servidor esté ejecutándose primero.");
        } catch (Exception e) {
            // Cualquier otro error del cliente
            System.err.println("Error en cliente: " + e.getMessage());
        }
    }
}
