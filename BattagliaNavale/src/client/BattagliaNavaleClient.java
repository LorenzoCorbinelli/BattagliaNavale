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
import java.io.PrintWriter;
import static java.lang.System.out;
import java.net.Socket;
import java.util.Scanner;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author informatica
 */
public class BattagliaNavaleClient 
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
    {
       socket = new Socket(serverAddress, 50900);
       input = new Scanner(socket.getInputStream());
       output = new PrintWriter(socket.getOutputStream(), true);
       
        //aggiungere controllo sui partecipanti
        
        //permettere inserimento navi
        
        //controllo che tutti abbiano inserito tutte le navi disponibili
        
        //inizio gioco
        
        //giocata
        
            //colpito o acqua?
            
            //punteggio
            
            //partita finita?
            
        //attendogiocata avversario
       
       messageLabel.setBackground(Color.lightGray);
       frame.getContentPane().add(messageLabel, BorderLayout.SOUTH);

       JPanel boardPanel = new JPanel();
       boardPanel.setBackground(Color.black);
       boardPanel.setLayout(new GridLayout(3, 3, 2, 2));
       for (int i = 0; i < board.length; i++) {
           final int j = i;
           board[i] = new Square();
           board[i].addMouseListener(new MouseAdapter() {
               public void mousePressed(MouseEvent e) {
                   currentSquare = board[j];
                   out.println("MOVE " + j);
               }
           });
           boardPanel.add(board[i]);
       }
       frame.getContentPane().add(boardPanel, BorderLayout.CENTER);
    }
}
