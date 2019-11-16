/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.OutputStreamWriter;
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
    public Player avversario; //delcaration of new Player's variable
    private final Socket socket; //delcaration of new Socket's variable
    private Scanner input; //delcaration of new Scanner's variable
    private PrintWriter output; //delcaration of new PrintWriter's variable
    public Partita partita; //delcaration of new Partita's variable
    private final ArrayList<Nave> navi; //delcaration of new ArrayList's variable
    public boolean waiting = false;
    
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
            input = new Scanner(socket.getInputStream(), "UTF-8"); //new instance of Scanner was assigned to local variable input
            output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true); //new instance ofPrintWriter was assigned to local variable output
            output.println("DIM " + partita.getDimensioneCampo()); //print a new line in output with the dimensions of the player's court
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
            if(avversario.waiting)
            {
                avversario.output.println("MSG Attendi che l'altro giocatore finisca di piazzare le navi..."); 
            }
        }
    }

    private void inserisciNavi() //inserisciNavi's method
    {
        int i = 0;  //declaration of an integer variable with a fixed value (0)
        
        while (i < 3) //here start a cycle that will continue until i is less than 3
        {
            output.println("STA INS 2");
            output.println("MSG Inserisci la "+(i+1)+"° nave da 2"); //print a new line in output that specify to the player that he/she have to insert the boat
            if(inserisciNave(2)) //check if inserisciNave was successful
            {
                i++; //increment a local variable i
            }
        }
        i=0; //reset the fixed value
        while (i < 2) //here start a cycle that will continue until i is less than 2
        {
            output.println("STA INS 3");
            output.println("MSG Inserisci la "+(i+1)+"° nave da 3"); //print a new line in output that specify to the player that he/she have to insert the boat
            if(inserisciNave(3)) //check if inserisciNave was successful
                i++; //increment a local variable i
        }
        do
        {
            output.println("STA INS 4");
            output.println("MSG Inserisci la nave da 4");
        }while(!inserisciNave(4)); //check if inserisciNave wasn't successful
        
        do
        {
            output.println("STA INS 5");
            output.println("MSG Inserisci la nave da 5"); //print a new line in output that specify to the player that he/she have to insert the boat
        }while(!inserisciNave(5)); //check if inserisciNave wasn't successful
        
        if(this.avversario==null) //check if there isn't another player connectto the server
        {   
            output.println("STA WAT");
            output.println("MSG Attendi che un altro giocatore si connetta..."); //print a new line in output taht specify that theclient havn't an opponent
            waiting = true;
        }
        else if(!avversario.waiting)
        {
            output.println("STA WAT");
            output.println("MSG Attendi che l'altro giocatore finisca di piazzare le navi..."); //print a new line in output taht specify that theclient havn't an opponent
            waiting = true;
        }
        else if(avversario.waiting)
        {
            output.println("STA WAT");
            output.println("MSG Turno dell'avversario"); //print a new line in output taht specify that theclient havn't an opponent
            avversario.attacca();
        }
        else
        {
            attacca(); //Will this ever be called?
        }
    }
    
    private boolean inserisciNave(int len) //inserisciNave' method with parameters (dimension of the boat)
    {
            String[] c = input.nextLine().split(" "); //declaration of a new variable that was initialized with coordinates and direction of the boat 
            System.out.println(Arrays.toString(c)); //print in terminal the array like a string
            int x = Integer.parseInt(c[0]); //declaration of a new variable x for coordinateX that was initialized with the value in c[0] modulation with the dimensions of the player's court
            int y = Integer.parseInt(c[1]);  //declaration of a new variable x for coordinateX that was initialized with the value in c[0] divided by the dimensions of the player's court
            //AGGIUNGERE CONTROLLI
            if(controllaNave(x, y, c[2].charAt(0), len)) //check if the method controlloNave returns true //x,y,direzione,lunghezza
            {
                for(Nave n : navi) 
                {
                    for(Pezzo p : n.pezzi)
                    {
                        output.println("PIE " + p.x + ' ' + p.y);
                    }
                }
                return true;
            }
            else
            {
                output.println("ERR Posizione non valida");
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
                            if(p.x == i && p.y == j)
                                return false;   //TO BE TESTED-- Working :D
                        }
                    }
                }
            }
        }
        
        navi.add(new Nave(compnave));
        return true;
    }

    private void attacca()
    {
            output.println("STA ATT");
            output.println("MSG È il tuo turno");
    }
    
}
