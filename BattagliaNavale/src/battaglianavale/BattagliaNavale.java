package battaglianavale;
import client.*;

public class BattagliaNavale 
{

    public static void main(String[] args) 
    {
        if(args.length == 0)
        {
            client.Start.Main();
        }
        else if(args[0] == "-server")
        {
            Partita P = new Partita();
        }
        else
        {
            System.out.println("<HELP>");
        }
    }
    
}
