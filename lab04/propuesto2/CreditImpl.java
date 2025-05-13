import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.HashMap;

public class CreditImpl extends UnicastRemoteObject implements CreditInterface {
    private HashMap<String, Double> accounts;

    public CreditImpl() throws RemoteException {
        super();
        accounts = new HashMap<>();
        accounts.put("123456", 1000.0);
        accounts.put("654321", 500.0);
    }

    public boolean authorizePurchase(String cardNumber, double amount) throws RemoteException {
        if (!accounts.containsKey(cardNumber)) return false;
        double balance = accounts.get(cardNumber);
        if (balance >= amount) {
            accounts.put(cardNumber, balance - amount);
            return true;
        }
        return false;
    }

    public double getBalance(String cardNumber) throws RemoteException {
        return accounts.getOrDefault(cardNumber, -1.0);
    }
}
