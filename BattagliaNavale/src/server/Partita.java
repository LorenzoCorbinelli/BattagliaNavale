package server;

import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Partita
{
    private final int dimensione; //declaration of new variable for the dimensions of the player's court
    public boolean inProgress;
    public ArrayList<Player> Players; //declaration of new Player variable that can identify the current player
    
    public Partita()
    {
        this.dimensione = 21; //new value assigned to dimensione's variable
        this.inProgress = true; //Ricordarsi di impostare le variabili potrebbe salvarti diverse ore di lavoro...
        Players = new ArrayList<>();
        this.start(); //call the start() method
    }
    
    public Partita(int dimensioneGriglia) //constructor with parameters (the dimensions of the player's court)
    {
        this.dimensione = dimensioneGriglia; //parameter dimensions assigned to local variable dimensione
        this.inProgress = true;
        Players = new ArrayList<>();
        this.start(); //call the start() method
    }

    private void start() //method start 
    {
        try (ServerSocket listener = new ServerSocket(42069)) //try to connect server with client at the port '42069' 
        {
            System.out.println("Welcome to the Ultimate Battleship server! Current version: 1.0\nWaiting for players to connect..."); //print that string (Server is running...)
            ExecutorService pool = Executors.newFixedThreadPool(4);
            while(true) //How many games can the server handle before shutting down at once? Infinite!
            {   
                Player opp = findFreePlayer();
                Player np = new Player(listener.accept(),this,opp);
                Future task = pool.submit(np);
                np.task = task;
                if(opp == null)
                    System.out.println("Player 1 joined. Waiting for Player 2...");
                else
                {
                    System.out.println("Player 2 joined. LET THE GAME BEGIN!");
                    opp.avversario = np;
                }
                Players.add(np);
            }
        }
        catch (Exception e) //if server doesn't connect to client
        {System.out.println(Arrays.toString(e.getStackTrace()));}
    }
    int getDimensioneCampo() //method getDimensioneCampo
    {
        return this.dimensione; //return the value in dimensione's variable
    }
    
    private Player findFreePlayer()
    {
        for(Player p : Players)
        {
            if(p.avversario == null)
                return p;
        }
        return null;
    }
}
