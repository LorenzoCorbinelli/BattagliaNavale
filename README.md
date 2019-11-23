# Battaglia Navale Client-Server

## Obiettivo:
Creare il gioco della **battaglia navale tra due giocatori**. 
I giocatori si alternano nelle giocate. 
La scacchiera è composta da una **matrice 21x21**
Ogni giocatore ha a disposizione:
- n. 3 navi da 2 posti
- n. 2 navi da 3 posti
- n. 1 nave da 4 posti
- n. 1 nave da 5 posti

Le navi devono avere una distanza di almeno 1 casella una dall’altra.

L’applicazione sarà composta da due “moduli”: la parte server e la parte client.

Il **server** accetterà al **massimo due client** e si occuperà di:
- **gestire l’avvio della partita** attendendo la connessione dei due client
- **far posizionare le navi** sulla griglia ai due giocatori
- **mantenere lo stato del gioco**
- **gestire la turnazione**
- **comunicare gli esiti delle giocate**
- **comunicare la conclusione della partita**

Il **client** dovrà:
- **connettersi ad un server**
- far **posizionare le navi** al giocatore, comunicando il posizionamento al server
- **attendere che l’altro giocatore abbia concluso il posizionamento**
- **attendere il proprio turno di gioco**
- **effettuare la giocata** inviando le coordinate al server, e mostrare l’esito della giocata/partita
 
## Lavoro svolto

Il gioco è stato diviso in tre packages: 
- *BattagliaNavale*
- *Client*
- *Server*

### BattagliaNavale package

È presente il file **BattagliaNavale.java** che *gestisce l'avvio del server* in locale e la *connessione da parte di due client*. 

### Client package

Sono presenti i seguenti file:
- **BattagliaNavaleClient.java** che corrisponde all'*intefaccia del gioco* e contiene i metodi per poter far interagire il client con l'interfaccia stessa;
- **Square.java** che corrisponde all'estensione della classe *JPanel*, utilizzata per la creazione dei quadretti del campo di gioco;
- **MessageListener.java** che corrisponde all'estensione della classe *Runnable*. È un *thread* che gestisce la comunicazione tra client e server, comunicando attraverso codici prestabili.

### Server package

Sono presenti i seguenti file:
- **Listener.java** che corrisponde all'estensione della classe *Runnable*.
- **Nave.java** che corrisponde alla classe che contiene i pezzi per la creazione e la composizione di una nave e il controllo sull'affondamento;
- **Partita.java** che corrisponde alla classe che permette la connessione del client al server, all'interno della quale viene inizializzata l'interfaccia grafica;
- **Pezzo.java** che corrisponde alla classe che contiene le informazioni relative ad ogni singolo pezzo della nave;
- **Player.java** che corrisponde all'estensione della classe *Runnable*. È un thread che identifica il singolo giocatore e si occupa della gestione dell'inserimento delle navi e dell'attacco, compreso di controlli sulla tipologia di attacco (acqua/nave e se questo attacco ha portato alla vittoria da parte del giocatore stesso);
- **Position.java** che corrisponde alla classe che controlla se il pezzo colpito è nave o no.

## Deployment

Per l'esecuzione del file BattagliaNavale.java come server, sarà necessario utilizzare il seguente comando nel *terminale* su **linux**: 
```
javac BattagliaNavale.java && java BattagliaNavale -server
```
Per poter eseguire da *cmd* su **Windows** sarà necessario 
```
clean and built project button on NetBeans
```
il quale restiturirà un percorso che, se inserito nel *cmd* potrà permettere l'esecuzione del client. 

### Ritorno della compilazione
```
Building jar: C:\Users\yourUser\BattagliaNavale\BattagliaNavale\dist\BattagliaNavale.jar
To run this application from the command line without Ant, try:
java -jar "C:\Users\yourUser\BattagliaNavale\BattagliaNavale\dist\BattagliaNavale.jar"
```
### Esecuzione cmd server (Windows)
Per l'esecuzione del server dorvà essere aggiunto '-server'
```
java -jar "C:\Users\yourUser\BattagliaNavale\BattagliaNavale\dist\BattagliaNavale.jar" -server
```
### Esecuzione cmd server (Linux)
Per l'esecuzione del server dorvà essere aggiunto '-server'
```
javac BattagliaNavale.java && java BattagliaNavale -server
```
### Esecuzione cmd client (Windows)
```
java -jar "C:\Users\yourUser\BattagliaNavale\BattagliaNavale\dist\BattagliaNavale.jar"
```
### Esecuzione cmd client (Linux)
```
javac BattagliaNavale.java && java BattagliaNavale
```
## Built With

* [Netbeans](https://netbeans.apache.org/download/)

## Autori

* *Riccardo Degli Esposti* - [Rufis01](https://github.com/Rufis01)
* *Lorenzo Corbinelli* - [LorenzoCorbinelli](https://github.com/LorenzoCorbinelli)
* *Giulia Giamberini* - [GiamberiniGiulia](https://github.com/giamberinigiulia)

Guarda anche la lista dei [contributors](https://github.com/LorenzoCorbinelli/BattagliaNavale/contributors) che hanno partecipato al progetto.
