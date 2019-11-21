/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Semaphore;

/**
 *
 * @author 5BIA
 */
public class Player implements Runnable 
{ 
    public Player avversario; //delcaration of new Player's variable
    public Partita partita; //delcaration of new Partita's variable
    private final ArrayList<Nave> navi; //delcaration of new ArrayList's variable
    private final ArrayList<Position> moves;
    public boolean waiting = false;
    public Semaphore yourTurn;
    public Semaphore opponentTurn;
    private final Listener listener;
    
    public Player(Socket s, Partita p) //constructor with parameters (a socket and a Partita)
    {
        this.partita = p; //variable p assigned to local variable partita
        this.navi = new ArrayList<>();  //new instance of ArrayList assigned to local variable navi
        this.moves = new ArrayList<>();
        this.listener = new Listener(s);
    }
    
    @Override
    public void run() //run's method
    {
        Setup(); //call the setup's method
        inserisciNavi(); //call the inserisciNavi's method
    }
    
    private void Setup() //Setup's method
    {
        listener.send("DIM " + partita.getDimensioneCampo()); //print a new line in output with the dimensions of the player's court
        
        if(partita.currentPlayer == null) //check if the local variable currentPlayer was null
        {
            partita.currentPlayer = this; //this object was assigned to local variable currentPlayer
            yourTurn = new Semaphore(0);
            opponentTurn = new Semaphore(0);
        }
        else //if the local variable currentPlayer wasn't null
        {
            partita.currentPlayer.avversario = this; //this object was assigned to other variable currentPlayer like avversario
            this.avversario = partita.currentPlayer; //other object was assigned to local variable currentPlayer like avversario
            yourTurn = avversario.opponentTurn;
            opponentTurn = avversario.yourTurn;
            if(avversario.waiting)
            {
                avversario.listener.send("MSG Attendi che l'altro giocatore finisca di piazzare le navi..."); 
            }
        }
    }

    private void inserisciNavi() //inserisciNavi's method
    {
        int i = 0;  //declaration of an integer variable with a fixed value (0)
        
        while (i < 3) //here start a cycle that will continue until i is less than 3
        {
            listener.send("STA INS 2");
            listener.send("MSG Inserisci la "+(i+1)+"° nave da 2"); //print a new line in output that specify to the player that he/she have to insert the boat
            if(inserisciNave(2)) //check if inserisciNave was successful
            {
                i++; //increment a local variable i
            }
        }
        i=0; //reset the fixed value
        while (i < 2) //here start a cycle that will continue until i is less than 2
        {
            listener.send("STA INS 3");
            listener.send("MSG Inserisci la "+(i+1)+"° nave da 3"); //print a new line in output that specify to the player that he/she have to insert the boat
            if(inserisciNave(3)) //check if inserisciNave was successful
                i++; //increment a local variable i
        }
        do
        {
            listener.send("STA INS 4");
            listener.send("MSG Inserisci la nave da 4");
        }while(!inserisciNave(4)); //check if inserisciNave wasn't successful
        
        do
        {
            listener.send("STA INS 5");
            listener.send("MSG Inserisci la nave da 5"); //print a new line in output that specify to the player that he/she have to insert the boat
        }while(!inserisciNave(5)); //check if inserisciNave wasn't successful
        
        if(this.avversario==null) //check if there isn't another player connectto the server
        {   
            listener.send("STA WAT");
            listener.send("MSG Attendi che un altro giocatore si connetta..."); //print a new line in output taht specify that theclient havn't an opponent
            waiting = true;
        }
        else if(!avversario.waiting)
        {
            listener.send("STA WAT");
            listener.send("MSG Attendi che l'altro giocatore finisca di piazzare le navi..."); //print a new line in output taht specify that theclient havn't an opponent
            waiting = true;
        }
        else if(avversario.waiting)
        {
            listener.send("STA WAT");
            listener.send("MSG Turno dell'avversario"); //print a new line in output taht specify that theclient havn't an opponent
            opponentTurn.release();
        }
        attacca();
    }
    
    private boolean inserisciNave(int len) //inserisciNave' method with parameters (dimension of the boat)
    {
            String[] c = listener.getLastCommand().split(" "); //declaration of a new variable that was initialized with coordinates and direction of the boat 
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
                        listener.send("PIE " + p.x + ' ' + p.y);
                    }
                }
                return true;
            }
            else
            {
                listener.send("ERR Posizione non valida");
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
        int x=0,y=0;
        boolean invalidPos, hit;

        do
        {
            try {
                yourTurn.acquire();
            } catch (InterruptedException ex) {System.out.println("Interrotto!");}
            
            if(partita.inProgress)
            {
                do
                {
                    hit = false;

                    do
                    {
                        listener.send("STA ATT");
                        listener.send("MSG È il tuo turno");
                        String[] c = listener.getLastCommand().split(" "); //declaration of a new variable that was initialized with coordinates and direction of the boat 
                        System.out.println(Arrays.toString(c)); //print in terminal the array like a string
                        x = Integer.parseInt(c[0]); //declaration of a new variable x for coordinateX that was initialized with the value in c[0] modulation with the dimensions of the player's court
                        y = Integer.parseInt(c[1]);  //declaration of a new variable x for coordinateX that was initialized with the value in c[0] divided by the dimensions of the player's court

                        invalidPos = moves.contains(new Position(x,y));
                        if(invalidPos)
                            listener.send("ERR Hai già attaccato in questo punto");
                    }while (invalidPos);

                    //AGGIUNGERE CONTROLLI
                    listener.send("BRD");
                    avversario.listener.send("BRD");
                    for (Nave n : avversario.navi)
                    {
                        if(!n.affondata)
                        {
                            for (Pezzo p : n.pezzi)
                            {
                                if(p.x == x && p.y == y)
                                {
                                    p.colpito = true;
                                    hit = true;
                                }
                            }

                            if(n.checkAffondata())
                            {
                                for(Pezzo p: n.pezzi)
                                {
                                    for(int i = p.x - 1; i <= p.x + 1; i++)
                                    {
                                        for(int j = p.y - 1; j <= p.y + 1; j++)
                                        {
                                            if(i >= 0 && i < partita.getDimensioneCampo() && j >= 0 && j < partita.getDimensioneCampo())
                                            {
                                                listener.send("WAT " + i + " " + j);
                                                avversario.listener.send("THW " + i + " " + j);
                                                moves.add(new Position(i,j));
                                            }
                                        }
                                    }
                                }
                                for(Pezzo p: n.pezzi)
                                {
                                    if(p.colpito)
                                    {
                                        listener.send("HIT " + p.x + " " + p.y);
                                        avversario.listener.send("THY " + p.x + " " + p.y);
                                    }
                                }
                            }
                            
                            
                            
                            if(hit)
                            {
                                listener.send("HIT " + x + " " + y);
                                avversario.listener.send("THY " + x + " " + y);
                                break;
                            }
                        }
                    }



                    moves.add(new Position(x,y));
                    listener.send("END");
                    avversario.listener.send("END");
                    
                    checkWin();
                }while(hit && partita.inProgress);
            }
            
            if(partita.inProgress)
            {
                listener.send("WAT " + x + " " + y);
                avversario.listener.send("THW " + x + " " + y);

                listener.send("STA WAT");
                listener.send("MSG È il turno dell'avversario");

                opponentTurn.release();
            }
            else
            {
                listener.send("END");
                avversario.listener.send("END");
            }
        }while(partita.inProgress);
        System.out.println("Aight, Imma head out");
    }

    private synchronized void checkWin()
    {
        for(Nave n : avversario.navi)
        {
            if (!n.affondata)
                return;
        }
        win();
        partita.inProgress=false;
    }

    private void win() 
    {
        listener.send("WIN");
        avversario.listener.send("LOS");
    }
    
}
