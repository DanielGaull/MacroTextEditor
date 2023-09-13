package com.danieljgaull.texteditor.texteditor.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileUtils {

    public static List<String> readAllLines(String filePath) throws FileNotFoundException {
        List<String> lines = new ArrayList<>();
        Scanner scanner = new Scanner(new File(filePath));
        while (scanner.hasNextLine()) {
            lines.add(scanner.nextLine());
        }
        scanner.close();
        return lines;
    }

}
