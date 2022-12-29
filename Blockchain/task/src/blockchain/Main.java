package blockchain;

import blockchain.client.BlockchainClient;
import blockchain.client.Miner;
import blockchain.core.Blockchain;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

//TODO eventually
// - Verify 'no transaction lost' guarantee
// - Revise client inheritance (now that it is known what Stage 6 tests expect)
// - Add proper comments + javadoc

public class Main {
    private static final Blockchain blockchain = Blockchain.getInstance();
    private static final int MAX_WAIT_BETWEEN_TRANSACTIONS_MILLIS = 100;
    public static void main(String[] args) throws InterruptedException {
        int threads = Runtime.getRuntime().availableProcessors() - 3;
        ExecutorService executorService = Executors.newFixedThreadPool(threads);
        Random rng = new Random();

        int i = 1;
        while (!transactions.isEmpty()) {
            BlockchainClient client;
            String[] transactionData = transactions.pop().split(",");

            String sender = transactionData[0].strip();
            String recipient = transactionData[1].strip();
            long amount = Long.valueOf(transactionData[2].strip());

            if (rng.nextDouble() <= 0.3 ? true : false) { // create miner with negative bias
                client = new Miner("miner" + i);
                executorService.submit(client);
                i++;
            } else {
                client = new BlockchainClient(sender, recipient, amount);
            }

            executorService.submit(client);
            Thread.sleep(rng.nextInt(MAX_WAIT_BETWEEN_TRANSACTIONS_MILLIS));  // send messages with random periodicity
        }

        // the blockchain may still have unprocessed transactions:
        // those added at runtime as mining award
        while (blockchain.hasUnprocessedTransactions()) {
            Miner miner = new Miner("miner" + i);
            executorService.submit(miner);
            i++;
            Thread.sleep(rng.nextInt(MAX_WAIT_BETWEEN_TRANSACTIONS_MILLIS));  // send messages with random periodicity
        }

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS); // waits for threads to terminate
    }
    private static final List<String> TRANSACTIONS_LIST = List.of(
        "Beci, Joci, 10",
        "Keke, Kata, 25",
        "Kata, Keke, 5",
        "Laci, Mici, 250",
        "miner1, Pepe, 70",
        "Boci, Juci, 30",
        "Frici, Alfonz, 12",
        "Alfonz, Gizi, 112",
        "Kalman, Bozse, 2",
        "Jani, Juli, 53",
        "Juli, Pista, 21",
        "Juci, Juli, 27",
        "Mici, Boci, 1",
        "Mici, Boci, 1",
        "Mici, Boci, 2",
        "Boci, Mici, 3",
        "Pista, Juli, 550",
        "Pista, Juli, 550",
        "Mici, Boci, 1",
        "Mici, Boci, 1",
        "Mici, Boci, 1",
        "Mici, Boci, 1",
        "Mici, Boci, 1",
        "Mici, Boci, 1",
        "Mici, Boci, 1",
        "Mici, Boci, 1",
        "Mici, Boci, 1",
        "Mici, Boci, 1",
        "Mici, Boci, 1"
    );

    private static final Deque<String> transactions = new ArrayDeque<>(TRANSACTIONS_LIST);
}
