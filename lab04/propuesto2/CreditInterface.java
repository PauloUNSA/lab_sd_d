import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CreditInterface extends Remote {
    boolean authorizePurchase(String cardNumber, double amount) throws RemoteException;
    double getBalance(String cardNumber) throws RemoteException;
}
