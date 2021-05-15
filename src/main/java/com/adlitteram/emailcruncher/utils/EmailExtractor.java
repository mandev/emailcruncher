package com.adlitteram.emailcruncher.utils;

import java.util.ArrayList;
import java.util.BitSet;
import org.apache.commons.validator.routines.EmailValidator;

public class EmailExtractor {

    private static final EmailValidator EMAIL_VALIDATOR = EmailValidator.getInstance();

    private static final String USER_ALLOW = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTVWXYZ0123456789._-";
    private static final String DOM_ALLOW = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTVWXYZ0123456789.-";
    private static final String EXT_ALLOW = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTVWXYZ";

    private static final BitSet USERNAME = matchChars(USER_ALLOW);
    private static final BitSet DOMAIN = matchChars(DOM_ALLOW);
    private static final BitSet EXTENSION = matchChars(EXT_ALLOW);

    private enum State {
        SEARCH,
        USERNAME,
        DOMAIN,
    }

    private static BitSet matchChars(String str) {
        final BitSet bs = new BitSet();
        for (int i = 0; i < str.length(); i++) {
            bs.set(str.charAt(i));
        }
        return bs;
    }

    public static ArrayList<String> extracts(String text) {
        ArrayList<String> list = new ArrayList<>();
        State state = State.SEARCH;

        StringBuilder email = null;
        int i = 0;

        while (i < text.length()) {
            switch (state) {
                case SEARCH:
                    if (text.charAt(i) == '@') {
                        state = State.USERNAME;
                    }
                    else {
                        i++;
                    }
                    break;

                case USERNAME:
                    int j = i - 1;
                    int min = Math.max(0, i - 64);
                    while (j >= min && USERNAME.get(text.charAt(j))) {
                        j--;
                    }
                    j++;

                    char b = text.charAt(j);
                    if (b == '.' || b == '-') {
                        j++;
                    }

                    // That fix a common problem: '>' is not escaped 
                    if ( (b == 'u' || b == 'U') 
                            && text.charAt(j + 1) == '0' && text.charAt(j + 2) == '0'
                            && text.charAt(j + 3) == '3' && text.charAt(j + 4) == 'E') {
                        j += 5;
                    }

                    if (j < i) {
                        email = new StringBuilder();
                        email.append(text, j, i).append('@');
                        state = State.DOMAIN;
                    }
                    else {
                        state = State.SEARCH;
                    }
                    i++;
                    break;

                case DOMAIN:
                    int k = i;
                    int max = Math.min(text.length(), i + 64);
                    while (k < max && DOMAIN.get(text.charAt(k))) {
                        k++;
                    }

                    int n = k - 1;
                    char d = text.charAt(n);

                    if (d == '.' || d == '-') {
                        k--;
                        n--;
                    }

                    while ((d = text.charAt(n)) != '.') {
                        if (!EXTENSION.get(d) || n < k - 8 || n == 0) {
                            n = -1;
                            break;
                        }
                        n--;
                    }

                    if (n > i && n < k - 2) {
                        email.append(text, i, k);
                        String mail = email.toString();
                        if (EMAIL_VALIDATOR.isValid(mail)) {
                            list.add(mail);
                        }
                    }

                    i = k;
                    state = State.SEARCH;
                    break;
            }
        }
        return list;
    }

}
