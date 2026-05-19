# 🧩 Rules & Extension Points

The **Fraud Detection Service** is designed to be extensible, allowing teams to define custom rules, adjust scoring weights, and hook into the evaluation pipeline. This page explains how to leverage those extension points.

---

## 🔌 Custom Rule Definitions

You can register custom rules that feed additional signals into the risk score. Rules are declarative JSON objects evaluated by the rules engine before the ML model scores.

<!-- prettier-ignore -->
!!! info
    Custom rules let you encode business-specific knowledge (e.g. "flag all crypto purchases over $1,000 for users with accounts less than 30 days old") without retraining the ML model.

**Example: High-Value New-Account Rule**

```json
{
  "ruleId": "HIGH_VALUE_NEW_ACCOUNT",
  "description": "Flag high-value transactions from recently created accounts",
  "conditions": [
    { "field": "amount", "operator": "gt", "value": 1000 },
    { "field": "accountAgeDays", "operator": "lt", "value": 30 }
  ],
  "action": {
    "addSignal": "NEW_ACCOUNT_HIGH_VALUE",
    "scoreAdjustment": 20
  }
}
```

**Available Rule Fields:**

| Field | Type | Description |
|---|---|---|
| `ruleId` | string | Unique identifier for the rule |
| `conditions` | array | List of field/operator/value checks |
| `action.addSignal` | string | Signal tag added to the evaluation result |
| `action.scoreAdjustment` | number | Points added (positive) or subtracted (negative) from the raw score |

---

## 🛠️ Evaluation Pipeline Hooks

You can inject custom logic at key stages of the evaluation pipeline.

```typescript
const client = new FraudDetectionClient({
  hooks: {
    beforeEvaluate: (txn) => {
      // Enrich transaction with additional context
      txn.metadata.ipReputation = lookupIPReputation(txn.ipAddress);
    },
    afterEvaluate: (result) => {
      // Emit custom metrics or notifications
      emitRiskScoreMetric(result.score);
    },
    onBlock: (result) => {
      // Trigger downstream notification for blocked transactions
      notifyRiskOpsTeam(result);
    },
  },
});
```

**Available Hooks:**

| Hook Name | When It Runs | Typical Use Cases |
|---|---|---|
| `beforeEvaluate` | Before scoring begins | Data enrichment, additional context injection |
| `afterEvaluate` | After score is computed | Metrics, audit notifications |
| `onBlock` | When decision is BLOCK | Ops alerts, customer notification |
| `onChallenge` | When decision is CHALLENGE | Step-up auth triggers |

---

## 🏗️ Registering a Custom ML Model

For teams with proprietary models, you can register an external model endpoint that the service will call as an additional scorer.

```typescript
client.registerModel({
  modelId: 'custom-chargeback-predictor-v2',
  endpoint: 'https://ml-platform.internal/models/cbp-v2/score',
  weight: 0.3,  // Blended 30% into the final score
  timeout: 50,  // ms — falls back to base score on timeout
});
```

<!-- prettier-ignore -->
!!! tip
    Keep custom model weights below 0.5 until the model has been validated in shadow mode for at least 30 days.

---

## 📊 Evaluation Pipeline Flow

Below is a UML sequence diagram showing a full evaluation with extension hooks and a custom rule:

```plantuml format="svg" classes="uml myDiagram" alt="Fraud Detection Evaluation UML" title="Fraud Detection Evaluation UML" width="600px" height="300px"
actor "Client Service" as Client
participant "FraudDetectionClient" as SDK
participant "Rules Engine" as Rules
participant "ML Model Registry" as ML
participant "Audit Service" as Audit
participant "Risk Decision Engine" as Decision

Client -> SDK: evaluate(transaction)
SDK -> SDK: beforeEvaluate Hook
SDK -> Rules: applyRules(transaction)
Rules --> SDK: signals[], scoreAdjustment
SDK -> ML: score(transaction + signals)
ML --> SDK: mlScore
SDK -> Decision: computeFinalScore(mlScore, adjustment)
Decision --> SDK: finalScore, decision
SDK -> SDK: afterEvaluate Hook
SDK -> Audit: logEvaluation(inputs, score, decision)
SDK --> Client: { score, decision, signals }
```

---

## 🧪 Testing Custom Rules and Hooks

Before deploying to production, validate your rules and hooks in shadow mode.

<!-- prettier-ignore -->
!!! warn
    Always run custom rules in **shadow mode** (observe-only, no score effect) for at least one week before enabling score adjustments in production.

See [Code Examples](code-sample.md) for integration patterns.

---

## 📚 Related Resources

- [Debugging & Runbooks](sub-page.md)
- [Code Examples](code-sample.md)
- [Official Backstage TechDocs Guide](https://backstage.io/docs/features/techdocs/)

---

## 📝 Best Practices

<!-- prettier-ignore -->
!!! tip
    - Keep rule conditions simple and composable — complex nested logic belongs in a custom hook.
    - Use `scoreAdjustment` conservatively; large adjustments can overwhelm the ML model's signal.
    - Document every custom rule and hook for future maintainers.

---

<!-- prettier-ignore -->
???+ note "How do I get support?"
    You can get support by contacting the Harness Platform Engineering team or by visiting the [Runbooks](sub-page.md) for troubleshooting tips.
