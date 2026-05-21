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


    public Result searchOneThreadPerFile(String searchTerm) {
        List<File> txtFiles = fileSearch.findAllTxtFilesRecursive();

        if (txtFiles.isEmpty()) {
            System.out.println("Nenhum arquivo .txt encontrado no diretório: " + directoryPath);
            return null;
        }

        List<WorkerThread> threads = new ArrayList<>();

        for (File file : txtFiles) {
        	WorkerThread thread = new WorkerThread(file, searchTerm);
            threads.add(thread);
            thread.start();
        }

        Result firstResult = null;
        for (WorkerThread thread : threads) {
            try {
                thread.join();
                if (thread.getResult() != null && firstResult == null) {
                    firstResult = thread.getResult();
                    for (WorkerThread t : threads) {
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

    public Result searchFixedThreadPool(String searchTerm) {
        List<File> txtFiles = fileSearch.findAllTxtFilesRecursive();

        if (txtFiles.isEmpty()) {
            System.out.println("Nenhum arquivo .txt encontrado no diretório: " + directoryPath);
            return null;
        }

        List<WorkerThread> activeThreads = new ArrayList<>();
        Result firstResult = null;

        for (File file : txtFiles) {
            if (firstResult != null) break;

            while (activeThreads.size() >= numThreads) {
                for (int i = activeThreads.size() - 1; i >= 0; i--) {
                	WorkerThread thread = activeThreads.get(i);
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
            	WorkerThread thread = new WorkerThread(file, searchTerm);
                activeThreads.add(thread);
                thread.start();
            }
        }

        for (WorkerThread thread : activeThreads) {
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

    public Result searchChunked(String searchTerm) {
        List<File> txtFiles = fileSearch.findAllTxtFilesRecursive();

        if (txtFiles.isEmpty()) {
            System.out.println("Nenhum arquivo .txt encontrado no diretório: " + directoryPath);
            return null;
        }

        List<List<File>> chunks = divideIntoChunks(txtFiles, numThreads);

        List<ChunkWorkerThread> threads = new ArrayList<>();

        for (List<File> chunk : chunks) {
        	ChunkWorkerThread thread = new ChunkWorkerThread(chunk, searchTerm);
            threads.add(thread);
            thread.start();
        }

        Result firstResult = null;
        for (ChunkWorkerThread thread : threads) {
            try {
                thread.join();
                if (thread.getResult() != null && firstResult == null) {
                    firstResult = thread.getResult();
                    for (ChunkWorkerThread t : threads) {
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
        private ParallelLineSearch lineSearch;
        private Result result;

        public ChunkWorkerThread(List<File> files, String searchTerm) {
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
