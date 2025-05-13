import java.rmi.Naming;

public class CreditServer {
    public static void main(String[] args) {
        try {
            CreditInterface obj = new CreditImpl();
            Naming.rebind("rmi://localhost:1099/CreditService", obj);
            System.out.println("Servidor listo");
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }
}

