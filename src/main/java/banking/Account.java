package banking;

public class Account {
    private int id;
    private String cardNumber;
    private int pin;
    private long balance;

    public Account(int id, CharSequence cardNumber, int pin, long balance) {
        this.id = id;
        this.cardNumber = String.valueOf(cardNumber);
        this.pin = pin;
        this.balance = balance;
    }

    public static Account getNewAccount(int id) {
        return new Account(id, CardGenerator.generateCardNumber(),
                CardGenerator.generatePin(), 0);
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public int getPin() {
        return pin;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
