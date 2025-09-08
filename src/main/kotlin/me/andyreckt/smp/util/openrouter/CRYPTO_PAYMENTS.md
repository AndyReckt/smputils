# Crypto Payments Integration Guide

This guide explains how to integrate cryptocurrency payments into your application using the OpenRouter Kotlin SDK.

## Overview

The crypto payments feature allows users to purchase OpenRouter credits using cryptocurrency on three supported chains:
- **Base (Chain ID: 8453)** - Recommended for lowest fees
- **Ethereum (Chain ID: 1)** - Highest liquidity
- **Polygon (Chain ID: 137)** - Lower fees than Ethereum

## Quick Start

### 1. Add Dependencies

```kotlin
dependencies {
    implementation("me.andyreckt.smp.util:openrouter-kotlin:1.1.0")
    implementation("org.web3j:core:4.10.3") // For Web3 integration
}
```

### 2. Initialize Client

```kotlin
val client = OpenRouterClient.create(
    apiKey = "your-openrouter-api-key",
    appName = "Your App Name"
)
```

### 3. Create a Crypto Charge

```kotlin
val charge = client.crypto.createChargeOnBase(
    amount = 25.0, // $25 USD worth of credits
    sender = "0x9a85CB3bfd494Ea3a8C9E50aA6a3c1a7E8BACE11"
)
```

## Complete Integration Example

### Android App Integration

```kotlin
class PaymentActivity : AppCompatActivity() {
    private lateinit var openRouterClient: OpenRouterClient
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        openRouterClient = OpenRouterClient.create(
            apiKey = BuildConfig.OPENROUTER_API_KEY,
            appName = "MyApp"
        )
        
        setupPaymentFlow()
    }
    
    private fun setupPaymentFlow() {
        // Check current balance
        lifecycleScope.launch {
            try {
                val balance = withContext(Dispatchers.IO) {
                    openRouterClient.crypto.getCreditsBalance()
                }
                
                updateBalanceUI(balance.data.currentBalance)
                
                if (openRouterClient.crypto.isBalanceLow(threshold = 5.0)) {
                    showTopUpSuggestion()
                }
                
            } catch (e: Exception) {
                showError("Failed to check balance: ${e.message}")
            }
        }
    }
    
    private fun initiatePayment(amount: Double, userWalletAddress: String) {
        lifecycleScope.launch {
            try {
                showLoading(true)
                
                // Create charge
                val charge = withContext(Dispatchers.IO) {
                    openRouterClient.crypto.createChargeOnBase(amount, userWalletAddress)
                }
                
                // Check if charge is still valid
                if (CryptoUtils.isChargeExpired(charge.data.expiresAt)) {
                    showError("Charge expired, please try again")
                    return@launch
                }
                
                // Launch wallet for transaction
                launchWalletTransaction(charge)
                
            } catch (e: Exception) {
                showError("Payment failed: ${e.message}")
            } finally {
                showLoading(false)
            }
        }
    }
    
    private fun launchWalletTransaction(charge: CryptoChargeResponse) {
        val callData = charge.data.web3Data.transferIntent.callData
        val metadata = charge.data.web3Data.transferIntent.metadata
        
        // Generate transaction parameters
        val txParams = CryptoUtils.generateViemTransactionParams(charge.data)
        
        // For WalletConnect or similar wallet integration
        val transactionRequest = WalletTransactionRequest(
            to = metadata.contractAddress,
            data = generateTransactionData(callData),
            value = txParams["value"].toString(),
            chainId = metadata.chainId
        )
        
        // Launch wallet app or show QR code
        walletConnector.sendTransaction(transactionRequest) { result ->
            when (result) {
                is TransactionResult.Success -> {
                    showTransactionSuccess(result.txHash)
                    monitorTransactionStatus(result.txHash)
                }
                is TransactionResult.Error -> {
                    showError("Transaction failed: ${result.message}")
                }
                is TransactionResult.Cancelled -> {
                    showMessage("Transaction cancelled by user")
                }
            }
        }
    }
    
    private fun monitorTransactionStatus(txHash: String) {
        // Monitor transaction confirmation
        // Credits will be added automatically once confirmed
        lifecycleScope.launch {
            delay(5000) // Wait 5 seconds then check balance
            
            try {
                val newBalance = withContext(Dispatchers.IO) {
                    openRouterClient.crypto.getCreditsBalance()
                }
                updateBalanceUI(newBalance.data.currentBalance)
                showMessage("Credits added successfully!")
            } catch (e: Exception) {
                // Balance might not be updated immediately
                showMessage("Transaction sent! Credits will be added once confirmed.")
            }
        }
    }
}
```

### Backend Service Integration

```kotlin
@RestController
@RequestMapping("/api/payments")
class PaymentController {
    
    @Autowired
    private lateinit var openRouterClient: OpenRouterClient
    
    @Autowired
    private lateinit var userService: UserService
    
    @PostMapping("/crypto/create-charge")
    fun createCryptoCharge(@RequestBody request: CreateChargeRequest): ResponseEntity<*> {
        try {
            // Validate user and wallet address
            val user = userService.getAuthenticatedUser()
            if (!CryptoUtils.isValidEthereumAddress(request.walletAddress)) {
                return ResponseEntity.badRequest().body("Invalid wallet address")
            }
            
            // Create charge
            val charge = openRouterClient.crypto.createCharge(
                amount = request.amount,
                sender = request.walletAddress,
                chainId = request.chainId
            )
            
            // Store charge in database for tracking
            paymentService.createPaymentRecord(
                userId = user.id,
                chargeId = charge.data.id,
                amount = request.amount,
                walletAddress = request.walletAddress,
                chainId = request.chainId,
                status = PaymentStatus.PENDING
            )
            
            return ResponseEntity.ok(charge)
            
        } catch (e: Exception) {
            return ResponseEntity.status(500).body("Failed to create charge: ${e.message}")
        }
    }
    
    @GetMapping("/balance")
    fun getBalance(): ResponseEntity<*> {
        return try {
            val balance = openRouterClient.crypto.getCreditsBalance()
            ResponseEntity.ok(balance)
        } catch (e: Exception) {
            ResponseEntity.status(500).body("Failed to get balance: ${e.message}")
        }
    }
    
    @PostMapping("/webhook/transaction-confirmed")
    fun handleTransactionConfirmed(@RequestBody webhook: TransactionWebhook) {
        // Handle webhook from blockchain monitoring service
        paymentService.updatePaymentStatus(
            chargeId = webhook.chargeId,
            txHash = webhook.txHash,
            status = PaymentStatus.CONFIRMED
        )
        
        // Notify user
        notificationService.sendPaymentConfirmation(webhook.userId)
    }
}
```

## Web3 Library Integration

### Using Web3j (Java/Kotlin)

```kotlin
class Web3PaymentProcessor {
    private val web3j = Web3j.build(HttpService("https://base-mainnet.g.alchemy.com/v2/your-api-key"))
    
    fun executePayment(charge: CryptoChargeResponse, credentials: Credentials): String {
        val callData = charge.data.web3Data.transferIntent.callData
        val metadata = charge.data.web3Data.transferIntent.metadata
        
        // Prepare function call
        val function = Function(
            "swapAndTransferUniswapV3Native",
            listOf(
                // Transfer intent parameters
                createTransferIntentStruct(callData),
                Uint24(BigInteger.valueOf(PoolFeeTiers.LOWEST.toLong()))
            ),
            emptyList()
        )
        
        // Encode function call
        val encodedFunction = FunctionEncoder.encode(function)
        
        // Get current gas price
        val gasPrice = web3j.ethGasPrice().send().gasPrice
        
        // Create transaction
        val nonce = web3j.ethGetTransactionCount(
            credentials.address, DefaultBlockParameterName.LATEST
        ).send().transactionCount
        
        val transaction = RawTransaction.createTransaction(
            nonce,
            gasPrice,
            CryptoUtils.getRecommendedGasLimit(metadata.chainId),
            metadata.contractAddress,
            CryptoUtils.calculateRecommendedValue(callData.recipientAmount, callData.feeAmount),
            encodedFunction
        )
        
        // Sign and send transaction
        val signedTransaction = TransactionEncoder.signMessage(transaction, credentials)
        val response = web3j.ethSendRawTransaction(Numeric.toHexString(signedTransaction)).send()
        
        if (response.hasError()) {
            throw Exception("Transaction failed: ${response.error.message}")
        }
        
        return response.transactionHash
    }
    
    private fun createTransferIntentStruct(callData: TransferCallData): Struct {
        return Struct(listOf(
            Uint256(BigInteger(callData.recipientAmount)),
            Uint256(CryptoUtils.parseDeadlineToTimestamp(callData.deadline)),
            Address(callData.recipient),
            Address(callData.recipientCurrency),
            Address(callData.refundDestination),
            Uint256(BigInteger(callData.feeAmount)),
            Bytes16(callData.id.toByteArray()),
            Address(callData.operator),
            DynamicBytes(callData.signature.toByteArray()),
            DynamicBytes(callData.prefix.toByteArray())
        ))
    }
}
```

### Using Viem (JavaScript/TypeScript)

```typescript
import { createWalletClient, createPublicClient, http, parseEther } from 'viem'
import { base } from 'viem/chains'
import { privateKeyToAccount } from 'viem/accounts'

// Coinbase Commerce ABI (simplified)
const coinbaseCommerceAbi = [
  {
    name: 'swapAndTransferUniswapV3Native',
    type: 'function',
    stateMutability: 'payable',
    inputs: [
      {
        name: '_intent',
        type: 'tuple',
        components: [
          { name: 'recipientAmount', type: 'uint256' },
          { name: 'deadline', type: 'uint256' },
          { name: 'recipient', type: 'address' },
          { name: 'recipientCurrency', type: 'address' },
          { name: 'refundDestination', type: 'address' },
          { name: 'feeAmount', type: 'uint256' },
          { name: 'id', type: 'bytes16' },
          { name: 'operator', type: 'address' },
          { name: 'signature', type: 'bytes' },
          { name: 'prefix', type: 'bytes' }
        ]
      },
      { name: 'poolFeesTier', type: 'uint24' }
    ]
  }
]

async function executePayment(charge, privateKey) {
  const account = privateKeyToAccount(privateKey)
  const client = createWalletClient({
    chain: base,
    transport: http(),
    account
  })
  
  const callData = charge.data.web3_data.transfer_intent.call_data
  const metadata = charge.data.web3_data.transfer_intent.metadata
  
  const txHash = await client.writeContract({
    address: metadata.contract_address,
    abi: coinbaseCommerceAbi,
    functionName: 'swapAndTransferUniswapV3Native',
    args: [
      {
        recipientAmount: BigInt(callData.recipient_amount),
        deadline: BigInt(Math.floor(new Date(callData.deadline).getTime() / 1000)),
        recipient: callData.recipient,
        recipientCurrency: callData.recipient_currency,
        refundDestination: callData.refund_destination,
        feeAmount: BigInt(callData.fee_amount),
        id: callData.id,
        operator: callData.operator,
        signature: callData.signature,
        prefix: callData.prefix
      },
      500 // Pool fees tier (0.05%)
    ],
    value: parseEther('0.02') // Include buffer for gas and slippage
  })
  
  return txHash
}
```

## Error Handling

### Common Error Scenarios

```kotlin
class CryptoPaymentHandler {
    
    fun handlePaymentWithRetry(
        amount: Double,
        senderAddress: String,
        maxRetries: Int = 3
    ): CryptoChargeResponse? {
        var attempt = 0
        var lastException: Exception? = null
        
        while (attempt < maxRetries) {
            try {
                return client.crypto.createChargeOnBase(amount, senderAddress)
            } catch (e: OpenRouterException) {
                lastException = e
                when (e.errorCode) {
                    400 -> {
                        // Bad request - don't retry
                        throw e
                    }
                    429 -> {
                        // Rate limited - wait and retry
                        delay(calculateBackoffDelay(attempt))
                        attempt++
                    }
                    500, 502, 503, 504 -> {
                        // Server errors - retry
                        delay(calculateBackoffDelay(attempt))
                        attempt++
                    }
                    else -> throw e
                }
            } catch (e: Exception) {
                lastException = e
                delay(calculateBackoffDelay(attempt))
                attempt++
            }
        }
        
        throw lastException ?: Exception("Max retries exceeded")
    }
    
    private fun calculateBackoffDelay(attempt: Int): Long {
        return minOf(1000L * (1 shl attempt), 30000L) // Exponential backoff, max 30s
    }
}
```

### Validation and Error Messages

```kotlin
class PaymentValidator {
    
    fun validatePaymentRequest(amount: Double, address: String, chainId: Int): ValidationResult {
        val errors = mutableListOf<String>()
        
        // Amount validation
        when {
            amount <= 0 -> errors.add("Amount must be greater than 0")
            amount > 100000 -> errors.add("Amount cannot exceed $100,000")
            amount < 1 -> errors.add("Minimum amount is $1")
        }
        
        // Address validation
        if (!CryptoUtils.isValidEthereumAddress(address)) {
            errors.add("Invalid Ethereum address format")
        }
        
        // Chain validation
        if (!CryptoUtils.isSupportedChain(chainId)) {
            errors.add("Unsupported blockchain. Supported chains: ${SupportedChains.NAMES.values}")
        }
        
        return if (errors.isEmpty()) {
            ValidationResult.Success
        } else {
            ValidationResult.Error(errors)
        }
    }
}

sealed class ValidationResult {
    object Success : ValidationResult()
    data class Error(val messages: List<String>) : ValidationResult()
}
```

## Monitoring and Analytics

### Payment Tracking

```kotlin
@Service
class PaymentAnalyticsService {
    
    fun trackPaymentAttempt(userId: String, amount: Double, chainId: Int) {
        analytics.track("payment_attempted", mapOf(
            "user_id" to userId,
            "amount" to amount,
            "chain_id" to chainId,
            "chain_name" to CryptoUtils.getChainName(chainId)
        ))
    }
    
    fun trackPaymentSuccess(userId: String, amount: Double, txHash: String, chainId: Int) {
        analytics.track("payment_completed", mapOf(
            "user_id" to userId,
            "amount" to amount,
            "tx_hash" to txHash,
            "chain_id" to chainId
        ))
    }
    
    fun trackPaymentFailure(userId: String, amount: Double, error: String, chainId: Int) {
        analytics.track("payment_failed", mapOf(
            "user_id" to userId,
            "amount" to amount,
            "error" to error,
            "chain_id" to chainId
        ))
    }
}
```

### Balance Monitoring Service

```kotlin
@Component
class BalanceMonitoringService {
    
    @Scheduled(fixedRate = 300000) // Check every 5 minutes
    fun monitorBalance() {
        try {
            val balance = openRouterClient.crypto.getCreditsBalance()
            
            if (balance.data.currentBalance < LOW_BALANCE_THRESHOLD) {
                notificationService.sendLowBalanceAlert(balance.data.currentBalance)
                
                // Auto-suggest top-up amount
                val suggested = openRouterClient.crypto.getRecommendedTopUpAmount()
                
                // Send to admin dashboard or user notification
                dashboardService.updateBalanceWarning(balance.data.currentBalance, suggested)
            }
            
            // Update metrics
            metricsService.recordBalance(balance.data.currentBalance)
            
        } catch (e: Exception) {
            logger.error("Failed to monitor balance", e)
        }
    }
    
    companion object {
        private const val LOW_BALANCE_THRESHOLD = 10.0 // $10
    }
}
```

## Best Practices

### 1. Security Considerations

- **Never store private keys** in your application
- **Validate all inputs** before creating charges
- **Use HTTPS** for all API communications
- **Implement rate limiting** to prevent abuse
- **Monitor for suspicious activity**

### 2. User Experience

- **Show clear pricing** in both USD and crypto
- **Display transaction fees** upfront
- **Provide transaction status updates**
- **Handle wallet connection gracefully**
- **Offer multiple payment chains**

### 3. Error Recovery

- **Implement retry logic** for network failures
- **Handle charge expiration** gracefully
- **Provide clear error messages**
- **Offer alternative payment methods**

### 4. Testing

```kotlin
// Test with small amounts on testnets first
@Test
fun testPaymentFlow() {
    val testClient = OpenRouterClient.create(
        apiKey = "test-api-key",
        enableLogging = true
    )
    
    // Use testnet addresses and small amounts
    val charge = testClient.crypto.createChargeOnBase(
        amount = 1.0, // $1 for testing
        sender = "0x..." // Test wallet address
    )
    
    assertNotNull(charge.data.id)
    assertFalse(CryptoUtils.isChargeExpired(charge.data.expiresAt))
}
```

## Troubleshooting

### Common Issues

1. **"Invalid address format"**
   - Ensure address starts with "0x" and is 42 characters long
   - Use `CryptoUtils.isValidEthereumAddress()` to validate

2. **"Charge expired"**
   - Charges expire after 1 hour
   - Create a new charge if expired
   - Check `CryptoUtils.isChargeExpired()`

3. **"Insufficient balance for transaction"**
   - User needs more ETH/MATIC for gas fees
   - Recommend higher buffer amount

4. **"Transaction failed"**
   - Check network congestion
   - Verify contract address
   - Ensure sufficient slippage tolerance

### Debug Mode

```kotlin
val client = OpenRouterClient.create(
    apiKey = "your-api-key",
    enableLogging = true, // Enable request/response logging
    enableRetries = true
)

// Additional debugging
CryptoUtils.validatePaymentParameters(amount, address, chainId)
```

## Support

For additional support:

1. Check the [OpenRouter Documentation](https://openrouter.ai/docs)
2. Review [Coinbase Commerce Protocol](https://github.com/coinbase/commerce-onchain-payment-protocol)
3. Join the OpenRouter Discord community
4. Submit issues on the GitHub repository

## Migration Guide

If upgrading from a previous version without crypto support:

```kotlin
// Before (v1.0.x)
val client = OpenRouterClient.create(apiKey)

// After (v1.1.x)
val client = OpenRouterClient.create(apiKey)
val cryptoApi = client.crypto // New crypto functionality available
```