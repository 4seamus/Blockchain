package blockchain.client;

import blockchain.core.Block;
import blockchain.core.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static blockchain.utils.CryptUtil.sign;

public class Miner extends BlockchainClient implements Runnable {

    public Miner(String name) {
        super(name, "VOID", -1);
    }

    public void mine() {
        Random rng = new Random();

        long timeStamp = new Date().getTime();
        Block b;

        List<Transaction> transactions = new ArrayList<>();
        if (blockchain.getLength() > 1) {
            transactions = blockchain.pullTransactions();
        }

        do {
            int magic_number = rng.nextInt();
            Block head = blockchain.getHead();
            String hashOfPrevBlock = blockchain.hashOfLastBlock();
            long id = blockchain.nextBlockId();

            if (id > 1) {
                // timestamp correction if a parallel miner has found a block in the meantime
                // tests don't allow out of order timestamps
                timeStamp = Math.max(timeStamp, head.getTimeStamp() + 1);
            }

            b = new Block(id, magic_number, timeStamp, hashOfPrevBlock, null, transactions, name);
        } while (!b.hashOfBlock().startsWith("0".repeat(blockchain.getPrefixZeroCount())));

        if (blockchain.acceptBlock(b)) { // award 100 coins to miner if mined block is accepted by the blockchain
            send("**BLOCKCHAIN**", name, 100);
        }
    }

    @Override
    public void run() {
        mine();
    }
}
