package client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.border.Border;

public class rulesFrame extends JFrame
{
    public JFrame fRules;
    private JLabel text;
    private Border rulesBorder;
    
    public rulesFrame()
    {
        fRules = new JFrame("Regole");
        fRules.setPreferredSize(new Dimension(500, 580));
        fRules.setResizable(false);
        
        text = new JLabel();
        String s = "<html>";
        try
        {
            InputStream file = getClass().getResourceAsStream("/resources/other/Regolamento.md.html");
            Scanner sc = new Scanner(file, "UTF-8");
            while(sc.hasNextLine())
                s+= sc.nextLine();
            s+="</html>";
            text.setText(s);
            file.close();
        }catch(Exception e) 
        {
            System.out.println(e);
        }
        rulesBorder = BorderFactory.createMatteBorder(10, 10, 10, 10, fRules.getBackground());
        text.setBorder(rulesBorder);
        fRules.getContentPane().add(text,BorderLayout.NORTH);
        fRules.pack();
    }
    
    public void setIcon(ArrayList<Image> icons)
    {
        fRules.setIconImages(icons);
    }
}
