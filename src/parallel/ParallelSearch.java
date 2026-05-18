package parallel;

import model.Result;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class ParallelSearch {
    private String directoryPath;
    private FileSearch fileSearch;
    private int numThreads;

    public ParallelSearch(String directoryPath) {
        this(directoryPath, Runtime.getRuntime().availableProcessors());
    }

    public ParallelSearch(String directoryPath, int numThreads) {
        this.directoryPath = directoryPath;
        this.fileSearch = new FileSearch(directoryPath);
        this.numThreads = numThreads;
    }


    public List<Result> searchOneThreadPerFile(String searchTerm, ParallelLineSearch.SearchType type) {
        List<Result> allResults = new ArrayList<>();

        List<File> txtFiles = fileSearch.findAllTxtFilesRecursive();

        if (txtFiles.isEmpty()) {
            System.out.println("Nenhum arquivo .txt encontrado no diretório: " + directoryPath);
            return allResults;
        }

        List<WorkerThread> threads = new ArrayList<>();

        for (File file : txtFiles) {
            WorkerThread thread = new WorkerThread(file, searchTerm, type);
            threads.add(thread);
            thread.start();
        }

        for (WorkerThread thread : threads) {
            try {
                thread.join();
                allResults.addAll(thread.getResults());
            } catch (InterruptedException e) {
                System.err.println("Erro ao aguardar thread");
                Thread.currentThread().interrupt();
            }
        }

        return allResults;
    }

    public List<Result> searchFixedThreadPool(String searchTerm, ParallelLineSearch.SearchType type) {
        List<Result> allResults = new ArrayList<>();

        List<File> txtFiles = fileSearch.findAllTxtFilesRecursive();

        if (txtFiles.isEmpty()) {
            System.out.println("Nenhum arquivo .txt encontrado no diretório: " + directoryPath);
            return allResults;
        }

        List<WorkerThread> activeThreads = new ArrayList<>();
        for (File file : txtFiles) {
            while (activeThreads.size() >= numThreads) {
                for (int i = activeThreads.size() - 1; i >= 0; i--) {
                    WorkerThread thread = activeThreads.get(i);
                    if (!thread.isAlive()) {
                        allResults.addAll(thread.getResults());
                        activeThreads.remove(i);
                    }
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            WorkerThread thread = new WorkerThread(file, searchTerm, type);
            activeThreads.add(thread);
            thread.start();
        }

        for (WorkerThread thread : activeThreads) {
            try {
                thread.join();
                allResults.addAll(thread.getResults());
            } catch (InterruptedException e) {
                System.err.println("Erro ao aguardar thread");
                Thread.currentThread().interrupt();
            }
        }

        return allResults;
    }

    public List<Result> searchChunked(String searchTerm, ParallelLineSearch.SearchType type) {
        List<Result> allResults = new ArrayList<>();

        List<File> txtFiles = fileSearch.findAllTxtFilesRecursive();

        if (txtFiles.isEmpty()) {
            System.out.println("Nenhum arquivo .txt encontrado no diretório: " + directoryPath);
            return allResults;
        }

        List<List<File>> chunks = divideIntoChunks(txtFiles, numThreads);

        List<ChunkWorkerThread> threads = new ArrayList<>();

        for (List<File> chunk : chunks) {
            ChunkWorkerThread thread = new ChunkWorkerThread(chunk, searchTerm, type);
            threads.add(thread);
            thread.start();
        }

        for (ChunkWorkerThread thread : threads) {
            try {
                thread.join();
                allResults.addAll(thread.getResults());
            } catch (InterruptedException e) {
                System.err.println("Erro ao aguardar thread");
                Thread.currentThread().interrupt();
            }
        }

        return allResults;
    }

    public List<Result> search(String searchTerm) {
        return searchOneThreadPerFile(searchTerm, ParallelLineSearch.SearchType.SIMPLE);
    }

    private List<List<File>> divideIntoChunks(List<File> files, int numChunks) {
        List<List<File>> chunks = new ArrayList<>();

        if (numChunks <= 0) numChunks = 1;
        if (numChunks > files.size()) numChunks = files.size();

        int chunkSize = (int) Math.ceil((double) files.size() / numChunks);

        for (int i = 0; i < files.size(); i += chunkSize) {
            int end = Math.min(i + chunkSize, files.size());
            chunks.add(files.subList(i, end));
        }

        return chunks;
    }

    private static class ChunkWorkerThread extends Thread {
        private List<File> files;
        private String searchTerm;
        private ParallelLineSearch.SearchType searchType;
        private List<Result> results;

        public ChunkWorkerThread(List<File> files, String searchTerm, ParallelLineSearch.SearchType searchType) {
            this.files = new ArrayList<>(files);
            this.searchTerm = searchTerm;
            this.searchType = searchType;
            this.results = new ArrayList<>();
        }

        @Override
        public void run() {
        	ParallelLineSearch lineSearch = new ParallelLineSearch();

            for (File file : files) {
                try {
                    switch (searchType) {
                        case SIMPLE:
                            results.addAll(lineSearch.searchSimple(file, searchTerm));
                            break;
                        case CASE_INSENSITIVE:
                            results.addAll(lineSearch.searchCaseInsensitive(file, searchTerm));
                            break;
                        case REGEX:
                            results.addAll(lineSearch.searchRegex(file, searchTerm));
                            break;
                        case WHOLE_WORD:
                            results.addAll(lineSearch.searchWholeWord(file, searchTerm));
                            break;
                    }
                } catch (Exception e) {
                    System.err.println("Erro ao processar arquivo: " + file.getName());
                }
            }
        }

        public List<Result> getResults() {
            return results;
        }
    }

    public int getNumThreads() {
        return numThreads;
    }

    public void setNumThreads(int numThreads) {
        this.numThreads = numThreads;
    }
}
