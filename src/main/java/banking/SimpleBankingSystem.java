package banking;

import org.sqlite.SQLiteDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import static java.lang.System.in;

public class SimpleBankingSystem {
    public static void main(String[] args) {
        //Change url when using at another computer.
        String dbUrl = "jdbc:sqlite:./";
        String sqliteFileName = args[0];
        dbUrl = dbUrl.concat(sqliteFileName);
//        File file = new File(dbUrl);
//        boolean isCreated = false;
//        if (!file.exists()) {
//            try {
//                isCreated = file.createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            if (!isCreated || !file.exists()) {
//                System.exit(-32);
//                System.err.println(file.getName());
//                System.err.println(dbUrl);
//                System.err.println(sqliteFileName);
//            }
//        }
        SQLiteDataSource sqLiteDataSource = new SQLiteDataSource();
        sqLiteDataSource.setUrl(dbUrl);
//        System.out.println("dbUrl = " + dbUrl);
        try (Scanner kb = new Scanner(in);
             Connection connection = sqLiteDataSource.getConnection()) {
            ArrayList<Account> accounts = new ArrayList<>();
            boolean isRunning = true;
            while (isRunning) {
                System.out.println("1. Create an account\n" +
                        "2. Log into account\n" +
                        "0. Exit");
                int choice = Integer.parseInt(kb.nextLine());
                switch (choice) {
                    case 1 -> {
                        long count;
                        Account account;
                        Statement statement = connection.createStatement();
                        String findIdQuery = "SELECT id FROM card ORDER BY ID DESC LIMIT 1";
                        int id = 0;
                        try (ResultSet lastId = statement.executeQuery(findIdQuery)) {
                            if (lastId.next()) {
                                id = lastId.getInt("id");
                            }
                        }
//                        System.out.println("id = " + id);
                        id++;
                        do {
                            account = Account.getNewAccount(id);
                            Account finalAccount = account;
                            count = accounts.stream()
                                    .filter(account1 -> account1.getCardNumber().equals(finalAccount.getCardNumber()))
                                    .count();
                        } while (count == 1);
                        System.out.println();
                        System.out.println("Your card has been created");
                        System.out.println("Your card number:");
                        System.out.println(account.getCardNumber());
                        System.out.println("Your card PIN:");
                        System.out.println(account.getPin());
                        System.out.println();
                        String insertQuery = "INSERT INTO card VALUES (" +
                                account.getId() + "," +
                                account.getCardNumber() + "," +
                                account.getPin() + "," +
                                account.getBalance() + ")";
                        statement.executeUpdate(insertQuery);
                        accounts.add(account);
                    }
                    case 2 -> {
                        System.out.println("\nEnter your card number:");
                        String cardNumber = kb.nextLine();
                        Statement statement = connection.createStatement();
                        String getAllQuery = "SELECT * FROM card";
                        ResultSet allResultSet = statement.executeQuery(getAllQuery);
                        List<Account> accountsToCheck = new ArrayList<>();
                        while (allResultSet.next()) {
                            int idToAdd = allResultSet.getInt("id");
                            String number = allResultSet.getString("number");
                            int pin = Integer.parseInt(allResultSet
                                    .getString("pin"));
                            int balance = allResultSet.getInt("balance");
                            accountsToCheck.add(new Account(idToAdd, number, pin, balance));
                        }
                        Optional<Account> foundAccount = accountsToCheck.stream()
                                .reduce((account1, account2) -> account1.getCardNumber()
                                        .equals(cardNumber) ? account1 : account2);
                        System.out.println("Enter your PIN:");
                        int pincode = Integer.parseInt(kb.nextLine());
                        if (foundAccount.isPresent() && foundAccount.get().
                                getCardNumber().equals(cardNumber)) {
                            if (foundAccount.get().getPin() == pincode) {
                                boolean isLoggedIn = true;
                                System.out.println("\nYou have successfully logged in!");
                                while (isLoggedIn) {
                                    System.out.println("\n" +
                                            "1. Balance\n" +
                                            "2. Add income\n" +
                                            "3. Do transfer\n" +
                                            "4. Close account\n" +
                                            "5. Log out\n" +
                                            "0. Exit");
                                    int accountChoice = Integer.parseInt(kb.nextLine());
                                    //What do you want to do with your account?
                                    switch (accountChoice) {
                                        case 1:
                                            System.out.println();
                                            System.out.println("Balance: " + AccountManager
                                                    .getAccountBalance(foundAccount.get()));

                                            break;
                                        case 2:
                                            System.out.println("Enter income: ");
                                            long income = Long.parseLong(kb.nextLine());
                                            AccountManager.addIncome(foundAccount.get(), income);
                                            System.out.println("Income was added!");
                                            String deleteQuery = "DELETE FROM card WHERE number='" + foundAccount.get()
                                                    .getCardNumber() + "'";
                                            Account toAdd = foundAccount.get();
                                            Statement updateStatement =
                                                    connection.createStatement();
                                            updateStatement.executeUpdate(deleteQuery);
                                            String updateQuery = "INSERT INTO card VALUES (" +
                                                    toAdd.getId() + "," +
                                                    toAdd.getCardNumber() + "," +
                                                    toAdd.getPin() + "," +
                                                    toAdd.getBalance() + ")";
                                            updateStatement.executeUpdate(updateQuery);
                                            break;
                                        case 3:
                                            System.out.println("Transfer\n" +
                                                    "Enter card number:");
                                            String transferNumber = kb.nextLine();
                                            Optional<Account> accountToAdd =
                                                    accountsToCheck.stream()
                                                            .reduce((account1, account2) -> account1.getCardNumber()
                                                                    .equals(transferNumber) ?
                                                                    account1 : account2);
                                            if (!accountToAdd.get().getCardNumber()
                                                    .equals(transferNumber)) {
                                                accountToAdd = Optional.empty();
                                            }
                                            boolean isSuccess;
                                            if (accountToAdd.isPresent()) {
                                                Account toAdd1 = foundAccount.get();
                                                Account toAdd2 = accountToAdd.get();
                                                if (toAdd1.getCardNumber().equals(toAdd2.getCardNumber())) {
                                                    System.out.println("You can't transfer money to the same account!");
                                                } else {
                                                    System.out.println("Enter how much money you want to transfer:");
                                                    long amountToTransfer = Long.parseLong(kb.nextLine());
                                                    isSuccess = AccountManager.doTransfer(toAdd1, toAdd2, amountToTransfer);
                                                    if (isSuccess) {
                                                        String deleteQuery1 = "DELETE FROM card WHERE number='" +
                                                                toAdd1.getCardNumber() + "'";
                                                        String deleteQuery2 = "DELETE FROM card WHERE number='" +
                                                                toAdd2.getCardNumber() + "'";
                                                        updateStatement = connection.createStatement();
                                                        updateStatement.executeUpdate(deleteQuery1);
                                                        updateStatement.executeUpdate(deleteQuery2);


                                                        String updateQuery1 = "INSERT INTO card VALUES (" +
                                                                toAdd1.getId() + "," +
                                                                toAdd1.getCardNumber() + "," +
                                                                toAdd1.getPin() + "," +
                                                                toAdd1.getBalance() + ")";
                                                        String updateQuery2 = "INSERT INTO card VALUES (" +
                                                                toAdd2.getId() + "," +
                                                                toAdd2.getCardNumber() + "," +
                                                                toAdd2.getPin() + "," +
                                                                toAdd2.getBalance() + ")";

                                                        updateStatement.executeUpdate(updateQuery1);
                                                        updateStatement.executeUpdate(updateQuery2);
                                                    }
                                                }
                                            } else {
                                                int index = transferNumber.length() - 1;
                                                int checksum = Integer
                                                        .parseInt(transferNumber
                                                                .substring(
                                                                        index
                                                                ));
                                                if (CardGenerator
                                                        .findCheckSum(transferNumber.substring(0, index)) !=
                                                        checksum) {

                                                    System.out.println("Probably you made mistake in the card number. Please try again!");
                                                } else {
                                                    System.out.println("Such a card does not exist.");
                                                }
                                            }
                                            break;
                                        case 4:
                                            deleteQuery = "DELETE FROM card WHERE number = '" + foundAccount.get()
                                                    .getCardNumber() + "'";
                                            statement = connection.createStatement();
                                            statement.executeUpdate(deleteQuery);
                                            System.out.println("The account has been closed!");
                                            break;
                                        case 5:
                                            System.out.println("You have successfully logged out!");
                                            isLoggedIn = false;
                                            break;
                                        case 0:
                                            isLoggedIn = false;
                                            isRunning = false;
                                            break;
                                        default:
                                            System.out.println("Invalid choice");
                                    }
                                }
                            } else {
                                System.out.println("\nWrong card number or PIN!\n");
                            }
                        } else {
                            System.out.println("\nWrong card number or PIN!\n");
                        }
                    }
                    case 0 -> isRunning = false;
                    default -> System.out.println("Invalid Choice!");
                }
            }
            System.out.println("\nBye!");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }
}
