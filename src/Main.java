import parallel.ParallelSearch;
import parallel.ParallelLineSearch;
import sequential.LineSearch;
import sequential.SequentialSearch;
import util.Timer;
import model.Result;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("====================================================");
        System.out.println("    Busca de Nomes em Arquivos - Java");
        System.out.println("    Sequencial vs Paralelo");
        System.out.println("====================================================\n");

        String directoryPath = "C:\\Users\\lab202a\\eclipse-workspace\\name_search-main\\datasets";
        
        System.out.print("Digite o nome a buscar: ");
        String searchTerm = scanner.nextLine().trim();


        System.out.println("\nEscolha o tipo de busca:");
        System.out.println("1 - Simples (contains)");
        System.out.println("2 - Case-insensitive");
        System.out.println("3 - Regex");
        System.out.println("4 - Palavra completa");
        System.out.print("Opção: ");

        int searchTypeChoice = Integer.parseInt(scanner.nextLine().trim());
        LineSearch.SearchType sequentialType = getSequentialSearchType(searchTypeChoice);
        ParallelLineSearch.SearchType parallelType = getParallelSearchType(searchTypeChoice);

        System.out.println("\n====================================================");
        System.out.println("    TESTE 1: BUSCA SEQUENCIAL (SEM PARALELISMO)");
        System.out.println("====================================================\n");

        Timer sequentialTimer = new Timer();
        sequentialTimer.start();

        SequentialSearch sequentialSearch = new SequentialSearch(directoryPath);
        List<Result> sequentialResults = sequentialSearch.search(searchTerm, sequentialType);

        sequentialTimer.stop();

        System.out.println("Resultados encontrados: " + sequentialResults.size());
        if (!sequentialResults.isEmpty()) {
            System.out.println("\nPrimeiros 10 resultados:");
            for (int i = 0; i < Math.min(10, sequentialResults.size()); i++) {
                System.out.println((i + 1) + ". " + sequentialResults.get(i));
            }
            if (sequentialResults.size() > 10) {
                System.out.println("... e mais " + (sequentialResults.size() - 10) + " resultados");
            }
        } else {
            System.out.println("Nenhum resultado encontrado.");
        }

        System.out.println("\nTempo de execução: " + sequentialTimer);

        System.out.println("\n====================================================");
        System.out.println("    TESTE 2: BUSCA PARALELA (COM THREADS)");
        System.out.println("====================================================\n");

        int numThreads = Runtime.getRuntime().availableProcessors();
        System.out.println("Número de processadores disponíveis: " + numThreads);
        System.out.println("Estratégias de paralelismo disponíveis:");
        System.out.println("1 - Uma thread por arquivo");
        System.out.println("2 - Pool fixo de threads");
        System.out.println("3 - Threads com chunks");
        System.out.print("Escolha a estratégia (1-3): ");

        int strategyChoice = Integer.parseInt(scanner.nextLine().trim());

        Timer parallelTimer = new Timer();
        parallelTimer.start();

        ParallelSearch parallelSearch = new ParallelSearch(directoryPath, numThreads);
        List<Result> parallelResults = null;

        switch (strategyChoice) {
            case 1:
                System.out.println("\nUsando estratégia: Uma thread por arquivo");
                parallelResults = parallelSearch.searchOneThreadPerFile(searchTerm, parallelType);
                break;
            case 2:
                System.out.println("\nUsando estratégia: Pool fixo de " + numThreads + " threads");
                parallelResults = parallelSearch.searchFixedThreadPool(searchTerm, parallelType);
                break;
            case 3:
                System.out.println("\nUsando estratégia: Threads com chunks");
                parallelResults = parallelSearch.searchChunked(searchTerm, parallelType);
                break;
            default:
                System.out.println("\nOpção inválida. Usando estratégia padrão.");
                parallelResults = parallelSearch.search(searchTerm);
        }

        parallelTimer.stop();

        System.out.println("\nResultados encontrados: " + parallelResults.size());
        if (!parallelResults.isEmpty()) {
            System.out.println("\nPrimeiros 10 resultados:");
            for (int i = 0; i < Math.min(10, parallelResults.size()); i++) {
                System.out.println((i + 1) + ". " + parallelResults.get(i));
            }
            if (parallelResults.size() > 10) {
                System.out.println("... e mais " + (parallelResults.size() - 10) + " resultados");
            }
        } else {
            System.out.println("Nenhum resultado encontrado.");
        }

        System.out.println("\nTempo de execução: " + parallelTimer);

        System.out.println("\n====================================================");
        System.out.println("    ANÁLISE DE PERFORMANCE");
        System.out.println("====================================================\n");

        long sequentialTime = sequentialTimer.getElapsedTime();
        long parallelTime = parallelTimer.getElapsedTime();
        double speedUp = Timer.calculateSpeedUp(sequentialTime, parallelTime);

        System.out.printf("Tempo Sequencial:  %.2f ms\n", (double) sequentialTime);
        System.out.printf("Tempo Paralelo:    %.2f ms\n", (double) parallelTime);
        System.out.printf("Speed Up:          %.2f x\n", speedUp);

        if (speedUp > 1.0) {
            System.out.printf("Ganho de Performance: %.1f%%\n", (speedUp - 1) * 100);
        } else if (speedUp < 1.0) {
            System.out.printf("Perda de Performance: %.1f%%\n", (1 - speedUp) * 100);
        }

        scanner.close();
    }

    private static sequential.LineSearch.SearchType getSequentialSearchType(int choice) {
        switch (choice) {
            case 1:
                return sequential.LineSearch.SearchType.SIMPLE;
            case 2:
                return sequential.LineSearch.SearchType.CASE_INSENSITIVE;
            case 3:
                return sequential.LineSearch.SearchType.REGEX;
            case 4:
                return sequential.LineSearch.SearchType.WHOLE_WORD;
            default:
                return sequential.LineSearch.SearchType.SIMPLE;
        }
    }

    private static ParallelLineSearch.SearchType getParallelSearchType(int choice) {
        switch (choice) {
            case 1:
                return ParallelLineSearch.SearchType.SIMPLE;
            case 2:
                return ParallelLineSearch.SearchType.CASE_INSENSITIVE;
            case 3:
                return ParallelLineSearch.SearchType.REGEX;
            case 4:
                return ParallelLineSearch.SearchType.WHOLE_WORD;
            default:
                return ParallelLineSearch.SearchType.SIMPLE;
        }
    }
}
