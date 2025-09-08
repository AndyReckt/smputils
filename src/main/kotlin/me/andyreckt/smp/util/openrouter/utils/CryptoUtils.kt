package me.andyreckt.smp.util.openrouter.utils

import me.andyreckt.smp.util.openrouter.models.*
import java.math.BigInteger
import java.text.SimpleDateFormat
import java.util.*

object CryptoUtils {
    
    /**
     * Convert transaction call data to format suitable for Web3 libraries
     */
    fun formatTransferIntent(callData: TransferCallData): Map<String, Any> {
        return mapOf(
            "recipientAmount" to BigInteger(callData.recipientAmount),
            "deadline" to parseDeadlineToTimestamp(callData.deadline),
            "recipient" to callData.recipient,
            "recipientCurrency" to callData.recipientCurrency,
            "refundDestination" to callData.refundDestination,
            "feeAmount" to BigInteger(callData.feeAmount),
            "id" to callData.id,
            "operator" to callData.operator,
            "signature" to callData.signature,
            "prefix" to callData.prefix
        )
    }
    
    /**
     * Parse deadline string to Unix timestamp
     */
    private fun parseDeadlineToTimestamp(deadline: String): Long {
        return try {
            val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
            formatter.timeZone = TimeZone.getTimeZone("UTC")
            formatter.parse(deadline)?.time?.div(1000) ?: 0L
        } catch (e: Exception) {
            // If parsing fails, try to parse as timestamp
            deadline.toLongOrNull() ?: 0L
        }
    }
    
    /**
     * Calculate recommended gas limit for the transaction
     */
    fun getRecommendedGasLimit(chainId: Int): BigInteger {
        return when (chainId) {
            SupportedChains.ETHEREUM -> BigInteger.valueOf(300_000)
            SupportedChains.POLYGON -> BigInteger.valueOf(250_000)
            SupportedChains.BASE -> BigInteger.valueOf(200_000)
            else -> BigInteger.valueOf(300_000)
        }
    }
    
    /**
     * Calculate recommended ETH value to send (with buffer for gas and slippage)
     */
    fun calculateRecommendedValue(
        recipientAmount: String,
        feeAmount: String,
        buffer: Double = 0.2 // 20% buffer
    ): BigInteger {
        val recipient = BigInteger(recipientAmount)
        val fee = BigInteger(feeAmount)
        val total = recipient + fee
        val bufferAmount = total.multiply(BigInteger.valueOf((buffer * 100).toLong()))
            .divide(BigInteger.valueOf(100))
        
        return total + bufferAmount
    }
    
    /**
     * Validate Ethereum address format
     */
    fun isValidEthereumAddress(address: String): Boolean {
        return address.matches(Regex("^0x[a-fA-F0-9]{40}$"))
    }
    
    /**
     * Check if a chain is supported
     */
    fun isSupportedChain(chainId: Int): Boolean {
        return chainId in SupportedChains.ALL
    }
    
    /**
     * Get chain name from ID
     */
    fun getChainName(chainId: Int): String {
        return SupportedChains.NAMES[chainId] ?: "Unknown Chain"
    }
    
    /**
     * Check if charge has expired
     */
    fun isChargeExpired(expiresAt: String): Boolean {
        return try {
            val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
            formatter.timeZone = TimeZone.getTimeZone("UTC")
            val expiry = formatter.parse(expiresAt)
            expiry?.before(Date()) ?: true
        } catch (e: Exception) {
            true // Assume expired if we can't parse
        }
    }
    
    /**
     * Get time remaining for charge in minutes
     */
    fun getChargeTimeRemaining(expiresAt: String): Long {
        return try {
            val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
            formatter.timeZone = TimeZone.getTimeZone("UTC")
            val expiry = formatter.parse(expiresAt)
            val now = Date()
            if (expiry != null && expiry.after(now)) {
                (expiry.time - now.time)
            } else {
                0L
            }
        } catch (e: Exception) {
            0L
        }
    }
    
    /**
     * Generate Web3 transaction parameters for popular libraries
     */
//    fun generateViemTransactionParams(
//        chargeData: CryptoChargeData,
//        poolFeesTier: Int = PoolFeeTiers.LOWEST
//    ): Map<String, Any> {
//        val callData = chargeData.web3Data.transferIntent.callData
//        val metadata = chargeData.web3Data.transferIntent.metadata
//
//        return mapOf(
//            "to" to metadata.contractAddress,
//            "data" to generateSwapAndTransferCalldata(callData, poolFeesTier),
//            "value" to calculateRecommendedValue(callData.recipientAmount, callData.feeAmount),
//            "gas" to getRecommendedGasLimit(metadata.chainId)
//        )
//    }
    
    /**
     * Generate function calldata for swapAndTransferUniswapV3Native
     */
    private fun generateSwapAndTransferCalldata(
        callData: TransferCallData,
        poolFeesTier: Int
    ): String {
        // This would need to be implemented with proper ABI encoding
        // For now, return a placeholder that indicates the function signature
        return "0x${getFunctionSelector("swapAndTransferUniswapV3Native")}"
    }
    
    /**
     * Get function selector (first 4 bytes of keccak256 hash)
     */
    private fun getFunctionSelector(functionName: String): String {
        // Simplified - in practice you'd use a proper keccak256 implementation
        return when (functionName) {
            "swapAndTransferUniswapV3Native" -> "a1a2a3a4" // Placeholder
            "transferNative" -> "b1b2b3b4" // Placeholder
            else -> "00000000"
        }
    }
}