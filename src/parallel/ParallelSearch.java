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

    // ===== MÉTODOS DE BUSCA DE TODAS AS OCORRÊNCIAS =====

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

    // ===== MÉTODOS DE BUSCA DA PRIMEIRA OCORRÊNCIA =====

    public Result searchFirstOccurrenceOneThreadPerFile(String searchTerm) {
        List<File> txtFiles = fileSearch.findAllTxtFilesRecursive();

        if (txtFiles.isEmpty()) {
            System.out.println("Nenhum arquivo .txt encontrado no diretório: " + directoryPath);
            return null;
        }

        List<WorkerThreadFirstOccurrence> threads = new ArrayList<>();

        for (File file : txtFiles) {
            WorkerThreadFirstOccurrence thread = new WorkerThreadFirstOccurrence(file, searchTerm);
            threads.add(thread);
            thread.start();
        }

        Result firstResult = null;
        for (WorkerThreadFirstOccurrence thread : threads) {
            try {
                thread.join();
                if (thread.getResult() != null && firstResult == null) {
                    firstResult = thread.getResult();
                    // Interromper outras threads
                    for (WorkerThreadFirstOccurrence t : threads) {
                        if (t.isAlive()) {
                            t.interrupt();
                        }
                    }
                }
            } catch (InterruptedException e) {
                System.err.println("Erro ao aguardar thread");
                Thread.currentThread().interrupt();
            }
        }

        return firstResult;
    }

    public Result searchFirstOccurrenceFixedThreadPool(String searchTerm) {
        List<File> txtFiles = fileSearch.findAllTxtFilesRecursive();

        if (txtFiles.isEmpty()) {
            System.out.println("Nenhum arquivo .txt encontrado no diretório: " + directoryPath);
            return null;
        }

        List<WorkerThreadFirstOccurrence> activeThreads = new ArrayList<>();
        Result firstResult = null;

        for (File file : txtFiles) {
            if (firstResult != null) break;

            while (activeThreads.size() >= numThreads) {
                for (int i = activeThreads.size() - 1; i >= 0; i--) {
                    WorkerThreadFirstOccurrence thread = activeThreads.get(i);
                    if (!thread.isAlive()) {
                        if (thread.getResult() != null && firstResult == null) {
                            firstResult = thread.getResult();
                        }
                        activeThreads.remove(i);
                    }
                }
                if (firstResult != null) break;
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            if (firstResult == null) {
                WorkerThreadFirstOccurrence thread = new WorkerThreadFirstOccurrence(file, searchTerm);
                activeThreads.add(thread);
                thread.start();
            }
        }

        for (WorkerThreadFirstOccurrence thread : activeThreads) {
            try {
                thread.join();
                if (thread.getResult() != null && firstResult == null) {
                    firstResult = thread.getResult();
                }
            } catch (InterruptedException e) {
                System.err.println("Erro ao aguardar thread");
                Thread.currentThread().interrupt();
            }
        }

        return firstResult;
    }

    public Result searchFirstOccurrenceChunked(String searchTerm) {
        List<File> txtFiles = fileSearch.findAllTxtFilesRecursive();

        if (txtFiles.isEmpty()) {
            System.out.println("Nenhum arquivo .txt encontrado no diretório: " + directoryPath);
            return null;
        }

        List<List<File>> chunks = divideIntoChunks(txtFiles, numThreads);

        List<ChunkWorkerThreadFirstOccurrence> threads = new ArrayList<>();

        for (List<File> chunk : chunks) {
            ChunkWorkerThreadFirstOccurrence thread = new ChunkWorkerThreadFirstOccurrence(chunk, searchTerm);
            threads.add(thread);
            thread.start();
        }

        Result firstResult = null;
        for (ChunkWorkerThreadFirstOccurrence thread : threads) {
            try {
                thread.join();
                if (thread.getResult() != null && firstResult == null) {
                    firstResult = thread.getResult();
                    // Interromper outras threads
                    for (ChunkWorkerThreadFirstOccurrence t : threads) {
                        if (t.isAlive()) {
                            t.interrupt();
                        }
                    }
                }
            } catch (InterruptedException e) {
                System.err.println("Erro ao aguardar thread");
                Thread.currentThread().interrupt();
            }
        }

        return firstResult;
    }

    public List<Result> search(String searchTerm) {
        return searchOneThreadPerFile(searchTerm, ParallelLineSearch.SearchType.WHOLE_WORD);
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

    // ===== INNER CLASSES =====

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

    private static class WorkerThreadFirstOccurrence extends Thread {
        private File file;
        private String searchTerm;
        private ParallelLineSearch lineSearch;
        private Result result;

        public WorkerThreadFirstOccurrence(File file, String searchTerm) {
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

    private static class ChunkWorkerThreadFirstOccurrence extends Thread {
        private List<File> files;
        private String searchTerm;
        private ParallelLineSearch lineSearch;
        private Result result;

        public ChunkWorkerThreadFirstOccurrence(List<File> files, String searchTerm) {
            this.files = new ArrayList<>(files);
            this.searchTerm = searchTerm;
            this.lineSearch = new ParallelLineSearch();
            this.result = null;
        }

        @Override
        public void run() {
            try {
                for (File file : files) {
                    result = lineSearch.searchWholeWordFirstOccurrence(file, searchTerm);
                    if (result != null) {
                        break;
                    }
                }
            } catch (Exception e) {
                System.err.println("Erro ao processar arquivo: " + files);
            }
        }

        public Result getResult() {
            return result;
        }
    }

    public int getNumThreads() {
        return numThreads;
    }

    public void setNumThreads(int numThreads) {
        this.numThreads = numThreads;
    }
}
