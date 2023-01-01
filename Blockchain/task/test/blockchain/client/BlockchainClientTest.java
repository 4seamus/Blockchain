package blockchain.client;

import org.junit.Test;
import static junit.framework.TestCase.assertFalse;

public class BlockchainClientTest {

    @Test
    public void sendUnsuccessful() {
        String transferSender = "A";
        String transferRecipient = "B";
        long amount = 520;
        BlockchainClient client = new BlockchainClient(transferSender, transferRecipient, amount);
        assertFalse(client.send(transferSender, transferRecipient, amount));

    }
}