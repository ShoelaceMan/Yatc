package com.shoelaceman.yatc;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.DocumentFilter;

import com.shoelaceman.yatc.highscore.HighscoreManager;
import com.shoelaceman.yatc.Shape.Tetrominoes;

public class Tetris extends JFrame
{
  static final long serialVersionUID = 23L;

  JLabel nextShape = new JLabel();
  JLabel scoreR = new JLabel();
  JLabel statusbar;

  public Tetris()
  {
    File f = new File("scores.dat");
    if(!(f.exists()))
    {
      setDefaultScores();
    }

    // The game itself
    Panel gameArea = new Panel();
    statusbar = new JLabel(" "); // Score
    Board board = new Board(this);
    board.setNextPiece(Tetrominoes.NoShape);

    gameArea.setLayout(new BorderLayout());
    board.setBorder(BorderFactory.createEtchedBorder()); // Border

    gameArea.add(board, BorderLayout.CENTER);

    // The toolbar
    JToolBar tools = new JToolBar();
    tools.setLayout(new FlowLayout(FlowLayout.LEFT));
    JButton newGameButton = new JButton("New Game");
    JButton scoresButton = new JButton("High-Scores");
    JButton settingsButton = new JButton("Settings");

    newGameButton.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        statusbar.setText("Score: 0 Lines: 0 Level: 0");
        board.requestFocus();
        board.start();
      }
    });

    scoresButton.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        showHighscores();
      }
    });

    settingsButton.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        System.out.println("Not implemented");
        //TODO: Implement this
      }
    });

    tools.add(newGameButton);
    tools.add(scoresButton);
    //tools.add(settingsButton);

    // Game info (Rules, next Tetromino, etc.)
    Panel infoArea = new Panel();
    infoArea.setLayout(new BorderLayout());
    nextShape.setBorder(BorderFactory.createEtchedBorder()); // Border
    JLabel scoreL = new JLabel("<html><body>Score: <br>Lines:" +
        " <br>Level: </body></html>");
    scoreR.setText("<html><body>0&ensp;<br>0&ensp;<br>0&ensp;</body></html>");

    infoArea.add(scoreR, BorderLayout.EAST);
    infoArea.add(scoreL, BorderLayout.WEST);
    infoArea.add(nextShape, BorderLayout.NORTH);

    // Window
    setTitle("Yatc");
    setSize(295,400);
    setDefaultCloseOperation(EXIT_ON_CLOSE);

    add(tools, BorderLayout.NORTH);
    add(gameArea, BorderLayout.CENTER);
    add(statusbar, BorderLayout.SOUTH);
    add(infoArea, BorderLayout.EAST);

    // Click field to grab focus
    gameArea.addMouseListener(new MouseAdapter()
    {
      @Override
      public void mouseClicked(MouseEvent e)
      {
        board.requestFocus();
        super.mouseClicked(e);
      }
    });
  }

  public void setNewHighscore(int score)
  {
    EventQueue.invokeLater(new Runnable()
    {
      @Override
      public void run()
      {
        try
        {
          UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException |
            IllegalAccessException | UnsupportedLookAndFeelException ex)
        {
          ex.printStackTrace();
        }

        JTextField pfPassword = new JTextField(20);
        ((AbstractDocument)pfPassword.getDocument())
            .setDocumentFilter(new LimitDocumentFilter(7));

        JFrame frame = new JFrame("New High Score");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridBagLayout());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        HighscoreManager hm = new HighscoreManager();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(355,255);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(true);
        JTextArea textArea = new JTextArea(11, 28);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setText(hm.getHighscoreString());
        JScrollPane scroller = new JScrollPane(textArea);
        JPanel inputpanel = new JPanel();
        inputpanel.setLayout(new FlowLayout());

        JButton enterButton = new JButton("Enter");
        enterButton.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
            hm.addScore(pfPassword.getText(),score);
            frame.dispose();
          }
        });

        DefaultCaret caret = (DefaultCaret) textArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        panel.add(scroller);
        inputpanel.add(pfPassword);
        inputpanel.add(enterButton);
        panel.add(inputpanel);
        frame.getContentPane().add(BorderLayout.CENTER, panel);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
        frame.setResizable(false);
        pfPassword.requestFocus();
      }
    });
  }

  public class LimitDocumentFilter extends DocumentFilter
  {
    private int limit;

    public LimitDocumentFilter(int limit)
    {
      if (limit <= 0)
      {
        throw new IllegalArgumentException("Limit can not be <= 0");
      }

      this.limit = limit;
    }

    @Override
    public void replace(
        FilterBypass fb, int offset, int length, String text,
        AttributeSet attrs) throws BadLocationException
    {
      int currentLength = fb.getDocument().getLength();
      int overLimit = (currentLength + text.length()) - limit - length;

      if (overLimit > 0)
      {
        text = text.substring(0, text.length() - overLimit);
      }

      if (text.length() > 0)
      {
        super.replace(fb, offset, length, text, attrs);
      }
    }
  }

  public void setDefaultScores()
  {
    // Generate default High Scores, and names
    HighscoreManager hm = new HighscoreManager();
    hm.addScore("Zalgo",3500);
    hm.addScore("Rudy",4500);
    hm.addScore("Doc",5500);
    hm.addScore("Klaskro",5750);
    hm.addScore("Ryuko",6500);
    hm.addScore("Divad",7000);
    hm.addScore("Cthulu",7250);
    hm.addScore("Chuck",8750);
    hm.addScore("Isaac",9500);
    hm.addScore("Amelia",10000);
  }

  public static void showHighscores()
  {
    EventQueue.invokeLater(new Runnable()
    {
      @Override
      public void run()
      {
        HighscoreManager hm = new HighscoreManager();
        JFrame frame = new JFrame("High Scores");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(true);
        JTextArea textArea = new JTextArea(11, 28);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setText(hm.getHighscoreString());
        JScrollPane scroller = new JScrollPane(textArea);
        JPanel inputpanel = new JPanel();
        DefaultCaret caret = (DefaultCaret) textArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        panel.add(scroller);
        frame.getContentPane().add(BorderLayout.CENTER, panel);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
        frame.setResizable(false);
      }
    });
  }

  public JLabel getStatusBar()
  {
    return statusbar;
  }

  public JLabel getscoreR()
  {
    return scoreR;
  }

  public JLabel getNextShape()
  {
    return nextShape;
  }

  public static void main(String[] args)
  {
    Tetris game = new Tetris();
    game.setLocationRelativeTo(null);
    game.setVisible(true);
  }
}
