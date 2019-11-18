package server;

import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Partita
{
    private final int dimensione; //declaration of new variable for the dimensions of the player's court
    public boolean inProgress;
    public Player currentPlayer; //declaration of new Player variable that can identify the current player
    
    public Partita() //obsolete constructor 
    {
        this.dimensione = 21; //new value assigned to dimensione's variable
        this.inProgress = true; //Ricordarsi di impostare le variabili potrebbe salvarti diverse ore di lavoro...
        this.start(); //call the start() method
    }
    
    public Partita(int dimensioneGriglia) //constructor with parameters (the dimensions of the player's court)
    {
        this.dimensione = dimensioneGriglia; //parameter dimensions assigned to local variable dimensione
        this.inProgress = true;
        this.start(); //call the start() method
    }

    private void start() //method start 
    {
        try (ServerSocket listener = new ServerSocket(50900)) //try to connect server with client at the port '50900' 
        {
            System.out.println("Server is Running... v0.2"); //print that string (Server is running...)
            ExecutorService pool = Executors.newFixedThreadPool(2);
            while(true) //FOR TESTING PURPOSES
                pool.execute(new Player(listener.accept(), this)); //?????????
        }
        catch (Exception E) //if server doesn't connectto client
        {}
    }
    int getDimensioneCampo() //method getDimensioneCampo
    {
        return this.dimensione; //return the value in dimensione's variable
    }
}
