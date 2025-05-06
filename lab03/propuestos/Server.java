package lab03.propuestos;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;
// el cliente puede ser corrido por consola
public class Server {
    // un ID unico para cada conexion
    private static int uniqueId;
    // un ArrayList para guaradar la lista de clientes
    private ArrayList<ClientThread> al;
    // para mostar el tiempo
    private SimpleDateFormat sdf;
    // el numero de puerto a la escucha de la conexion
    private int port;
    // para comprobar si el servidor esta corriendo
    private boolean keepGoing;
    // notificacion
    private String notif = " *** ";
    //constructor que recine el puerto a la escucha de la conexion como parametro
    public Server(int port) {
        // el puerto
        this.port = port;
        // muestra el formato hh:mm:ss
        sdf = new SimpleDateFormat("HH:mm:ss");
        // un ArrayList para guardar la lista de clientes
        al = new ArrayList<ClientThread>();
    }
    public void start() {
        keepGoing = true;
        // creacion de socket de servidor y espera de peticiones de conexion
        try
        {
            // un socket usado por el servidor
            ServerSocket serverSocket = new ServerSocket(port);
            // loop infinito para esperar conexiones ( mientras el servidor este activo )
            while(keepGoing)
            {
                display("Servidor esperando en el puerto " + port + ".");
                // acepta la connexion si es requierida por el cliente
                Socket socket = serverSocket.accept();
                // break si el se detiene
                if(!keepGoing)
                    break;
                // si el cliente esta desconectado crea un thread
                ClientThread t = new ClientThread(socket);
                //añade un cliente al arraylist
                al.add(t);
                t.start();
            }
            // intenta detener al servidor
            try {
                serverSocket.close();
                for(int i = 0; i < al.size(); ++i) {
                    ClientThread tc = al.get(i);
                    try {
                        // cierra el flujo de dara y el socket
                        tc.sInput.close();
                        tc.sOutput.close();
                        tc.socket.close();
                    }
                    catch(IOException ioE) {
                    }
                }
            }
            catch(Exception e) {
                display("Excepcion cerrando el servidor y los clientes: " + e);
            }
        }
        catch (IOException e) {

            String msg = sdf.format(new Date()) + " Excepcion en el nuevo ServerSocket: " + e + "\n";

            display(msg);
        }
    }
    // para detener al servidor
    protected void stop() {
        keepGoing = false;
        try {
            new Socket("localhost", port);
        }
        catch(Exception e) {
        }
    }
    // muestra un evento en la consola
    private void display(String msg) {
        String time = sdf.format(new Date()) + " " + msg;
        System.out.println(time);
    }
    // transmite el mensaje a todos los clientes
    private synchronized boolean broadcast(String message) {
        // añadir marca de tiempo al mensaje
        String time = sdf.format(new Date());
        // para comprobar si el mensaje es privado i.e. mensaje cliente a cliente
        String[] w = message.split(" ",3);
        boolean isPrivate = false;
        if(w[1].charAt(0)=='@')
            isPrivate=true;
        // si el mensaje es privado, envia el mensaje solo al usuario mencinado
        if(isPrivate==true)
        {
            String tocheck=w[1].substring(1, w[1].length());
            message=w[0]+w[2];
            String messageLf = time + " " + message + "\n";
            boolean found=false;
            // se itera en orden reverso para encontrar el username mencionado
            for(int y=al.size(); --y>=0;)
            {
                ClientThread ct1=al.get(y);
                String check=ct1.getUsername();
                if(check.equals(tocheck))
                {
                    // intenta escribir al cliente, si falla lo remueve de la lista
                    if(!ct1.writeMsg(messageLf)) {
                        al.remove(y);
                        display("Cliente desconectado " + ct1.username + " removido de la lista.");

                    }
                    // username encontrado y mensaje enviado
                    found=true;
                    break;
                }

            }
            // username no encontrado, retorna false
            if(found!=true)
            {
                return false;
            }
        }
        // si el mensaje es global
        else
        {
            String messageLf = time + " " + message + "\n";
            // muestra el mensaje
            System.out.print(messageLf);
            // se itera en orden reverso en caso de que algun cliente este desconectado para removerlo de la lista
            for(int i = al.size(); --i >= 0;) {
                ClientThread ct = al.get(i);
                // intenta escribir al cliente, si falla se remueve de la lista
                if(!ct.writeMsg(messageLf)) {
                    al.remove(i);
                    display("Cliente desconectado " + ct.username + " removido de la lista.");
                }
            }
        }
        return true;
    }
    // si el cliente envia LOGOUT para salir
    synchronized void remove(int id) {
        String disconnectedClient = "";
        // se escanea la lista hasta que se encuentre el ID
        for(int i = 0; i < al.size(); ++i) {
            ClientThread ct = al.get(i);
            // si se encuentra es removido
            if(ct.id == id) {
                disconnectedClient = ct.getUsername();
                al.remove(i);
                break;
            }
        }
        broadcast(notif + disconnectedClient + " ha dejado el salon de charlas." + notif);
    }
/*
 * Pasos para correr en consola
 * > java Server
 * > java Server portNumber
 * If the port number is not specified 1500 is used
 */
public static void main(String[] args) {
    // inicia el servidor en el puerto 1500 si no esta especificado
    int portNumber = 1500;
    switch(args.length) {
        case 1:
            try {
                portNumber = Integer.parseInt(args[0]);
            }
            catch(Exception e) {
                System.out.println("Numero de puerto invalid.");
                System.out.println("Usar asi: > java Server [portNumber]");
                return;
            }
        case 0:
            break;
        default:
            System.out.println("Usar ais: > java Server [portNumber]");
            return;

    }
    // crea un objeto servidor y lo inicia
    Server server = new Server(portNumber);
    server.start();
}
    // una instancia de este hilo sera ejecutada por cada cliente
    class ClientThread extends Thread {
        // el socket para obtener mensajes del cliente
        Socket socket;
        ObjectInputStream sInput;
        ObjectOutputStream sOutput;
        // id unico (facilita la deconnexion)
        int id;
        // el username del cliente
        String username;
        // objeto mennsaje para recibir mensajes y su tipo
        ChatMessage cm;
        // instantanea de tiempo
        String date;
        // Constructor
        ClientThread(Socket socket) {
            // un unico id
            id = ++uniqueId;
            this.socket = socket;
            //Crea ambos flujos de datos
            System.out.println("Hilo que intenta crear flujos de entrada/salida de objetos");
            try
            {
                sOutput = new ObjectOutputStream(socket.getOutputStream());
                sInput = new ObjectInputStream(socket.getInputStream());
                // lee el username
                username = (String) sInput.readObject();
                broadcast(notif + username + " se ha unido a la sala de charla." + notif);
            }
            catch (IOException e) {
                display("Excepcion creando el nuevo flujo de Entrada/Salida: " + e);
                return;
            }
            catch (ClassNotFoundException e) {
            }

            date = new Date().toString() + "\n";

        }
        public String getUsername() {
            return username;
        }
        public void setUsername(String username) {
            this.username = username;
        }
        // bucle infinito para leer y reenviar mensajes
        public void run() {
            // bucle hasta el LOGOUT
            boolean keepGoing = true;
            while(keepGoing) {
                // lee el mensaje (el cual es un objeto)
                try {
                    cm = (ChatMessage) sInput.readObject();
                }
                catch (IOException e) {
                    display(username + " Exception reading Streams: " + e);
                    break;
                }
                catch(ClassNotFoundException e2) {
                    break;
                }
                // obtiene el mensaje desde el objeto ChatMessage receivido
                String message = cm.getMessage();
                // diferentes opciones basadas en el tipo de mensaje
                switch(cm.getType()) {
                    case ChatMessage.MESSAGE:
                        boolean confirmation = broadcast(username + ": " + message);
                        if(confirmation==false){
                            String msg = notif + "Disculpe. El usuario no existe" + notif;
                            writeMsg(msg);
                        }
                        break;
                    case ChatMessage.LOGOUT:
                        display(username + " desconectado a traves del mensaje LOGOUT.");
                        keepGoing = false;
                        break;
                    case ChatMessage.WHOISIN:
                        writeMsg("Lista de usuarios conectados en " + sdf.format(new Date()) +

                                "\n");
                        // envia la lista de clientes activos
                        for(int i = 0; i < al.size(); ++i) {
                            ClientThread ct = al.get(i);
                            writeMsg((i+1) + ") " + ct.username + " desde " + ct.date);
                        }
                        break;
                }
            }
            // si esta fuera del bucle se desconecta y remueve el usuario de la lusta
            remove(id);
            close();
        }
        // cierra todo
        private void close() {
            try {
                if(sOutput != null) sOutput.close();
            }
            catch(Exception e) {}
            try {
                if(sInput != null) sInput.close();
            }
            catch(Exception e) {};
            try {
                if(socket != null) socket.close();
            }
            catch (Exception e) {}
        }
        // escribe un String en el flujo de salida del cliente
        private boolean writeMsg(String msg) {
            // si el cliente sigue conectado le envia un mensaje
            if(!socket.isConnected()) {
                close();
                return false;
            }
            // write the message to the stream
            try {
                sOutput.writeObject(msg);
            }
            // si ocurrio un error, no aborta, solamente informa al usuario
            catch(IOException e) {
                display(notif + "Error enviando el mensaje a " + username + notif);
                display(e.toString());
            }
            return true;
        }
    }
}