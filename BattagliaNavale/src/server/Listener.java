/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

/**
 *
 * @author Ricca
 */
public class Listener implements Runnable   //This is quite a mess, but it seems to be working
{

    private boolean running;
    private Socket socket;
    private Scanner input;
    private PrintWriter output;
    private final Thread thread;
    private final Player player;
    public boolean requiredInput;
    String lastCommand;
    Semaphore commandPresent;
    
    public Listener(Socket serverSocket, Player p)
    {
        player = p;
        running = true;
        this.socket = serverSocket;
        commandPresent = new Semaphore(0);
        try
        {
            input = new Scanner(socket.getInputStream(), "UTF-8");
            output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true); 
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
        String command;
        while(running)
        {
            try
            {
                command = input.nextLine();
                if(requiredInput)
                {
                    requiredInput = false;
                    lastCommand = command;
                    commandPresent.release();
                }
                System.out.println(lastCommand);
            }
            catch(java.util.NoSuchElementException ex)
            {
                player.avversario.listener.send("DIS");
            }
        }
    }
    
    public String getCommand()
    {
        requiredInput = true;
        try {
            commandPresent.acquire();
        } catch (InterruptedException ex) {}
        String ret = lastCommand;
        lastCommand = null;
        return ret;
    }
}
