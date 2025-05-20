import java.rmi.Naming;
import java.util.Scanner;

public class CreditClient {
    public static void main(String[] args) {
        try {
            CreditInterface credit = (CreditInterface) Naming.lookup("rmi://localhost:1099/CreditService");
            Scanner sc = new Scanner(System.in);

            while (true) {
                System.out.println();
                System.out.println("1. Crear tarjeta");
                System.out.println("2. Comprar");
                System.out.println("3. Eliminar tarjeta");
                System.out.print("Seleccione una opcion: ");
                int op = sc.nextInt();

                if (op == 1) {
                    System.out.print("Monto inicial: ");
                    double amount = sc.nextDouble();
                    String card = credit.createCard(amount);
                    System.out.println("Tarjeta creada: " + card);
                    System.out.println("Saldo actual: " + credit.getBalance(card));
                } else if (op == 2) {
                    if (!checkCardExists(credit)) continue;
                    System.out.print("Numero de tarjeta: ");
                    String card = sc.next();
                    System.out.println("\nSaldo disponible: " + credit.getBalance(card));
                    System.out.print("Monto a pagar: ");
                    double amount = sc.nextDouble();
                    boolean ok = credit.authorizePurchase(card, amount);
                    if (ok) {
                        System.out.println("Compra autorizada");
                        System.out.println("Nuevo saldo: " + credit.getBalance(card));
                    } else {
                        System.out.println("Compra rechazada");
                    }
                } else if (op == 3) {
                    if (!checkCardExists(credit)) continue;
                    System.out.print("Numero de tarjeta a eliminar: ");
                    String card = sc.next();
                    boolean ok = credit.deleteCard(card);
                    if (ok) {
                        System.out.println("Tarjeta eliminada");
                    } else {
                        System.out.println("No se encontro la tarjeta");
                    }
                } else {
                    System.out.println("Opción inválida");
                }
            }

        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    private static boolean checkCardExists(CreditInterface credit) {
        try {
            double balance = credit.getAnyBalance();
            if (balance == -1.0) {
                System.out.println("No hay tarjetas registradas");
                return false;
            }
        } catch (Exception e) {
            System.out.println("Error al verificar tarjetas: " + e);
            return false;
        }
        return true;
    }
}

