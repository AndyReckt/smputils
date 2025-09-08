# 🚀 Crypto Top-up System for Discord Bot

This guide explains the new crypto payment system that allows users to contribute to the bot's OpenRouter credits using cryptocurrency.

## 🎯 Overview

The bot now supports community-funded AI features through cryptocurrency payments. Users can contribute funds to keep the AI services running using ETH, MATIC, or ETH on Base.

## 💰 Available Commands

### For All Users

#### `/topup` - Create a crypto payment
```
/topup amount:25 wallet:0x... chain:base
```
- **amount**: $1-$100 USD worth of credits
- **wallet**: Your wallet address (must start with 0x)
- **chain**: `base` (recommended), `ethereum`, or `polygon`

#### `/balance` - Check bot's credit balance
```
/balance
```
Shows current balance, usage, and whether more credits are needed.

#### `/crypto-help` - Get help with payments
```
/crypto-help
```
Detailed instructions on how to complete crypto payments.

### For Administrators

#### `/crypto-status` - Detailed account status
```
/crypto-status
```
Shows comprehensive account information including rate limits, monitoring status, etc.

#### `/force-balance-check` - Manual balance check
```
/force-balance-check
```
Immediately checks and updates the balance cache.

## 🔧 How It Works

### 1. Payment Creation
- User runs `/topup` command with amount and wallet address
- Bot creates a charge through OpenRouter's crypto API
- Payment expires in 1 hour

### 2. Transaction Execution
- User receives contract address and payment details
- User sends transaction through their Web3 wallet
- Credits are automatically added once transaction confirms

### 3. Monitoring
- Bot monitors balance every 10 minutes
- Sends notifications when balance is low
- Prevents spam notifications with 2-hour cooldown

## 💡 Supported Blockchains

| Chain | Chain ID | Benefits | Gas Costs |
|-------|----------|----------|-----------|
| **Base** | 8453 | ✅ Recommended, lowest fees | ~$0.01-0.05 |
| **Ethereum** | 1 | Highest liquidity | ~$5-20 |
| **Polygon** | 137 | Lower fees than Ethereum | ~$0.01-0.10 |

## 🛠️ Technical Implementation

### Key Components

1. **CryptoCommands.kt** - User-facing commands for payments and balance checks
2. **AdminCryptoCommands.kt** - Admin commands for detailed monitoring
3. **BalanceMonitorService.kt** - Background service for automatic monitoring
4. **OpenRouter Crypto SDK** - Integration with OpenRouter's payment system

### Security Features

- Input validation for amounts ($1-$100 range)
- Ethereum address format validation
- Automatic charge expiration (1 hour)
- Rate limiting through OpenRouter
- Admin-only access to sensitive commands

### Error Handling

- Comprehensive error messages for users
- Automatic retry logic for network issues
- Graceful degradation when payments fail
- Detailed logging for debugging

## 🚀 Usage Examples

### User Contributing $25
```
User: /topup amount:25 wallet:0x742d35Cc6634C0532925a3b8D4C15634776f88BB chain:base

Bot: 🚀 Bot Top-up Payment Created
     Amount: $25 USD
     Chain: Base
     From: @username
     Expires: in 59 minutes
     
     Contract: 0x03059433bcdb6144624cc2443159d9445c32b7a8
     Recipient: 0x...
     
     Thank you for contributing to keep the bot running! 🤖✨
```

### Low Balance Notification
```
Bot: ⚠️ Low OpenRouter Balance
     Current Balance: $3.47
     Recommended Top-up: $15.00
     
     Users can help by using /topup or ~topup commands!
     
     Example: /topup amount:25 wallet:0x... chain:base
```

### Balance Check
```
User: /balance

Bot: 🤖 Bot Credit Balance
     Current Balance: $23.45
     Total Credits: $50.00
     Total Usage: $26.55
     
     Account Label: API User
     Free Tier: No
     
     ✅ Balance looks good
```

## 🔒 Security Considerations

### For Users
- **Never share private keys** - Only provide wallet addresses
- **Verify contract addresses** - Always double-check before sending
- **Use small amounts first** - Test with minimal amounts
- **Check transaction fees** - Ensure you have enough for gas

### For Administrators
- **Monitor regularly** - Check balance status frequently
- **Set up alerts** - Use the automatic monitoring system
- **Keep API keys secure** - Protect OpenRouter credentials
- **Review transactions** - Check payment logs regularly

## 📊 Monitoring and Analytics

### Automatic Monitoring
- Balance checked every 10 minutes
- Low balance alerts when < $5
- Critical alerts when < $1
- Error notifications for API issues

### Manual Monitoring
- Admin commands for detailed status
- Force balance checks when needed
- Comprehensive logging in designated channel

## 🛟 Troubleshooting

### Common Issues

**"Invalid address format"**
- Ensure address starts with `0x`
- Address must be exactly 42 characters
- Use checksummed addresses when possible

**"Payment failed to create"**
- Check OpenRouter API key is valid
- Verify network connectivity
- Ensure amount is within $1-$100 range

**"Transaction not confirming"**
- Check gas fees are sufficient
- Verify correct contract address
- Wait for network congestion to clear

**"Credits not added"**
- Small amounts (<$500) are instant once confirmed
- Large amounts (>$500) have 15-minute delay
- Check transaction on blockchain explorer

### Getting Help

1. Use `/crypto-help` for payment instructions
2. Contact server administrators for technical issues
3. Check OpenRouter status page for service updates
4. Review transaction on blockchain explorer

## 🔄 Maintenance

### Regular Tasks
- Monitor balance daily
- Review payment logs weekly  
- Update documentation as needed
- Test payment flow monthly

### Emergency Procedures
- Manual top-up through OpenRouter dashboard
- Disable AI features if balance critical
- Contact OpenRouter support for urgent issues
- Use backup API keys if available

## 📈 Future Enhancements

### Planned Features
- Payment history tracking
- Contributor leaderboards
- Automated thank-you messages
- Integration with more wallets
- Multi-signature support for large payments

### Potential Improvements
- Support for more cryptocurrencies
- Subscription-based payments
- Smart contract integration
- Mobile wallet deep linking
- Payment scheduling

---

**Note**: This system is designed to be community-driven. Users contribute voluntarily to keep AI features running. All payments are processed through OpenRouter's secure infrastructure using Coinbase's on-chain payment protocol.