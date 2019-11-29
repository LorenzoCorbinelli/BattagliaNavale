package client;

import java.io.InputStream;
import java.util.Scanner;
import javax.swing.JLabel;

public class rulesFrame 
{
    public JLabel rules()
    {
        JLabel text = new JLabel();
        String s = "";
        try
        {
            InputStream file = getClass().getResource("GameRules.html").openStream();
            Scanner sc = new Scanner(file, "UTF-8");
            while(sc.hasNextLine())
                s+= sc.nextLine();
            text.setText(s);
            file.close();
            
        }catch(Exception e) 
        {
            System.out.println(e);
        }
        return text;
    }
}
