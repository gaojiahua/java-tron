package stest.tron.wallet.Wallettest_p1;

import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.spongycastle.util.encoders.Hex;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.tron.api.GrpcAPI;
import org.tron.api.GrpcAPI.NumberMessage;
import org.tron.api.GrpcAPI.Return;
import org.tron.api.WalletGrpc;
import org.tron.common.crypto.ECKey;
import org.tron.common.utils.ByteArray;
import org.tron.common.utils.Utils;
import org.tron.protos.Contract;
import org.tron.protos.Protocol.Account;
import org.tron.protos.Protocol.Block;
import org.tron.protos.Protocol.Transaction;
import stest.tron.wallet.common.client.Configuration;
import stest.tron.wallet.common.client.utils.Base58;
import stest.tron.wallet.common.client.utils.PublicMethed;
import stest.tron.wallet.common.client.utils.TransactionUtils;
import java.util.*;
import java.math.BigInteger;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
public class WalletTest_p1_AssetIssue_010 {

    //testng001、testng002、testng003、testng004
    private final static  String testKey002     = "FC8BF0238748587B9617EB6D15D47A66C0E07C1A1959033CF249C6532DC29FE6";
    //private final static  String testKeyForAssetIssue010 = "782FD0B99F6F5EDC4AD167A935E1A6FE22C06E788FCCB87A80E7A466E80E4A17";
    //private final static  String transferAssetCreateKey ="895FBF2CEE60509EC4EE6F7D3ACE608FD30AEBD3A95293C46ECE7FD851B3FA72";


    //testng001、testng002、testng003、testng004
    private static final byte[] FROM_ADDRESS    = Base58.decodeFromBase58Check("27WvzgdLiUvNAStq2BCvA1LZisdD3fBX8jv");
    //private static final byte[] ASSET010ADDRESS = Base58.decodeFromBase58Check("27TiutowZrUTFJso5vBe8zp9ZJKF62e1eAH");
    //private static final byte[] TRANSFER_ASSET_CREATE_ADDRESS = Base58.decodeFromBase58Check("27RC2QnokoC1QBAGc1NMj7FSe2KZ5CTLvfV");


    private static final long now = System.currentTimeMillis();
    private static  String name = "testAssetIssue_" + Long.toString(now);
    private static final long TotalSupply = now;
    private static final long sendAmount = 10000000000L;
    String Description = "just-test";
    String Url = "https://github.com/tronprotocol/wallet-cli/";
    String updateDescription = "This is test for update asset issue, case AssetIssue_010";
    String updateUrl = "www.updateassetissue.010.cn";
    private static final String tooLongDescription = "1qazxswedcvqazxswedcvqazxswedcvqazxswedcvqazxswedcvqazxswedcvqazxswedcvqazxswedcvqazxswedcvqazxswedcvqazxswedcvqazxswedcvqazxswedcvqazxswedcvqazxswedcvqazxswedcvqazxswedcvqazxswedcvqazxswedcvqazxswedcv";
    private static final String tooLongUrl="qaswqaswqaswqaswqaswqaswqaswqaswqaswqaswqaswqaswqaswqasw1qazxswedcvqazxswedcvqazxswedcvqazxswedcvqazxswedcvqazxswedcvqazxswedcvqazxswedcvqazxswedcvqazxswedcvqazxswedcvqazxswedcvqazxswedcvqazxswedcvqazxswedcvqazxswedcvqazxswedcvqazxswedcvqazxswedcvqazxswedcv";

    Long freeAssetNetLimit = 1000L;
    Long publicFreeAssetNetLimit = 1000L;
    Long updateFreeAssetNetLimit = 10001L;
    Long updatePublicFreeAssetNetLimit = 10001L;

    private ManagedChannel channelFull = null;
    private WalletGrpc.WalletBlockingStub blockingStubFull = null;
    private String fullnode = Configuration.getByPath("testng.conf").getStringList("fullnode.ip.list").get(0);

    //get account
    ECKey ecKey        =  new ECKey(Utils.getRandom());
    byte[] ASSET010ADDRESS    = ecKey.getAddress();
    String testKeyForAssetIssue010 = ByteArray.toHexString(ecKey.getPrivKeyBytes());

    @BeforeClass(enabled = true)
    public void beforeClass(){

        logger.info(testKeyForAssetIssue010);
        channelFull = ManagedChannelBuilder.forTarget(fullnode)
                .usePlaintext(true)
                .build();
        blockingStubFull = WalletGrpc.newBlockingStub(channelFull);

        //Sendcoin to this account
        ByteString addressBS1 = ByteString.copyFrom(ASSET010ADDRESS);
        Account request1 = Account.newBuilder().setAddress(addressBS1).build();
        GrpcAPI.AssetIssueList assetIssueList1 = blockingStubFull
                .getAssetIssueByAccount(request1);
        Optional<GrpcAPI.AssetIssueList> queryAssetByAccount = Optional.ofNullable(assetIssueList1);
        if (queryAssetByAccount.get().getAssetIssueCount() == 0){
            Assert.assertTrue(PublicMethed.Sendcoin(ASSET010ADDRESS,sendAmount,FROM_ADDRESS,testKey002,blockingStubFull));
            Assert.assertTrue(PublicMethed.FreezeBalance(ASSET010ADDRESS,100000000L,3,testKeyForAssetIssue010,blockingStubFull));
            Long start = System.currentTimeMillis() + 2000;
            Long end   = System.currentTimeMillis() + 1000000000;
            Assert.assertTrue(PublicMethed.CreateAssetIssue(ASSET010ADDRESS,name,TotalSupply,1,1,start,end,1,Description,Url
                    ,freeAssetNetLimit,publicFreeAssetNetLimit,1L,1L,testKeyForAssetIssue010,blockingStubFull));
        }
        else{
            logger.info("This account already create an assetisue");
            Optional<GrpcAPI.AssetIssueList> queryAssetByAccount1 = Optional.ofNullable(assetIssueList1);
            name = ByteArray.toStr(queryAssetByAccount1.get().getAssetIssue(0).getName().toByteArray());
            Assert.assertTrue(PublicMethed.updateAsset(ASSET010ADDRESS,Description.getBytes(),Url.getBytes(),freeAssetNetLimit,
                    publicFreeAssetNetLimit,testKeyForAssetIssue010,blockingStubFull));
        }


    }

    @Test(enabled = true)
    public void TestUpdateAssetIssue(){
        //Query the description and url,freeAssetNetLimit and publicFreeAssetNetLimit
        ByteString assetNameBs = ByteString.copyFrom(name.getBytes());
        GrpcAPI.BytesMessage request = GrpcAPI.BytesMessage.newBuilder().setValue(assetNameBs).build();
        Contract.AssetIssueContract assetIssueByName = blockingStubFull.getAssetIssueByName(request);

        Assert.assertTrue(ByteArray.toStr(assetIssueByName.getDescription().toByteArray()).equals(Description));
        Assert.assertTrue(ByteArray.toStr(assetIssueByName.getUrl().toByteArray()).equals(Url));
        Assert.assertTrue(assetIssueByName.getFreeAssetNetLimit() == freeAssetNetLimit);
        Assert.assertTrue(assetIssueByName.getPublicFreeAssetNetLimit() == publicFreeAssetNetLimit);

        //Test update asset issue
        Assert.assertTrue(PublicMethed.updateAsset(ASSET010ADDRESS,updateDescription.getBytes(),updateUrl.getBytes(),updateFreeAssetNetLimit,
                updatePublicFreeAssetNetLimit,testKeyForAssetIssue010,blockingStubFull));

        //After update asset issue ,query the description and url,freeAssetNetLimit and publicFreeAssetNetLimit
        assetNameBs = ByteString.copyFrom(name.getBytes());
        request = GrpcAPI.BytesMessage.newBuilder().setValue(assetNameBs).build();
        assetIssueByName = blockingStubFull.getAssetIssueByName(request);

        Assert.assertTrue(ByteArray.toStr(assetIssueByName.getDescription().toByteArray()).equals(updateDescription));
        Assert.assertTrue(ByteArray.toStr(assetIssueByName.getUrl().toByteArray()).equals(updateUrl));
        Assert.assertTrue(assetIssueByName.getFreeAssetNetLimit() == updateFreeAssetNetLimit);
        Assert.assertTrue(assetIssueByName.getPublicFreeAssetNetLimit() == updatePublicFreeAssetNetLimit);
    }

    @Test(enabled = true)
    public void TestUpdateAssetIssueExcption(){
        //Test update asset issue for wrong parameter
        //publicFreeAssetNetLimit is -1
        Assert.assertFalse(PublicMethed.updateAsset(ASSET010ADDRESS,updateDescription.getBytes(),updateUrl.getBytes(),updateFreeAssetNetLimit,
                -1L,testKeyForAssetIssue010,blockingStubFull));
        //publicFreeAssetNetLimit is 0
        Assert.assertTrue(PublicMethed.updateAsset(ASSET010ADDRESS,updateDescription.getBytes(),updateUrl.getBytes(),updateFreeAssetNetLimit,
                0,testKeyForAssetIssue010,blockingStubFull));
        //FreeAssetNetLimit is -1
        Assert.assertFalse(PublicMethed.updateAsset(ASSET010ADDRESS,updateDescription.getBytes(),updateUrl.getBytes(),-1,
                publicFreeAssetNetLimit,testKeyForAssetIssue010,blockingStubFull));
        //FreeAssetNetLimit is 0
        Assert.assertTrue(PublicMethed.updateAsset(ASSET010ADDRESS,updateDescription.getBytes(),updateUrl.getBytes(),0,
                publicFreeAssetNetLimit,testKeyForAssetIssue010,blockingStubFull));
        //Description is null
        Assert.assertTrue(PublicMethed.updateAsset(ASSET010ADDRESS,"".getBytes(),updateUrl.getBytes(),freeAssetNetLimit,
                publicFreeAssetNetLimit,testKeyForAssetIssue010,blockingStubFull));
        //Url is null
        Assert.assertFalse(PublicMethed.updateAsset(ASSET010ADDRESS,Description.getBytes(),"".getBytes(),freeAssetNetLimit,
                publicFreeAssetNetLimit,testKeyForAssetIssue010,blockingStubFull));
        //Too long discription
        Assert.assertFalse(PublicMethed.updateAsset(ASSET010ADDRESS,tooLongDescription.getBytes(),Url.getBytes(),freeAssetNetLimit,
                publicFreeAssetNetLimit,testKeyForAssetIssue010,blockingStubFull));
        //Too long URL
        Assert.assertFalse(PublicMethed.updateAsset(ASSET010ADDRESS,Description.getBytes(),tooLongUrl.getBytes(),freeAssetNetLimit,
                publicFreeAssetNetLimit,testKeyForAssetIssue010,blockingStubFull));
    }

    @AfterClass(enabled = true)
    public void shutdown() throws InterruptedException {
        //Assert.assertTrue(PublicMethed.updateAsset(ASSET010ADDRESS,Description.getBytes(),Url.getBytes(),freeAssetNetLimit,
         //       publicFreeAssetNetLimit,testKeyForAssetIssue010,blockingStubFull));
        Assert.assertTrue(PublicMethed.updateAsset(ASSET010ADDRESS,Description.getBytes(),Url.getBytes(),1999999999,
                199,testKeyForAssetIssue010,blockingStubFull));
        if (channelFull != null) {
            channelFull.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        }
    }

    public Boolean CreateAssetIssue(byte[] address, String name, Long TotalSupply, Integer TrxNum, Integer IcoNum, Long StartTime, Long EndTime,
                                     Integer VoteScore, String Description, String URL, Long fronzenAmount, Long frozenDay,String priKey){
        ECKey temKey = null;
        try {
            BigInteger priK = new BigInteger(priKey, 16);
            temKey = ECKey.fromPrivate(priK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        ECKey ecKey= temKey;
        Account search = PublicMethed.queryAccount(priKey,blockingStubFull);

            try {
                Contract.AssetIssueContract.Builder builder = Contract.AssetIssueContract.newBuilder();
                builder.setOwnerAddress(ByteString.copyFrom(address));
                builder.setName(ByteString.copyFrom(name.getBytes()));
                builder.setTotalSupply(TotalSupply);
                builder.setTrxNum(TrxNum);
                builder.setNum(IcoNum);
                builder.setStartTime(StartTime);
                builder.setEndTime(EndTime);
                builder.setVoteScore(VoteScore);
                builder.setDescription(ByteString.copyFrom(Description.getBytes()));
                builder.setUrl(ByteString.copyFrom(URL.getBytes()));
                Contract.AssetIssueContract.FrozenSupply.Builder frozenBuilder = Contract.AssetIssueContract.FrozenSupply.newBuilder();
                frozenBuilder.setFrozenAmount(fronzenAmount);
                frozenBuilder.setFrozenDays(frozenDay);
                builder.addFrozenSupply(0,frozenBuilder);

                Transaction transaction = blockingStubFull.createAssetIssue(builder.build());
                if (transaction == null || transaction.getRawData().getContractCount() == 0) {
                    logger.info("transaction == null");
                    return false;
                }
                transaction = signTransaction(ecKey,transaction);
                Return response = blockingStubFull.broadcastTransaction(transaction);
                if (response.getResult() == false){
                    logger.info(ByteArray.toStr(response.getMessage().toByteArray()));
                    return false;
                }
                else{
                    logger.info(name);
                    return true;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
    }

    public Account queryAccount(ECKey ecKey,WalletGrpc.WalletBlockingStub blockingStubFull) {
        byte[] address;
        if (ecKey == null) {
            String pubKey = loadPubKey(); //04 PubKey[128]
            if (StringUtils.isEmpty(pubKey)) {
                logger.warn("Warning: QueryAccount failed, no wallet address !!");
                return null;
            }
            byte[] pubKeyAsc = pubKey.getBytes();
            byte[] pubKeyHex = Hex.decode(pubKeyAsc);
            ecKey = ECKey.fromPublicOnly(pubKeyHex);
        }
        return grpcQueryAccount(ecKey.getAddress(), blockingStubFull);
    }

    public static String loadPubKey() {
        char[] buf = new char[0x100];
        return String.valueOf(buf, 32, 130);
    }

    public byte[] getAddress(ECKey ecKey) {
        return ecKey.getAddress();
    }

    public Account grpcQueryAccount(byte[] address, WalletGrpc.WalletBlockingStub blockingStubFull) {
        ByteString addressBS = ByteString.copyFrom(address);
        Account request = Account.newBuilder().setAddress(addressBS).build();
        return blockingStubFull.getAccount(request);
        }

    public Block getBlock(long blockNum, WalletGrpc.WalletBlockingStub blockingStubFull) {
        NumberMessage.Builder builder = NumberMessage.newBuilder();
        builder.setNum(blockNum);
        return blockingStubFull.getBlockByNum(builder.build());

    }

    private Transaction signTransaction(ECKey ecKey, Transaction transaction) {
        if (ecKey == null || ecKey.getPrivKey() == null) {
            logger.warn("Warning: Can't sign,there is no private key !!");
            return null;
        }
        transaction = TransactionUtils.setTimestamp(transaction);
        return TransactionUtils.sign(transaction, ecKey);
    }

    public boolean TransferAsset(byte[] to, byte[] assertName, long amount, byte[] address, String priKey) {
        ECKey temKey = null;
        try {
            BigInteger priK = new BigInteger(priKey, 16);
            temKey = ECKey.fromPrivate(priK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        ECKey ecKey= temKey;


        Contract.TransferAssetContract.Builder builder = Contract.TransferAssetContract.newBuilder();
        ByteString bsTo = ByteString.copyFrom(to);
        ByteString bsName = ByteString.copyFrom(assertName);
        ByteString bsOwner = ByteString.copyFrom(address);
        builder.setToAddress(bsTo);
        builder.setAssetName(bsName);
        builder.setOwnerAddress(bsOwner);
        builder.setAmount(amount);

        Contract.TransferAssetContract contract = builder.build();
        Transaction transaction =  blockingStubFull.transferAsset(contract);
        if (transaction == null || transaction.getRawData().getContractCount() == 0) {
            return false;
        }
        transaction = signTransaction(ecKey,transaction);
        Return response = blockingStubFull.broadcastTransaction(transaction);
        if (response.getResult() == false){
            return false;
        }
        else{
            Account search = queryAccount(ecKey, blockingStubFull);
            return true;
        }

    }

    public boolean UnFreezeAsset(byte[] Address, String priKey) {
        byte[] address = Address;

        ECKey temKey = null;
        try {
            BigInteger priK = new BigInteger(priKey, 16);
            temKey = ECKey.fromPrivate(priK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        ECKey ecKey= temKey;

        Contract.UnfreezeAssetContract.Builder builder = Contract.UnfreezeAssetContract
                .newBuilder();
        ByteString byteAddreess = ByteString.copyFrom(address);

        builder.setOwnerAddress(byteAddreess);

        Contract.UnfreezeAssetContract contract = builder.build();


        Transaction transaction = blockingStubFull.unfreezeAsset(contract);

        if (transaction == null || transaction.getRawData().getContractCount() == 0) {
            return false;
        }

        transaction = TransactionUtils.setTimestamp(transaction);
        transaction = TransactionUtils.sign(transaction, ecKey);
        Return response = blockingStubFull.broadcastTransaction(transaction);
        if (response.getResult() == false){
            logger.info(ByteArray.toStr(response.getMessage().toByteArray()));
            return false;
        }
        else{
            return true;
        }
    }


    public boolean participateAssetIssue(byte[] to, byte[] assertName, long amount,byte[] from, String priKey) {
        ECKey temKey = null;
        try {
            BigInteger priK = new BigInteger(priKey, 16);
            temKey = ECKey.fromPrivate(priK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        ECKey ecKey= temKey;

        Contract.ParticipateAssetIssueContract.Builder builder = Contract.ParticipateAssetIssueContract
                .newBuilder();
        ByteString bsTo = ByteString.copyFrom(to);
        ByteString bsName = ByteString.copyFrom(assertName);
        ByteString bsOwner = ByteString.copyFrom(from);
        builder.setToAddress(bsTo);
        builder.setAssetName(bsName);
        builder.setOwnerAddress(bsOwner);
        builder.setAmount(amount);
        Contract.ParticipateAssetIssueContract contract = builder.build();

        Transaction transaction = blockingStubFull.participateAssetIssue(contract);
        transaction = signTransaction(ecKey,transaction);
        Return response = blockingStubFull.broadcastTransaction(transaction);
        if (response.getResult() == false){
            logger.info(ByteArray.toStr(response.getMessage().toByteArray()));
            return false;
        }
        else{
            logger.info(name);
            return true;
        }
    }

}

