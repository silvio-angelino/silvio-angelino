# Il Cronista - Operazione Ombra

Gioco di ruolo investigativo ambientato nel 1935.
Sei un agente segreto sotto copertura che deve
smascherare una rete di spie straniere prima che
il tempo scada.

**Package:** `it.unicam.cs.mpgc.rpg130929`
**Matricola:** 130929

---

## Come eseguire il progetto

### Prerequisiti
- Java 25 (LTS)
- Gradle

### Istruzioni

```bash
git clone https://github.com/silvio-angelino/silvio-angelino.git il-cronista
cd il-cronista
```

### Build del progetto

```bash
./gradlew build
```

### Esecuzione

```bash
./gradlew run
```

---

## Come si gioca

→ **WASD / Frecce** Muovi il personaggio sulla mappa
→ **OTTIENI PROVE** Appare automaticamente quando
sei in un luogo con prove disponibili
→ **Parla con i CONTATTI** Clicca sui personaggi
per ottenere informazioni. Alcuni dialoghi rivelano
indizi esclusivi sulla trama, non ottenibili in
nessun altro modo
→ **SCRIVI RAPPORTO** Quando hai abbastanza prove
scrivi il rapporto finale
→ **Obiettivo** Raggiungi 100 di credibilità
entro 10 giorni senza essere scoperto
→ **Attenzione** Evita che il sospetto raggiunga
100 o la missione fallirà!
→ **Continua partita** Se esiste un salvataggio,
all'avvio puoi scegliere se riprendere o iniziare
una nuova partita

---

## Funzionalità presenti

- Mappa esplorabile con movimento in tempo reale
- Sistema di dialoghi con NPC, con scelte vincolate
  al carisma del personaggio
- Indizi esclusivi ottenibili solo tramite dialogo,
  distinti da quelli raccoglibili esplorando
- Sistema di missioni tracciate automaticamente
- Persistenza su file JSON (salvataggio e caricamento
  partita funzionanti)
- Sistema di progressione (livello, esperienza,
  statistiche)
- Interfaccia grafica JavaFX interamente
  programmatica (nessun FXML)

## Idee per sviluppi futuri

- Missioni scelte dal giocatore, non solo tracciate
  automaticamente
- Specializzazione delle statistiche (invece di
  salire tutte insieme ad ogni livello)
- Più varietà negli eventi durante l'esplorazione

---

## Note tecniche

Il progetto segue il pattern architetturale MVC.
La persistenza usa Gson su file JSON, con repository
intercambiabile tramite l'interfaccia `GameRepository`
(principio DIP). Per i dettagli su architettura,
classi, interfacce e principi SOLID applicati,
vedi la Wiki del repository.

---

## Tecnologie utilizzate

- Java 25
- JavaFX 24
- Gson 2.13.2
- Gradle
- Font "Press Start 2P" (Google Fonts, licenza SIL
  Open Font License)

---

## Uso di strumenti di AI

Utilizzato **Claude (Anthropic)** come assistente IA per:

- Assimilare concetti teorici
- Automatizzazione codice ripetitivo
- Suggerimenti sulla struttura del codice