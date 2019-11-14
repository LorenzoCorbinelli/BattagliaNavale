package client;

import java.awt.Color;
import javax.swing.JPanel;

/**
 *
 * @author informatica
 */
class Square extends JPanel
{
    private Color color;
    
    public Square() 
    {
        color = Color.white;
        setBackground(Color.white);
    }
    
    public void resetColor()
    {
        setBackground(color);
    }
    
    public void setColor(Color c)
    {
        this.color = c;
    }
}
