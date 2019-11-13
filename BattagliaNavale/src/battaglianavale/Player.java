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
    
    public Player(Socket s, Partita p) //constructor with parameters (a socket and a Partita)
    {
        this.socket = s; //variable s assigned to local variable socket
        this.partita = p; //variable p assigned to local variable partita
        this.navi = new ArrayList<>();  //new instance of ArrayList assigned to local variable navi
    }
    
    @Override
    public void run() //run's method
    {
        Setup(); //call the setup's method
        inserisciNavi(); //call the inserisciNavi's method
    }
    
    private void Setup() //Setup's method
    {
        try
        {
            input = new Scanner(socket.getInputStream()); //new instance of Scanner was assigned to local variable input
            output = new PrintWriter(socket.getOutputStream(), true); //new instance ofPrintWriter was assigned to local variable output
            output.println(partita.getDimensioneCampo()); //print a new line in output with the dimensions of the player's court
        }
        catch(Exception E)
        {
            return;
        }
        
        if(partita.currentPlayer == null) //check if the local variable currentPlayer was null
        {
            partita.currentPlayer = this; //this object was assigned to local variable currentPlayer
            
        }
        else //if the local variable currentPlayer wasn't null
        {
            partita.currentPlayer.avversario = this; //this object was assigned to other variable currentPlayer like avversario
            this.avversario = partita.currentPlayer; //other object was assigned to local variable currentPlayer like avversario
        }
    }

    private void inserisciNavi() //inserisciNavi's method
    {
        int i = 0;  //declaration of an integer variable with a fixed value (0)
        
        while (i < 3) //here start a cycle that will continue until i is less than 3
        {
            output.println("Inserisci la "+(i+1)+"° nave da 2"); //print a new line in output that specify to the player that he/she have to insert the boat
            if(inserisciNave(2)) //check if inserisciNave was successful
            {
                i++; //increment a local variable i
            }
        }
        i=0; //reset the fixed value
        while (i < 2) //here start a cycle that will continue until i is less than 2
        {
           output.println("Inserisci la "+(i+1)+"° nave da 3"); //print a new line in output that specify to the player that he/she have to insert the boat
           if(inserisciNave(3)) //check if inserisciNave was successful
              i++; //increment a local variable i
        }
        output.println("Inserisci la nave da 4"); //print a new line in output that specify to the player that he/she have to insert the boat
        while(!inserisciNave(4)) //check if inserisciNave wasn't successful
        {
            output.println("Inserisci la nave da 4"); //print a new line in output that specify to the player that he/she have to insert the boat
        }
        output.println("Inserisci la nave da 5"); //print a new line in output that specify to the player that he/she have to insert the boat
        while(!inserisciNave(5)) //check if inserisciNave wasn't successful
        {
            output.println("Inserisci la nave da 5"); //print a new line in output that specify to the player that he/she have to insert the boat
        }
        if(this.avversario==null) //check if there isn't another player connectto the server
            output.println("Attendi che un altro giocatore si connetta..."); //print a new line in output taht specify that theclient havn't an opponent
    }
    
    private boolean inserisciNave(int len) //inserisciNave' method with parameters (dimension of the boat)
    {
          //output.println("INS 2");  //command to the client to insert the two-pieces boat 
            String[] c = input.nextLine().split(" "); //declaration of a new variable that was initialized with coordinates and direction of the boat 
            System.out.println(Arrays.toString(c)); //print in terminal the array like a string
            int x = Integer.parseInt(c[0])%partita.getDimensioneCampo(); //declaration of a new variable x for coordinateX that was initialized with the value in c[0] modulation with the dimensions of the player's court
            int y = Integer.parseInt(c[0])/partita.getDimensioneCampo();  //declaration of a new variable x for coordinateX that was initialized with the value in c[0] divided by the dimensions of the player's court
            //AGGIUNGERE CONTROLLI
            if(controllaNave(x, y, c[1].charAt(0), len)) //check if the method controlloNave returns true //x,y,direzione,lunghezza
            {
                output.println("OK"); //print in output a new line 'OK'
                for(Nave n : navi) 
                {
                    for(Pezzo p : n.pezzi)
                    {
                        output.println("PIE " + ((partita.getDimensioneCampo() * p.y) + p.x));
                    }
                }
                output.println("END");
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
