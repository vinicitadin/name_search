package sequential;

import model.Result;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class SequentialSearch {
    private String directoryPath;
    private FileSearch fileSearch;
    private LineSearch lineSearch;

    public enum SearchType {
        SIMPLE, CASE_INSENSITIVE, REGEX, WHOLE_WORD
    }

    public SequentialSearch(String directoryPath) {
        this.directoryPath = directoryPath;
        this.fileSearch = new FileSearch(directoryPath);
        this.lineSearch = new LineSearch();
    }


    public List<Result> search(String searchTerm, sequential.LineSearch.SearchType sequentialType, boolean recursive) {
        List<Result> allResults = new ArrayList<>();

        List<File> txtFiles = recursive ? fileSearch.findAllTxtFilesRecursive() : fileSearch.findAllTxtFiles();

        if (txtFiles.isEmpty()) {
            System.out.println("Nenhum arquivo .txt encontrado no diretório: " + directoryPath);
            return allResults;
        }

        for (File file : txtFiles) {
            List<Result> fileResults = null;

            switch (sequentialType) {
                case SIMPLE:
                    fileResults = lineSearch.searchSimple(file, searchTerm);
                    break;
                case CASE_INSENSITIVE:
                    fileResults = lineSearch.searchCaseInsensitive(file, searchTerm);
                    break;
                case REGEX:
                    fileResults = lineSearch.searchRegex(file, searchTerm);
                    break;
                case WHOLE_WORD:
                    fileResults = lineSearch.searchWholeWord(file, searchTerm);
                    break;
            }

            if (fileResults != null) {
                allResults.addAll(fileResults);
            }
        }

        return allResults;
    }

    public List<Result> search(String searchTerm) {
        return search(searchTerm, sequential.LineSearch.SearchType.SIMPLE, false);
    }
}
