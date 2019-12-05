package client;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import javax.swing.JOptionPane;

public class messageListener implements Runnable
{

    private boolean running;
    private Socket socket;
    private Scanner input;
    private PrintWriter output;
    private BattagliaNavaleClient client;
    private final Thread thread;
    private final String serverAddress;
    private final int port;
    
    public messageListener(String serverAddress, BattagliaNavaleClient c, int port)
    {
        client = c;
        running = true;
        this.serverAddress = serverAddress;
        this.port = port;
        
        try
        {
            setup();
        } catch (IOException ex)
        {}
        
        thread = new Thread(this, "listener");
        thread.start();
    }

    public void setRunning(boolean running)
    {
        this.running = running;
    }

    public boolean isRunning()
    {
        return running;
    }
    
    public void send(String comm)
    {
        output.println(comm);
    }
    
    @Override
    public void run()
    {
        String[] command;
        while(running && input.hasNextLine())
        {
            command = input.nextLine().split(" ");
            System.out.println(Arrays.toString(command));
            switch(command[0])
            {
                case "STP": //Setup
                    String [] s;
                    String gdim;
                    ArrayList<String> dim = new ArrayList<>();
                    gdim = input.nextLine();
                    System.out.println(gdim);
                    int griglia = Integer.parseInt(gdim.split(" ")[1]);
                    String com = input.nextLine();
                    while(!com.equals("END"))
                    {
                        s = com.split(" ");
                        System.out.println(com);
                        dim.add(s[1]);
                        com=input.nextLine();
                    }
                    client.setup(griglia,dim);
                    break;
                case "MSG": //Message
                    client.setText(reassCommand(command));
                    break;
                case "PIE": //Piece
                    client.drawPiece(Integer.parseInt(command[1]), Integer.parseInt(command[2]));
                    break;
                case "HIT": //Hit
                    client.drawHit(Integer.parseInt(command[1]), Integer.parseInt(command[2]));
                    break;
                case "WAT": //Water
                    client.drawWater(Integer.parseInt(command[1]), Integer.parseInt(command[2]));
                    break;
                case "THY": //They Hit You
                    client.drawOppHit(Integer.parseInt(command[1]), Integer.parseInt(command[2]));
                    break;
                case "THW": //They Hit Water
                    client.drawOppHitWater(Integer.parseInt(command[1]), Integer.parseInt(command[2]));
                    break;
                case "STA": //Status
                    client.setStatus(reassCommand(command));
                    break;
                case "ERR":
                    client.setError(reassCommand(command));
                    try
                    {Thread.sleep(1000);}
                    catch (InterruptedException ex) {}
                    break;
                case "WIN":
                    client.setText("Ammettilo, hai speso delle ore a pianifacare l'attacco. (Hai vinto)");
                    break;
                case "LOS":
                    client.setText("Non c'è vento favorevole al marinaio che non sa dove andare! (Hai perso)");
                    break;
                case "SNH": //Ships Not Hit
                    client.drawNotHit(Integer.parseInt(command[1]), Integer.parseInt(command[2]));
                    break;
                case "BRD": //Board
                    String comm = input.nextLine();
                    ArrayList<String> brd = new ArrayList<>();
                    while(!comm.equals("END"))
                    {
                        System.out.println(comm);
                        brd.add(comm);
                        comm = input.nextLine();
                    }
                    client.drawBoard(brd);
                    break;
                case "OKI": //OK Insert
                    client.setSelectedShip();
                    break;
                case "SND":
                    switch(command[1])  //TODO: Add checks
                    {
                        case "EXP": //Explosion
                            new SoundFX(getClass().getResourceAsStream("/resources/sounds/HitSound.wav"));
                        break;
                        case "SPL": //Splash
                            new SoundFX(getClass().getResourceAsStream("/resources/sounds/WaterSound.wav"));
                        break;
                        case "WIN":
                            new SoundFX(getClass().getResourceAsStream("/resources/sounds/VictorySoundEffect.wav"));
                        break;
                        case "LOS":
                            new SoundFX(getClass().getResourceAsStream("/resources/sounds/LoseSound.wav"));
                        break;
                    }
                    break;
                case "DIS":
                    try {
                        socket.close();
                    } catch (IOException ex) {}
                    client.setError("L'avversario si è disconnesso");
                    client.setStatus("WAT");
                    Object[] options = {"Sì", "No"};
                    if (JOptionPane.showOptionDialog(client.frame, "Il tuo avversario si è disconnesso.\nVuoi avviare un'altra partita?","Avviso",JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0])
                        == JOptionPane.YES_OPTION)
                    {
                        client.reset();
                        try
                        {
                            setup();
                        }
                        catch(IOException e){System.out.println(e);}
                    }
                    break;
            }
        }
    }
    
    private String reassCommand (String[] c)
    {
        String ret = "";
        for(String sc : c)
        {
            ret += sc + " ";
        }
        ret = ret.substring(4, ret.length() - 1);
        return ret;
    }

    private void setup() throws IOException
    {
        socket = new Socket(serverAddress, port);
        input = new Scanner(socket.getInputStream(), "UTF-8");
        output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true); 
    }
    
}
