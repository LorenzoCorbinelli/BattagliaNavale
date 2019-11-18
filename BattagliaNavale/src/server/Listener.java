/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

/**
 *
 * @author Ricca
 */
public class Listener implements Runnable
{

    private boolean running;
    private Socket socket;
    private Scanner input;
    private PrintWriter output;
    private final Thread thread;
    public boolean ignoreInput;
    String lastCommand;
    Semaphore commandPresent;
    
    public Listener(Socket serverSocket)
    {
        running = true;
        this.socket = serverSocket;
        commandPresent = new Semaphore(0);
        try
        {
            input = new Scanner(socket.getInputStream(), "UTF-8");
            output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true); 
        } catch (Exception ex)
        {
            System.out.println("Something's wrong here");
        }
        
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
        while(running)
        {
            lastCommand = input.nextLine();
            if(commandPresent.availablePermits() == 0)
            {
                commandPresent.release();
            }
            System.out.println(lastCommand);
        }
    }
    
    public String getLastCommand()  //I'm not 100% confident this works. Indeed
    {
        try {
            commandPresent.acquire();
        } catch (InterruptedException ex) {}
        return lastCommand;
    }
    
}
