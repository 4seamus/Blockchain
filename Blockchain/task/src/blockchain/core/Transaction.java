package blockchain.core;

public record Transaction(long id, String sender, String recipient, long amount, byte[] signature, String publicKey) {

    @Override
    // Format spec
    // miner9 sent 30 VC to miner1
    public String toString() {
        return String.format("%s sent %d VC to %s", sender, amount, recipient) ;
    }
}
