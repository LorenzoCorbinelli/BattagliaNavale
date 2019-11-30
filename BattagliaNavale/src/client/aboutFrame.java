package client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.border.Border;

public class aboutFrame extends JFrame
{
    public JFrame fAbout;
    private JLabel text;
    private Border aboutBorder;
    
    public aboutFrame()
    {
        fAbout = new JFrame("About");
        fAbout.setPreferredSize(new Dimension(250, 200));
        fAbout.setResizable(false);
        text = new JLabel("<html> <h3> Sviluppato da: </h3> Riccardo Degli Esposti <br> Lorenzo Corbinelli <br> Giulia Giamberini <br><br> ITIS A. Meucci Firenze <br> A.S. 2019/2020 </html>");
        aboutBorder = BorderFactory.createMatteBorder(10, 10, 10, 10, fAbout.getBackground());
        text.setBorder(aboutBorder);
        fAbout.getContentPane().add(text,BorderLayout.NORTH);
        fAbout.pack();
    }
    
    public void setIcon(ArrayList<Image> icons)
    {
        fAbout.setIconImages(icons);
    }
}
