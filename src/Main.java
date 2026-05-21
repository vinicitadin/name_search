import parallel.ParallelSearch;
import sequential.SequentialSearch;
import util.Timer;
import model.Result;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("====================================================");
        System.out.println("    Busca de Nomes em Arquivos - Java");
        System.out.println("    Sequencial vs Paralelo");
        System.out.println("====================================================\n");

        String directoryPath = "C:\\Users\\User\\Documents\\nameSearch\\name_search\\datasets";
        
        System.out.print("Digite o nome e sobrenome a buscar (separados por espaço): ");
        String searchTerm = scanner.nextLine().trim();

        // Validar se contém nome e sobrenome
        if (!isValidFullName(searchTerm)) {
            System.out.println("Erro: Você deve fornecer um nome E um sobrenome (separados por espaço).");
            scanner.close();
            return;
        }

        System.out.println("\n====================================================");
        System.out.println("    TESTE 1: BUSCA SEQUENCIAL (SEM PARALELISMO)");
        System.out.println("====================================================\n");

        Timer sequentialTimer = new Timer();
        sequentialTimer.start();

        SequentialSearch sequentialSearch = new SequentialSearch(directoryPath);
        Result sequentialResult = sequentialSearch.searchFirstOccurrence(searchTerm);

        sequentialTimer.stop();

        if (sequentialResult != null) {
            System.out.println("Resultado encontrado:");
            System.out.println(sequentialResult);
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
        Result parallelResult = null;

        switch (strategyChoice) {
            case 1:
                System.out.println("\nUsando estratégia: Uma thread por arquivo");
                parallelResult = parallelSearch.searchFirstOccurrenceOneThreadPerFile(searchTerm);
                break;
            case 2:
                System.out.println("\nUsando estratégia: Pool fixo de threads");
                parallelResult = parallelSearch.searchFirstOccurrenceFixedThreadPool(searchTerm);
                break;
            case 3:
                System.out.println("\nUsando estratégia: Threads com chunks");
                parallelResult = parallelSearch.searchFirstOccurrenceChunked(searchTerm);
                break;
            default:
                System.out.println("Opção inválida!");
                scanner.close();
                return;
        }

        parallelTimer.stop();

        if (parallelResult != null) {
            System.out.println("Resultado encontrado:");
            System.out.println(parallelResult);
        } else {
            System.out.println("Nenhum resultado encontrado.");
        }

        System.out.println("\nTempo de execução: " + parallelTimer);

        // Comparação de performance
        if (sequentialResult != null && parallelResult != null) {
        	double sequentialTime = sequentialTimer.getElapsedTime();
        	double parallelTime = parallelTimer.getElapsedTime();
            double speedUp = Timer.calculateSpeedUp(
                sequentialTimer.getElapsedTime(),
                parallelTimer.getElapsedTime()
            );
            System.out.println("\n====================================================");
            System.out.println("    ANÁLISE DE PERFORMANCE");
            System.out.println("====================================================");
            System.out.printf("Tempo Sequencial: " + String.format("%.2f", sequentialTime) + "ms\n");
            System.out.printf("Tempo Paralelo: " + String.format("%.2f", parallelTime) + "ms\n");
            System.out.println("Speed Up: " + String.format("%.2f", speedUp) + "x");
        }

        scanner.close();
    }

    private static boolean isValidFullName(String input) {
        String[] parts = input.trim().split("\\s+");
        return parts.length >= 2;
    }
}
