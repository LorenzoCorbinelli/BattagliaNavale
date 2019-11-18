/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

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
                case "DIM":
                    client.setup(Integer.parseInt(command[1]));
                    break;
                case "MSG":
                    client.setText(reassCommand(command));
                    break;
                case "PIE":
                    client.drawPiece(Integer.parseInt(command[1]), Integer.parseInt(command[2]));
                    break;
                case "STA":
                    client.setStatus(reassCommand(command));
                    break;
                case "HIT":
                    client.drawHit(Integer.parseInt(command[1]), Integer.parseInt(command[2]));
                    break;
                case "ERR":
                    client.setError(reassCommand(command));
                    try
                    {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {}
                    break;
                case "":
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
