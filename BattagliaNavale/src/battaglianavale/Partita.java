package battaglianavale;

import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Partita
{
    private int mat[][]; //declaration of new matrix variable 
    private int dimensione; //declaration of new variable for the dimensions of the player's court
    public Player currentPlayer; //declaration of new Player variable that can identify the current player
    
    public Partita() //obsolete constructor 
    {
        this.mat=new int[][]{
        {0,0,1,1,0},
        {1,0,0,0,0},
        {1,0,0,0,1},
        {1,0,1,0,0},
        {0,0,1,0,0}
        }; //new istance of matrix, with parameters
        this.dimensione=5; //new value assigned to dimensione's variable
        
        this.start(); //call the start() method
    }
    
    public Partita(int dimensioneGriglia) //constructor with parameters (the dimensions of the player's court)
    {
        this.dimensione=dimensioneGriglia; //parameter dimensions assigned to local variable dimensione
        this.start(); //call the start() method
    }
    
    private boolean controllo(int i, int j) //obsolete method //method controllo with parameters (coordinates x and y)
    {
        //controllo angoli
        if(i==0 && j==0)    //controllare i++ e j++
        {
            
        }else if(i==0 && j==(dimensione-1)) 
        {
            //controllo i++ e j--
        }else if(i==(dimensione-1)&& j==0)
        {
            //controllo i-- e j++
        }else if(i==(dimensione-1)&& j==(dimensione-1))
        {
            //controllo j-- e i--
        }
        //controllo lati
     return false;   
    }

    private void start() //method start 
    {
        try (ServerSocket listener = new ServerSocket(50900)) //try to connect server with client at the port '50900' 
        {
            System.out.println("Server is Running..."); //print that string (Server is running...)
            ExecutorService pool = Executors.newFixedThreadPool(200); //declarationof new ExecutorService's pool with a dimension (200)
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
