/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author informatica
 */
class Square extends JPanel
{
    JLabel label = new JLabel();

    public Square() 
    {
        setBackground(Color.white);
        setLayout(new GridBagLayout());
        label.setFont(new Font("Arial", Font.BOLD, 40));
        add(label);
    }

    public void setText(char text) 
    {
        label.setForeground(text == 'X' ? Color.RED : Color.BLUE); //ricontrollare
        label.setText(text + "");
    }
}
