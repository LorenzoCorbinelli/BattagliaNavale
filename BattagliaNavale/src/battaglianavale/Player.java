/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battaglianavale;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author 5BIA
 */
public class Player implements Runnable 
{ 
    Player avversario;
    Socket socket;
    Scanner input;
    PrintWriter output;
    Partita partita;
    ArrayList<Nave> navi;
    
    public Player(Socket s, Partita p)
    {
        this.socket = s;
        this.partita = p;
        this.navi = new ArrayList<>();
    }
    
    @Override
    public void run() 
    {
        Setup();
        inserisciNavi();
    }
    
    private void Setup()
    {
        try
        {
            input = new Scanner(socket.getInputStream());
            output = new PrintWriter(socket.getOutputStream(), true);
        }
        catch(Exception E)
        {
            return;
        }
        
        if(partita.currentPlayer == null)
        {
            partita.currentPlayer = this;
        }
        else
        {
            partita.currentPlayer.avversario = this;
            this.avversario = partita.currentPlayer;
        }
    }

    private void inserisciNavi()
    {
        int i = 0;
        while (i < 3)
        {
            output.println("???");
            String c = input.nextLine();
            if(inserisciNave(0,0,'n',0))
                i++;
            else
                System.out.println("ErRoRe");
        }
    }
    
    private boolean inserisciNave(int x, int y, char dir, int l)
    {
        
        return false;
    }
    
}
