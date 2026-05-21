package parallel;

import model.Result;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ParallelLineSearch {

    public enum SearchType {
        WHOLE_WORD
    }

    public Result searchWholeWordFirstOccurrence(File file, String searchTerm) {
        String pattern = "\\b" + searchTerm + "\\b";

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 1;

            while ((line = reader.readLine()) != null) {
                if (line.matches(".*" + pattern + ".*")) {
                    return new Result(file.getName(), lineNumber, line);
                }
                lineNumber++;
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler arquivo: " + file.getName());
        }

        return null;
    }
}
