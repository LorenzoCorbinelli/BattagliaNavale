
package battaglianavale;
import server.Partita;
import client.*;

public class BattagliaNavale 
{
    static BattagliaNavaleClient client;
    static Partita server;
    public static void main(String[] args) 
    {
        if(args.length == 0)    //client
        {
            try
            {
                client = new BattagliaNavaleClient("172.16.10.49"); //connessione del client al server sullo stesso host 
            }catch(Exception e){}
        }
        else if(args[0].equals("-server")) //server
        {
            if (args.length > 1) 
                server = new Partita(Integer.parseInt(args[1]));
            else
                server = new Partita();
        }
        else
        {
            System.out.println("<HELP>");
        }
    }
    
}
