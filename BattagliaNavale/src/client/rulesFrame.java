package client;

import java.io.IOException;
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
            InputStream file = getClass().getResourceAsStream("/resources/other/GameRules.html");
            Scanner sc = new Scanner(file, "UTF-8");
            while(sc.hasNextLine())
                s+= sc.nextLine();
            text.setText(s);
            file.close();
            
        }catch(IOException e) 
        {
            System.out.println(e);
        }
        return text;
    }
}
