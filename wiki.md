# Il Cronista - Operazione Ombra
## Wiki del Progetto

---

## Indice

1. Descrizione del Progetto
2. Funzionalità Implementate
3. Architettura MVC
4. Responsabilità Individuate
5. Classi e Responsabilità
6. Interfacce
7. Persistenza dei Dati
8. Estensibilità
9. Principi SOLID
10. Altri Design Pattern Utilizzati
11. Test
12. Dichiarazione Uso AI

---

## 1. Descrizione del Progetto

Il Cronista - Operazione Ombra è un gioco di ruolo
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

Mappa interattiva con visuale dall'alto e movimento
del personaggio tramite tasti WASD e frecce direzionali.
Sistema di esplorazione con 6 luoghi visitabili.
Sistema dialoghi a scelte multiple con requisiti
di carisma.
Indizi esclusivi ottenibili esplorando la mappa e
parlando con i contatti.
Raccolta prove con dossier visibile.
Sistema di articoli e pubblicazione rapporti.
Sistema di reputazione e credibilità, obiettivo 100.
Sistema RPG con statistiche (Intelligenza, Carisma,
Furtività), livelli ed esperienza.
Sistema missioni con obiettivi e barra progresso.
Metro del sospetto, aumenta con le azioni rischiose.
Sistema giorni, 10 giorni per completare la missione.
Pannello obiettivo sempre visibile che guida il
giocatore passo passo.
Persistenza completa dello stato di gioco in formato
JSON: salvataggio automatico alla pubblicazione di
un articolo, e caricamento tramite una schermata
Continua / Nuova Partita mostrata all'avvio se
esiste un salvataggio precedente.
Schermata introduttiva con effetto macchina da
scrivere e città notturna pixel art.
Stile grafico sepia vintage anni '30.

---

## 3. Architettura MVC

Il progetto segue il pattern architetturale
Model-View-Controller (MVC).

### Model

Contiene le classi del dominio del gioco.
Non dipende né dalla View né dal Controller, nessuna
classe del package model importa componenti JavaFX.
Ogni classe ha una responsabilità ben definita.

### View

Contiene le classi JavaFX per la GUI.
Dipende dal Controller per leggere e aggiornare i dati.
Non contiene logica di gioco: la pubblicazione di un
articolo, ad esempio, è delegata interamente al
Controller tramite writeArticleFromNotebook().

### Controller

Collega Model e View.
Gestisce tutta la logica di gioco e la persistenza.
Dipende dalle interfacce, non dalle implementazioni.
Come il Model, non importa alcun componente JavaFX:
espone solo metodi pubblici che qualunque tipo di View
(desktop, mobile, web) può chiamare.

---

## 4. Responsabilità Individuate

Prima di assegnare le responsabilità alle singole
classi, sono state individuate le responsabilità
principali che il sistema nel suo complesso deve
garantire.

→ Gestione del personaggio giocante: tracciare
progressione, statistiche RPG, indizi raccolti,
luoghi visitati e missioni attive
→ Gestione del mondo di gioco: rappresentare i luoghi
esplorabili e le informazioni necessarie alla loro
visualizzazione
→ Gestione dei personaggi non giocanti: fornire
dialoghi, scelte a requisiti di carisma e indizi
esclusivi
→ Gestione delle prove: tracciare quali indizi sono
stati scoperti e quali sono ancora disponibili
→ Gestione degli articoli e della reputazione:
calcolare il valore di reputazione derivato dalle
prove utilizzate
→ Gestione delle missioni: tracciare obiettivi e
verificare il completamento
→ Gestione dello stato di rischio: tracciare il
livello di sospetto e i giorni rimasti, determinando
le condizioni di vittoria e sconfitta
→ Gestione della persistenza: salvare e caricare lo
stato di gioco, senza vincolare il resto del sistema
a un formato di salvataggio specifico
→ Gestione del caricamento dei dati di configurazione:
caricare luoghi, indizi, NPC e missioni da file
esterni, separandoli dal codice
→ Gestione dell'interfaccia utente: mostrare mappa,
stato del personaggio, dialoghi e messaggi, e
raccogliere l'input del giocatore

Queste responsabilità di alto livello sono state
distribuite tra le classi descritte nella sezione
successiva, seguendo il principio di singola
responsabilità (SRP).

---

## 5. Classi e Responsabilità

### Package model

GameCharacter (abstract)
Classe astratta base per tutti i personaggi del gioco.
Responsabilità: gestire nome e ruolo del personaggio.
Dichiara il metodo astratto introduceSelf() che ogni
sottoclasse deve implementare.

Journalist extends GameCharacter
Rappresenta il giornalista e agente controllato dal
giocatore.
Responsabilità: gestire taccuino indizi, articoli,
luoghi visitati (tracciati tramite id, non oggetti
interi, per evitare di duplicare i dati dei luoghi
nel file di salvataggio), missioni attive e completate.
Aggiorna automaticamente le statistiche ad ogni azione.
Tutti i getter di collezioni restituiscono copie non
modificabili, per proteggere lo stato interno da
modifiche esterne non controllate.

NPC extends GameCharacter implements Identifiable
Rappresenta i personaggi non giocanti della città.
Responsabilità: contenere dialoghi e indizi fornibili
al giocatore. Getter di collezioni con copie difensive,
come Journalist.

Location implements Identifiable, Describable
Rappresenta un luogo della città esplorabile.
Responsabilità: contenere gli indizi presenti, tracciare
se è stato visitato, e fornire i dati necessari alla
sua rappresentazione visiva sulla mappa (coordinate,
simbolo, colore, nome breve). Questo rende la mappa
(MapView) completamente guidata dai dati: aggiungere
un nuovo luogo richiede solo una voce in locations.json.

Clue implements Identifiable, Describable, Discoverable
Rappresenta una prova raccoglibile.
Responsabilità: contenere la descrizione e tracciare
se è stata scoperta. Alcuni indizi hanno un luogo
associato (raccoglibili esplorando), altri no: sono
ottenibili solo tramite un dialogo specifico con un NPC.

Article implements Identifiable, Publishable
Rappresenta un articolo scritto dal giornalista.
Responsabilità: contenere gli id delle prove usate
(non gli oggetti interi, per lo stesso motivo di
Journalist) e calcolare il valore di reputazione,
ogni prova vale 10 punti.

PlayerStats
Gestisce le statistiche RPG del personaggio.
Responsabilità: intelligenza, carisma, furtività,
livello ed esperienza. Gestisce il level-up automatico
ogni 100 XP.

Quest implements Identifiable
Rappresenta una missione del gioco.
Responsabilità: tenere traccia degli obiettivi e
verificare il completamento della missione.
equals() e hashCode() basati sull'id garantiscono
un confronto corretto anche tra istanze diverse create
da un caricamento da file.

GameState
Rappresenta lo stato corrente del gioco.
Responsabilità: contenere il giornalista e l'id del
luogo corrente per la serializzazione JSON.

GameStats
Classe di utilità per le statistiche di gioco,
collocata nel package model insieme al resto del
dominio.
Responsabilità: usare Stream API per calcoli sui dati
(prove scoperte, reputazione derivata dagli articoli,
articoli pubblicati, raggruppamento prove per stato).

### Package controller

GameController
Controller principale del gioco.
Responsabilità: inizializzare il gioco dai file JSON,
gestire movimento, raccolta prove, dialoghi, missioni,
metro sospetto, sistema giorni e persistenza (sia
salvataggio che caricamento tramite loadGame()).
Usa ReputationCalculator come lambda per il calcolo
della reputazione.
Fornisce getCurrentObjective() per guidare il
giocatore passo passo.
Costruisce un indice NPC-per-luogo al caricamento
iniziale, evitando di ricalcolarlo ad ogni richiesta.
Il risultato di una scelta di dialogo è rappresentato
da DialogueResult, un record interno: un value
object immutabile, non una classe con campi pubblici
mutabili.

### Package repository

JsonGameRepository implements GameRepository
Responsabilità: salvare e caricare lo stato di gioco
in formato JSON tramite la libreria Gson.

GameDataLoader
Responsabilità: caricare i dati di gioco dai file JSON
in resources (locations, clues, npcs, quests). Contiene
le inner class NpcData, ChoiceData e QuestData per la
deserializzazione dei dati grezzi. Segnala in modo
esplicito, con un'eccezione dal messaggio chiaro, il
caso in cui un file atteso non venga trovato nelle
risorse.

### Package view

GameView
Vista principale del gioco.
Responsabilità: mostrare mappa, statistiche, dossier,
missioni, obiettivo corrente e log messaggi.
Mostra il pulsante OTTIENI PROVE solo quando ci
sono prove disponibili nel luogo corrente. Il pulsante
SCRIVI RAPPORTO si disabilita automaticamente quando
il taccuino è vuoto, tramite binding JavaFX
(disableProperty().bind(...)) invece di un controllo
manuale ripetuto. Applica un foglio di stile condiviso
(style.css) anche ai dialoghi nativi (Alert,
TextInputDialog), per uniformarli visivamente al resto
del gioco.

MapView
Vista della mappa con movimento WASD e frecce.
Responsabilità: disegnare la mappa pixel art su Canvas
JavaFX, gestire il movimento del personaggio animato
tramite AnimationTimer, notificare il controller
al cambio di posizione. Le posizioni, i colori e i
simboli degli edifici non sono più definiti nel codice:
vengono letti direttamente dagli oggetti Location,
già caricati dal Controller. L'input da tastiera è
gestito tramite Command Pattern: ogni tasto è associato
a un comando di movimento registrato in una mappa,
invece di una catena di if/else. Aggiungere un nuovo
controllo richiede solo una riga in più, senza toccare
la logica di aggiornamento.

WelcomeView
Schermata introduttiva con tutorial a più passi.
Responsabilità: mostrare la storia del gioco con
effetto macchina da scrivere e sfondo città notturna
pixel art disegnata su Canvas (disegnato una sola volta
e riutilizzato tra gli step, non ridisegnato ad ogni
cambio). Ogni step del tutorial è rappresentato da un
record (TutorialStep) che accoppia titolo e
contenuto, invece di due array paralleli da mantenere
sincronizzati a mano. Se esiste un salvataggio
precedente, mostra una schermata Continua / Nuova
Partita prima del tutorial.

FontRegistry
Registro singleton per il font pixel art del gioco.
Responsabilità: caricare il file del font una sola
volta in assoluto, indipendentemente da quante view lo
richiedano, e fornire qualsiasi dimensione richiesta
riutilizzando la stessa famiglia già in memoria, invece
di rileggere il file da risorse ad ogni view.

---

## 6. Interfacce

Il progetto segue il principio ISP con interfacce
piccole e mirate.

Interfaccia GameRepository: gestisce la persistenza
dello stato gioco, implementata da JsonGameRepository.

Interfaccia Identifiable: fornisce un ID univoco,
implementata da Clue, Location, Article, NPC, Quest.

Interfaccia Describable: fornisce una descrizione,
implementata da Clue, Location.

Interfaccia Discoverable: rappresenta ciò che può
essere scoperto, implementata da Clue.

Interfaccia Publishable: rappresenta ciò che può
essere pubblicato, implementata da Article.

Interfaccia ReputationCalculator: calcola la
reputazione, è un'interfaccia funzionale, implementata
da una lambda in GameController.

Discoverable include anche un metodo default,
discoveryStatus(), che restituisce una descrizione
testuale dello stato di scoperta riusando i due metodi
astratti dell'interfaccia. Dimostra l'uso dei metodi
default nelle interfacce Java visto a lezione, senza
che le classi che la implementano debbano definirlo.

---

## 7. Persistenza dei Dati

### Dati di configurazione (statici)

I dati del gioco sono definiti in file JSON nella
cartella src/main/resources/.

locations.json contiene 6 luoghi, con descrizione
narrativa e con i dati necessari alla rappresentazione
sulla mappa (coordinate, simbolo, colore, nome breve).

clues.json contiene 15 prove: 10 raccoglibili
esplorando i luoghi, 5 ottenibili solo tramite un
dialogo specifico con un NPC, identificabili
dall'assenza di un luogo associato.

npcs.json contiene 5 personaggi con dialoghi e scelte.

quests.json contiene le missioni con i relativi
obiettivi.

Questi file vengono caricati all'avvio tramite
GameDataLoader usando la libreria Gson.
Modificare questi file non richiede modifiche al
codice Java, mappa inclusa: questo è un esempio di
OCP applicato end-to-end.

### Stato di gioco (dinamico)

Lo stato viene salvato automaticamente in
savegame.json ogni volta che il giocatore pubblica
un articolo. La classe JsonGameRepository gestisce
la serializzazione e deserializzazione tramite Gson.
Per evitare di duplicare i dati nel file di salvataggio,
Journalist e Article salvano gli id delle entità
collegate (luoghi visitati, indizi usati) invece
degli oggetti interi. Il caricamento è effettivamente
collegato all'esperienza di gioco: se un salvataggio
esiste, all'avvio viene proposta la scelta tra
Continua e Nuova Partita.

---

## 8. Estensibilità

Il progetto è progettato per supportare future
estensioni su più dispositivi (desktop, mobile, web)
grazie alla separazione MVC e all'uso di interfacce.

### Estendere il progetto su più dispositivi

L'architettura MVC scelta rende questa estensione
concreta, non solo teorica: nessuna classe del
package model o controller importa componenti
JavaFX. Solo il package view dipende dal framework
grafico. Questo significa che:

Resterebbe identico portando il gioco su un altro
dispositivo: tutte le classi in model/ (Journalist,
NPC, Location, Clue, Article, Quest, PlayerStats,
GameState), GameController con tutta la logica di
gioco, GameRepository e la sua implementazione JSON,
i file di dati in resources/ (locations.json,
clues.json, npcs.json, quests.json).

Andrebbe scritto ex novo, per ciascuna piattaforma:
per mobile (es. Android), una nuova implementazione
delle view che usi i widget nativi della piattaforma
al posto di Canvas, Label, Button di JavaFX, chiamando
comunque gli stessi metodi pubblici di GameController
(moveToLocation(), collectAllCluesInCurrentLocation(),
processChoice(), eccetera). Per il web, una nuova view
che traduca lo stato del GameController in HTML e
JavaScript, o tramite un framework come Vaadin, che
permette di scrivere la UI ancora in Java, comunicando
con lo stesso Controller lato server.

In entrambi i casi, il punto di contatto tra la nuova
interfaccia e il resto del sistema resta lo stesso:
i metodi pubblici di GameController, che non cambiano
in base al dispositivo che li chiama.

### Aggiungere nuovi luoghi

Aggiungere un elemento a locations.json (con
coordinate, simbolo e colore) senza modificare il
codice Java. Sia il GameController sia la mappa
(MapView) caricano automaticamente qualsiasi luogo
presente nel file.

### Aggiungere nuovi NPC e dialoghi

Aggiungere elementi a npcs.json con nuovi dialoghi
e scelte multiple. Il sistema di carisma gestisce
automaticamente le opzioni disponibili. È possibile
definire indizi esclusivi del dialogo semplicemente
non assegnando loro un locationId in clues.json.

### Aggiungere nuove missioni

Aggiungere elementi a quests.json. Il sistema di
missioni carica e traccia automaticamente i progressi.

### Aggiungere nuove statistiche RPG

Estendere la classe PlayerStats con nuove statistiche
senza modificare le altre classi.

### Aggiungere nuova persistenza

Creare una classe che implementa GameRepository
(ad esempio DatabaseGameRepository) e passarla al
GameController senza modificare nient'altro.
Questo è il principio DIP applicato: lo stesso
meccanismo usato per fornire una repository fittizia
in memoria durante i test, vedi sezione Test.

### Cambiare il calcolo della reputazione

Modificare solo la lambda in GameController, ad
esempio impostando reputationCalculator in modo che
ogni prova valga 20 punti invece di 10. Questo è
possibile grazie a ReputationCalculator, che è
un'interfaccia funzionale.

### Aggiungere nuovi tipi di personaggio

Creare una nuova classe che estende GameCharacter
e implementa introduceSelf(). Il polimorfismo garantisce
la compatibilità con il resto del sistema.

### Aggiungere nuovi comandi di input

Grazie al Command Pattern usato in MapView, un nuovo
controllo da tastiera si aggiunge registrando una nuova
voce nella mappa movementCommands, senza toccare la
logica di aggiornamento del personaggio.

### Estendere il sistema di sospetto

Il metro del sospetto può essere esteso con eventi
casuali che aumentano il sospetto, oggetti che lo
riducono, oppure abilità speciali legate alla
furtività.

### Estendere il sistema giorni

Il numero di giorni disponibili è definito dalla
costante MAX_DAYS in GameController. Modificarla
cambia la difficoltà del gioco senza toccare
altra logica.

---

## 9. Principi SOLID Applicati

### S, Single Responsibility Principle

Ogni classe ha una sola responsabilità. Journalist
gestisce solo il giornalista, GameDataLoader carica
solo i dati, JsonGameRepository gestisce solo la
persistenza, PlayerStats gestisce solo le statistiche
RPG, Quest gestisce solo una singola missione,
FontRegistry gestisce solo il caricamento dei font.

### O, Open/Closed Principle

Aggiungere nuovi luoghi, NPC o missioni modificando
solo i file JSON, senza toccare il codice Java, vale
anche per la rappresentazione sulla mappa, non solo
per i dati di gioco. Nuovi tipi di persistenza si
aggiungono implementando GameRepository. Nuovi comandi
di input si aggiungono registrando un nuovo
MovementCommand, senza modificare updatePlayer().

### L, Liskov Substitution Principle

JsonGameRepository sostituisce GameRepository senza
alterare il comportamento del controller, lo stesso
vale per InMemoryGameRepository nei test. Journalist
e NPC sostituiscono GameCharacter rispettando il
contratto della classe base.

### I, Interface Segregation Principle

Interfacce piccole e mirate invece di una grande.
Clue implementa Identifiable, Describable e
Discoverable, solo quello che gli serve. Article
implementa Identifiable e Publishable. Ogni classe
implementa solo le interfacce di cui ha bisogno.

### D, Dependency Inversion Principle

GameController dipende da GameRepository, che è
un'astrazione, non da JsonGameRepository, che è
un'implementazione concreta. Lo schema è: Controller
verso interfaccia, interfaccia implementata da classe
concreta. Nei test, la stessa astrazione permette di
sostituire JsonGameRepository con
InMemoryGameRepository senza toccare il Controller.

---

## 10. Altri Design Pattern Utilizzati

Oltre ai principi SOLID, il progetto applica alcuni
design pattern specifici.

Command Pattern: in MapView, ogni tasto di movimento
è associato a un comando (MovementCommand) registrato
in una mappa, invece di una catena di if/else.
Aggiungere un nuovo controllo richiede solo una nuova
registrazione.

Singleton: FontRegistry garantisce che il font pixel
art venga caricato una sola volta in tutta
l'applicazione e riutilizzato da tutte le view, invece
di essere riletto da risorse ripetutamente.

Strategy, tramite lambda: ReputationCalculator, essendo
un'interfaccia funzionale, permette di cambiare
l'algoritmo di calcolo della reputazione fornendo una
lambda diversa, senza modificare GameController.

Value Object, tramite record: DialogueResult (in
GameController) e TutorialStep (in WelcomeView) sono
record, oggetti dato immutabili, usati al posto di
classi con campi pubblici mutabili o di array paralleli
da mantenere sincronizzati a mano.

---

## 11. Test

Il progetto include test JUnit 5 che verificano il
comportamento di GameController senza toccare il
filesystem, grazie a una repository fittizia in memoria.

InMemoryGameRepository (src/test/java)
Implementazione di GameRepository che tiene lo stato
in una variabile invece di scriverlo su file. Resa
possibile dal principio DIP: GameController dipende
dall'interfaccia, non dall'implementazione concreta,
quindi può ricevere questa repository fittizia al posto
di JsonGameRepository durante i test.

GameControllerTest
Verifica: assenza di salvataggi su una partita nuova,
disponibilità del salvataggio dopo saveGame(),
corretto aumento di sospetto e avanzamento dei giorni
dopo uno spostamento, corretto conteggio degli indizi
raccolti in un luogo, ed eccezione corretta quando si
tenta di spostarsi verso un luogo inesistente.

---

## 12. Dichiarazione Uso AI

Durante lo sviluppo del progetto è stato utilizzato
Claude (Anthropic) come assistente AI.

### Attività supportate dall'AI

Assimilare concetti teorici: 
Utilizzato per comprendere a fondo argomenti trattati a 
lezione, come i principi SOLID, il pattern MVC, 
l'uso delle Stream API in Java e le interfacce funzionali, 
verificando poi ogni concetto del corso 
prima di applicarlo sul codice.

Automatizzazione del codice ripetitivo: Utilizzato per generare rapidamente elementi ripetitivi come getter, setter, 
costruttori e piccoli blocchi di codice simili tra loro 
(ad esempio la registrazione dei comandi di movimento in MapView).

Suggerimenti sulla struttura del codice: Utilizzato per ricevere pareri su come organizzare 
package e classi, ad esempio la separazione tra model, view, controller e repository,
valutando poi personalmente quali suggerimenti applicare in base a quanto visto a lezione.