package me.andyreckt.smp.util.openrouter.models

import com.google.gson.annotations.SerializedName

// Request Models
data class CryptoChargeRequest(
    val amount: Double, // Target credit amount in USD (up to $100,000)
    val sender: String, // Ethereum address sending the transaction
    @SerializedName("chain_id") val chainId: Int // EVM chain ID (1, 137, or 8453)
)

// Response Models
data class CryptoChargeResponse(
    val data: CryptoChargeData
)

data class CryptoChargeData(
    val id: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("expires_at") val expiresAt: String,
    @SerializedName("web3_data") val web3Data: Web3Data
)

data class Web3Data(
    @SerializedName("transfer_intent") val transferIntent: TransferIntent
)

data class TransferIntent(
    val metadata: TransferMetadata,
    @SerializedName("call_data") val callData: TransferCallData
)

data class TransferMetadata(
    @SerializedName("chain_id") val chainId: Int,
    @SerializedName("contract_address") val contractAddress: String,
    val sender: String
)

data class TransferCallData(
    @SerializedName("recipient_amount") val recipientAmount: String,
    val deadline: String,
    val recipient: String,
    @SerializedName("recipient_currency") val recipientCurrency: String,
    @SerializedName("refund_destination") val refundDestination: String,
    @SerializedName("fee_amount") val feeAmount: String,
    val id: String,
    val operator: String,
    val signature: String,
    val prefix: String
)

// Credits Balance Models
data class CreditsBalanceResponse(
    val data: CreditsBalanceData
)

data class CreditsBalanceData(
    @SerializedName("total_credits") val totalCredits: Double,
    @SerializedName("total_usage") val totalUsage: Double
) {
    val currentBalance: Double get() = totalCredits - totalUsage
}

// Chain Constants
object SupportedChains {
    const val ETHEREUM = 1
    const val POLYGON = 137
    const val BASE = 8453 // Recommended
    
    val ALL = listOf(ETHEREUM, POLYGON, BASE)
    val NAMES = mapOf(
        ETHEREUM to "Ethereum",
        POLYGON to "Polygon",
        BASE to "Base"
    )
}

// Pool Fee Tiers for Uniswap V3
object PoolFeeTiers {
    const val LOWEST = 500 // 0.05% - Recommended for ETH pairs
    const val LOW = 3000   // 0.30% - Standard for most pairs
    const val MEDIUM = 10000 // 1.00% - For exotic pairs
    
    val ALL = listOf(LOWEST, LOW, MEDIUM)
}

// Transaction Status
enum class CryptoPaymentStatus {
    PENDING,
    CONFIRMED,
    FAILED,
    EXPIRED
}