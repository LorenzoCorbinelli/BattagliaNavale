package client;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import javax.swing.border.Border;

public final class BattagliaNavaleClient implements MouseListener, MouseMotionListener, ActionListener
{
    private final JFrame frame;
    private final JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenu helpMenu;
    private JFrame  fRules;
    private JPanel[][] ships;
    private JLabel face;
    private JPanel yourBoardPanel;
    private JPanel opponentBoardPanel;
    private JLabel messageLabel;
    private JPanel shipsPanel;
    private final JPanel centralPanel;
    private JPanel facePanel;
    private Square[][] yourBoard;
    private Square[][] opponentBoard;
    private Square mouseOverSquare;
    private Square selectedSquare;
    private String status;
    private int dim;
    private ImageIcon explosion;
    private final messageListener listener; //Using threads comes with HUGE problems, like race conditions. They shuoldn'y cause many problems in this application, thus they're not checked (yet)
    private int selectedShip = 0;
    private final Border tlBorder = BorderFactory.createMatteBorder(1, 1, 0, 0, Color.black);
    private final Border tlbBorder = BorderFactory.createMatteBorder(1, 1, 1, 0, Color.black);
    private final Border tlrBorder = BorderFactory.createMatteBorder(1, 1, 0, 1, Color.black);
    private final Border tlbrBorder = BorderFactory.createMatteBorder(1, 1, 1, 1, Color.black);
    
    public void setStatus(String status)
    {
        this.status = status;
        if(this.mouseOverSquare != null)
        {   
            switch(status.substring(0,3))
            {
                case "INS":
                    if(this.mouseOverSquare.getParent().equals(yourBoardPanel))
                        this.mouseOverSquare.setBackground(Color.green);
                    break;
                case "WAT":
                    this.mouseOverSquare.setBackground(Color.gray);
                    this.setText("Attendi...");
                    break;
                case "ATT":
                    if(this.mouseOverSquare.getParent().equals(opponentBoardPanel))
                        this.mouseOverSquare.setBackground(Color.red);
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
        centralPanel = new JPanel(new BorderLayout());
        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        helpMenu = new JMenu("Help");
        
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        fRules = new JFrame("Regole");
        
        ArrayList<Image> icons = new ArrayList<>();
        icons.add(scaleImage(new ImageIcon(getClass().getResource("/resources/images/icona.png")),16,16).getImage());
        icons.add(scaleImage(new ImageIcon(getClass().getResource("/resources/images/icona.png")),64,64).getImage());
        icons.add(scaleImage(new ImageIcon(getClass().getResource("/resources/images/icona.png")),128,128).getImage());
        frame = new JFrame("Ultimate Battleship");
        frame.setIconImages(icons);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(1024, 490));
        frame.setVisible(true);
        frame.setResizable(false);
        frame.pack();
        
        fRules.setIconImages(icons);
        fRules.pack();
        
        messageLabel = new JLabel();
        messageLabel.setBackground(Color.lightGray);
        frame.getContentPane().add(messageLabel, BorderLayout.SOUTH);
        frame.getContentPane().add(centralPanel,BorderLayout.CENTER);
        frame.add(menuBar, BorderLayout.NORTH);
        frame.pack();
        listener = new messageListener(serverAddress, this);
    }
    
    public void elencoNavi(ArrayList <String> dim)
    {
        shipsPanel = new JPanel();
        ships = new JPanel[6][7];  
        shipsPanel.setLayout(new GridLayout(7, 6,0,3));
        JLabel selectorLabel;
        int lunghezzaNave;
        frame.pack();
        
        for(int i=0;i<dim.size();i++)
        {
            lunghezzaNave = Integer.parseInt(dim.get(i));
            ships[0][i] = new JPanel(new BorderLayout());
            selectorLabel = new JLabel("✦",JLabel.LEFT);
            selectorLabel.setFont(new Font(selectorLabel.getFont().getName(), selectorLabel.getFont().getStyle(), 17));
            ships[0][i].add(selectorLabel);
            if(i>0)
                selectorLabel.setVisible(false);
            ships[0][i].setPreferredSize(new Dimension(16,16));
            shipsPanel.add(ships[0][i]);
            for(int j=1;j<6;j++)
            {
                ships[j][i]=new JPanel(new BorderLayout());
                
                if(j==lunghezzaNave)
                    ships[j][i].setBorder(tlbrBorder);
                else if(j<lunghezzaNave)
                    ships[j][i].setBorder(tlbBorder);
                if(j<lunghezzaNave+1)
                    ships[j][i].setBackground(Color.red);
                ships[j][i].setPreferredSize(new Dimension(16,16));
                shipsPanel.add(ships[j][i]);
            }
        }
        centralPanel.add(shipsPanel,BorderLayout.NORTH);
        frame.pack();
        int hPadding = shipsPanel.getWidth()-(16*6);
        System.out.println(shipsPanel.getWidth() + " - " + 16*6 + " = " + hPadding);
        shipsPanel.setBorder(BorderFactory.createMatteBorder(25,hPadding/2,0,hPadding/2,frame.getBackground()));
        frame.pack();
    }
    
    public void setSelectedShip()
    {
        try
        {
            for(int i=1;i<6;i++)
            {
                if(!ships[i][selectedShip].getBackground().equals(Color.red))
                    break;
                ships[i][selectedShip].setBackground(Color.GRAY);
            }
            ships[0][selectedShip].getComponent(0).setVisible(false);
            selectedShip++;
            ships[0][selectedShip].getComponent(0).setVisible(true);
        }catch(Exception e)
        {
            selectedShip=-1;
        }
    }
    
    public void setup(int dim, ArrayList<String> shipsDim)
    { 
        this.dim = dim;
        yourBoardPanel = new JPanel();
        opponentBoardPanel = new JPanel();
        yourBoard = new Square[dim][dim];   
        opponentBoard = new Square[dim][dim];  
        messageLabel= new JLabel();
        yourBoardPanel.setLayout(new GridLayout(dim+2, dim+2,0,0));
        opponentBoardPanel.setLayout(new GridLayout(dim+2, dim+2,0,0));
        
        face = new JLabel("(·‿·)",JLabel.CENTER);
        face.setVerticalAlignment(JLabel.CENTER);

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
                    yourBoardPanel.add(new JLabel(""+(char)(i+'A'), JLabel.CENTER));
                    opponentBoardPanel.add(new JLabel(""+(char)(i+'A'), JLabel.CENTER));
                }
                else if(i==-1||i==dim)  //left and right column
                {
                    yourBoardPanel.add(new JLabel((j+1)+"", JLabel.CENTER));
                    opponentBoardPanel.add(new JLabel((j+1)+"", JLabel.CENTER));
                }
                else
                {
                    yourBoard[i][j] = new Square(i,j);
                    yourBoard[i][j].addMouseListener(this);
                    yourBoard[i][j].addMouseMotionListener(this);
                    yourBoardPanel.add(yourBoard[i][j]);
                    opponentBoard[i][j] = new Square(i,j);
                    opponentBoard[i][j].addMouseListener(this);
                    opponentBoard[i][j].addMouseMotionListener(this);
                    opponentBoardPanel.add(opponentBoard[i][j]);
                    if(i == dim - 1 && j == dim -1)
                    {
                        yourBoard[i][j].setBorder(tlbrBorder);
                        opponentBoard[i][j].setBorder(tlbrBorder);
                    }
                    else if(i == dim-1)
                    {
                        yourBoard[i][j].setBorder(tlrBorder);
                        opponentBoard[i][j].setBorder(tlrBorder);
                    }
                    else if(j == dim - 1)
                    {
                        yourBoard[i][j].setBorder(tlbBorder);
                        opponentBoard[i][j].setBorder(tlbBorder);
                    }
                    else
                    {
                        yourBoard[i][j].setBorder(tlBorder);
                        opponentBoard[i][j].setBorder(tlBorder);
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
        facePanel = new JPanel(null);
        messageLabel.setText("Praise the sun");
        facePanel.add(face);
        centralPanel.add(facePanel, BorderLayout.CENTER);
        frame.addMouseMotionListener(this);
        try 
        {
            explosion = scaleImage(new ImageIcon(ImageIO.read(BattagliaNavaleClient.class.getResource("/resources/images/explosion.png"))),yourBoard[0][0].getWidth(),yourBoard[0][0].getHeight());
        } catch (IOException ex) {}
        elencoNavi(shipsDim);
        
        face.setSize(face.getPreferredSize());
        frame.pack();
        face.setLocation(facePanel.getWidth()/2-(int)face.getPreferredSize().getWidth()/2,(frame.getHeight()/2)-facePanel.getY()-(int)face.getPreferredSize().getHeight()-menuBar.getHeight());
        
         fRules.setPreferredSize(new Dimension(500, 650));
         fRules.setResizable(false);
         fRules.pack();
        JMenuItem jmi = new JMenuItem("Regole");
        helpMenu.add(jmi);
        jmi.addActionListener(this);
    }

    @Override
    public void mousePressed(MouseEvent e)
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
                    this.mouseOverSquare = source;
                    if(selectedSquare.equals(source))
                    {
                        selectedSquare = null;
                        return;
                    }
                    insert(selectedSquare.x, selectedSquare.y, calcDir(selectedSquare, source));
                    selectedSquare = null;
                }
            }
        }
        else
        {
            if(status.equals("ATT"))
            {
                attack(source.x, source.y);
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e){}    //unused

    @Override
    public void mouseReleased(MouseEvent e){}   //unused

    @Override
    public void mouseEntered(MouseEvent e)  //TODO: Mancano controlli
    {
        this.mouseOverSquare = (Square) e.getSource();
        if(this.mouseOverSquare.getParent().equals(yourBoardPanel))
        //<editor-fold defaultstate="collapsed" desc="Mouse in yourField">
        {
            if(status.startsWith("INS"))
            {
                if(selectedSquare == null)
                    (mouseOverSquare).setBackground(Color.green);
                else
                {
                    int len = Integer.parseInt(this.status.split(" ")[1]);
                    char dir = calcDir(selectedSquare, mouseOverSquare);
                    if(isInBounds(selectedSquare.x, selectedSquare.y, dir, len) && isPositionValid(selectedSquare.x, selectedSquare.y, dir, len))
                    {
                        (mouseOverSquare).setBackground(Color.blue);
                        switch(dir)
                        {
                            case 'n':
                                for(int i = 0; i < len; i++)
                                {
                                    yourBoard[selectedSquare.x][selectedSquare.y-i].setBackground(Color.red);
                                }
                                break;
                            case 'w':
                                for(int i = 0; i < len; i++)
                                {
                                    yourBoard[selectedSquare.x-i][selectedSquare.y].setBackground(Color.red);
                                }
                                break;
                            case 's':
                                for(int i = 0; i < len; i++)
                                {
                                    yourBoard[selectedSquare.x][selectedSquare.y+i].setBackground(Color.red);
                                }
                                break;
                            case 'e':
                                for(int i = 0; i < len; i++)
                                {
                                    yourBoard[selectedSquare.x+i][selectedSquare.y].setBackground(Color.red);
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
                this.mouseOverSquare.setBackground(Color.red);
            }
            else
            {
                this.mouseOverSquare.setBackground(Color.gray);
            }
        }
    }

    @Override
    public void mouseExited(MouseEvent e)   //TODO: Mancano controlli
    {
        this.mouseOverSquare = null;
        Square source = (Square)e.getSource();
        source.resetColor();
        if(source.getParent().equals(yourBoardPanel))
        {
            if(status.startsWith("INS"))
            {
                if(selectedSquare != null)
                {
                    int len = Integer.parseInt(status.split(" ")[1]);
                    switch(calcDir(selectedSquare, source))     //Should be moved in another method
                    {
                        case 'n':
                            for(int i = 0; i < len; i++)
                            {
                                if(selectedSquare.y-i>=0)
                                    yourBoard[selectedSquare.x][selectedSquare.y-i].resetColor();
                            }
                        break;
                        case 'w':
                            for(int i = 0; i < len; i++)
                            {
                                if(selectedSquare.x-i>=0)
                                    yourBoard[selectedSquare.x-i][selectedSquare.y].resetColor();
                            }
                        break;
                        case 's':
                            for(int i = 0; i < len; i++)
                            {
                                if(selectedSquare.y+i<dim)
                                    yourBoard[selectedSquare.x][selectedSquare.y+i].resetColor();
                            }
                        break;
                        case 'e':
                            for(int i = 0; i < len; i++)
                            {
                                if(selectedSquare.x+i<dim)
                                    yourBoard[selectedSquare.x+i][selectedSquare.y].resetColor();
                            }
                        break;
                    }
                }
            }
        }
    }
    
    private char calcDir(Square s1, Square s2)
    {
        int diffx = s1.x - s2.x;
        int diffy = s1.y - s2.y;

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
                            if(i>=0 && i<dim && j>=0 && j<dim)
                                if(yourBoard[i][j].getBackground().equals(Color.red))
                                    return false;
                        }
                    }
                    break;
                case 'w':
                    for(int i = x + 1; i >= x - len; i--)
                    {
                        for(int j = y - 1; j <= y + 1; j++)
                        {
                            if(i>=0 && i<dim && j>=0 && j<dim)
                                if(yourBoard[i][j].getBackground().equals(Color.red))
                                    return false;
                        }
                    }
                    break;
                case 's':
                    for(int i = x - 1; i <= x+1; i++)
                    {
                        for(int j = y - 1; j <= y + len; j++)
                        {
                            if(i>=0 && i<dim && j>=0 && j<dim)
                                if(yourBoard[i][j].getBackground().equals(Color.red))
                                    return false;
                        }
                    }
                    break;
                case 'e':
                    for(int i = x - 1; i <= x + len; i++)
                    {
                        for(int j = y - 1; j <= y + 1; j++)
                        {
                            if(i>=0 && i<dim && j>=0 && j<dim)
                                if(yourBoard[i][j].getBackground().equals(Color.red))
                                    return false;
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
    
    void drawWater(int x, int y)
    {
        opponentBoard[x][y].setColor(Color.CYAN);
        opponentBoard[x][y].setBackground(Color.CYAN);
    }
    
    void drawOppHitWater(int x, int y)
    {
        yourBoard[x][y].setColor(Color.CYAN);
        yourBoard[x][y].setBackground(Color.CYAN);
    }
    
    void drawOppHit(int x, int y)
    {
        yourBoard[x][y].setLayout(new BorderLayout());
        yourBoard[x][y].add(new JLabel(explosion));
        yourBoard[x][y].setColor(Color.red);
        yourBoard[x][y].setBackground(Color.red);
    }

    void setError(String err)
    {
        messageLabel.setText(err);
        messageLabel.setForeground(Color.red);
    }
    
    void drawNotHit(int x, int y)
    {
        opponentBoard[x][y].setColor(Color.ORANGE);
        opponentBoard[x][y].setBackground(Color.ORANGE);
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {} //Unused

    @Override
    public void mouseMoved(MouseEvent e)
    {
        if(e.getXOnScreen() < frame.getX() + (frame.getWidth() / 2.34))
        {  
            if(e.getYOnScreen() < frame.getY() + (frame.getHeight() / 2.69))  //Needs to be recalibrated
            {
                face.setText("('◡' )");
            }
            else if(e.getYOnScreen() > frame.getY() + (frame.getHeight() / 1.68))  //Needs to be recalibrated
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
            if(e.getYOnScreen() < frame.getY() + (frame.getHeight() / 2.69))  //Needs to be recalibrated
            {
                face.setText("( '◡')");
            }
            else if(e.getYOnScreen() > frame.getY() + (frame.getHeight() / 1.68))  //Needs to be recalibrated
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
            if(e.getYOnScreen() < frame.getY() + (frame.getHeight()/2.69))  //Needs to be recalibrated
            {
                face.setText("('◡')");
            }
            else if(e.getYOnScreen() > frame.getY() + (frame.getHeight()/1.68))  //Needs to be recalibrated
            {
                face.setText("(.‿.)");
            }
            else
            {
                face.setText("(｡◕‿◕｡)");
            }
        }
        face.setSize(face.getPreferredSize());
        frame.pack();
        face.setLocation(facePanel.getWidth()/2-(int)face.getPreferredSize().getWidth()/2,(frame.getHeight()/2)-facePanel.getY()-(int)face.getPreferredSize().getHeight()-menuBar.getHeight());
    }

    void drawBoard(ArrayList<String> board)
    {
        for(String s : board)
        {
            String[] command = s.split(" ");
            switch(command[0])
            {
                case "HIT": //Hit
                    drawHit(Integer.parseInt(command[1]), Integer.parseInt(command[2]));
                    break;
                case "WAT": //Water
                    drawWater(Integer.parseInt(command[1]), Integer.parseInt(command[2]));
                    break;
                case "THY": //They Hit You
                    drawOppHit(Integer.parseInt(command[1]), Integer.parseInt(command[2]));
                    break;
                case "THW": //They Hit Water
                    drawOppHitWater(Integer.parseInt(command[1]), Integer.parseInt(command[2]));
                    break;
            }
        }
        yourBoardPanel.updateUI();
    }
    
    public ImageIcon scaleImage(ImageIcon icon, int w, int h)
    {
        int nw = icon.getIconWidth();
        int nh = icon.getIconHeight();

        if(icon.getIconWidth() > w)
        {
            nw = w;
            nh = (nw * icon.getIconHeight()) / icon.getIconWidth();
        }

        if(nh > h)
        {
            nh = h;
            nw = (icon.getIconWidth() * nh) / icon.getIconHeight();
        }

        return new ImageIcon(icon.getImage().getScaledInstance(nw, nh, Image.SCALE_SMOOTH));
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if(e.getActionCommand().equals("Regole"))
        {
            rulesFrame rules = new rulesFrame();
            JLabel text;
            text = rules.rules();
             fRules.getContentPane().add(text,BorderLayout.NORTH);
             
        }
         fRules.show();
    }
}
