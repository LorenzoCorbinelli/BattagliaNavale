package battaglianavale;
import server.Partita;
import client.*;

public class BattagliaNavale 
{

    public static void main(String[] args) 
    {
        if(args.length == 0)    //client
        {
            try
            {
                new BattagliaNavaleClient("127.0.0.1"); //connessione del client al server sullo stesso host 
            }catch(Exception e){}
        }
        else if(args[0].equals("-server")) //server
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
