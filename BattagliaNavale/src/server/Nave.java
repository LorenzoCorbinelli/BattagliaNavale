package server;

import java.util.ArrayList;

public class Nave 
{
    public ArrayList<Pezzo> pezzi;
    public boolean affondata = false;
    
    public Nave()
    {
        pezzi = new ArrayList<>();
    }
    
    public Nave(ArrayList<Pezzo> pezzi)
    {
        this.pezzi = new ArrayList<>();
        for(Pezzo p : pezzi)
        {
            this.pezzi.add(p);
        }
    }

    public boolean add(int x, int y)
    {
        return pezzi.add(new Pezzo(x,y));
    }
    
    public boolean checkAffondata()
    {
        for(Pezzo p : pezzi)
        {
            if(!p.colpito)
                return false;
        }
        this.affondata = true;
        return true;
    }
    
}
