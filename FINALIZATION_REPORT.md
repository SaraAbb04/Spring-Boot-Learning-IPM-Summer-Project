# Finalization review report

## Applied in this pass

1. **Password security**
   - Registration now stores BCrypt hashes.
   - Existing plaintext-password accounts are upgraded to BCrypt after their next successful login or password change.
   - Password is JSON write-only, so it is not serialized in user responses.
   - Registration forces `USER` role and checks both unique email and username.
   - Login returns the same generic authentication message for unknown email and incorrect password.

2. **Authorization / ownership**
   - Security filter chain now returns JSON `401` and `403` responses.
   - Fixed a confirmed IDOR issue: ticket deletion now verifies that the authenticated user owns the ticket before deleting it.
   - Ticket read, update and history endpoints already checked ownership in the supplied source and remain in place.

3. **Pagination, search, filter and sort**
   - Added a shared pagination validator for user-facing AI usage, conversation and message list endpoints (page >= 0, size 1..100).
   - Ticket search validates page and size, restricts sort fields to `createdAt`, `title`, `status`, `priority`, validates direction and treats search input literally rather than as a raw regex.
   - Existing Admin Usage and Admin Payment filtering/pagination were retained.

4. **Validation and error responses**
   - Added request validation for login, profile updates, password changes and message content.
   - Added consistent JSON error responses for validation failures, malformed request bodies, invalid query parameter types, unsupported HTTP methods, missing endpoints, duplicate keys and unexpected server errors.
   - AI provider failures are returned as `502 Bad Gateway` without exposing internal exception details.

5. **AI usage accuracy**
   - AI provider failures now create an `AiUsage` record with `FAILED` status and zero tokens.
   - Successful AI calls create an `AiUsage` record even if the upstream response omitted token usage (zero token values in that case).
   - Historical failed requests that were never recorded cannot be reconstructed.

6. **Payment hardening**
   - If payment initiation fails at the gateway, the already-created payment is marked `FAILED` instead of remaining indefinitely `PENDING`.
   - Mock gateway HTML output escapes dynamic values to prevent reflected HTML/script injection.
   - Mock gateway remains limited to the `dev` profile.
   - Added a fail-closed non-dev adapter: until a real gateway is integrated, payment initiation outside `dev` is rejected rather than accidentally using the mock gateway.
   - Removed the test-only `/admin/model-pricing/test-conflict` endpoint.

7. **Configuration and documentation**
   - MongoDB URI, AI base URL, JWT secret and payment callback URL are environment-configurable.
   - Added a development-profile JWT fallback and isolated test properties.
   - Added `README.md` and a Postman smoke-test collection under `postman/`.

## Important remaining work / limitations

- **Build verification:** Maven is not installed in the execution environment used for this review, so `mvn clean test` could not be executed here. Run it locally in IntelliJ/terminal and resolve any dependency or compile issues before considering the artifact verified.
- **Real payments:** `UnavailablePaymentGateway` is deliberately a placeholder. Integrate the actual payment provider and verify signatures, amount/currency, transaction identity, idempotency and callback replay protection before production.
- **Payment concurrency/recovery:** concurrent callbacks and stuck `PROCESSING` records still need provider-specific reconciliation/locking or an idempotent recovery job.
- **Unpaged legacy lists:** some existing endpoints still return complete lists (admin users, plans, subscriptions, user payment history and ticket history). They were not changed in order to avoid breaking existing response contracts. Add pagination before these collections grow.
- **KPI scale:** current KPI implementation reads usage and payment documents into application memory. Use MongoDB aggregation for large datasets.
- **User KPI fields:** User currently has no `createdAt` or `active` field; therefore new-user and active-user counts are not available.
- **Credit model:** there is no independent Credit collection; limits are derived from subscription plan limits and usage records.
- **Operational security:** configure HTTPS, CORS allow-list, rate limiting (especially login and AI endpoints), monitoring, backups, database access control and secret rotation before deployment.

## Required local verification

1. Set `AI_API_KEY`; set a unique `JWT_SECRET` (at least 32 bytes).
2. Start MongoDB and run with `--spring.profiles.active=dev` for mock payment tests.
3. Run `mvn clean test`.
4. Import `postman/Finalization-Smoke-Tests.postman_collection.json` into Postman.
5. Run the authorization, ownership, validation, KPI, usage, ticket and mock payment scenarios described in `README.md`.
6. Specifically test that USER B cannot delete a ticket created by USER A.
7. Specifically test a failed AI provider call and confirm a `FAILED` record appears in `ai_usage`.
