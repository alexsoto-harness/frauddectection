# 💻 Code Examples

This page provides practical examples for integrating the **Fraud Detection Service** into your project, along with a summary of its core features.

---

## ✨ Key Features

- **Real-Time Evaluation:** Synchronous risk scoring with sub-100ms p99 latency.
- **Batch Evaluation:** Score large sets of historical transactions offline.
- **Configurable Thresholds:** Override global risk bands per use case or merchant category.
- **Audit Logging:** Every evaluation is logged with inputs, scores, and the decision taken.
- **Webhook Callbacks:** Receive async notifications when a decision changes (e.g. chargeback signals).

---

## 🚀 Basic Evaluation Example

```typescript
import { FraudDetectionClient } from '@harness/fraud-detection-client';

const client = new FraudDetectionClient();

const result = await client.evaluate({
  transactionId: 'txn_abc123',
  amount: 4500,
  currency: 'USD',
  userId: 'user_9821',
  merchantId: 'merch_441',
});

console.log('Risk score:', result.score);     // 0–100
console.log('Decision:', result.decision);    // APPROVE | CHALLENGE | BLOCK
console.log('Signals:', result.signals);      // e.g. ['VELOCITY_HIGH', 'NEW_DEVICE']
```

---

## 🔢 Custom Risk Threshold Example

```typescript
const result = await client.evaluate({
  transactionId: 'txn_xyz789',
  amount: 250,
  currency: 'CAD',
  userId: 'user_1102',
  merchantId: 'merch_09',
  thresholds: {
    challengeAbove: 40,   // default: 30
    blockAbove: 80,       // default: 70
  },
});
```

---

## 📦 Batch Evaluation Example

```typescript
const results = await client.evaluateBatch([
  { transactionId: 'txn_001', amount: 100, currency: 'USD', userId: 'u1', merchantId: 'm1' },
  { transactionId: 'txn_002', amount: 9999, currency: 'USD', userId: 'u2', merchantId: 'm2' },
]);

results.forEach(({ transactionId, score, decision }) => {
  console.log(`${transactionId}: score=${score}, decision=${decision}`);
});
```

---

## 🪝 Webhook Callback Registration

```typescript
client.registerWebhook({
  url: 'https://your-service.internal/fraud-callback',
  events: ['DECISION_CHANGED', 'CHARGEBACK_SIGNAL'],
  secret: process.env.FRAUD_WEBHOOK_SECRET,
});
```

---

## 📝 Error Handling Example

```typescript
try {
  const result = await client.evaluate({
    transactionId: 'txn_err01',
    amount: 750,
    currency: 'USD',
    userId: 'user_555',
    merchantId: 'merch_12',
  });
} catch (error) {
  if (error.code === 'EVALUATION_TIMEOUT') {
    // Fall back to a conservative default decision
    return { decision: 'CHALLENGE' };
  }
  reportError(error);
}
```

---

For more advanced scenarios, see the [Debugging & Runbooks](sub-page.md) and [Rules & Extension Points](extensions.md).
