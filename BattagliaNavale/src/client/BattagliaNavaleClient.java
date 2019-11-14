package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class BattagliaNavaleClient implements MouseListener
{
    private JFrame frame;
    private JLabel messageLabel;
    
    private Square[][] board;
    private Square currentSquare;
    private String status;

    public void setStatus(String status)
    {
        this.status = status;
    }
    private int dim;
    private messageListener listener;
    
    public BattagliaNavaleClient(String serverAddress)
    {
        frame = new JFrame("Battaglia Navale");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(505, 487));
        frame.setVisible(true);
        frame.setResizable(false);
        frame.pack();
        
        messageLabel = new JLabel();
        messageLabel.setBackground(Color.lightGray);
        frame.getContentPane().add(messageLabel, BorderLayout.SOUTH);
        frame.pack();
        
        listener = new messageListener(serverAddress, this);
    }
    
    public void setup(int dim)
    {
        this.dim = dim;
        JPanel boardPanel = new JPanel();
        board = new Square[dim][dim];
        messageLabel= new JLabel();
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
        
        boardPanel.setBackground(Color.black);
        boardPanel.setSize(505,497);
        frame.add(boardPanel);
        frame.pack();
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
        if(status.startsWith("INS"))
        {
            if(currentSquare == null)
            {
                ((Square)e.getSource()).setBackground(Color.blue);
                currentSquare = (Square)e.getSource();
            }
            else
            {
                mouseExited(e);
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
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseEntered(MouseEvent e)  //TODO: Mancano controlli
    {
        if(status.startsWith("INS"))
        {
            if(currentSquare == null)
                ((Square)e.getSource()).setBackground(Color.green);
            else
            {
                int[] s1 = findSquare(currentSquare);
                int[] s2 = findSquare((Square)e.getSource());
                int len = Integer.parseInt(this.status.split(" ")[1]);
                char dir = calcDir(s1, s2);
                if(isInBounds(s1[0], s1[1], dir, len) && isPositionValid(s1[0], s1[1], dir, len))
                {
                    ((Square)e.getSource()).setBackground(Color.blue);
                    switch(dir)
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
                else
                {
                    ((Square)e.getSource()).setBackground(Color.yellow);
                }
            }
        }
    }

    @Override
    public void mouseExited(MouseEvent e)   //TODO: Mancano controlli
    {
        if(status.startsWith("INS"))
        {
            ((Square)e.getSource()).resetColor();
            if(currentSquare != null)
            {
                int[] s1 = findSquare(currentSquare);
                int[] s2 = findSquare((Square)e.getSource());
                int len = Integer.parseInt(status.split(" ")[1]);
                try
                {
                    switch(calcDir(s1, s2))
                    {
                        case 'n':
                            for(int i = 0; i < len; i++)
                            {
                                board[s1[0]][s1[1]-i].resetColor();
                            }
                        break;
                        case 'w':
                            for(int i = 0; i < len; i++)
                            {
                                board[s1[0]-i][s1[1]].resetColor();
                            }
                        break;
                        case 's':
                            for(int i = 0; i < len; i++)
                            {
                                board[s1[0]][s1[1]+i].resetColor();
                            }
                        break;
                        case 'e':
                            for(int i = 0; i < len; i++)
                            {
                                board[s1[0]+i][s1[1]].resetColor();
                            }
                        break;
                    }
                }
                catch(ArrayIndexOutOfBoundsException ex)
                {
                    System.out.println("Perhaps you should add proper controls instead of relying on a try-catch..."); //I mean, it works, but it's hella ugly
                }
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
    
    void insert(int x, int y, char dir)
    {
        listener.send("" + x + y + dir);
    }
    
    boolean isInBounds(int x, int y, char dir, int l)
    {
        switch (dir) {
            case 'n':
                for(int i = y; i>y-l; i--)
                {
                    if(i < 0 || i >= dim)   //Out of Bounds, return
                        return false;
                }  
                break;
            case 'e':
                for(int i = x; i<x+l; i++)
                {
                    if(i < 0 || i >= dim)   //Out of Bounds, return
                        return false;
                }
                break;
            case 's':
                for(int i = y; i<y+l; i++)
                {
                    if(i < 0 || i >= dim)   //Out of Bounds, return
                        return false;
                }
                break;
            case 'w':
                for(int i = x; i>x-l; i--)
                {
                    if(i < 0 || i >= dim)   //Out of Bounds, return
                        return false;
                }
                break;
        }
        return true;
    }

    private boolean isPositionValid(int x, int y, char dir, int len)
    {
        try
        {
            switch(dir)
            {
                case 'n':
                    for(int i = x - 1; i <= x+1; i++)
                    {
                        for(int j = y + 1; j >= y - len; j--)
                        {
                            if(board[i][j].getBackground().equals(Color.red))
                                return false;
                        }
                    }
                    break;
                case 'w':
                    for(int i = x + 1; i >= x - len; i--)
                    {
                        for(int j = y - 1; j <= y + 1; j++)
                        {
                            if(board[i][j].getBackground().equals(Color.red))
                                return false;
                        }
                    }
                    break;
                case 's':
                    for(int i = x - 1; i <= x+1; i++)
                    {
                        for(int j = y - 1; j <= y + len; j++)
                        {
                            if(board[i][j].getBackground().equals(Color.red))
                                return false;
                        }
                    }
                    break;
                case 'e':
                    for(int i = x - 1; i <= x + len; i++)
                    {
                        for(int j = y - 1; j <= y + 1; j++)
                        {
                            if(board[i][j].getBackground().equals(Color.red))
                                return false;
                        }
                    }
                    break;
            }
        } catch (ArrayIndexOutOfBoundsException ex)
        {
                System.out.println("Perhaps you should add proper controls instead of relying on a try-catch..."); //It doesn't even work properly :(
        }
        return true;
    }
}
