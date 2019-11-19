package server;

import java.util.ArrayList;

public class Nave 
{
    public ArrayList<Pezzo> pezzi; //declaration of new variable
    public boolean affondata = false;
    
    public Nave() //constructor
    {
        pezzi = new ArrayList<>(); //new instance of array
    }
    
    public Nave(ArrayList<Pezzo> pezzi) //constructor with parametetrs (a Pezzo's list of 'pezzi')
    {
        this.pezzi = new ArrayList<>(); //new istance of array
        for(Pezzo p : pezzi) //for each Pezzo in array pezzi
        {
            this.pezzi.add(p); //add a new pezzo
        }
    }

    public boolean add(int x, int y) //method add with parameters (coordinates of pezzo)
    {
        return pezzi.add(new Pezzo(x,y)); //return true if a new istance of Pezzo with coordinates (x,y) is added to the list of pezzi
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
