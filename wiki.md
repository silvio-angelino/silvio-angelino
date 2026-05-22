# Il Cronista - Operazione Ombra
## Wiki del Progetto

---

## Indice

1. [Descrizione del Progetto](#1-descrizione-del-progetto)
2. [Funzionalità Implementate](#2-funzionalità-implementate)
3. [Architettura MVC](#3-architettura-mvc)
4. [Classi e Responsabilità](#4-classi-e-responsabilità)
5. [Interfacce](#5-interfacce)
6. [Persistenza dei Dati](#6-persistenza-dei-dati)
7. [Estensibilità](#7-estensibilità)
8. [Principi SOLID](#8-principi-solid)
9. [Dichiarazione Uso AI](#9-dichiarazione-uso-ai)

---

## 1. Descrizione del Progetto

"Il Cronista - Operazione Ombra" è un gioco di ruolo
investigativo ambientato nel 1935.
Il giocatore interpreta un agente segreto sotto copertura
che deve smascherare una rete di spie straniere operante
in città prima che il tempo scada.

Il progetto è stato sviluppato come applicativo Java
con interfaccia grafica JavaFX, applicando i principi
e gli strumenti visti durante il corso di Metodologie
di Programmazione / Modellazione e Gestione della
Conoscenza A.A. 2025/26.

---

## 2. Funzionalità Implementate

- Mappa interattiva con visuale dall'alto e movimento
  del personaggio tramite tasti WASD e frecce direzionali
- Sistema di esplorazione con 6 luoghi visitabili
- Sistema dialoghi a scelte multiple con requisiti
  di carisma
- Raccolta prove con dossier visibile
- Sistema di articoli e pubblicazione rapporti
- Sistema di reputazione/credibilità (obiettivo 100)
- Sistema RPG con statistiche (Intelligenza, Carisma,
  Furtività), livelli ed esperienza
- Sistema missioni con obiettivi e barra progresso
- Metro del sospetto — aumenta con le azioni rischiose
- Sistema giorni — 10 giorni per completare la missione
- Pannello obiettivo sempre visibile che guida il
  giocatore passo passo
- Persistenza dello stato di gioco in formato JSON
- Schermata introduttiva con effetto macchina da
  scrivere e città notturna pixel art
- Stile grafico sepia vintage anni '30

---

## 3. Architettura MVC

Il progetto segue il pattern architetturale
Model-View-Controller (MVC):
### Model
Contiene le classi del dominio del gioco.
Non dipende né dalla View né dal Controller.
Ogni classe ha una responsabilità ben definita.

### View
Contiene le classi JavaFX per la GUI.
Dipende dal Controller per leggere e aggiornare i dati.
Non contiene logica di gioco.

### Controller
Collega Model e View.
Gestisce tutta la logica di gioco e la persistenza.
Dipende dalle interfacce, non dalle implementazioni.

---

## 4. Classi e Responsabilità

### Package model

#### GameCharacter (abstract)
Classe astratta base per tutti i personaggi del gioco.
Responsabilità: gestire nome e ruolo del personaggio.
Dichiara il metodo astratto presentati() che ogni
sottoclasse deve implementare.

#### Journalist extends GameCharacter
Rappresenta il giornalista/agente controllato dal
giocatore.
Responsabilità: gestire taccuino indizi, articoli,
luoghi visitati, missioni attive e completate.
Aggiorna automaticamente le statistiche ad ogni azione.

#### NPC extends GameCharacter
Rappresenta i personaggi non giocanti della città.
Responsabilità: contenere dialoghi e indizi fornibili
al giocatore.

#### Location implements Identifiable, Describable
Rappresenta un luogo della città esplorabile.
Responsabilità: contenere gli indizi presenti e
tracciare se è stato visitato.

#### Clue implements Identifiable, Describable, Discoverable
Rappresenta una prova raccoglibile.
Responsabilità: contenere la descrizione e tracciare
se è stata scoperta.

#### Article implements Identifiable, Publishable
Rappresenta un articolo scritto dal giornalista.
Responsabilità: contenere le prove usate e calcolare
il valore di reputazione (ogni prova vale 10 punti).

#### PlayerStats
Gestisce le statistiche RPG del personaggio.
Responsabilità: intelligenza, carisma, furtività,
livello ed esperienza. Gestisce il level-up automatico
ogni 100 XP.

#### Quest
Rappresenta una missione del gioco.
Responsabilità: tenere traccia degli obiettivi e
verificare il completamento della missione.

#### DialogueChoice
Rappresenta una scelta in un dialogo con un NPC.
Responsabilità: contenere testo, risposta, requisito
di carisma e ricompensa in esperienza.

#### GameState
Rappresenta lo stato corrente del gioco.
Responsabilità: contenere il giornalista e la posizione
corrente per la serializzazione JSON.

### Package controller

#### GameController
Controller principale del gioco.
Responsabilità: inizializzare il gioco dai file JSON,
gestire movimento, raccolta prove, dialoghi, missioni,
metro sospetto, sistema giorni e persistenza.
Usa ReputationCalculator come lambda per il calcolo
della reputazione.
Fornisce getCurrentObjective() per guidare il giocatore
passo passo.

### Package repository

#### JsonGameRepository implements GameRepository
Responsabilità: salvare e caricare lo stato di gioco
in formato JSON tramite la libreria Gson.

#### GameDataLoader
Responsabilità: caricare i dati di gioco dai file JSON
in resources (locations, clues, npcs, quests).
Contiene le inner class NpcData, ChoiceData e QuestData
per la deserializzazione dei dati.

### Package view

#### GameView
Vista principale del gioco.
Responsabilità: mostrare mappa, statistiche, dossier,
missioni, obiettivo corrente e log messaggi.
Mostra il pulsante "OTTIENI PROVE" solo quando ci
sono prove disponibili nel luogo corrente.

#### MapView
Vista della mappa con movimento WASD e frecce.
Responsabilità: disegnare la mappa pixel art su Canvas
JavaFX, gestire il movimento del personaggio animato
tramite AnimationTimer, notificare il controller
al cambio di posizione.

#### WelcomeView
Schermata introduttiva con tutorial a più passi.
Responsabilità: mostrare la storia del gioco con
effetto macchina da scrivere e sfondo città notturna
pixel art disegnata su Canvas.

### Package principale

#### GameStats
Classe di utilità per le statistiche di gioco.
Responsabilità: usare Stream API per calcoli sui dati
(prove scoperte, reputazione totale, articoli pubblicati,
raggruppamento prove per stato).

---

## 5. Interfacce

Il progetto segue il principio ISP con interfacce
piccole e mirate:

| Interfaccia | Responsabilità | Implementata da |
|-------------|----------------|-----------------|
| GameRepository | Persistenza stato gioco | JsonGameRepository |
| Identifiable | Fornisce un ID univoco | Clue, Location, Article |
| Describable | Fornisce una descrizione | Clue, Location |
| Discoverable | Può essere scoperto | Clue |
| Publishable | Può essere pubblicato | Article |
| ReputationCalculator | Calcola reputazione | Lambda in GameController |

---

## 6. Persistenza dei Dati

### Dati di configurazione (statici)
I dati del gioco sono definiti in file JSON nella
cartella src/main/resources/:

- locations.json — 6 luoghi con coordinate mappa
- clues.json — 10 prove disponibili
- npcs.json — 5 personaggi con dialoghi e scelte
- quests.json — 4 missioni con obiettivi

Questi file vengono caricati all'avvio tramite
GameDataLoader usando la libreria Gson.
Modificare questi file non richiede modifiche al
codice Java — questo è un esempio di OCP applicato.

### Stato di gioco (dinamico)
Lo stato viene salvato automaticamente in
savegame.json ogni volta che il giocatore pubblica
un articolo. La classe JsonGameRepository gestisce
la serializzazione e deserializzazione tramite Gson.

---

## 7. Estensibilità

Il progetto è progettato per supportare future
estensioni su più dispositivi (desktop, mobile, web)
grazie alla separazione MVC e all'uso di interfacce.

### Aggiungere nuovi luoghi
Aggiungere un elemento a locations.json senza
modificare il codice Java. Il GameDataLoader carica
automaticamente tutti i luoghi presenti nel file.

### Aggiungere nuovi NPC e dialoghi
Aggiungere elementi a npcs.json con nuovi dialoghi
e scelte multiple. Il sistema di carisma gestisce
automaticamente le opzioni disponibili.

### Aggiungere nuove missioni
Aggiungere elementi a quests.json. Il sistema di
missioni carica e traccia automaticamente i progressi.

### Aggiungere nuove statistiche RPG
Estendere la classe PlayerStats con nuove statistiche
senza modificare le altre classi.

### Aggiungere nuova persistenza
Creare una classe che implementa GameRepository
(es. DatabaseGameRepository) e passarla al
GameController senza modificare nient'altro.
Questo è il principio DIP applicato.

### Aggiungere nuova interfaccia grafica
Creare una nuova View (es. per mobile o web) senza
modificare il Model o il Controller. La separazione
MVC garantisce questa estendibilità.

### Cambiare il calcolo della reputazione
Modificare solo la lambda in GameController:
```java
this.reputationCalculator = cluesCount -> cluesCount * 20;
```
Questo è possibile grazie a ReputationCalculator
(@FunctionalInterface).

### Aggiungere nuovi tipi di personaggio
Creare una nuova classe che estende GameCharacter
e implementa presentati(). Il polimorfismo garantisce
la compatibilità con il resto del sistema.

### Estendere il sistema di sospetto
Il metro del sospetto può essere esteso con:
- Eventi casuali che aumentano il sospetto
- Oggetti che riducono il sospetto
- Abilità speciali legate alla furtività

### Estendere il sistema giorni
Il numero di giorni disponibili è definito dalla
costante MAX_DAYS in GameController. Modificarla
cambia la difficoltà del gioco senza toccare
altra logica.

---

## 8. Principi SOLID Applicati

### S — Single Responsibility Principle
Ogni classe ha una sola responsabilità:
- Journalist gestisce solo il giornalista
- GameDataLoader carica solo i dati
- JsonGameRepository gestisce solo la persistenza
- PlayerStats gestisce solo le statistiche RPG
- Quest gestisce solo una singola missione

### O — Open/Closed Principle
- Aggiungere nuovi luoghi/NPC/missioni modificando
  solo i file JSON, senza toccare il codice Java
- Nuovi tipi di persistenza si aggiungono implementando
  GameRepository

### L — Liskov Substitution Principle
- JsonGameRepository sostituisce GameRepository
  senza alterare il comportamento del controller
- Journalist e NPC sostituiscono GameCharacter
  rispettando il contratto della classe base

### I — Interface Segregation Principle
- Interfacce piccole e mirate invece di una grande
- Clue implementa Identifiable, Describable e
  Discoverable — solo quello che gli serve
- Article implementa Identifiable e Publishable
- Ogni classe implementa solo le interfacce
  di cui ha bisogno

### D — Dependency Inversion Principle
- GameController dipende da GameRepository
  (astrazione) non da JsonGameRepository (concreto)
- Schema: Controller → Interfaccia ← Implementazione

---

## 9. Dichiarazione Uso AI

Durante lo sviluppo del progetto è stato utilizzato
Claude (Anthropic) come assistente AI.

### Attività supportate dall'AI
- Progettazione architetturale — suggerimenti sulla
  struttura MVC e organizzazione dei package
- Spiegazione concetti — chiarimenti sui principi
  SOLID, Clean Code, Stream API, JavaFX
- Generazione codice — generazione di porzioni di
  codice successivamente comprese e adattate
- Debug — supporto nell'identificazione e risoluzione
  di errori di compilazione
- Configurazione — supporto per Gradle, JavaFX,
  dipendenze
- Revisione e miglioramento dello stile del codice
  per renderlo più leggibile e naturale

### Livello di intervento personale
- Tutte le scelte architetturali sono state discusse
  e comprese prima dell'implementazione
- Il codice generato è stato analizzato e adattato
  alle esigenze del progetto
- I concetti teorici sono stati verificati sulle
  slide del corso
- La personalizzazione del gioco (tema spionaggio,
  storia, personaggi, stile grafico) è una scelta
  personale dello studente

### Comprensione del codice
Lo studente è in grado di spiegare:
- La struttura MVC e le responsabilità di ogni layer
- L'applicazione dei principi SOLID nel codice
- Il funzionamento delle Stream API e delle lambda
- Il meccanismo di persistenza JSON con Gson
- La gerarchia di classi e interfacce
- Il sistema RPG con statistiche e missioni