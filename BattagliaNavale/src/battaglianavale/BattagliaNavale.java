
package battaglianavale;
import server.BattagliaNavaleServer;
import client.*;
import org.apache.commons.cli.*;

public class BattagliaNavale 
{
    static BattagliaNavaleClient client;
    static BattagliaNavaleServer server;
    public static void main(String[] args) 
    {
        Options options = new Options();
        options.addOption("s","server",false, "Launch an instance of the server");
        options.addOption("d","dimensions",true, "Defines the dimension of the grid. SERVER ONLY");
        options.addOption("P","port",true, "Defines on what port the server should be listening. SERVER ONLY");
        options.addOption("m","max-matches",true, "Defines the number of matches the server can handle at once. SERVER ONLY");
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;
        try
        {
            cmd = parser.parse( options, args);
            
            if(cmd.hasOption("s"))
            {
                int d=21;
                int p=42069;
                int m=1;

                if(cmd.hasOption("d"))
                    d=Integer.parseInt(cmd.getOptionValue("d"));
                if(cmd.hasOption("P"))
                    p=Integer.parseInt(cmd.getOptionValue("P"));
                if(cmd.hasOption("m"))
                    m=Integer.parseInt(cmd.getOptionValue("m"));
                server = new BattagliaNavaleServer(d,p);
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
        catch(NumberFormatException | ParseException e)
        {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "java -jar BattagliaNavale.jar [SERVER_IP] [SERVER_PORT] [OPTIONS]\nNote: [SERVER_IP] and [SERVER_PORT] are client-only\n ", options);
        }
    }
    
}
