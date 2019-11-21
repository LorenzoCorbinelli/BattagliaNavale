/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 *
 * @author Ricca
 */
public class messageListener implements Runnable
{

    private boolean running;
    private Socket socket;
    private Scanner input;
    private PrintWriter output;
    private BattagliaNavaleClient client;
    private final Thread thread;
    
    public messageListener(String serverAddress, BattagliaNavaleClient c)
    {
        client = c;
        running = true;
        
        try
        {
            socket = new Socket(serverAddress, 50900);
            input = new Scanner(socket.getInputStream(), "UTF-8");
            output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true); 
        } catch (Exception ex)
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
                case "DIM": //Dimensions
                    client.setup(Integer.parseInt(command[1]));
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
                    client.setText("Hai vinto!");
                    break;
                case "LOS":
                    client.setText("Oh no! Hai perso!");
                    break;
                case "STP":
                    client.stopUpdate();
                    break;
                case "GO":
                    client.resetUpdate();
                    break;
                case "BRD":
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
    
}
