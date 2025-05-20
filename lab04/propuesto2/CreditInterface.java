import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CreditInterface extends Remote {
    String createCard(double amount) throws RemoteException;
    boolean authorizePurchase(String cardNumber, double amount) throws RemoteException;
    double getBalance(String cardNumber) throws RemoteException;
    boolean deleteCard(String cardNumber) throws RemoteException;
    double getAnyBalance() throws RemoteException;
}

