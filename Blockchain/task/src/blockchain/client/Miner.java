package blockchain.client;

import blockchain.core.Block;
import blockchain.core.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class Miner extends BlockchainClient implements Runnable {

    public Miner(String name) {
        super(name, "VOID", -1);
    }

    public void mine() {
        Random rng = new Random();

        long timeStamp = new Date().getTime();
        Block b;

        List<Transaction> transactions = new ArrayList<>();
        if (blockchain.size() > 1) transactions = blockchain.pullTransactions(); // 1st block will not have transactions, as per spec

        do {
            int magic_number = rng.nextInt();
            String hashOfPrevBlock = blockchain.hashOfLastBlock();
            int blockId = blockchain.size() + 1;

            if (blockId > 1) {
                // tests don't allow out of order time stamps
                // time stamp correction if a parallel miner has found a block in the meantime
                timeStamp = Math.max(timeStamp, blockchain.getTail().getTimeStamp() + 1);
            }

            b = new Block(blockId, magic_number, timeStamp, hashOfPrevBlock, transactions, this.name);
        } while (!b.hashOfBlock().startsWith("0".repeat(blockchain.getDifficulty())));

        if (blockchain.acceptBlock(b)) { // award 100 coins to miner if mined block is accepted by the blockchain
            send("**BLOCKCHAIN**", this.name, 100);
        }
    }

    @Override
    public void run() {
        mine();
    }
}
