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

### Server package

##

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

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/your/project/tags). 

## Autori

* *Riccardo Degli Esposti* - [Rufis01](https://github.com/Rufis01)
* *Lorenzo Corbinelli* - [LorenzoCorbinelli](https://github.com/LorenzoCorbinelli)
* *Giulia Giamberini* - [GiamberiniGiulia](https://github.com/giamberinigiulia)

Guarda anche la lista dei [contributors](https://github.com/LorenzoCorbinelli/BattagliaNavale/contributors) che hanno partecipato al progetto.
