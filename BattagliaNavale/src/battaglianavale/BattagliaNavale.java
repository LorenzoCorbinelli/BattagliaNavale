package battaglianavale;
import client.*;

public class BattagliaNavale 
{

    public static void main(String[] args) 
    {
        if(args.length == 0)    //client
        {
            try
            {
                Start s = new Start("127.0.0.1");
            }catch(Exception e){}
        }
        else if(args[0].equals("-server"))
        {
            if (args.length > 1)
                new Partita(Integer.parseInt(args[1]));
            else
                new Partita();
        }
        else
        {
            System.out.println("<HELP>");
        }
    }
    
}
