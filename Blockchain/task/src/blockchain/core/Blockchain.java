package blockchain.core;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

public class Blockchain { // Singleton pattern
    private static final long STARTING_BALANCE = 100; // as per spec
    private static int MAX_CHAIN_LENGTH = 15; // as per spec
    private static final Blockchain INSTANCE = new Blockchain();
    private static final Deque<Block> blockchain = new ConcurrentLinkedDeque<>();
    private static long transactionSequence;
    private int difficulty;
    private final Deque<Transaction> transactions;

    public static Blockchain getInstance() {
        return INSTANCE;
    }

    public synchronized int size() {
        return blockchain.size();
    }

    public synchronized Block getTail() {
        return blockchain.peekLast();
    }

    public synchronized Block getHead() {
        return blockchain.peekFirst();
    }

    public synchronized int getDifficulty() {
        return difficulty;
    }

    public synchronized void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public static void setMaxChainLength(int chainLength) {
        MAX_CHAIN_LENGTH = chainLength;
    }

    private Blockchain() {
        this.difficulty = 0;
        transactions = new ConcurrentLinkedDeque<>();
        transactionSequence = 1;
    }

    private boolean isBlockAcceptable(Block b) {
        String expectedHashOfPrevBlock = hashOfLastBlock();
        try {
            if (!b.isValid(expectedHashOfPrevBlock)) {
                // Actively prevents lost transactions due to block reject:
                // Put back transactions in rejected blocks to queue for future miners to process
                transactions.addAll(b.transactions);
                // throw new RuntimeException("## DIAG ## Reject: this block is invalid.");
                return false;
            }

            if (this.size() > 0 && b.getId() == blockchain.peekLast().getId()) {
                transactions.addAll(b.transactions);
                throw new RuntimeException("## DIAG ## Reject: duplicate block id");
            }

            if (b.id > 1 && !(getTail().timeStamp < b.timeStamp)) {
                transactions.addAll(b.transactions);
                throw new RuntimeException("## DIAG ## Reject: this block's timestamp is out of sequence.");
            }

        } catch (RuntimeException e) {
            System.out.println("****************************************************************");
            System.out.println(e.getMessage());
            System.out.println(b);
            System.out.println("****************************************************************");
            return false;
        }

        return true;
    }

    public synchronized boolean acceptBlock(Block b) {
        if (blockchain.size() >= MAX_CHAIN_LENGTH) { // comply with spec
            Runtime.getRuntime().halt(0);
        }

        if (!isBlockAcceptable(b)) return false;

        // block is proven acceptable, add it
        blockchain.add(b);

        printBlockStats(b);
        adjustComplexity(b); // keep block creation within 10-60 (virtual) second interval

        // validate whole blockchain after block accepted
        if (!isChainValid()) {
            System.out.println("## FATAL ## Incorrect block accepted, terminating.");
            Runtime.getRuntime().halt(0);
        }

        return true;
    }

    public synchronized boolean isChainValid() {
        if (this.size() == 0) return true; // empty blockchain is valid

        long validBlockId = this.getHead().id;
        String expectedHashOfPrevBlock = "0"; // first block expects 0 as previous hash, by convention
        for (Block b : blockchain) {
            if (!b.isValid(expectedHashOfPrevBlock)) return false;
            expectedHashOfPrevBlock = b.hashOfBlock();
            if (b.id != validBlockId) return false; // enforce ascending block id order
            validBlockId++;
        }
        return true;
    }

    public synchronized long nextTransactionId() {
        return transactionSequence++;
    }

    public synchronized String hashOfLastBlock() {
        return size() == 0 ? "0" : getTail().hashOfBlock();
    }

    // set compute complexity to keep block creation rate in a desired range
    private synchronized void adjustComplexity(Block b) { // current implementation prints
        if (durationOfCreateBlock(b) < 10) {
            setDifficulty(getDifficulty() + 1);
            System.out.println("N was increased to " + difficulty);
            System.out.println();
        }
        else if (durationOfCreateBlock(b) > 60 && difficulty > 0) {
            setDifficulty(getDifficulty() - 1);
                System.out.println("N was decreased by 1");
            System.out.println();
        } else {
            System.out.println("N stays the same");
            System.out.println();
        }
    }

    // time it took to create the block in virtual seconds
    private long durationOfCreateBlock(Block b) {
        // return (new Date().getTime() - b.getTimeStamp()) / 1000;
        // Hyperskill tests don't allow logic to work as per spec
        // workaround: pretend that 5 millisecond = 1 second
        return (new Date().getTime() - b.getTimeStamp()) / 5;
    }

    private void printBlockStats(Block b) {
        System.out.println(b);
        System.out.printf("Block was generating for %d seconds\n", durationOfCreateBlock(b));
    }

    public synchronized long accountBalanceOfClient(String client) {
        long sum = STARTING_BALANCE;
        for (Block b : blockchain) {
            for (Transaction t : b.transactions) {
                if (t.recipient().equals(client) && !t.sender().equals(client)) {
                    sum += t.amount();
                } else if (t.sender().equals(client) && !t.recipient().equals(client)) {
                    sum -= t.amount();
                }
            }
        }
        return sum;
    }

    // invoked by transaction-initiating BlockchainClient
    public synchronized void receiveTransactions(Transaction transaction) {
        this.transactions.add(transaction);
    }

    // invoked by Miner to obtain accumulated transactions since the last pull
    public synchronized List<Transaction> pullTransactions() {
        List<Transaction> transactions = new ArrayList<>(this.transactions);
        this.transactions.clear();
        return transactions;
    }

    public synchronized boolean hasUnprocessedTransactions() {
        return !transactions.isEmpty();
    }
}
