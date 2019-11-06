package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Start
{
    private JFrame frame;
    private JLabel messageLabel;
    
   // private int dim=21;
    private Square[] board;
    private Square currentSquare;
    private int dim;
    private Socket socket;
    private Scanner input;
    private PrintWriter output;
    public static void setup()
    {
        
    
    }
   
    
    public Start(String serverAddress) throws Exception 
    {
       socket = new Socket(serverAddress, 50900);
       input = new Scanner(socket.getInputStream());
       output = new PrintWriter(socket.getOutputStream(), true); 
       dim=Integer.parseInt(input.nextLine());  //ricevo dimensione dal server
       frame = new JFrame("Battaglia Navale");
       board = new Square[dim*dim];
       messageLabel= new JLabel("...");
       frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       frame.setPreferredSize(new Dimension(505, 487));
       
       frame.setVisible(true);
      // frame.setResizable(false);
       frame.pack();
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
       boardPanel.setLayout(new GridLayout(dim, dim,1,1));
       for (int i = 0; i < board.length; i++) {
           final int j = i;
           board[i] = new Square();
           board[i].addMouseListener(new MouseAdapter() {
               public void squareClick(MouseEvent e) {
                    currentSquare = board[j];
                    String dir=JOptionPane.showInputDialog("Inserisci la direzione");
                    output.println(j%dim+ " "+j/dim+" "+dir.charAt(0)); //j%dim = x, j/dim = y, dir.charAt(0) = direzione
               }
           });
           boardPanel.add(board[i]);
       }
       boardPanel.setSize(505,497);
       frame.add(boardPanel);
       frame.pack();
    }
    
}