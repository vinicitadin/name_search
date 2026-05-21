package parallel;

import model.Result;
import java.io.File;

public class WorkerThread extends Thread {
    private File file;
    private String searchTerm;
    private ParallelLineSearch lineSearch;
    private Result result;

    public WorkerThread(File file, String searchTerm) {
        this.file = file;
        this.searchTerm = searchTerm;
        this.lineSearch = new ParallelLineSearch();
        this.result = null;
    }

    @Override
    public void run() {
        try {
            result = lineSearch.searchWholeWordFirstOccurrence(file, searchTerm);
        } catch (Exception e) {
            System.err.println("Erro na thread para arquivo: " + file.getName());
            e.printStackTrace();
        }
    }

    public Result getResult() {
        return result;
    }
}