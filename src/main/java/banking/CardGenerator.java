package banking;

import java.util.Random;

public class CardGenerator {
    public static CharSequence generateCardNumber() {
        StringBuilder cardNumber = new StringBuilder("400000");
        Random rand = new Random();
        long number;
        do {
            number = Math.abs(rand.nextLong() % 999_999_999L);
        } while (String.valueOf(number).length() < 9);
        cardNumber.append(number);
//        cardNumber.append("7380944551");
        cardNumber.append(findCheckSum(String.valueOf(cardNumber).trim()));
        return cardNumber;
    }

    public static int findCheckSum(String cardNumber) {
        cardNumber = String.valueOf(findControlNumber(cardNumber));
        int total = 0;
        int i = 0;
        while (i < cardNumber.length()) {
            total += Integer.parseInt(cardNumber.substring(i++, i));
        }
        return total % 10 == 0 ? 0 : 10 - (total % 10);
    }

    public static CharSequence findControlNumber(String cardNumber) {
        StringBuilder controlNumber = new StringBuilder();
        for (int i = 0; i < cardNumber.length(); i++) {
            int currentNumber = Integer.parseInt(cardNumber.substring(i, i + 1));
            int toAdd;
            if (i % 2 == 0) {
                toAdd = currentNumber * 2;
            } else {
                toAdd = currentNumber;
            }
            if (toAdd > 9) {
                toAdd -= 9;
            }
            controlNumber.append(toAdd);
        }
        System.out.println(controlNumber);
        return controlNumber;
    }

    public static int generatePin() {
        int pin;
        do {
            pin = Math.abs(new Random().nextInt() % 9_999);
        } while (String.valueOf(pin).length() != 4);
        return pin;
    }
}
