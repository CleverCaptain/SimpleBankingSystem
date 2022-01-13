package banking;

public class AccountManager {

    public static long getAccountBalance(Account accountToBalance) {

        return accountToBalance.getBalance();
    }

    public static void addIncome(Account accountToUpdate, long amountToAdd) {
        accountToUpdate.setBalance(accountToUpdate.getBalance() + amountToAdd);
    }

    public static boolean doTransfer(Account fromAccount, Account toAccount, long amount) {

        if (fromAccount.getBalance() < amount) {
            System.out.println("Not enough money!");
            return false;
        } else if (fromAccount.getCardNumber().equals(toAccount.getCardNumber())) {
            System.out.println("You can't transfer money to the same account!");
            return false;
        } else {
            fromAccount.setBalance(fromAccount.getBalance() - amount);
            toAccount.setBalance(toAccount.getBalance() + amount);
            System.out.println("Success!");
            return true;
        }
    }
}
