package blockchain;

import blockchain.client.BlockchainClient;
import blockchain.client.Miner;
import blockchain.core.Blockchain;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

//TODO list - future, whenever
// - Convert tests to JUnit5 and improve coverage
// - Add logging

public class Main {
    private static final Blockchain blockchain = Blockchain.getInstance();
    private static final int MAX_WAIT_BETWEEN_TRANSACTIONS_MILLIS = 50;
    private static final int NUM_OF_TRANSACTIONS_TO_GENERATE = 25;
    public static void main(String[] args) throws InterruptedException {
        int threads = Runtime.getRuntime().availableProcessors() - 1;
        ExecutorService executorService = Executors.newFixedThreadPool(threads);
        Random rng = new Random();

        final Deque<String> transactions = generateTransactions();

        int minerId = 1;
        while (!transactions.isEmpty()) {
            BlockchainClient client;

            if (rng.nextDouble() <= 0.4) { // create miner with negative bias
                client = new Miner("miner" + minerId);
                minerId++;
            } else {
                String[] transactionData = transactions.pop().split(", ");
                String sender = transactionData[0];
                String recipient = transactionData[1];
                long amount = Long.parseLong(transactionData[2]);
                client = new BlockchainClient(sender, recipient, amount);
            }

            executorService.submit(client);
            Thread.sleep(rng.nextInt(MAX_WAIT_BETWEEN_TRANSACTIONS_MILLIS));  // send messages with random periodicity
        }

        // the blockchain may still have unprocessed transactions which were added at runtime, as mining award
        while (blockchain.hasUnprocessedTransactions()) {
            Miner miner = new Miner("miner" + minerId);
            executorService.submit(miner);
            minerId++;
            Thread.sleep(rng.nextInt(MAX_WAIT_BETWEEN_TRANSACTIONS_MILLIS));  // send messages with random periodicity
        }

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS); // waits for threads to terminate
    }

    static Deque<String> generateTransactions() {
        Random rng = new Random();
        List<String> senders = new ArrayList<>(List.of("Juli", "Joci", "Boci", "Mici", "Frici", "Laci", "Alfonz"));
        Deque<String> transactions = new ArrayDeque<>();
        for (int i = 0; i < NUM_OF_TRANSACTIONS_TO_GENERATE; i++) {
            Collections.shuffle(senders);
            transactions.add(String.format("%s, %s, %d", senders.get(0), senders.get(1), rng.nextInt(200)));
        }
        return transactions;
    }
}
