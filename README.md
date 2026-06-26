# AI Reliability Assessment

## Prompt Strategy

Applikationen använder OpenAI:s Chat Completions API för att analysera sentiment i en text.

För att få ett konsekvent svar används en **System Prompt** som instruerar modellen att endast returnera ett JSON-objekt med följande struktur:

```json
{
  "sentiment": "positive",
  "score": 95
}
```

Prompten innehåller även regler som säger att:

- endast JSON får returneras
- inga förklaringar eller markdown får skickas tillbaka
- `sentiment` ska vara `positive`, `negative` eller `neutral`
- `score` ska vara ett tal mellan 0 och 100

Dessutom används `temperature = 0.1`, vilket gör att modellen ger mer konsekventa och förutsägbara svar.

---

## Error Mitigation

För att göra applikationen mer robust har flera skydd implementerats.

### API Key & Fail Fast

API-nyckeln lagras som en miljövariabel istället för direkt i koden.

Vid uppstart kontrolleras att nyckeln finns med hjälp av en `@PostConstruct`-metod. Om nyckeln saknas kastas ett `IllegalStateException`, vilket gör att applikationen avslutas direkt istället för att starta med en felaktig konfiguration.

### Timeouts

För att undvika att applikationen väntar för länge på svar från OpenAI används en `SimpleClientHttpRequestFactory`.

Nuvarande timeout-inställningar är:

- Connect Timeout: **2000 ms**
- Read Timeout: **8000 ms**

Om OpenAI inte svarar inom dessa tidsgränser avbryts anropet automatiskt.

### Rate Limits (Exponential Backoff)

Om OpenAI returnerar **HTTP 429 (Too Many Requests)** försöker applikationen automatiskt igen.

Maximalt tre försök görs.

Väntetiden ökar mellan varje försök:

- Försök 1 → 1 sekund
- Försök 2 → 2 sekunder
- Försök 3 → 4 sekunder

Detta minskar belastningen på API:t och ökar chansen att anropet lyckas.

### Validation

När OpenAI svarar parsas JSON-svaret med Jackson (`ObjectMapper`) till ett `AiResponseDto`.

Objektet valideras sedan med Bean Validation.

Följande regler kontrolleras:

- `sentiment` måste vara `positive`, `negative`, `neutral` eller `unknown`
- `score` måste ligga mellan **0** och **100**

Om valideringen misslyckas returneras ett fallback-svar.

### Fallback

Om något går fel, till exempel:

- timeout
- felaktigt JSON
- valideringsfel
- annat oväntat fel

returneras istället:

```json
{
  "sentiment": "unknown",
  "score": 0
}
```

Detta gör att applikationen inte kraschar även om AI-tjänsten returnerar ett oväntat svar.

---

## Reliability Assessment

LLM:er är kraftfulla men inte helt tillförlitliga. De kan ibland returnera felaktig information eller svar som inte följer instruktionerna.

För att minska dessa risker har följande lösningar implementerats:

- System Prompt för ett bestämt JSON-format
- Låg temperature (`0.1`) för mer konsekventa svar
- Fail Fast om API-nyckeln saknas
- Timeouts för att undvika långa väntetider
- Exponential Backoff vid HTTP 429
- Parsing av JSON med Jackson
- Bean Validation av AI-svaret
- Fallback-svar om något går fel

Dessa åtgärder gör applikationen mer robust och säkerställer att vanliga fel kan hanteras utan att applikationen kraschar.