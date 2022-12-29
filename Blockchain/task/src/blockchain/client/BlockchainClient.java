package blockchain.client;

import blockchain.core.Blockchain;
import blockchain.core.Transaction;
import blockchain.utils.RSAKeyPair;

import java.security.NoSuchAlgorithmException;

import static blockchain.utils.CryptUtil.createKeyPair;
import static blockchain.utils.CryptUtil.sign;

public class BlockchainClient implements Runnable{
    protected String name;
    protected String recipient;
    protected long amount;
    protected final Blockchain blockchain;
    protected RSAKeyPair rsaKeyPair;
    protected int coins;

    public BlockchainClient(String name, String recipient, long amount) {
        blockchain = Blockchain.getInstance();
        this.name = name;
        this.recipient = recipient;
        this.amount = amount;

        try {
            rsaKeyPair = createKeyPair(1024);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("## ERROR ## Sender was unable to generate RSA keypair:");
            throw new RuntimeException(e);
        }
    }

    public void send() {
        send(name, recipient, amount);
    }

    public void send(String sender, String recipient, long amount) {
        Transaction t;
        String transactionContent = sender + recipient + amount;

        if (amount > getBalance()) return; // when a client tries to send more than they have, send becomes NOP

        try {
            t = new Transaction(blockchain.nextTransactionId(),
                    //name, recipient, amount,
                    sender, recipient, amount,
                    sign(transactionContent, rsaKeyPair.PrivateKey()),
                    rsaKeyPair.PublicKey());
        } catch (Exception e) {
            System.out.println("## Error ## Unable to sign transaction.");
            throw new RuntimeException(e);
        }
        blockchain.receiveTransactions(t);
    }

    public long getBalance() {
        return blockchain.accountBalanceOfClient(name);
    }

    @Override
    public void run() {
        send();
    }
}
