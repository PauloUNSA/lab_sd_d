import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Random;

public class CreditImpl extends UnicastRemoteObject implements CreditInterface {
    private HashMap<String, Double> accounts;
    private Random rand;

    public CreditImpl() throws RemoteException {
        super();
        accounts = new HashMap<>();
        rand = new Random();
    }

    public String createCard(double amount) throws RemoteException {
        String cardNumber;
        do {
            cardNumber = String.valueOf(100000 + rand.nextInt(900000));
        } while (accounts.containsKey(cardNumber));
        accounts.put(cardNumber, amount);
        return cardNumber;
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

    public boolean deleteCard(String cardNumber) throws RemoteException {
        if (accounts.containsKey(cardNumber)) {
            accounts.remove(cardNumber);
            return true;
        }
        return false;
    }

    public double getAnyBalance() throws RemoteException {
        if (accounts.isEmpty()) return -1.0;
        return 0.0;
    }
}

