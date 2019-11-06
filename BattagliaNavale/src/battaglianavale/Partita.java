package battaglianavale;

import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Partita
{
    private int mat[][];
    private int dimensione;
    public Player currentPlayer;
    
    public Partita()
    {
        this.mat=new int[][]{
        {0,0,1,1,0},
        {1,0,0,0,0},
        {1,0,0,0,1},
        {1,0,1,0,0},
        {0,0,1,0,0}
        };
        this.dimensione=5;
        
        this.start();
    }
    
    public Partita(int dimensioneGriglia)
    {
        this.dimensione=dimensioneGriglia;
        this.start();
    }
    
    private boolean controllo(int i, int j)
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

    private void start()
    {
        try (ServerSocket listener = new ServerSocket(58901))
        {
            System.out.println("Server is Running...");
            ExecutorService pool = Executors.newFixedThreadPool(200);
            pool.execute(new Player(listener.accept(), this));
            pool.execute(new Player(listener.accept(), this));
        }
        catch (Exception E)
        {}
    }
    int getDimensioneCampo()
    {
        return this.dimensione;
    }
}
