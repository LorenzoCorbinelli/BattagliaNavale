package server;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

public class Player implements Runnable 
{ 
    public Player opponent;
    public BattagliaNavaleServer server;
    private final ArrayList<Ship> ships;
    private final ArrayList<Position> moves;
    public boolean waiting;
    public Semaphore yourTurn;
    public Semaphore opponentTurn;
    public final Semaphore rematchAnswered;
    public final Listener listener;
    public Future task;
    public AtomicBoolean matchInProgress;     //Needs to be an object so it can be shared with the opponent
    public boolean wantsRematch;
    public boolean won;
    
    public Player(Socket s, BattagliaNavaleServer bns, Player opponent)
    {
        waiting = false;
        won = false;
        this.server = bns;
        this.ships = new ArrayList<>();
        this.moves = new ArrayList<>();
        this.listener = new Listener(s, this);
        this.opponent = opponent;
        this.rematchAnswered = new Semaphore(0);
    }
    
    @Override
    public void run()
    {
        Setup();
        inserisciNavi();
    }
    
    private void Setup()
    {
        listener.send("STP"); //setup
        listener.send("DIM " + server.getDimensioneCampo());   //Grid size
        elencoNavi();
        if(opponent == null)
        {
            yourTurn = new Semaphore(0);
            opponentTurn = new Semaphore(0);
            matchInProgress = new AtomicBoolean(true);
        }
        else
        {
            yourTurn = opponent.opponentTurn;
            opponentTurn = opponent.yourTurn;
            matchInProgress = opponent.matchInProgress;
            if(opponent.waiting)
            {
                opponent.listener.send("MSG Il tuo avversario si è collegato, attendi che finisca di piazzare le navi..."); 
            }
        }
    }
    
    private void elencoNavi()
    {
        int i = 0;
        
        while(i < 3)
        {
            listener.send("SHL 2");
            i++;
        }
        i = 0;
        while(i < 2)
        {
            listener.send("SHL 3");
            i++;
        }
        listener.send("SHL 4");
        listener.send("SHL 5");
        listener.send("END");
    }
    
    private void inserisciNavi()
    {
        int i = 0;
        
        while (i < 3)
        {
            listener.send("STA INS 2");
            listener.send("MSG Inserisci la "+(i+1)+"° nave da 2");
            if(inserisciNave(2))
            {
                i++;
            }
        }
        i=0;
        while (i < 2)
        {
            listener.send("STA INS 3");
            listener.send("MSG Inserisci la "+(i+1)+"° nave da 3");
            if(inserisciNave(3))
                i++;
        }
        do
        {
            listener.send("STA INS 4");
            listener.send("MSG Inserisci la nave da 4");
        }while(!inserisciNave(4));
        
        do
        {
            listener.send("STA INS 5");
            listener.send("MSG Inserisci la nave da 5");
        }while(!inserisciNave(5));
        
        if(this.opponent==null)
        {   
            listener.send("STA WAT");
            listener.send("MSG Attendi che l'avversario si connetta...");
            waiting = true;
        }
        else if(!opponent.waiting)
        {
            listener.send("STA WAT");
            listener.send("MSG Attendi che l'avversario finisca di piazzare le navi...");
            waiting = true;
        }
        else if(opponent.waiting)
        {
            listener.send("STA WAT");
            listener.send("MSG Turno dell'avversario");
            opponentTurn.release();
        }
        attacca();
    }
    
    private boolean inserisciNave(int len)
    {
            String[] c = listener.getCommand().split(" ");
            System.out.println(Arrays.toString(c));
            boolean check;
            try
            {
                int x = Integer.parseInt(c[0]);
                int y = Integer.parseInt(c[1]);
                check = controllaNave(x, y, c[2].charAt(0), len);//x,y,direzione,lunghezza
            }
            catch (Exception e)
            {
                listener.send("ERR Malformed command!");
                return false;
            }
            
            if(check) //start assembling the ship
            {
                for(Ship n : ships) 
                {
                    for(Piece p : n.pieces)
                    {
                        listener.send("PIE " + p.x + ' ' + p.y);
                    }
                }
                listener.send("OKI");
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
        
        ArrayList<Piece> compnave = new ArrayList<>();
        
        switch (dir) {
            case 'n':
                for(int i = y; i>y-l; i--)
                {
                    if(i < 0 || i >= server.getDimensioneCampo())   //Out of Bounds, return
                        return false;
                    compnave.add(new Piece(x,i));
                }   break;
            case 'e':
                for(int i = x; i<x+l; i++)
                {
                    if(i < 0 || i >= server.getDimensioneCampo())   //Out of Bounds, return
                        return false;
                    compnave.add(new Piece(i,y));
                }   break;
            case 's':
                for(int i = y; i<y+l; i++)
                {
                    if(i < 0 || i >= server.getDimensioneCampo())   //Out of Bounds, return
                        return false;
                    compnave.add(new Piece(x,i));
                }   break;
            case 'w':
                for(int i = x; i>x-l; i--)
                {
                    if(i < 0 || i >= server.getDimensioneCampo())   //Out of Bounds, return
                        return false;
                    compnave.add(new Piece(i,y));
                }   break;
            default:
                return false;
        }

        for (Piece c : compnave)
        {
            for (Ship n : ships)
            {
                for (Piece p : n.pieces)
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
        
        ships.add(new Ship(compnave));
        return true;
    }

    private void attacca()
    {
        int x=0,y=0;
        boolean invalidPos, hit;

        do
        {
            try
            {
                yourTurn.acquire();
            } 
            catch (InterruptedException ex)
            {   //Player has disconnected, quit
                Thread.currentThread().interrupt();
                return;
            }
            if(matchInProgress.get())
            {
                do
                {
                    hit = false;
                    do
                    {
                        listener.send("STA ATT");
                        listener.send("MSG È il tuo turno");
                        String[] c = listener.getCommand().split(" ");
                        System.out.println(Arrays.toString(c));
                        
                        try
                        {
                            x = Integer.parseInt(c[0]);
                            y = Integer.parseInt(c[1]);
                            invalidPos = moves.contains(new Position(x,y));
                            if(invalidPos)
                                listener.send("ERR Hai già attaccato in questo punto");
                        }catch(Exception e)
                        {
                            listener.send("ERR Malformed command!");
                            invalidPos = true;
                        }
                    }while (invalidPos);
                    
                    //AGGIUNGERE CONTROLLI
                    listener.send("BRD");
                    opponent.listener.send("BRD");
                    for (Ship n : opponent.ships)
                    {
                        if(!n.sunk)
                        {
                            for (Piece p : n.pieces)
                            {
                                if(p.x == x && p.y == y)
                                {
                                    p.hit = true;
                                    hit = true;
                                }
                            }

                            if(n.checkAffondata())
                            {
                                for(Piece p: n.pieces)
                                {
                                    for(int i = p.x - 1; i <= p.x + 1; i++)
                                    {
                                        for(int j = p.y - 1; j <= p.y + 1; j++)
                                        {
                                            if(i >= 0 && i < server.getDimensioneCampo() && j >= 0 && j < server.getDimensioneCampo())
                                            {
                                                listener.send("WAT " + i + " " + j);
                                                opponent.listener.send("THW " + i + " " + j);
                                                moves.add(new Position(i,j));
                                            }
                                        }
                                    }
                                }
                                for(Piece p: n.pieces)
                                {
                                    if(p.hit)
                                    {
                                        listener.send("HIT " + p.x + " " + p.y);
                                        opponent.listener.send("THY " + p.x + " " + p.y);
                                    }
                                }
                            }
                            
                            
                            
                            if(hit)
                            {
                                listener.send("HIT " + x + " " + y);
                                opponent.listener.send("THY " + x + " " + y);
                                break;
                            }
                        }
                    }

                    moves.add(new Position(x,y));
                    listener.send("END");
                    opponent.listener.send("END");
                    
                    if(hit)
                    {
                        listener.send("SND EXP");
                    }
                    
                    won = checkWin();
                }while(hit && matchInProgress.get());
            }
            
            if(matchInProgress.get())
            {
                listener.send("WAT " + x + " " + y);
                listener.send("SND SPL");
                opponent.listener.send("THW " + x + " " + y);

                listener.send("STA WAT");
                listener.send("MSG È il turno dell'avversario");
                
                opponentTurn.release();
            }
            else
            {
                listener.send("END");
                opponent.listener.send("END");
                if(won)
                    win();
                else
                    lost();
            }
        }while(matchInProgress.get());
        System.out.println("Aight, Imma head out");
    }

    private boolean checkWin()
    {
        for(Ship n : opponent.ships)
        {
            if (!n.sunk)
               return false;
        }
        matchInProgress.set(false);
        return true;
    }
    
    private void NotHit()   //ships not hit
    {
        for(Ship n : opponent.ships)
        {
            for(Piece P : n.pieces)
            {
                if(!P.hit)
                    listener.send("SNH " + P.x + " " + P.y);
            }
        }
    }
    
    private void win() 
    {
        opponentTurn.release();
        listener.send("WIN");
        listener.send("SND WIN");
        rematch();
    }
    
    private void lost()
    {
        NotHit();
        listener.send("LOS");
        listener.send("SND LOS");
        rematch();
    }
    
    private void rematch()
    {
        if(listener.getCommand().equals("REM"))
        {
            wantsRematch = true;
            rematchAnswered.release();
            try {
                opponent.rematchAnswered.acquire();
            } catch (InterruptedException ex)
            {}
            if(opponent.wantsRematch)
            {
                waiting = false;
                ships.clear();
                moves.clear();
                matchInProgress.set(true);
                run();  //restart
            }
        }
        else
        {
            rematchAnswered.release();
            task.cancel(true);
        }
    }

    void kill()
    {
        System.out.println("Am ded");
        task.cancel(true);
    }
}
