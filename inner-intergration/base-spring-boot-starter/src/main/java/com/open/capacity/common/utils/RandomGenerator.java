package com.open.capacity.common.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class RandomGenerator {

    private static char[] _letters = {
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
            'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K',
            'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

    private static char[] _numbers = {
            '1', '2', '3', '4', '5', '6', '7', '8', '9', '0'};

    private static char[] _characters = {
            '~', '!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '_', '+', '`', '-', '=',
            '{', '}', '|', ':', '<', '>', '?', '[', ']', '\\', ';', '\'', ',', '.', '/'};

    private static final Map<Integer, char[]> CharsMap = new HashMap<Integer, char[]>() {{
        this.put(LETTER, _letters);
        this.put(NUMBER, _numbers);
        this.put(CHARACTER, _characters);
    }};

    public static final int LETTER = 0b001;
    public static final int NUMBER = 0b010;
    public static final int CHARACTER = 0b100;
    public static final int ALL = LETTER | NUMBER | CHARACTER;

    public static String generate(int length, int charTypes) {
        if (length <= 0 || charTypes <= 0) {
            throw new RuntimeException("illegal parameter. length or charTypes");
        }
        char[] codes = new char[length];
        int bound = randomBound(charTypes);
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(bound);
            for (Map.Entry<Integer, char[]> entry : CharsMap.entrySet()) {
                if ((entry.getKey() & charTypes) > 0) {
                    char[] chars = entry.getValue();
                    if (index < chars.length) {
                        codes[i] = chars[index];
                        break;
                    } else {
                        index -= chars.length;
                    }
                }
            }
        }
        return new String(codes);
    }

    private static int randomBound(int charTypes) {
        int length = 0;
        for (Map.Entry<Integer, char[]> entry : CharsMap.entrySet()) {
            if ((entry.getKey() & charTypes) > 0) {
                length += entry.getValue().length;
            }
        }
        return length;
    }

//    public static void main(String[] args) {
//        long start = System.currentTimeMillis();
//        for (int i = 0; i < 10000; i++) {
//            String generate = generate(6, NUMBER);
//            System.out.println(generate);
//        }
//        System.out.println(System.currentTimeMillis() - start);
//    }
}
