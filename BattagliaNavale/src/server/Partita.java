package server;

import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Partita
{
    private final int dimensione;
    public boolean inProgress;
    public int porta;
    public ArrayList<Player> Players; //active players
    
    
    public Partita(int dimensioneGriglia, int porta)
    {
        this.dimensione = dimensioneGriglia;
        this.inProgress = true;
        this.porta=porta;
        Players = new ArrayList<>();
        this.start();
    }

    private void start() 
    {
        try (ServerSocket listener = new ServerSocket(porta))
        {
            System.out.println("Welcome to the Ultimate Battleship server! Current version: 1.0\nWaiting for players to connect..."); //print that string (Server is running...)
            ExecutorService pool = Executors.newFixedThreadPool(4);
            for(int i=0;;) //How many games can the server handle before shutting down at once? Infinite!
            {   
                Player opp = findFreePlayer();
                Player np = new Player(listener.accept(),this,opp);
                Future task = pool.submit(np);
                np.task = task;
                if(opp == null)
                    System.out.println("Player 1 joined. Waiting for Player 2...");
                else
                {
                    System.out.println("Player 2 joined. LET THE GAME N°" + i++ + " BEGIN!");
                    opp.avversario = np;
                }
                Players.add(np);
            }
        }
        catch (Exception e)
        {System.out.println(Arrays.toString(e.getStackTrace()));}
    }
    int getDimensioneCampo()
    {
        return this.dimensione;
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
