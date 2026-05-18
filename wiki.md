# Il Cronista - Operazione Ombra
## Wiki del Progetto

---

## 📋 Indice

1. [Descrizione del Progetto](#descrizione)
2. [Funzionalità Implementate](#funzionalità)
3. [Architettura MVC](#architettura)
4. [Classi e Responsabilità](#classi)
5. [Interfacce](#interfacce)
6. [Persistenza dei Dati](#persistenza)
7. [Estensibilità](#estensibilità)
8. [Principi SOLID](#solid)
9. [Dichiarazione Uso AI](#ai)

---

## 1. Descrizione del Progetto

"Il Cronista - Operazione Ombra" è un gioco di ruolo
investigativo ambientato nel 1935.
Il giocatore interpreta un giornalista infiltrato che
deve smascherare una rete di spie straniere operante
in città.

Il progetto è stato sviluppato come applicativo Java
con interfaccia grafica JavaFX, applicando i principi
e gli strumenti visti durante il corso di Metodologie
di Programmazione.

---

## 2. Funzionalità Implementate

- **Mappa interattiva** con visuale dall'alto e
  movimento del personaggio tramite tasti WASD
- **Sistema di esplorazione** — il giocatore visita
  luoghi della città e raccoglie prove
- **Sistema di dialogo** — interazione con personaggi
  non giocanti (NPC) che forniscono informazioni
- **Taccuino/Dossier** — raccolta e visualizzazione
  delle prove trovate
- **Sistema di articoli** — scrittura e pubblicazione
  di rapporti investigativi
- **Sistema di reputazione** — la credibilità aumenta
  pubblicando articoli con più prove
- **Persistenza** — lo stato di gioco viene salvato
  automaticamente in formato JSON
- **Schermata introduttiva** con effetto macchina da
  scrivere e guida al gioco

---

## 3. Architettura MVC

Il progetto segue il pattern architetturale
**Model-View-Controller (MVC)**:
### Model
Contiene le classi del dominio del gioco.
Non dipende né dalla View né dal Controller.

### View
Contiene le classi JavaFX per la GUI.
Dipende dal Controller per aggiornare i dati.

### Controller
Collega Model e View.
Gestisce la logica di gioco e la persistenza.

---

## 4. Classi e Responsabilità

### Package `model`

#### `GameCharacter` (abstract)
Classe astratta base per tutti i personaggi del gioco.
- Gestisce nome e ruolo del personaggio
- Dichiara il metodo astratto `presentati()`
- Base per `Journalist` e `NPC`

#### `Journalist`
Rappresenta il giornalista controllato dal giocatore.
- Gestisce il taccuino degli indizi raccolti
- Tiene traccia degli articoli scritti
- Gestisce i luoghi visitati
- Aggiorna la reputazione alla pubblicazione

#### `NPC`
Rappresenta i personaggi non giocanti della città.
- Contiene i dialoghi del personaggio
- Gestisce gli indizi che il personaggio può fornire
- Estende `GameCharacter`

#### `Location`
Rappresenta un luogo della città esplorabile.
- Contiene gli indizi presenti nel luogo
- Traccia se il luogo è stato visitato
- Implementa `Identifiable` e `Describable`

#### `Clue`
Rappresenta un indizio/prova raccoglibile.
- Tiene traccia se è stato scoperto
- Contiene la descrizione della prova
- Implementa `Identifiable`, `Describable`, `Discoverable`

#### `Article`
Rappresenta un articolo/rapporto scritto dal giornalista.
- Contiene gli indizi usati nell'articolo
- Calcola il valore di reputazione
- Implementa `Identifiable` e `Publishable`

#### `GameState`
Rappresenta lo stato corrente del gioco.
- Contiene il giornalista e la posizione corrente
- Usato per la serializzazione/deserializzazione JSON

### Package `controller`

#### `GameController`
Controller principale del gioco.
- Inizializza il gioco caricando i dati da JSON
- Gestisce il movimento tra i luoghi
- Coordina la raccolta degli indizi
- Gestisce la creazione e pubblicazione degli articoli
- Delega la persistenza al `GameRepository`
- Usa `ReputationCalculator` (lambda) per il calcolo

### Package `repository`

#### `JsonGameRepository`
Implementazione concreta di `GameRepository`.
- Salva lo stato di gioco in formato JSON
- Carica lo stato di gioco da file JSON
- Usa la libreria Gson

#### `GameDataLoader`
Carica i dati di gioco dai file JSON in resources.
- Carica i luoghi da `locations.json`
- Carica gli indizi da `clues.json`
- Carica gli NPC da `npcs.json`

### Package `view`

#### `GameView`
Vista principale del gioco.
- Mostra la mappa interattiva
- Visualizza le informazioni del luogo corrente
- Mostra il dossier degli indizi raccolti
- Gestisce i dialoghi con gli NPC

#### `MapView`
Vista della mappa con movimento WASD.
- Disegna la mappa pixel art su Canvas JavaFX
- Gestisce il movimento del personaggio
- Anima l'omino che cammina
- Notifica il controller al cambio di posizione

#### `WelcomeView`
Schermata introduttiva con tutorial.
- Mostra la storia del gioco
- Guida il giocatore alle meccaniche
- Effetto macchina da scrivere per il testo

### Package principale

#### `GameStats`
Classe di utilità per le statistiche di gioco.
- Usa Stream API per calcoli sui dati
- Conta gli indizi scoperti
- Calcola la reputazione totale
- Raggruppa gli indizi per stato

---

## 5. Interfacce

Il progetto segue il principio ISP (Interface Segregation
Principle) con interfacce piccole e mirate:

| Interfaccia | Responsabilità | Implementata da |
|-------------|---------------|-----------------|
| `GameRepository` | Persistenza stato gioco | `JsonGameRepository` |
| `Identifiable` | Fornisce un ID univoco | `Clue`, `Location`, `Article` |
| `Describable` | Fornisce una descrizione | `Clue`, `Location` |
| `Discoverable` | Può essere scoperto | `Clue` |
| `Publishable` | Può essere pubblicato | `Article` |
| `ReputationCalculator` | Calcola reputazione (lambda) | Implementata con lambda |

---

## 6. Persistenza dei Dati

### Dati di configurazione (statici)
I dati del gioco sono definiti in file JSON nella
cartella `src/main/resources/`:

- `locations.json` — luoghi della città
- `clues.json` — indizi disponibili
- `npcs.json` — personaggi non giocanti

Questi file vengono caricati all'avvio tramite
`GameDataLoader` usando la libreria **Gson**.

### Stato di gioco (dinamico)
Lo stato di gioco viene salvato automaticamente
in `savegame.json` nella directory di esecuzione
ogni volta che il giocatore pubblica un articolo.

La classe `JsonGameRepository` implementa
`GameRepository` e gestisce la serializzazione
e deserializzazione tramite Gson.

---

## 7. Estensibilità

Il progetto è progettato per supportare future
estensioni su più dispositivi (desktop, mobile, web):

### Aggiungere nuovi luoghi
Aggiungere un elemento a `locations.json` senza
modificare il codice Java.

### Aggiungere nuovi NPC
Aggiungere un elemento a `npcs.json` senza
modificare il codice Java.

### Aggiungere nuova persistenza
Creare una nuova classe che implementa `GameRepository`
(es. `DatabaseGameRepository`) senza modificare
il `GameController`.

### Aggiungere nuova interfaccia grafica
Creare una nuova View (es. per mobile) senza
modificare il Model o il Controller.

### Cambiare il calcolo della reputazione
Modificare solo la lambda in `GameController`:
```java
this.reputationCalculator = cluesCount -> cluesCount * 20;
```

---

## 8. Principi SOLID Applicati

### S — Single Responsibility Principle
Ogni classe ha una sola responsabilità:
- `Journalist` gestisce solo il giornalista
- `GameDataLoader` carica solo i dati
- `JsonGameRepository` gestisce solo la persistenza

### O — Open/Closed Principle
- `GameController` è aperto all'estensione tramite
  `GameRepository` senza modificare il codice esistente
- Nuovi tipi di persistenza si aggiungono implementando
  l'interfaccia

### L — Liskov Substitution Principle
- `JsonGameRepository` sostituisce `GameRepository`
  senza alterare il comportamento del controller
- `Journalist` e `NPC` sostituiscono `GameCharacter`
  rispettando il contratto della classe base

### I — Interface Segregation Principle
- Interfacce piccole e mirate invece di una grande
- `Clue` implementa solo le interfacce che gli servono
- Nessuna classe è costretta a implementare metodi
  che non usa

### D — Dependency Inversion Principle
- `GameController` dipende da `GameRepository`
  (astrazione) non da `JsonGameRepository` (concreto)
- Il pattern è: Controller → Interfaccia ← Implementazione

---

## 9. Dichiarazione Uso AI

Durante lo sviluppo del progetto è stato utilizzato
**Claude (Anthropic)** come assistente AI.

### Attività supportate dall'AI:
- **Progettazione architetturale** — suggerimenti
  sulla struttura MVC e organizzazione dei package
- **Spiegazione concetti** — chiarimenti sui principi
  SOLID, Clean Code, Stream API, JavaFX
- **Generazione codice** — generazione di porzioni
  di codice successivamente comprese e adattate
- **Debug** — supporto nell'identificazione e
  risoluzione di errori di compilazione
- **Configurazione** — supporto per Gradle, JavaFX,
  dipendenze

### Livello di intervento personale:
- Tutte le scelte architetturali sono state discusse
  e comprese prima dell'implementazione
- Il codice generato è stato analizzato e adattato
  alle esigenze del progetto
- I concetti teorici sono stati verificati sulle
  slide del corso
- La personalizzazione del gioco (tema spionaggio,
  storia, personaggi) è una scelta personale

### Comprensione del codice:
Lo studente è in grado di spiegare:
- La struttura MVC e le responsabilità di ogni layer
- L'applicazione dei principi SOLID nel codice
- Il funzionamento delle Stream API e delle lambda
- Il meccanismo di persistenza JSON con Gson
- Il pattern Observer usato nella MapView
- La gerarchia di classi e interfacce