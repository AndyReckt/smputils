package me.andyreckt.smp.util.openrouter.api

import com.google.gson.Gson
import me.andyreckt.smp.util.openrouter.config.ClientConfig
import me.andyreckt.smp.util.openrouter.models.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class CryptoApi(
    private val httpClient: OkHttpClient,
    private val gson: Gson,
    private val config: ClientConfig
) : BaseApi() {

    /**
     * Create a new crypto charge for purchasing credits
     * @param amount Target credit amount in USD (up to $100,000)
     * @param sender Ethereum address that will send the transaction
     * @param chainId EVM chain ID (1=Ethereum, 137=Polygon, 8453=Base)
     * @return CryptoChargeResponse containing transaction data
     */
    fun createCharge(amount: Double, sender: String, chainId: Int): CryptoChargeResponse {
        require(amount > 0 && amount <= 100000) {
            "Amount must be between 0 and $100,000"
        }
        require(chainId in SupportedChains.ALL) {
            "Unsupported chain ID. Must be one of: ${SupportedChains.ALL}"
        }
        require(sender.matches(Regex("^0x[a-fA-F0-9]{40}$"))) {
            "Invalid Ethereum address format"
        }

        val request = CryptoChargeRequest(
            amount = amount,
            sender = sender,
            chainId = chainId
        )

        val requestBody = gson.toJson(request)
            .toRequestBody("application/json".toMediaType())

        val httpRequest = Request.Builder()
            .url("${BASE_URL}/credits/coinbase")
            .post(requestBody)
            .build()

        return httpClient.newCall(httpRequest).execute().use { response ->
            handleResponse<CryptoChargeResponse>(response, gson)
        }
    }

    /**
     * Create a charge using Base chain (recommended)
     */
    fun createChargeOnBase(amount: Double, sender: String): CryptoChargeResponse {
        return createCharge(amount, sender, SupportedChains.BASE)
    }

    /**
     * Create a charge using Ethereum mainnet
     */
    fun createChargeOnEthereum(amount: Double, sender: String): CryptoChargeResponse {
        return createCharge(amount, sender, SupportedChains.ETHEREUM)
    }

    /**
     * Create a charge using Polygon
     */
    fun createChargeOnPolygon(amount: Double, sender: String): CryptoChargeResponse {
        return createCharge(amount, sender, SupportedChains.POLYGON)
    }

    /**
     * Get current credits balance
     * @return CreditsBalanceResponse with total credits, usage, and current balance
     */
    fun getCreditsBalance(): CreditsBalanceResponse {
        val request = Request.Builder()
            .url("${BASE_URL}/credits")
            .get()
            .build()

        return httpClient.newCall(request).execute().use { response ->
            handleResponse<CreditsBalanceResponse>(response, gson)
        }
    }

    /**
     * Check if balance is below threshold and needs topping up
     * @param threshold Minimum balance threshold in USD
     * @return true if balance is below threshold
     */
    fun isBalanceLow(threshold: Double = 5.0): Boolean {
        val balance = getCreditsBalance()
        return balance.data.currentBalance < threshold
    }

    /**
     * Get recommended amount to add based on current usage patterns
     * @param minimumTopUp Minimum amount to add (default $10)
     * @return Recommended top-up amount in USD
     */
    fun getRecommendedTopUpAmount(minimumTopUp: Double = 10.0): Double {
        val balance = getCreditsBalance().data
        val dailyUsage = balance.totalUsage / 30.0 // Rough estimate
        val recommendedDays = 7 // Week's worth of usage
        
        return maxOf(minimumTopUp, dailyUsage * recommendedDays)
    }

    companion object {
        const val BASE_URL = "https://openrouter.ai/api/v1"
        
        // Contract addresses for each chain (from Coinbase's onchain payment protocol)
        const val ETHEREUM_CONTRACT = "0x03059433bcdb6144624cc2443159d9445c32b7a8"
        const val POLYGON_CONTRACT = "0x03059433bcdb6144624cc2443159d9445c32b7a8"
        const val BASE_CONTRACT = "0x03059433bcdb6144624cc2443159d9445c32b7a8"
        
        fun getContractAddress(chainId: Int): String {
            return when (chainId) {
                SupportedChains.ETHEREUM -> ETHEREUM_CONTRACT
                SupportedChains.POLYGON -> POLYGON_CONTRACT
                SupportedChains.BASE -> BASE_CONTRACT
                else -> throw IllegalArgumentException("Unsupported chain ID: $chainId")
            }
        }
    }
}