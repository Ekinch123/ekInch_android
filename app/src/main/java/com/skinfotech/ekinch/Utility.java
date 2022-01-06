package com.skinfotech.ekinch;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class Utility {

    public static boolean isEmpty(String text) {
        return (text == null || text.isEmpty());
    }

    public static boolean isEmpty(List list) {
        return (list == null || list.isEmpty());
    }

    public static String toCamelCase(final String init) {
        if (init == null) {
            return "";
        }
        final StringBuilder ret = new StringBuilder(init.length());
        for (final String word : init.split(" ")) {
            if (!word.isEmpty()) {
                ret.append(Character.toUpperCase(word.charAt(0)));
                ret.append(word.substring(1).toLowerCase());
            }
            if (!(ret.length() == init.length())) {
                ret.append(" ");
            }
        }
        return ret.toString();
    }

    public static String getAmountInCurrencyFormat(String amountStr) {
        if (isEmpty(amountStr)) {
            return "";
        }
        double amount = Double.parseDouble(amountStr);
        if (amount < 0) {
            amount *= -1;
        }
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String retVal = NumberFormat.getCurrencyInstance(new Locale("en", "in")).format(Double.parseDouble(decimalFormat.format(amount)));
        return retVal.substring(2);
    }

    public static String setCalendarPadding(int pInput) {
        String lValue;
        if (pInput < 10) {
            lValue = "0" + pInput;
        } else {
            lValue = String.valueOf(pInput);
        }
        return lValue;
    }

    public static boolean isNotEmpty(String text) {
        return !isEmpty(text);
    }

    public static long generateRandomNumber() {
        StringBuilder sb = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 1; i < 10; i++) {
            int next;
            if (i == 1) {
                next = random.nextInt(10);
                while (next == 0) {
                    next = random.nextInt(10);
                }
            } else {
                next = random.nextInt(10);
            }
            sb.append(next);
        }
        return new BigInteger(sb.toString()).longValue();
    }
}
