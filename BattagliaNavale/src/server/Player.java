package server;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;

public class Player implements Runnable 
{ 
    public Player avversario;
    public Partita partita;
    private final ArrayList<Nave> navi;
    private final ArrayList<Position> moves;
    public boolean waiting = false;
    public Semaphore yourTurn;
    public Semaphore opponentTurn;
    public final Listener listener;
    public Future task;
    
    public Player(Socket s, Partita p, Player avversario)
    {
        this.partita = p;
        this.navi = new ArrayList<>();
        this.moves = new ArrayList<>();
        this.listener = new Listener(s, this);
        this.avversario = avversario;
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
        listener.send("DIM " + partita.getDimensioneCampo());   //Grid size
        elencoNavi();
        if(avversario == null)
        {
            yourTurn = new Semaphore(0);
            opponentTurn = new Semaphore(0);
        }
        else
        {
            yourTurn = avversario.opponentTurn;
            opponentTurn = avversario.yourTurn;
            if(avversario.waiting)
            {
                avversario.listener.send("MSG Il tuo avversario si è collegato, attendi che finisca di piazzare le navi..."); 
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
        
        if(this.avversario==null)
        {   
            listener.send("STA WAT");
            listener.send("MSG Attendi che l'avversario si connetta...");
            waiting = true;
        }
        else if(!avversario.waiting)
        {
            listener.send("STA WAT");
            listener.send("MSG Attendi che l'avversario finisca di piazzare le navi...");
            waiting = true;
        }
        else if(avversario.waiting)
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
                for(Nave n : navi) 
                {
                    for(Pezzo p : n.pezzi)
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
        
        ArrayList<Pezzo> compnave = new ArrayList<>();
        
        switch (dir) {
            case 'n':
                for(int i = y; i>y-l; i--)
                {
                    if(i < 0 || i >= partita.getDimensioneCampo())   //Out of Bounds, return
                        return false;
                    compnave.add(new Pezzo(x,i));
                }   break;
            case 'e':
                for(int i = x; i<x+l; i++)
                {
                    if(i < 0 || i >= partita.getDimensioneCampo())   //Out of Bounds, return
                        return false;
                    compnave.add(new Pezzo(i,y));
                }   break;
            case 's':
                for(int i = y; i<y+l; i++)
                {
                    if(i < 0 || i >= partita.getDimensioneCampo())   //Out of Bounds, return
                        return false;
                    compnave.add(new Pezzo(x,i));
                }   break;
            case 'w':
                for(int i = x; i>x-l; i--)
                {
                    if(i < 0 || i >= partita.getDimensioneCampo())   //Out of Bounds, return
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
            try
            {
                yourTurn.acquire();
            } 
            catch (InterruptedException ex)
            {   //Player has disconnected, quit
                Thread.currentThread().interrupt();
                return;
            }
            
            if(partita.inProgress)
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
                    
                    if(hit)
                    {
                        listener.send("SND EXP");
                    }
                    
                    checkWin();
                }while(hit && partita.inProgress);
            }
            
            if(partita.inProgress)
            {
                listener.send("WAT " + x + " " + y);
                listener.send("SND SPL");
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
    
    private void NotHit()   //ships not hit
    {
        for(Nave n : navi)
        {
            for(Pezzo P : n.pezzi)
            {
                if(!P.colpito)
                    avversario.listener.send("SNH " + P.x + " " + P.y);
            }
        }
    }
    
    private void win() 
    {
        NotHit();
        listener.send("WIN");
        listener.send("SND WIN");
        avversario.listener.send("LOS");
        avversario.listener.send("SND LOS");
    }

    void kill()
    {
        System.out.println("Am ded");
        task.cancel(true);
    }
}
