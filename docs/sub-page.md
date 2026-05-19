# 🔍 Debugging & Runbooks

Welcome to the in-depth guide for debugging and operating the **Fraud Detection Service**. This page covers common issues, troubleshooting steps, available GitHub Actions workflows, and runbooks for rapid resolution.

---

## 🛠️ Common Debugging Scenarios

<!-- prettier-ignore -->
???+ warn "Warning: Sensitive Data"
    Always ensure that logs and debugging output do **not** contain sensitive customer, transaction, or PII data. Follow Harness compliance guidelines when sharing logs.

### 1. False Positive Rate Spike

<!-- prettier-ignore -->
??? note "Symptoms & Steps"
    **Symptom:** Legitimate transactions being blocked or challenged at an unusually high rate.

    **Possible Causes:** Recent rule change, model drift, upstream data quality degradation.

    **Resolution Steps:**
    1. Check the Fraud Ops dashboard for score distribution shifts.
    2. Review recent rule deployments in the audit log.
    3. Compare signal frequencies before and after the spike.
    4. If a new rule is suspected, disable it and monitor for recovery.
    5. [See Rules & Extension Points](extensions.md).

---

### 2. Evaluation Latency Exceeds SLA

<!-- prettier-ignore -->
??? note "Symptoms & Steps"
    **Symptom:** `evaluate()` calls returning after 100ms p99 threshold, causing downstream timeouts.

    **Possible Causes:** ML model endpoint degraded, rules engine overloaded, cold-start after deploy.

    **Resolution Steps:**
    1. Check ML model endpoint health at `/health` on the model registry.
    2. Review rules engine metrics — look for rules with high fanout or expensive lookups.
    3. Verify the `timeout` setting on any registered custom models.
    4. Scale the fraud detection service pods if CPU utilization is above 80%.

---

### 3. Missing Audit Log Entries

<!-- prettier-ignore -->
??? note "Symptoms & Steps"
    **Symptom:** Evaluation completed successfully, but no entry in the audit log.

    **Possible Causes:** Audit service unavailable, misconfigured `AUDIT_SERVICE_URL`, `afterEvaluate` hook throwing silently.

    **Resolution Steps:**
    1. Verify the `AUDIT_SERVICE_URL` environment variable is set correctly.
    2. Check network connectivity from the fraud detection pods to the audit service.
    3. Review any custom `afterEvaluate` hooks for uncaught exceptions.

---

### 4. Score Returning `null` or `undefined`

<!-- prettier-ignore -->
??? note "Symptoms & Steps"
    **Symptom:** Evaluation returns without a score; decision defaults to `CHALLENGE`.

    **Possible Causes:** ML model registry unreachable, invalid transaction payload, missing required fields.

    **Resolution Steps:**
    1. Validate that all required fields (`transactionId`, `amount`, `userId`, `merchantId`) are present.
    2. Check ML model registry connectivity.
    3. Review the service logs: `/var/log/fraud-detection/*.log`.
    4. Ensure `currency` is a valid ISO 4217 code.

---

## ⚙️ Available GitHub Actions Workflows

| Workflow Name | Description | Link |
|---|---|---|
| CI Pipeline | Lints, builds, and tests the code | [View Workflow](.github/workflows/ci.yml) |
| Security Scan | Runs SAST and dependency checks | [View Workflow](.github/workflows/security.yml) |
| Model Shadow Deploy | Deploys a new model in shadow mode | [View Workflow](.github/workflows/shadow-deploy.yml) |
| Rule Validation | Validates rule JSON schemas and logic | [View Workflow](.github/workflows/rule-validation.yml) |

---

## 📖 Runbooks

<!-- prettier-ignore -->
??? info "What is a runbook?"
    Runbooks are step-by-step guides for resolving common issues or performing operational tasks.

| Runbook Name | Description | Link |
|---|---|---|
| False Positive Investigation | Diagnosing and rolling back bad rules or model regressions | [Open Runbook](runbooks/false-positives.md) |
| Latency Troubleshooting | Resolving evaluation SLA breaches | [Open Runbook](runbooks/latency.md) |
| Audit Trail Troubleshooting | Debugging missing audit log entries | [Open Runbook](runbooks/audit-trail.md) |
| Model Rollback Guide | Safely rolling back to a previous ML model version | [Open Runbook](runbooks/model-rollback.md) |

---

## 🧑‍💻 Need More Help?

<!-- prettier-ignore -->
???+ note "How do I get support?"
    You can get support by contacting the Harness Platform Engineering team or by visiting the [Debugging & Runbooks](sub-page.md) section.

- [Rules & Extension Points](extensions.md)
- [Code Examples](code-sample.md)

---
