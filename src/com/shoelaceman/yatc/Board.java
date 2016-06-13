package com.shoelaceman.yatc;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import com.shoelaceman.yatc.Shape.Tetrominoes;
import com.shoelaceman.yatc.highscore.HighscoreManager;

public class Board extends JPanel implements ActionListener
{
  static final long serialVersionUID = 35L;

  final int BoardWidth = 10;
  final int BoardHeight = 22;

  Timer timer;
  boolean isFallingFinished = false;
  boolean isStarted = false;
  boolean isPaused = false;
  int numLinesRemoved = 0;
  int curX = 0;
  int curY = 0;
  JLabel statusbar;
  JLabel nextShape;
  JLabel rules;
  Shape curPiece;
  Tetrominoes[] board;

  public Board(Tetris parent)
  {
    setFocusable(true);
    curPiece = new Shape();
    timer = new Timer(400, this);

    statusbar =  parent.getStatusBar();
    nextShape = parent.getNextShape();
    board = new Tetrominoes[BoardWidth * BoardHeight];
    addKeyListener(new TAdapter());
    clearBoard();
  }

  public void actionPerformed(ActionEvent e)
  {
    if (isFallingFinished)
    {
      isFallingFinished = false;
      newPiece();
    } else
    {
      oneLineDown();
    }
  }

  int squareWidth() { return 200 / BoardWidth; }
  int squareHeight() { return 400 / BoardHeight; }
  Tetrominoes shapeAt(int x, int y) { return board[(y * BoardWidth) + x]; }

  public void start()
  {
    if (isPaused)
    {
      return;
    }

    isStarted = true;
    isFallingFinished = false;
    numLinesRemoved = 0;
    clearBoard();

    newPiece();
    timer.start();
    }

  private void pause()
  {
    if (!isStarted)
    {
      return;
    }

    isPaused = !isPaused;
    if (isPaused)
    {
      timer.stop();
      statusbar.setText("paused");
    } else
    {
      timer.start();
      statusbar.setText(String.valueOf(numLinesRemoved));
    }
    repaint();
  }

  public void paint(Graphics g)
  {
    super.paint(g);

    Dimension size = getSize();
    int boardTop = (int) size.getHeight() - BoardHeight * squareHeight();


    for (int i = 0; i < BoardHeight; ++i)
    {
      for (int j = 0; j < BoardWidth; ++j)
      {
        Tetrominoes shape = shapeAt(j, BoardHeight - i - 1); // Landed Shapes
        if (shape != Tetrominoes.NoShape)
        {
          drawSquare(g, 0 + j * squareWidth(),
              boardTop + i * squareHeight(), shape);
        }
      }
    }

    if (curPiece.getShape() != Tetrominoes.NoShape) // Falling shapes
    {
      for (int i = 0; i < 4; ++i)
      {
        int x = curX + curPiece.x(i);
        int y = curY - curPiece.y(i);
        drawSquare(g, 0 + x * squareWidth(),
            boardTop + (BoardHeight - y - 1) * squareHeight(),
            curPiece.getShape());
      }
    }
  }

  private void dropDown()
  {
    int newY = curY;
    while (newY > 0)
    {
      if (!tryMove(curPiece, curX, newY - 1))
      {
        break;
      }
      --newY;
    }
    pieceDropped();
  }

  private void oneLineDown()
  {
    if (!tryMove(curPiece, curX, curY - 1))
    {
      pieceDropped();
    }
  }

  private void clearBoard()
  {
    for (int i = 0; i < BoardHeight * BoardWidth; ++i)
    {
      board[i] = Tetrominoes.NoShape;
    }
  }

  private void pieceDropped()
  {
    for (int i = 0; i < 4; ++i)
    {
      int x = curX + curPiece.x(i);
      int y = curY - curPiece.y(i);
      board[(y * BoardWidth) + x] = curPiece.getShape();
    }

    removeFullLines();

    if (!isFallingFinished)
    {
      newPiece();
    }
  }

  private void newPiece()
  {
    curPiece.setRandomShape();
    curX = BoardWidth / 2 + 1;
    curY = BoardHeight - 1 + curPiece.minY();

    if (!tryMove(curPiece, curX, curY))
    {
      curPiece.setShape(Tetrominoes.NoShape);
      timer.stop();
      isStarted = false;
      statusbar.setText("game over");

      HighscoreManager hm = new HighscoreManager();
      if (numLinesRemoved > hm.getLowHighscoreInt())
      {
        Tetris intf = new Tetris();
        intf.setNewHighscore(numLinesRemoved);
      }
    }

    URL iconUrl = this.getClass().getResource("/Resources/" + curPiece.getNextShape() + ".png");
    Toolkit tk = this.getToolkit();
    nextShape.setIcon(new ImageIcon(tk.getImage(iconUrl)));

    /*
    nextShape.setIcon(new ImageIcon("/Resources/" +
          curPiece.getNextShape() + ".png"));
    */
  }

  private boolean tryMove(Shape newPiece, int newX, int newY)
  {
    for (int i = 0; i < 4; ++i)
    {
      int x = newX + newPiece.x(i);
      int y = newY - newPiece.y(i);
      if (x < 0 || x >= BoardWidth || y < 0 || y >= BoardHeight)
      {
        return false;
      }
      if (shapeAt(x, y) != Tetrominoes.NoShape)
      {
        return false;
      }
    }

    curPiece = newPiece;
    curX = newX;
    curY = newY;
    repaint();
    return true;
  }

  private void removeFullLines()
  {
    int numFullLines = 0;

    for (int i = BoardHeight - 1; i >= 0; --i)
    {
      boolean lineIsFull = true;

      for (int j = 0; j < BoardWidth; ++j)
      {
        if (shapeAt(j, i) == Tetrominoes.NoShape)
        {
          lineIsFull = false;
          break;
        }
      }

      if (lineIsFull)
      {
        ++numFullLines;
        if (numFullLines <= 300) //Speed up as the player scores
        {
          timer.setDelay((400 - numFullLines));
        }

        for (int k = i; k < BoardHeight - 1; ++k)
        {
          for (int j = 0; j < BoardWidth; ++j)
          {
            board[(k * BoardWidth) + j] = shapeAt(j, k + 1);
          }
        }
      }
    }

    if (numFullLines > 0)
    {
      numLinesRemoved += numFullLines;
      statusbar.setText(String.valueOf(numLinesRemoved));
      isFallingFinished = true;
      curPiece.setShape(Tetrominoes.NoShape);
      repaint();
    }
  }

  private void drawSquare(Graphics g, int x, int y, Tetrominoes shape)
  {
    Color colors[] =
    { new Color(0, 0, 0), new Color(204, 102, 102),
      new Color(102, 204, 102), new Color(102, 102, 204),
      new Color(204, 204, 102), new Color(204, 102, 204),
      new Color(102, 204, 204), new Color(218, 170, 0)
    };

    Color color = colors[shape.ordinal()];

    g.setColor(color);
    g.fillRect(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2);

    g.setColor(color.brighter());
    g.drawLine(x, y + squareHeight() - 1, x, y);
    g.drawLine(x, y, x + squareWidth() - 1, y);

    g.setColor(color.darker());
    g.drawLine(x + 1, y + squareHeight() - 1,
        x + squareWidth() - 1, y + squareHeight() - 1);

    g.drawLine(x + squareWidth() - 1, y + squareHeight() - 1,
        x + squareWidth() - 1, y + 1);
  }

  class TAdapter extends KeyAdapter
  {
    public void keyPressed(KeyEvent e)
    {
      if (!isStarted || curPiece.getShape() == Tetrominoes.NoShape)
      {
        return;
      }

      int keycode = e.getKeyCode();

      if (keycode == 'p' || keycode == 'P')
      {
        pause();
        return;
      }

      if (isPaused)
      {
        return;
      }

      switch (keycode)
      {
        case KeyEvent.VK_LEFT:
          tryMove(curPiece, curX - 1, curY);
          break;
        case KeyEvent.VK_RIGHT:
          tryMove(curPiece, curX + 1, curY);
          break;
        case KeyEvent.VK_DOWN:
          dropDown();
          break;
        case KeyEvent.VK_Z:
          tryMove(curPiece.rotateLeft(), curX, curY);
          break;
        case KeyEvent.VK_X:
          tryMove(curPiece.rotateRight(), curX, curY);
          break;
      }
    }
  }
}
