package sequential;

import model.Result;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class SequentialSearch {
    private String directoryPath;
    private FileSearch fileSearch;
    private LineSearch lineSearch;

    public SequentialSearch(String directoryPath) {
        this.directoryPath = directoryPath;
        this.fileSearch = new FileSearch(directoryPath);
        this.lineSearch = new LineSearch();
    }

    public Result searchFirstOccurrence(String searchTerm) {
        List<File> txtFiles = fileSearch.findAllTxtFilesRecursive();

        if (txtFiles.isEmpty()) {
            System.out.println("Nenhum arquivo .txt encontrado no diretório: " + directoryPath);
            return null;
        }

        for (File file : txtFiles) {
            Result result = lineSearch.searchWholeWordFirstOccurrence(file, searchTerm);
            if (result != null) {
                return result;
            }
        }

        return null;
    }

    public List<Result> search(String searchTerm) {
        List<Result> allResults = new ArrayList<>();

        List<File> txtFiles = fileSearch.findAllTxtFilesRecursive();

        if (txtFiles.isEmpty()) {
            System.out.println("Nenhum arquivo .txt encontrado no diretório: " + directoryPath);
            return allResults;
        }

        for (File file : txtFiles) {
            List<Result> fileResults = lineSearch.searchWholeWord(file, searchTerm);
            if (fileResults != null) {
                allResults.addAll(fileResults);
            }
        }

        return allResults;
    }
}
