import java.rmi.Naming;
import java.util.Scanner;

public class CreditClient {
    public static void main(String[] args) {
        try {
            CreditInterface credit = (CreditInterface) Naming.lookup("rmi://localhost:1099/CreditService");
            Scanner sc = new Scanner(System.in);
            System.out.print("Número de tarjeta: ");
            String card = sc.nextLine();
            System.out.print("Monto a pagar: ");
            double amount = sc.nextDouble();

            boolean result = credit.authorizePurchase(card, amount);
            if (result) {
                System.out.println("Compra autorizada.");
                System.out.println("Nuevo saldo: " + credit.getBalance(card));
            } else {
                System.out.println("Compra rechazada. Saldo insuficiente o tarjeta inválida.");
            }
            sc.close();
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }
}

