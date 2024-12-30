package com.micasend;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Database {
    public final static String DB_FILENAME = "micasend.db";
    
    public static void saveLast(int number) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DB_FILENAME))) {
            writer.write(String.valueOf(number));
        } catch (IOException e) {
            // System.err.println("Error writing to db: " + e.getMessage());
        }
    }

    public static int getLast() {
        try (BufferedReader reader = new BufferedReader(new FileReader(DB_FILENAME))) {
            String line = reader.readLine();
            return Integer.parseInt(line);
        } catch (IOException | NumberFormatException e) {
            // System.err.println("Error reading from db: " + e.getMessage());
            return -1;
        }
    }
}
