package battaglianavale;
import client.*;

public class BattagliaNavale 
{

    public static void main(String[] args) 
    {
        if(args.length == 0)
        {
            client.Start.setup();
        }
        else if(args[0] == "-server")
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
