package blockchain.core;

import static blockchain.utils.CryptUtil.verifySignature;

public record Transaction(long id, String sender, String recipient, long amount, String signature, String publicKey) {
    @Override
    // Format spec:
    // miner9 sent 30 VC to miner1
    public String toString() {
        return String.format("%s sent %d VC to %s", sender, amount, recipient) ;
    }

    public boolean isSignatureValid() {
        String transactionContent = sender() + recipient() + amount();
        try {
            if (!verifySignature(transactionContent, signature, publicKey)) {
                throw new RuntimeException("## ERROR ## Invalid RSA signature.");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }

        return true;
    }
}
