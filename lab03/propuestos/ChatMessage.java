package lab03.propuestos;

import java.io.*;
/*
 * Esta clase degines los diferenetes tipos de mensajes que seran intercambiados entre
 * el cliente y el servidor
 * Cuando se comunica un cliente de java hacia un sevidor de java nas facilmente se pasan obejetos,
 * no necesita contar bytes i esperar por una linea al de fin en el frame
 *
 */
public class ChatMessage implements Serializable {
    // Los diferentes tipos de mensahes enviados por el cliente
    // WHOISIN para recibir la lista de usuarios conectados
    // MESSAGE un mensaje ordinario de texto
    // LOGOUT para desconectarse del sevidor
    static final int WHOISIN = 0, MESSAGE = 1, LOGOUT = 2;
    private int type;
    private String message;
    // constructor
    ChatMessage(int type, String message) {
        this.type = type;
        this.message = message;
    }
    int getType() {
        return type;
    }
    String getMessage() {
        return message;
    }
}