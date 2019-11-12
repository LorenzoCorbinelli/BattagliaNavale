/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.PrintWriter;
import static java.lang.System.out;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author informatica
 */
public class BattagliaNavaleClient implements MouseListener
{
    private JFrame frame = new JFrame("Battaglia Navale");
    private JLabel messageLabel = new JLabel("...");
    
    private int dim=21;
    private Square[] board = new Square[dim*dim];
    private Square currentSquare;

    private Socket socket;
    private Scanner input;
    private PrintWriter output;
    
    public BattagliaNavaleClient(String serverAddress) throws Exception 
    {}
    
    void insert(int pos)
    {
        output.println(pos + " N");
        String status = input.nextLine();
        System.out.println(status);
        if(status.equals("OK"))
        {
            while(input.hasNextLine())
            {
                String comm = input.nextLine();
                System.out.println(comm);
                int coords = Integer.parseInt(comm.split(" ")[1]);
                board[coords].setBackground(Color.red);
            }
        }
        else if (status.startsWith("ERR"))
        {
            switch(status.split(" ")[1])    //Error code
            {
                
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
        //Check status
        insert(Arrays.asList(board).indexOf(e.getSource()));
    }

    @Override
    public void mousePressed(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseExited(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
