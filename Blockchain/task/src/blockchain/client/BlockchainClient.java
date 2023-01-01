package blockchain.client;

import blockchain.core.Blockchain;
import blockchain.core.Transaction;
import blockchain.utils.RSAKeyPair;

import java.security.NoSuchAlgorithmException;

import static blockchain.utils.CryptUtil.createKeyPair;
import static blockchain.utils.CryptUtil.sign;

public class BlockchainClient implements Runnable{
    protected String name;
    protected String transferRecipient;
    protected long amount;
    protected final Blockchain blockchain;
    protected RSAKeyPair rsaKeyPair;

    public BlockchainClient(String name, String transferRecipient, long amount) {
        this.blockchain = Blockchain.getInstance();
        this.name = name;
        this.transferRecipient = transferRecipient;
        this.amount = amount;

        try {
            rsaKeyPair = createKeyPair(1024);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("## ERROR ## Sender was unable to generate RSA keypair:");
            throw new RuntimeException(e);
        }
    }

    public BlockchainClient(String name) {
        this.blockchain = Blockchain.getInstance();
        this.name = name;
    }

    public void send() {
        send(name, transferRecipient, amount);
    }

    public boolean send(String transferSender, String transferRecipient, long amount) {
        Transaction t;
        String transactionContent = transferSender + transferRecipient + amount;

        if (amount > getBalance()) return false; // when a client tries to send more than they have, send becomes NOP

        try {
            t = new Transaction(blockchain.nextTransactionId(),
                    transferSender, transferRecipient, amount,
                    sign(transactionContent, rsaKeyPair.PrivateKey()),
                    rsaKeyPair.PublicKey());
        } catch (Exception e) {
            System.out.println("## Error ## Unable to sign transaction.");
            throw new RuntimeException(e);
        }
        blockchain.receiveTransactions(t);
        return true;
    }

    public long getBalance() {
        return blockchain.accountBalanceOfClient(name);
    }

    @Override
    public void run() {
        send();
    }
}
