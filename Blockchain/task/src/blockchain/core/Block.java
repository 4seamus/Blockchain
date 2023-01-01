package blockchain.core;

import static blockchain.utils.CryptUtil.*;

import java.util.List;

public class Block {
    private static final Blockchain blockchain = Blockchain.getInstance();
    int id;
    long magic_number;
    long timeStamp;
    String hashOfPrevBlock;
    String minerName;
    List<Transaction> transactions;

    public Block(int id, long magic_number, long timeStamp, String hashOfPrevBlock, List<Transaction> transactions, String minerName) {
        this.id = id;
        this.magic_number = magic_number;
        this.timeStamp = timeStamp;
        this.hashOfPrevBlock = hashOfPrevBlock;
        this.transactions = transactions;
        this.minerName = minerName;
    }

    public boolean isValid(String expectedHashOfPrevBlock) {
        long prevTransactionId = 0;
        for (Transaction t : transactions) {
            if (!t.isSignatureValid()) { // tamper-validate: RSA signature
                return false;
            }

            if (t.id() <= prevTransactionId) { // tamper-validate: order of transaction content
                return false;
            }
        }

        if (blockchain.size() == 0) return true; // first block, skip hash validation
        return hashOfPrevBlock.equals(expectedHashOfPrevBlock);
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

    public int getId() {
        return id;
    }

    public long getTimeStamp() {
        return timeStamp;
    }
}
