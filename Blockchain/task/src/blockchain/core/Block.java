package blockchain.core;

import static blockchain.utils.CryptUtil.*;

import java.util.List;

public class Block {
    long id;
    long magic_number;
    long timeStamp;
    String hashOfPrevBlock;
    Block next;
    String minerName;
    List<Transaction> transactions;

    public Block(long id, long magic_number, long timeStamp, String hashOfPrevBlock, Block next, List<Transaction> transactions, String minerName) {
        this.id = id;
        this.magic_number = magic_number;
        this.timeStamp = timeStamp;
        this.hashOfPrevBlock = hashOfPrevBlock;
        this.next = next;
        this.minerName = minerName;
        this.transactions = transactions;
    }

    public boolean isValid() {
        long prevTransactionId = 0;
        for (Transaction p : transactions) {
            if (!isTransactionSignatureValid(p)) // tamper-validate: RSA signature
                return false;

            if (p.id() <= prevTransactionId) // tamper-validate: order of transaction content
                return false;
        }

        if (next == null) return true; // last block: skip hash validation

        return hashOfBlock().equals(next.hashOfPrevBlock); // validate hash
    }

    private boolean isTransactionSignatureValid(Transaction t) {
        // String TEST_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCv6VQZqNbT/4W+tu/dw6L/EjZc1uRMihS1gcrwkVt7xHTmnmS039OgqLkV/o3EbEIUiUc1/z99KnVSaWZr53Ts9VOG8eNWA18GEXNr7IaO7cdUn8pMs2ILJ8rSeYa39U4w4fm+M/W0jQ00lGGtvJ/frbKPEg8mBSviFHv+o4igqwIDAQAB";

        String transactionContent = t.sender() + t.recipient() + t.amount();
        try {
            // if (!verifySignature(transactionContent, t.signature(), TEST_PUBLIC_KEY)) { // TEST: call with hard-coded TEST_PUBLIC_KEY to trigger failures
            if (!verifySignature(transactionContent, t.signature(), t.publicKey())) {
                throw new RuntimeException("## ERROR ## Invalid RSA signature.");
            }
        } catch (Exception e) {
            System.out.println("## ERROR ## RSA signature verification failed.\n" + e.getMessage());
            return false;
        }

        return true;
    }

    public String hashOfBlock() {
        return applySha256(id + magic_number + timeStamp + transactions.toString() + hashOfPrevBlock);
    }

    @Override
    public String toString() {
        /*
        Format Model:
            Block:
            Created by: miner9
            miner9 gets 100 VC
            Id: 1
            Timestamp: 1539810682545
            Magic number: 1234567
            Hash of the previous block:
            0
            Hash of the block:
            796f0a5106c0e114cef3ee14b5d040ecf331dbf1281cef5a7b43976f5715160d
            Block data: [either "no message" on same line OR print message(s) on newline]
         */

        StringBuilder blockAsString = new StringBuilder(String.format("""
                Block:
                Created by: %s
                %s gets 100 VC
                Id: %d
                Timestamp: %d
                Magic number: %d
                Hash of the previous block:
                %s
                Hash of the block:
                %s
                Block data:""", minerName, minerName, id, timeStamp, magic_number, hashOfPrevBlock, hashOfBlock()));
        if (transactions.isEmpty()) {
            blockAsString.append('\n').append("No transactions");
        } else {
            for (Transaction t : transactions) {
                blockAsString.append('\n').append(t);
            }
        }

        return blockAsString.toString();
    }

    public long getId() {
        return id;
    }

    public long getTimeStamp() {
        return timeStamp;
    }
}
