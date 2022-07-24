package com.fullcycle.admin.catalog.domain;

import java.util.Random;

public final class StringHelper {
    private StringHelper() {}

    public static String generateRandomString(int length) {
        final int leftLimit = 48;
        final int rightLimit = 122;
        final Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
            .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
            .limit(length)
            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
            .toString();
    }
}
