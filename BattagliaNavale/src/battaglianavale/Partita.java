package battaglianavale;

public class Partita implements Runnable
{
    private int mat[][];
    private int dimensione;
    
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
        
    }
}
