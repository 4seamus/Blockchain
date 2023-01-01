package blockchain.core;

import org.junit.Test;

import java.util.ArrayList;
import static org.junit.Assert.assertTrue;

public class BlockTest {
    @Test
    public void isValidValid() {
        Block b2 = new Block(2, -813114044L, 1672497645244L,
                "8f2b94aed3187e21e838dea73fcf293b72af69eea3cc10edbab6ae27dc33d539",
                new ArrayList<Transaction>(), "miner-2");
        assertTrue(b2.isValid("8f2b94aed3187e21e838dea73fcf293b72af69eea3cc10edbab6ae27dc33d539"));
    }

    @Test
    public void isValidInvalid() {
        Block b2 = new Block(2, -813114044L, 1672497645244L,
                "8f2b94aed3187e21e838dea73fcf293b72af69eea3cc10edbab6ae27dc33d539",
                new ArrayList<Transaction>(), "miner-2");
        assertTrue(b2.isValid("XYZ"));
    }
}