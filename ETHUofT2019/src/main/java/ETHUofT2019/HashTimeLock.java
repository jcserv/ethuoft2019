package ETHUofT2019;

import org.aion.avm.api.ABIDecoder;
import org.aion.avm.api.Address;
import java.math.BigInteger;
import org.aion.avm.api.BlockchainRuntime;

public class HashTimeLock
{
    public static Address assetLocker;
    public static Address assetFetcher;

    public static byte[] hashOfSecret;
    public static byte[] secret;

    private static long startBlockNumber;
    private static long endBlockNumber;

    private static boolean isSecretPublished;

    public static void constructor(Address _assetLocker, Address _assetFetcher, int periodBlockNumber, byte[] _hashOfSecret) {
        assetLocker = _assetLocker;
        assetFetcher = _assetFetcher;
        startBlockNumber = BlockchainRuntime.getBlockNumber();
        endBlockNumber = startBlockNumber + periodBlockNumber;
        hashOfSecret = _hashOfSecret;
        isSecretPublished = false;
    }
    
    public static void transfer(Address to, BigInteger value) {
        BlockchainRuntime.call(to, value, new byte[0], BlockchainRuntime.getRemainingEnergy());
    }

    public void returnToLocker() {
        if (BlockchainRuntime.getBlockNumber() >= endBlockNumber) {
            transfer(assetLocker, BlockchainRuntime.getBalance(assetLocker));
        }
    }

    public static void sendToFetcher(byte[] _secret) {
        if (BlockchainRuntime.getBlockNumber() < endBlockNumber && BlockchainRuntime.keccak256(secret) == hashOfSecret) {
            transfer(assetFetcher, BlockchainRuntime.getBalance(assetFetcher));
            isSecretPublished = true;
            secret = _secret;
        }
    }

    //main should be last function
    public static byte[] main() {
        return ABIDecoder.decodeAndRunWithClass(HashTimeLock.class, BlockchainRuntime.getData());
    }
}