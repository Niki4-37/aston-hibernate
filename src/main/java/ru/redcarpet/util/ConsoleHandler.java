package ru.redcarpet.util;

import java.util.Scanner;

public class ConsoleHandler {

    private static final Scanner SCANNER = new Scanner(System.in);

    public static void write (String value) {
        System.out.println(value);
    }

    public static String read () {
        String consoleString = SCANNER.nextLine().trim();
        if ("exit".equalsIgnoreCase(consoleString)) {
            write("Application closed");
            System.exit(0);
        }
        return consoleString;
    }

    private ConsoleHandler() {}
}
