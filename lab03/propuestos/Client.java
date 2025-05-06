package lab03.propuestos;

import java.net.*;
import java.io.*;
import java.util.*;
// El cliente se puede correr como una consola
public class Client {
    // notification - notificacion
    private String notif = " *** ";
    // for I/O - para entrada y salida
    private ObjectInputStream sInput; // lee desde el socket
    private ObjectOutputStream sOutput; // escribe en el socket
    private Socket socket; // objecto socket
    private String server, username; // servidor y nombre de usuario
    private int port; //ouerto
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    /*
     * Constructor para establecer lo sigte
     * server: la direccion del servidor
     * port: el numero de puerto
     * username: el nombre de usuario
     */
    Client(String server, int port, String username) {
        this.server = server;
        this.port = port;
        this.username = username;
    }
    /*
     * Comienza la charla
     */
    public boolean start() {
        // intenta conectar al servidor
        try {
            socket = new Socket(server, port);
        }
        // exceocion si el handler (manejador) fallo
        catch(Exception ec) {
            display("Error al conectar al servidor:" + ec);
            return false;
        }
        String msg = "Connexion acceptada " + socket.getInetAddress() + ":" + socket.getPort();
        display(msg);
        /* Creando ambos flujos de data */
        try
        {
            sInput = new ObjectInputStream(socket.getInputStream());
            sOutput = new ObjectOutputStream(socket.getOutputStream());
        }
        catch (IOException eIO) {
            display("Excepcion creabdo nuevo Flujo de Entrada/Salida: " + eIO);
            return false;
        }
        // create el hilo para escuchar desde el servidor
        new ListenFromServer().start();
        // Envia nuestro nombre de usuario al servidor, este el el unico mensaje que
        // enviaremos como String. Todos los otros mensajes seran objetos ChatMessage
        try
        {
            sOutput.writeObject(username);
        }
        catch (IOException eIO) {
            display("Excepcion en el login : " + eIO);
            disconnect();
            return false;
        }
        // exito, informamos al al llamante que funciona
        return true;
    }
    /*
     * Para enviar un mensaje a la consola
     */
    private void display(String msg) {
        System.out.println(msg);
    }
    /*
     * Para enviar un mensaje al servidor
     */
    void sendMessage(ChatMessage msg) {
        try {
            sOutput.writeObject(msg);
        }
        catch(IOException e) {
            display("Excepcion esxribiendo al servidor: " + e);
        }
    }
    /*
     * Cuando algo va mal
     * Cierra el flujo de Entrada/Salida y se desconecta
     */
    private void disconnect() {
        try {
            if(sInput != null) sInput.close();
        }
        catch(Exception e) {}
        try {
            if(sOutput != null) sOutput.close();
        }
        catch(Exception e) {}

        try{

            if(socket != null) socket.close();
        }
        catch(Exception e) {}
    }
    /*
     * Para iniciar el Cliente en modo console use alguno de los siguientes comandos
     * > java Client
     * > java Client username
     * > java Client username portNumber
     * > java Client username portNumber serverAddress
     * en el promt de la consola
     * Si el portNumbre no se especifica por defecto se usa 1500
     * Si el serverAddress no se especifica "localHost" sera usado
     * Si el username no se especifica "Anonymous" sera usado
     */
    public static void main(String[] args) {
        // Valores por defecto si no fueron ingresados
        int portNumber = 1500;
        String serverAddress = "localhost";
        String userName = "Anonymous";
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter the username: ");
        userName = scan.nextLine();
        // diferentes casos dependiendo de la cantidad de parametros enviados
        switch(args.length) {
            case 3:
                // para > javac Client username portNumber serverAddr
                serverAddress = args[2];
            case 2:
                // para > javac Client username portNumber
                try {
                    portNumber = Integer.parseInt(args[1]);
                }
                catch(Exception e) {
                    System.out.println("Invalid port number.");
                    System.out.println("Usage is: > java Client [username] [portNumber] [serverAddress]");
                    return;
                }
            case 1:
                // para > javac Client username
                userName = args[0];
            case 0:
                // para > java Client
                break;
                // si el numero de argumentos es invalido
            default:
                System.out.println("Metodo de uso: > java Client [username] [portNumber] [serverAddress]");

                return;
        }
        // crea un objecto Client
        Client client = new Client(serverAddress, portNumber, userName);
        // intenta conectar ak servidor y retorna si no se conecto
        if(!client.start())
            return;
        System.out.println("\nHola.! Bienvenido a la sala de charlas.");
        System.out.println("Instrucciones:");
        System.out.println("1. Simplemente tipea el mensaje que quieras que envie el broadcast a todos los clientes activos");
        System.out.println("2. Tipea '@nombredeusuario<espacio>tumensaje' sin las comillas para enviar a un cliente especifico");
        System.out.println("3. Tipea 'WHOISIN' sin las comillas para ver la lista de clientes activos");
        System.out.println("4. Tipea 'LOGOUT' sin las comillas para hacer logoff en el servidor");
        // loop infinito para obtener una entrada del usuario
        while(true) {
            System.out.print("> ");
            // lee el mensaje del usuario
            String msg = scan.nextLine();
            // logout si el mensaje es LOGOUT
            if(msg.equalsIgnoreCase("LOGOUT")) {
                client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
                break;
            }
            // mensaje para ver quien esta presete en la sala de charlas
            else if(msg.equalsIgnoreCase("WHOISIN")) {
                client.sendMessage(new ChatMessage(ChatMessage.WHOISIN, ""));
            }
            // mensaje de texto regular
            else {
                client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, msg));
            }
        }
        // cierra el recurso
        scan.close();
        // el cliente completo su trabajo, desconecta al cliente
        client.disconnect();
    }
/*
 * una clase que espera por el mensaje del servidor
 */
class ListenFromServer extends Thread {
    public void run() {
        while(true) {
            try {
                // leer el mensaje del flujo de datos de entrada
                String msg = (String) sInput.readObject();
                // imprime el mensaje
                System.out.println(msg);
                System.out.print("> ");
            }
            catch(IOException e) {
                display(notif + "El servidor a cerrado la conexion: " + e + notif);
                break;
            }
            catch(ClassNotFoundException e2) {
            }
        }
    }
}
}