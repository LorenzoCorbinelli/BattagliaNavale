/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battaglianavale;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 *
 * @author 5BIA
 */
public class Player implements Runnable 
{ 
    Player avversario; //delcaration of new Player's variable
    Socket socket; //delcaration of new Socket's variable
    Scanner input; //delcaration of new Scanner's variable
    PrintWriter output; //delcaration of new PrintWriter's variable
    Partita partita; //delcaration of new Partita's variable
    ArrayList<Nave> navi; //delcaration of new ArrayList's variable
    
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
            output.println(partita.getDimensioneCampo());
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
        int i = 0;  //quantità navi
        
        while (i < 3)
        {
          if(inserisciNave(2))
              i++;
        }
        i=0;
        while (i < 2)
        {
           if(inserisciNave(3))
              i++; 
        }
        while(!inserisciNave(4)){}
        while(!inserisciNave(5)){}
        output.println("Attendi che un altro giocatore si connetta");
    }
    
    private boolean inserisciNave(int len)
    {
          //output.println("INS 2");  //comando al client per inserire navi di lunghezza 2
            String[] c = input.nextLine().split(" ");    //coordinate e direzione ricevute dal client
            System.out.println(Arrays.toString(c));
            int x = Integer.parseInt(c[0])%partita.getDimensioneCampo();
            int y = Integer.parseInt(c[0])/partita.getDimensioneCampo();
            //AGGIUNGERE CONTROLLI
            if(controllaNave(x, y, c[1].charAt(0), len)) //x,y,direzione,lunghezza
            {
                output.println("OK");
                for(Nave n : navi)
                {
                    for(Pezzo p : n.pezzi)
                    {
                        output.println("PIE " + ((partita.getDimensioneCampo() * p.y) + p.x));
                    }
                }
                return true;
            }
            else
            {
                output.println("ERR 0");
            }
        return false;
    }
    
    private boolean controllaNave(int x, int y, char dir, int l)
    {
        if( x < 0 || y < 0)
            return false;   //Out of Bounds, return
        
        ArrayList<Pezzo> compnave = new ArrayList<>();
        
        switch (dir) {
            case 'n':
                for(int i = y; i>y-l; i--)
                {
                    if(i < 0 || i > partita.getDimensioneCampo())   //Out of Bounds, return
                        return false;
                    compnave.add(new Pezzo(x,i));
                }   break;
            case 'e':
                for(int i = x; i<x+l; i++)
                {
                    if(i < 0 || i > partita.getDimensioneCampo())   //Out of Bounds, return
                        return false;
                    compnave.add(new Pezzo(i,y));
                }   break;
            case 's':
                for(int i = y; i<y+l; i++)
                {
                    if(i < 0 || i > partita.getDimensioneCampo())   //Out of Bounds, return
                        return false;
                    compnave.add(new Pezzo(x,i));
                }   break;
            case 'w':
                for(int i = x; i>x-l; i--)
                {
                    if(i < 0 || i > partita.getDimensioneCampo())   //Out of Bounds, return
                        return false;
                    compnave.add(new Pezzo(i,y));
                }   break;
            default:
                return false;
        }

        for (Pezzo c : compnave)
        {
            for (Nave n : navi)
            {
                for (Pezzo p : n.pezzi)
                {
                    for(int i = c.x - 1; i <= c.x + 1; i++)
                    {
                        for(int j = c.y - 1; j <= c.y + 1; j++)
                        {
                            if(p.x == i || p.y == j)
                                return false;   //TO BE TESTED
                        }
                    }
                }
            }
        }
        
        navi.add(new Nave(compnave));
        return true;
    }
    
}
