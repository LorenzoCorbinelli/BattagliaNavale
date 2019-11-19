package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class BattagliaNavaleClient implements MouseListener, MouseMotionListener
{
    private final JFrame frame;
    private JLabel face;
    private JPanel yourBoardPanel;
    private JPanel opponentBoardPanel;
    private JLabel messageLabel;
    private Square[][] yourBoard;
    private Square[][] opponentBoard;
    private Square mouseOverSquare;
    private Square selectedSquare;
    private String status;
    private int dim;
    private final messageListener listener; //Using threads comes with HUGE problems, like race conditions. They shuoldn'y cause many problems in this application, thus they're not checked (yet)

    public void setStatus(String status)
    {
        this.status = status;
        if(mouseOverSquare != null)
        {    switch(status.substring(0,3))
            {
                case "INS":
                    mouseOverSquare.setBackground(Color.green);
                    break;
                case "WAT":
                    mouseOverSquare.setBackground(Color.gray);
                    break;
            }
        }
    }
    
    public void setText(String msg)
    {
        messageLabel.setText(msg);
        messageLabel.setForeground(Color.black);
    }
    
    public BattagliaNavaleClient(String serverAddress)
    {
        status = "WAT";
        frame = new JFrame("Battaglia Navale");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(1024, 490));
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
        yourBoardPanel = new JPanel();
        opponentBoardPanel = new JPanel();
        yourBoard = new Square[dim][dim];   
        opponentBoard = new Square[dim][dim];  
        messageLabel= new JLabel();
        yourBoardPanel.setLayout(new GridLayout(dim+2, dim+2,0,0));
        opponentBoardPanel.setLayout(new GridLayout(dim+2, dim+2,0,0));
        Border border = BorderFactory.createMatteBorder(1, 1, 0, 0, Color.black);
        Border bottomBorder = BorderFactory.createMatteBorder(1, 1, 1, 0, Color.black);
        Border rightBorder = BorderFactory.createMatteBorder(1, 1, 0, 1, Color.black);
        Border cornerBorder = BorderFactory.createMatteBorder(1, 1, 1, 1, Color.black);
        face = new JLabel("(·‿·)", JLabel.CENTER);
        for (int j = -1; j < dim+1; j++)
        {
            for (int i = -1; i < dim+1; i++)
            {
                if((j==-1&&i==-1)||(j==-1&&i==dim)||(j==dim&&i==-1)||(j==dim&&i==dim))  //corners
                {
                 yourBoardPanel.add(new JLabel(" "));
                 opponentBoardPanel.add(new JLabel(" "));
                }
                else if(j==-1||j==dim)  //top and bottom row
                {
                 yourBoardPanel.add(new JLabel("  "+(char)(i+'A')));
                 opponentBoardPanel.add(new JLabel("  "+(char)(i+'A')));
                }
                else if(i==-1||i==dim)  //left and right column
                {
                 yourBoardPanel.add(new JLabel((j+1)+""));
                 opponentBoardPanel.add(new JLabel((j+1)+""));
                }
                else
                {
                    yourBoard[i][j] = new Square();
                    yourBoard[i][j].addMouseListener(this);
                    yourBoard[i][j].addMouseMotionListener(this);
                    yourBoardPanel.add(yourBoard[i][j]);
                    opponentBoard[i][j] = new Square();
                    opponentBoard[i][j].addMouseListener(this);
                    opponentBoard[i][j].addMouseMotionListener(this);
                    opponentBoardPanel.add(opponentBoard[i][j]);
                    if(i == dim - 1 && j == dim -1)
                    {
                        yourBoard[i][j].setBorder(cornerBorder);
                        opponentBoard[i][j].setBorder(cornerBorder);
                    }
                    else if(i == dim-1)
                    {
                        yourBoard[i][j].setBorder(rightBorder);
                        opponentBoard[i][j].setBorder(rightBorder);
                    }
                    else if(j == dim - 1)
                    {
                        yourBoard[i][j].setBorder(bottomBorder);
                        opponentBoard[i][j].setBorder(bottomBorder);
                    }
                    else
                    {
                        yourBoard[i][j].setBorder(border);
                        opponentBoard[i][j].setBorder(border);
                    }
                }
            }
        }
        yourBoardPanel.setPreferredSize(new Dimension(432,432));
        opponentBoardPanel.setPreferredSize(new Dimension(432,432));
        messageLabel.setBackground(Color.lightGray);
        frame.getContentPane().add(messageLabel, BorderLayout.SOUTH);
        frame.getContentPane().add(yourBoardPanel, BorderLayout.WEST);
        frame.getContentPane().add(opponentBoardPanel, BorderLayout.EAST);
        frame.getContentPane().add(face, BorderLayout.CENTER);
        messageLabel.setText("I'm blue da-ba-dee");
        frame.pack();
        frame.addMouseMotionListener(this);
        System.out.println(yourBoardPanel.getHeight() + " ," + yourBoardPanel.getWidth());
        System.out.println(yourBoard[1][1].getHeight() + " ," + yourBoard[1][1].getWidth());
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
        Square source = (Square)e.getSource();
        
        if(source.getParent().equals(yourBoardPanel))
        {
            if(status.startsWith("INS"))
            {
                if(selectedSquare == null)
                {
                    source.setBackground(Color.blue);
                    selectedSquare = source;
                }
                else
                {
                    mouseExited(e);
                    if(selectedSquare.equals(source))
                    {
                        selectedSquare = null;
                        return;
                    }
                    int[] s1 = findSquare(selectedSquare, yourBoard);
                    int[] s2 = findSquare(source, yourBoard);

                    selectedSquare = null;

                    insert(s1[0], s1[1], calcDir(s1, s2));
                }
            }
        }
        else
        {
            //if(status.equals("ATT"))
            {
                int[] s1 = findSquare(source, opponentBoard);
                attack(s1[0], s1[1]);
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e){}    //unused

    @Override
    public void mouseReleased(MouseEvent e){}   //unused

    @Override
    public void mouseEntered(MouseEvent e)  //TODO: Mancano controlli
    {
        mouseOverSquare = (Square) e.getSource();
        if(mouseOverSquare.getParent().equals(yourBoardPanel))
        //<editor-fold defaultstate="collapsed" desc="Mouse in yourField">
        {
            if(status.startsWith("INS"))
            {
                if(selectedSquare == null)
                    (mouseOverSquare).setBackground(Color.green);
                else
                {
                    int[] s1 = findSquare(selectedSquare, yourBoard);
                    int[] s2 = findSquare(mouseOverSquare, yourBoard);
                    int len = Integer.parseInt(this.status.split(" ")[1]);
                    char dir = calcDir(s1, s2);
                    if(isInBounds(s1[0], s1[1], dir, len) && isPositionValid(s1[0], s1[1], dir, len))
                    {
                        (mouseOverSquare).setBackground(Color.blue);
                        switch(dir)
                        {
                            case 'n':
                                for(int i = 0; i < len; i++)
                                {
                                    yourBoard[s1[0]][s1[1]-i].setBackground(Color.red);
                                }
                                break;
                            case 'w':
                                for(int i = 0; i < len; i++)
                                {
                                    yourBoard[s1[0]-i][s1[1]].setBackground(Color.red);
                                }
                                break;
                            case 's':
                                for(int i = 0; i < len; i++)
                                {
                                    yourBoard[s1[0]][s1[1]+i].setBackground(Color.red);
                                }
                                break;
                            case 'e':
                                for(int i = 0; i < len; i++)
                                {
                                    yourBoard[s1[0]+i][s1[1]].setBackground(Color.red);
                                }
                                break;
                        }
                    }
                    else
                    {
                        (mouseOverSquare).setBackground(Color.yellow);
                    }
                }
            }
            else
            {
                (mouseOverSquare).setBackground(Color.gray);
            }
        }
        //</editor-fold>
        else
        {
            if(status.startsWith("ATT"))
            {
                mouseOverSquare.setBackground(Color.red);
            }
            else
            {
                mouseOverSquare.setBackground(Color.gray);
            }
        }
    }

    @Override
    public void mouseExited(MouseEvent e)   //TODO: Mancano controlli
    {
        Square source = (Square)e.getSource();
        source.resetColor();
        if(source.getParent().equals(yourBoardPanel))
        {
            if(status.startsWith("INS"))
            {
                if(selectedSquare != null)
                {
                    int[] s1 = findSquare(selectedSquare, yourBoard);
                    int[] s2 = findSquare(source, yourBoard);
                    int len = Integer.parseInt(status.split(" ")[1]);
                    try
                    {
                        switch(calcDir(s1, s2))
                        {
                            case 'n':
                                for(int i = 0; i < len; i++)
                                {
                                    yourBoard[s1[0]][s1[1]-i].resetColor();
                                }
                            break;
                            case 'w':
                                for(int i = 0; i < len; i++)
                                {
                                    yourBoard[s1[0]-i][s1[1]].resetColor();
                                }
                            break;
                            case 's':
                                for(int i = 0; i < len; i++)
                                {
                                    yourBoard[s1[0]][s1[1]+i].resetColor();
                                }
                            break;
                            case 'e':
                                for(int i = 0; i < len; i++)
                                {
                                    yourBoard[s1[0]+i][s1[1]].resetColor();
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
        else
        {
            
        }
    }
    
    private int[] findSquare (Square s, Square[][] board)
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
        setStatus("WAT");
        listener.send(x + " " + y + " " + dir);
    }
    
    void attack(int x, int y)
    {
        setStatus("WAT");
        listener.send(x + " " + y + " ");
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
            switch(dir)
            {
                case 'n':
                    for(int i = x - 1; i <= x+1; i++)
                    {
                        for(int j = y + 1; j >= y - len; j--)
                        {
                            try
                            {
                                if(yourBoard[i][j].getBackground().equals(Color.red))
                                    return false;
                            } catch (ArrayIndexOutOfBoundsException ex)
                            {
                                    System.out.println("Perhaps you should add proper controls instead of relying on a try-catch...");  //I mean, it works, but it's hella ugly
                            }
                        }
                    }
                    break;
                case 'w':
                    for(int i = x + 1; i >= x - len; i--)
                    {
                        for(int j = y - 1; j <= y + 1; j++)
                        {
                            try
                            {
                            if(yourBoard[i][j].getBackground().equals(Color.red))
                                return false;
                            } catch (ArrayIndexOutOfBoundsException ex)
                            {
                                    System.out.println("Perhaps you should add proper controls instead of relying on a try-catch...");  //I mean, it works, but it's hella ugly
                            }
                        }
                    }
                    break;
                case 's':
                    for(int i = x - 1; i <= x+1; i++)
                    {
                        for(int j = y - 1; j <= y + len; j++)
                        {
                            try
                            {
                            if(yourBoard[i][j].getBackground().equals(Color.red))
                                return false;
                            } catch (ArrayIndexOutOfBoundsException ex)
                            {
                                    System.out.println("Perhaps you should add proper controls instead of relying on a try-catch...");  //I mean, it works, but it's hella ugly
                            }
                        }
                    }
                    break;
                case 'e':
                    for(int i = x - 1; i <= x + len; i++)
                    {
                        for(int j = y - 1; j <= y + 1; j++)
                        {
                            try
                            {
                            if(yourBoard[i][j].getBackground().equals(Color.red))
                                return false;
                            } catch (ArrayIndexOutOfBoundsException ex)
                            {
                                    System.out.println("Perhaps you should add proper controls instead of relying on a try-catch...");  //I mean, it works, but it's hella ugly
                            }
                        }
                    }
                    break;
            }
        return true;
    }

    void drawPiece(int x, int y)
    {
        yourBoard[x][y].setColor(Color.red);
        yourBoard[x][y].setBackground(Color.red);
    }

    void drawHit(int x, int y)
    {
        opponentBoard[x][y].setColor(Color.red);
        opponentBoard[x][y].setBackground(Color.red);
    }

    void setError(String err)
    {
        messageLabel.setText(err);
        messageLabel.setForeground(Color.red);
    }

    @Override
    public void mouseDragged(MouseEvent e) {} //Unused

    @Override
    public void mouseMoved(MouseEvent e)
    {
        if(e.getXOnScreen() < frame.getX() + (frame.getWidth() / 2.34))
        {  
            if(e.getYOnScreen() < frame.getY() + (frame.getHeight() / 2.69))
            {
                face.setText("('◡' )");
            }
            else if(e.getYOnScreen() > frame.getY() + (frame.getHeight() / 1.68))
            {
                face.setText("(.‿. )");
            }
            else
            {
                face.setText("(·‿· )");
            }
        }
        else if(e.getXOnScreen() > frame.getX() + (frame.getWidth()/1.75))
        {
            if(e.getYOnScreen() < frame.getY() + (frame.getHeight() / 2.69))
            {
                face.setText("( '◡')");
            }
            else if(e.getYOnScreen() > frame.getY() + (frame.getHeight() / 1.68))
            {
                face.setText("( .‿.)");
            }
            else
            {
                face.setText("( ·‿·)");
            }
        }
        else
        {
            if(e.getYOnScreen() < frame.getY() + (frame.getHeight()/2.69))
            {
                face.setText("('◡')");
            }
            else if(e.getYOnScreen() > frame.getY() + (frame.getHeight()/1.68))
            {
                face.setText("(.‿.)");
            }
            else
            {
                face.setText("(｡◕‿◕｡)");
            }
        }
    }
}
