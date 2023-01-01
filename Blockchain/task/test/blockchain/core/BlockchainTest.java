package blockchain.core;

import java.util.ArrayList;
import java.util.Set;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class BlockchainTest {
    @Test
    public void isChainValidValid() {
        Blockchain blockchain = Blockchain.getInstance();
        Block b1 = new Block(1, 1934734114L, 1672497645042L, "0",
                new ArrayList<Transaction>(), "miner-1");
        Block b2 = new Block(2, -813114044L, 1672497645244L,
                "8f2b94aed3187e21e838dea73fcf293b72af69eea3cc10edbab6ae27dc33d539",
                new ArrayList<Transaction>(), "miner-2");
        assertTrue(blockchain.isChainValid());
    }

    @Test
    public void isChainValidInvalidBlockAttempted() {
        Blockchain blockchain = Blockchain.getInstance();
        Block b1 = new Block(1, 1934734114L, 1672497645042L, "0",
                new ArrayList<Transaction>(), "miner-1");
        Block b2 = new Block(5, -813114044L, 1672497645244L,
                "***NOPE***",
                new ArrayList<Transaction>(), "miner-2");
        assertTrue(blockchain.acceptBlock(b1));
        assertFalse(blockchain.acceptBlock(b2));
        assertTrue(blockchain.isChainValid());
    }

    @Test
    public void hasUnprocessedTransactionsFalse() {
        Blockchain blockchain = Blockchain.getInstance();
        assertFalse(blockchain.hasUnprocessedTransactions());
    }

    @Test
    public void blockchainEntToEndTest() {
        // TODO implement
    }
}
