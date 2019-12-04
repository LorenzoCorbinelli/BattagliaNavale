
package battaglianavale;
import server.Partita;
import client.*;
import org.apache.commons.cli.*;

public class BattagliaNavale 
{
    static BattagliaNavaleClient client;
    static Partita server;
    public static void main(String[] args) 
    {
        Options options = new Options();
        options.addOption("s","server",false, "Server mode");
        options.addOption("d","dimension",true, "Dimension");
        options.addOption("P","port",true, "Port");
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;
        try
        {
            cmd = parser.parse( options, args);
            
            if(cmd.hasOption("s"))
            {
                int d=21;
                int p=42069;

                if(cmd.hasOption("d"))
                    d=Integer.parseInt(cmd.getOptionValue("d"));
                if(cmd.hasOption("P"))
                    p=Integer.parseInt(cmd.getOptionValue("P"));
                server = new Partita(d,p);
            }
            else
            {
                String ip="";
                int p=42069;

                if(args.length>0)
                    ip=args[0];
                if(args.length>1)
                    p=Integer.parseInt(args[1]);

                client = new BattagliaNavaleClient(ip,p);
            }
        }
        catch(Exception e)
        {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "ant", options );
        }
    }
    
}
