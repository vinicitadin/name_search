package parallel;

import model.Result;
import java.io.File;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class WorkerThread extends Thread {
    private File file;
    private String searchTerm;
    private ParallelLineSearch lineSearch;
    private List<Result> results;
    private ParallelLineSearch.SearchType searchType;

    public WorkerThread(File file, String searchTerm, ParallelLineSearch.SearchType searchType) {
        this.file = file;
        this.searchTerm = searchTerm;
        this.searchType = searchType;
        this.lineSearch = new ParallelLineSearch();
        this.results = new CopyOnWriteArrayList<>();
    }

    @Override
    public void run() {
        try {
            switch (searchType) {
                case WHOLE_WORD:
                    results.addAll(lineSearch.searchWholeWord(file, searchTerm));
                    break;
            }
        } catch (Exception e) {
            System.err.println("Erro na thread para arquivo: " + file.getName());
            e.printStackTrace();
        }
    }

    public List<Result> getResults() {
        return results;
    }

    public File getFile() {
        return file;
    }
}