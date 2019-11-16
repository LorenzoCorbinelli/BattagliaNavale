package client;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
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
        try
        {
            //BufferedImage previewImage = ImageIO.read(new URL("https://cdn2.iconfinder.com/data/icons/aspneticons_v1.0_Nov2006/delete_16x16.gif"));
            color = Color.white;
            setBackground(Color.white);
            //add(new JLabel(new ImageIcon(previewImage)));
        } 
        catch (Exception ex)
        {
        
        }
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
