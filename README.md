# AI Reliability Lab – Säkerhetsrapport

## Översikt

Detta projekt bygger vidare på Labb 1 där en Spring Boot-applikation utvecklades för att analysera sentiment med hjälp av OpenAI:s API. I denna labb har fokus legat på att göra applikationen säkrare och mer robust samt att containerisera den med Docker och automatisera byggprocessen med GitHub Actions.

---

## Identifierade säkerhetsproblem

### OWASP A01 – Broken Access Control

**Problem**

API-endpointen kunde från början anropas av vem som helst. Eftersom applikationen använder OpenAI:s API hade detta kunnat leda till obehörig användning och onödiga kostnader.

**Åtgärd**

Jag implementerade en egen API-nyckel som måste skickas i headern `X-API-KEY`. Om nyckeln saknas eller är fel returnerar applikationen HTTP 401 Unauthorized.

---

### OWASP A05 – Security Misconfiguration

**Problem**

OpenAI API-nyckeln får inte ligga hårdkodad i projektet eftersom den då riskerar att hamna på GitHub eller delas med andra.

**Åtgärd**

API-nyckeln lagras som en miljövariabel (`OPENAI_API_KEY`) och läses in av Spring Boot. Jag implementerade även Fail Fast med `@PostConstruct`, vilket gör att applikationen avslutas direkt om nyckeln saknas.

---

### Robust felhantering

**Problem**

Applikationen är beroende av en extern AI-tjänst. Om tjänsten svarar långsamt, returnerar HTTP 429 eller skickar tillbaka ett oväntat svar finns risk att applikationen inte fungerar som den ska.

**Åtgärd**

För att göra applikationen mer robust implementerade jag:

- Connect Timeout (2 sekunder)
- Read Timeout (8 sekunder)
- Exponential Backoff vid HTTP 429
- Bean Validation av AI-svaret
- Fallback-svar (`unknown`, `0`) om något går fel

---

## Docker och CI/CD

Applikationen containeriserades med Docker genom en multi-stage Dockerfile.

Jag skapade även en GitHub Actions-pipeline som automatiskt:

- bygger projektet
- kör tester
- bygger Docker-imagen
- publicerar imagen till Docker Hub

GitHub Secrets används för att lagra känslig information, exempelvis Docker Hub-token.

---

## Reflektion

Den här labben har visat hur viktigt det är att tänka på säkerhet redan under utvecklingen. Genom att skydda endpointen, använda miljövariabler och implementera felhantering blir applikationen både säkrare och mer tillförlitlig.

Docker och GitHub Actions gör dessutom att projektet blir enklare att bygga, testa och distribuera på ett automatiserat sätt.