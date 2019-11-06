package battaglianavale;

public class BattagliaNavale 
{

    public static void main(String[] args) 
    {
        if(args.length == 0)
        {
            System.out.println("<HELP>");
        }
        else if(args[0] == "-server")
        {
            Partita P = new Partita();
        }
        else
        {
            //client
        }
    }
    
}
