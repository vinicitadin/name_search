package sequential;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileSearch {
    private String directoryPath;

    public FileSearch(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    public List<File> findAllTxtFiles() {
        List<File> txtFiles = new ArrayList<>();
        File directory = new File(directoryPath);

        if (!directory.exists() || !directory.isDirectory()) {
            System.err.println("Diretório inválido: " + directoryPath);
            return txtFiles;
        }

        File[] files = directory.listFiles((dir, name) -> name.endsWith(".txt"));

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    txtFiles.add(file);
                }
            }
        }

        return txtFiles;
    }

    public List<File> findAllTxtFilesRecursive() {
        List<File> txtFiles = new ArrayList<>();
        findTxtFilesRecursive(new File(directoryPath), txtFiles);
        return txtFiles;
    }

    private void findTxtFilesRecursive(File directory, List<File> txtFiles) {
        if (!directory.exists() || !directory.isDirectory()) {
            return;
        }

        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".txt")) {
                    txtFiles.add(file);
                } else if (file.isDirectory()) {
                    findTxtFilesRecursive(file, txtFiles);
                }
            }
        }
    }
}
