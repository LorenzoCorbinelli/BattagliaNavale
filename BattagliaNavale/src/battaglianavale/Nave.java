package battaglianavale;

import java.util.ArrayList;

public class Nave
{
    public ArrayList<Pezzo> pezzi;
    
    public Nave()
    {
        pezzi = new ArrayList<>();
    }

    public boolean add(int x, int y)
    {
        return pezzi.add(new Pezzo(x,y));
    }
    
}
