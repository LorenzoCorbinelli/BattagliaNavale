package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class BattagliaNavaleClient implements MouseListener
{
    private JFrame frame;
    private JLabel messageLabel;
    
   // private int dim=21;
    private Square[][] board;
    private Square currentSquare;
    private String status;
    private int dim;
    private Socket socket;
    private Scanner input;
    private PrintWriter output;
    public static void setup()
    {
        
    
    }
   
    
    public BattagliaNavaleClient(String serverAddress) throws Exception 
    {
       socket = new Socket(serverAddress, 50900);
       input = new Scanner(socket.getInputStream());
       output = new PrintWriter(socket.getOutputStream(), true); 
       dim=Integer.parseInt(input.nextLine());  //ricevo dimensione dal server
       frame = new JFrame("Battaglia Navale");
       board = new Square[dim][dim];
       messageLabel= new JLabel();
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
       boardPanel.setLayout(new GridLayout(dim, dim,0,0));
       for (int j = 0; j < board.length; j++)
       {
            for (int i = 0; i < board.length; i++)
            {
                Border border = BorderFactory.createMatteBorder(1, 1, 0, 0, Color.black);
                board[i][j] = new Square();
                board[i][j].addMouseListener(this);
                board[i][j].setBorder(border);
                boardPanel.add(board[i][j]);
            }
       }
       boardPanel.setSize(505,497);
       frame.add(boardPanel);
       frame.pack();
       status = input.nextLine();
       messageLabel.setText(input.nextLine());
    }
    

    void insert(int x, int y, char dir)
    {
        System.out.println("Dir: '" + dir + "'");
        output.println(x + " " + y + " " + dir);
        String statuscode = input.nextLine();
        System.out.println("Status: " + statuscode);
        
        if(statuscode.equals("OK"))
        {
            while(input.hasNextLine())
            {
                String comm = input.nextLine();
                System.out.println(comm);
                if(comm.equals("END"))
                {
                    this.status = input.nextLine();
                    System.out.println(statuscode);
                    messageLabel.setText(input.nextLine());
                    break;
                }
                int i = Integer.parseInt(comm.split(" ")[1]);
                int j = Integer.parseInt(comm.split(" ")[2]);
                board[i][j].setBackground(Color.red);
            }
        }
        else if (statuscode.startsWith("ERR"))
        {
            switch(statuscode.split(" ")[1])    //Error code
            {
                default:
                    this.status = input.nextLine();
                    messageLabel.setText(input.nextLine());
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
        //Check status
        if(currentSquare == null)
        {
            currentSquare = (Square)e.getSource();
        }//insert(Arrays.asList(board).indexOf(e.getSource()));
        else
        {
            if(currentSquare.equals(e.getSource()))
            {
                currentSquare = null;
                return;
            }
            int[] s1 = findSquare(currentSquare);
            int[] s2 = findSquare((Square)e.getSource());
            
            currentSquare = null;
            
            insert(s1[0], s1[1], calcDir(s1, s2));
        }
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
    public void mouseEntered(MouseEvent e)  //TODO: Mancano controlli
    {
        if(currentSquare == null)
            ((Square)e.getSource()).setBackground(Color.green);
        else
        {
            ((Square)e.getSource()).setBackground(Color.blue);
            int[] s1 = findSquare(currentSquare);
            int[] s2 = findSquare((Square)e.getSource());
            System.out.println(this.status);
            int len = Integer.parseInt(this.status.split(" ")[1]);
            switch(calcDir(s1, s2))
            {
                case 'n':
                    for(int i = 0; i < len; i++)
                    {
                        board[s1[0]][s1[1]-i].setBackground(Color.red);
                    }
                break;
                case 'w':
                    for(int i = 0; i < len; i++)
                    {
                        board[s1[0]-i][s1[1]].setBackground(Color.red);
                    }
                break;
                case 's':
                    for(int i = 0; i < len; i++)
                    {
                        board[s1[0]][s1[1]+i].setBackground(Color.red);
                    }
                break;
                case 'e':
                    for(int i = 0; i < len; i++)
                    {
                        board[s1[0]+i][s1[1]].setBackground(Color.red);
                    }
                break;
            }
        }
    }

    @Override
    public void mouseExited(MouseEvent e)   //TODO: Mancano controlli
    {
        ((Square)e.getSource()).setBackground(Color.white);
        if(currentSquare != null)
        {
            ((Square)e.getSource()).setBackground(Color.white);
            int[] s1 = findSquare(currentSquare);
            int[] s2 = findSquare((Square)e.getSource());
            int len = Integer.parseInt(status.split(" ")[1]);
            switch(calcDir(s1, s2))
            {
                case 'n':
                    for(int i = 0; i < len; i++)
                    {
                        board[s1[0]][s1[1]-i].setBackground(Color.white);
                    }
                break;
                case 'w':
                    for(int i = 0; i < len; i++)
                    {
                        board[s1[0]-i][s1[1]].setBackground(Color.white);
                    }
                break;
                case 's':
                    for(int i = 0; i < len; i++)
                    {
                        board[s1[0]][s1[1]+i].setBackground(Color.white);
                    }
                break;
                case 'e':
                    for(int i = 0; i < len; i++)
                    {
                        board[s1[0]+i][s1[1]].setBackground(Color.white);
                    }
                break;
            }
        }
    }
    
    private int[] findSquare (Square s)
    {
        int ret[] = new int[2];
        
        for(int i = 0; i < board.length; i++)
        {
            for(int j = 0; j < board.length; j++)
            {
                if(board[i][j].equals(s))
                {
                    ret[0]=i; ret[1]=j;
                    return ret;
                }
            }
        }
        
        return null;
    }
    
    private char calcDir(int s1[], int s2[])
    {
        int diffx = s1[0] - s2[0];
        int diffy = s1[1] - s2[1];

        if(Math.abs(diffy) > Math.abs(diffx))
        {
            if(diffy > 0)
                return 'n';
            else if(diffy < 0)
                return 's';
        }
        else
        {
            if(diffx > 0)
                return 'w';
            else if(diffx < 0)
                return 'e';
        }
        return ' ';
    }
}
