package parallel;

import model.Result;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ParallelLineSearch {

    public enum SearchType {
        SIMPLE, CASE_INSENSITIVE, REGEX, WHOLE_WORD
    }

    public List<Result> searchSimple(File file, String searchTerm) {
        List<Result> results = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 1;

            while ((line = reader.readLine()) != null) {
                if (line.contains(searchTerm)) {
                    results.add(new Result(file.getName(), lineNumber, line));
                }
                lineNumber++;
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler arquivo: " + file.getName());
        }

        return results;
    }

    public List<Result> searchCaseInsensitive(File file, String searchTerm) {
        List<Result> results = new ArrayList<>();
        String searchTermLower = searchTerm.toLowerCase();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 1;

            while ((line = reader.readLine()) != null) {
                if (line.toLowerCase().contains(searchTermLower)) {
                    results.add(new Result(file.getName(), lineNumber, line));
                }
                lineNumber++;
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler arquivo: " + file.getName());
        }

        return results;
    }

    public List<Result> searchRegex(File file, String pattern) {
        List<Result> results = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 1;

            while ((line = reader.readLine()) != null) {
                if (line.matches(".*" + pattern + ".*")) {
                    results.add(new Result(file.getName(), lineNumber, line));
                }
                lineNumber++;
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler arquivo: " + file.getName());
        }

        return results;
    }

    public List<Result> searchWholeWord(File file, String searchTerm) {
        List<Result> results = new ArrayList<>();
        String pattern = "\\b" + searchTerm + "\\b";

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 1;

            while ((line = reader.readLine()) != null) {
                if (line.matches(".*" + pattern + ".*")) {
                    results.add(new Result(file.getName(), lineNumber, line));
                }
                lineNumber++;
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler arquivo: " + file.getName());
        }

        return results;
    }
}
