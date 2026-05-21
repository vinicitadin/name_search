package parallel;

import model.Result;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChunkWorkerThread extends Thread {
    private List<File> files;
    private String searchTerm;
    private ParallelLineSearch lineSearch;
    private List<Result> results;
    private ParallelLineSearch.SearchType searchType;

    public ChunkWorkerThread(List<File> files, String searchTerm, ParallelLineSearch.SearchType searchType) {
        this.files = new ArrayList<>(files);
        this.searchTerm = searchTerm;
        this.searchType = searchType;
        this.lineSearch = new ParallelLineSearch();
        this.results = new CopyOnWriteArrayList<>();
    }

    @Override
    public void run() {
        for (File file : files) {
            try {
                switch (searchType) {
                    case WHOLE_WORD:
                        results.addAll(lineSearch.searchWholeWord(file, searchTerm));
                        break;
                }
            } catch (Exception e) {
                System.err.println("Erro ao processar arquivo em chunk: " + file.getName());
                e.printStackTrace();
            }
        }
    }

    public List<Result> getResults() {
        return results;
    }
}
