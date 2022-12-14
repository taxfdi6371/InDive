package com.ssafy.indive.utils

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ssafy.indive.blockchain.*
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.protocol.admin.Admin
import org.web3j.protocol.core.methods.request.Transaction
import org.web3j.tx.FastRawTransactionManager
import org.web3j.tx.gas.DefaultGasProvider
import org.web3j.tx.response.PollingTransactionReceiptProcessor
import java.math.BigInteger

fun Admin.unlockAccount(address: String, password: String) {
    val unlockAccount = personalUnlockAccount(address, password).sendAsync().get()

    Log.d(TAG, "unlockAccount: $address : ${unlockAccount.accountUnlocked()}")
}

fun Web3j.sendTransaction(from: String, to: String, value: BigInteger, data: String) {
    val transaction = Transaction.createFunctionCallTransaction(
        from,
        null, null, null,
        to,
        value,
        toHex(data)
    )

    // 트랜잭션 발생 및 트랜잭션 해시 저장
    val ethSendTransaction = ethSendTransaction(transaction).sendAsync().get()
    val hash = ethSendTransaction.transactionHash

    Log.d(TAG, "endTransaction: from : $from | to : $to | value : $value | data : $data \n| hash : $hash")
}

fun toHex(str: String): String {
    return String.format("%x", BigInteger(1, str.toByteArray()))
}

/**
 * ===========
 * InDive
 * ===========
 */

// 후원자 목록
fun Web3j.donate(privateKey: String, to: String, value: Int, message: String){
    val credential = Credentials.create(privateKey)
    val gasProvider = DefaultGasProvider()
    val manager = FastRawTransactionManager(
        this,
        credential,
        CHAIN_ID,
        PollingTransactionReceiptProcessor(this, TX_END_CHECK_DURATION, TX_END_CHECK_RETRY)
    )

    val inDive = InDive.load(INDIVE_ADDRESS, this, manager, gasProvider)

    val result = inDive.donate(to, BigInteger(value.toString()), message, BigInteger.valueOf(System.currentTimeMillis())).sendAsync().get()

    Log.d(TAG, "donate: ${result.transactionHash}")
}

// 후원자 목록
fun Web3j.getDonatorList(privateKey: String, artistAddress: String){
    val credential = Credentials.create(privateKey)
    val gasProvider = DefaultGasProvider()
    val manager = FastRawTransactionManager(
        this,
        credential,
        PollingTransactionReceiptProcessor(this, TX_END_CHECK_DURATION, TX_END_CHECK_RETRY)
    )

    val inDive = InDive.load(INDIVE_ADDRESS, this, manager, gasProvider)

    val result = inDive.getDonatorList(artistAddress).sendAsync().get()

    Log.d(TAG, "getDonatorList: $result")
}

// 후원 기록
fun Web3j.getDonationHistoryList(privateKey: String, artistAddress: String): List<DonationHistory> {
    val credential = Credentials.create(privateKey)
    val gasProvider = DefaultGasProvider()
    val manager = FastRawTransactionManager(
        this,
        credential,
        PollingTransactionReceiptProcessor(this, TX_END_CHECK_DURATION, TX_END_CHECK_RETRY)
    )

    val inDive = InDive.load(INDIVE_ADDRESS, this, manager, gasProvider)

    val result = inDive.getDonationHistoryList(artistAddress).sendAsync().get()
    Log.d(TAG, "getDonationHistoryList: $result")

    val gson = Gson()

    return gson.fromJson(result, object : TypeToken<List<DonationHistory>>() {}.type)
}

/**
 * ===========
 * InDiveToKen
 * ===========
 */
// Approve
fun Web3j.setTokenApprove(privateKey: String, spender: String, amount: Int){
    val credential = Credentials.create(privateKey)
    val gasProvider = DefaultGasProvider()
    val manager = FastRawTransactionManager(
        this,
        credential,
        CHAIN_ID,
        PollingTransactionReceiptProcessor(this, TX_END_CHECK_DURATION, TX_END_CHECK_RETRY)
    )

    val inDiveToken = InDiveToken.load(INDIVETOKEN_ADDRESS, this, manager, gasProvider)

    val result = inDiveToken.approve(spender, BigInteger(amount.toString())).sendAsync().get()

    Log.d(TAG, "setTokenApprove: ${result.transactionHash}")
}

// 토큰 개수 조회
fun Web3j.getTokenBalanceOf(privateKey : String, owner: String) : Int{
    val credential = Credentials.create(privateKey)
    val gasProvider = DefaultGasProvider()
    val manager = FastRawTransactionManager(
        this,
        credential,
        PollingTransactionReceiptProcessor(this, TX_END_CHECK_DURATION, TX_END_CHECK_RETRY)
    )

    val inDiveToken = InDiveToken.load(INDIVETOKEN_ADDRESS, this, manager, gasProvider)

    val balance = inDiveToken.balanceOf(owner).sendAsync().get()

    Log.d(TAG, "getTokenBalanceOf: Address = $owner | Value = $balance")

    return balance.toInt()
}

/**
 * ===========
 * InDiveNFT
 * ===========
 */

// 토큰 발급
fun Web3j.safeMint(privateKey: String, to: String, tokenURI: String) : Int{
    val credential = Credentials.create(privateKey)
    val gasProvider = DefaultGasProvider()
    val manager = FastRawTransactionManager(
        this,
        credential,
        CHAIN_ID,
        PollingTransactionReceiptProcessor(this, TX_END_CHECK_DURATION, TX_END_CHECK_RETRY)
    )
    
    val inDiveNFT = InDiveNFT.load(INDIVENFT_ADDRESS, this, manager, gasProvider)
    
    val result = inDiveNFT.safeMint(to, tokenURI).sendAsync().get()

    Log.d(TAG, "safeMint: ${result.transactionHash}")
    
    val tokenId = inDiveNFT.curruntTokenId().sendAsync().get().toInt()

    Log.d(TAG, "safeMint: tokenId : $tokenId")
    
    return tokenId
}

// 토큰 id로 URI 조회
fun Web3j.getTokenURI(privateKey: String, tokenId: Int) : String{
    val credential = Credentials.create(privateKey)
    val gasProvider = DefaultGasProvider()
    val manager = FastRawTransactionManager(
        this,
        credential,
        PollingTransactionReceiptProcessor(this, TX_END_CHECK_DURATION, TX_END_CHECK_RETRY)
    )

    val inDiveNFT = InDiveNFT.load(INDIVENFT_ADDRESS, this, manager, gasProvider)

    val tokenURI = inDiveNFT.tokenURI(BigInteger(tokenId.toString())).sendAsync().get()

    Log.d(TAG, "getTokenURI: $tokenURI")

    return tokenURI
}

fun Web3j.getNFTTokens(privateKey: String, ownerAddress: String) : List<String> {
    val credential = Credentials.create(privateKey)
    val gasProvider = DefaultGasProvider()
    val manager = FastRawTransactionManager(
        this,
        credential,
        PollingTransactionReceiptProcessor(this, TX_END_CHECK_DURATION, TX_END_CHECK_RETRY)
    )

    val inDiveNFT = InDiveNFT.load(INDIVENFT_ADDRESS, this, manager, gasProvider)

    val nftTokenList = inDiveNFT.getNFTTokens(ownerAddress).sendAsync().get() as List<String>

    Log.d(TAG, "getNFTTokens: $nftTokenList")

    return nftTokenList
}