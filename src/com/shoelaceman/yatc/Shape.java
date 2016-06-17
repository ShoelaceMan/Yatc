package com.shoelaceman.yatc;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Shape
{
  enum Tetrominoes { NoShape, ZShape, SShape, LineShape,
    TShape, SquareShape, LShape, MirroredLShape };

  Tetrominoes[] values = Tetrominoes.values();

  Random rand = new Random();
  private Tetrominoes pieceShape;
  private Tetrominoes nextPieceShape;
  private int inarow;
  private int[] bag = {1, 2, 3, 4, 4, 5, 5, 6, 6, 7};
  private int coords[][];
  private int[][][] coordsTable;

  public Shape()
  {
    coords = new int[4][2];
    setShape(Tetrominoes.NoShape);
  }

  public void setShape(Tetrominoes shape)
  {
    coordsTable = new int[][][]
    {
      { { 0, 0 },   { 0, 0 },   { 0, 0 },   { 0, 0 } },
      { { 0, -1 },  { 0, 0 },   { -1, 0 },  { -1, 1 } },
      { { 0, -1 },  { 0, 0 },   { 1, 0 },   { 1, 1 } },
      { { 0, -1 },  { 0, 0 },   { 0, 1 },   { 0, 2 } },
      { { -1, 0 },  { 0, 0 },   { 1, 0 },   { 0, 1 } },
      { { 0, 0 },   { 1, 0 },   { 0, 1 },   { 1, 1 } },
      { { -1, -1 }, { 0, -1 },  { 0, 0 },   { 0, 1 } },
      { { 1, -1 },  { 0, -1 },  { 0, 0 },   { 0, 1 } }
    };

    for (int i = 0; i < 4 ; i++)
    {
      for (int j = 0; j < 2; ++j)
      {
        coords[i][j] = coordsTable[shape.ordinal()][i][j];
      }
    }

    pieceShape = shape;
  }

  private void setX(int index, int x)
  {
    coords[index][0] = x;
  }

  private void setY(int index, int y)
  {
    coords[index][1] = y;
  }

  public int x(int index)
  {
    return coords[index][0];
  }

  public int y(int index)
  {
    return coords[index][1];
  }

  public Tetrominoes getShape()
  {
    return pieceShape;
  }


  public Tetrominoes getNextShape()
  {
    return nextPieceShape;
  }

  public void setNewShapeBag(int when)
  {
    if (when == 1)
    {
      shuffleArray(bag);
      nextPieceShape = values[bag[1]];
    } else if (when == 2)
    {
      inarow = 0;
      int[] dupeCheck = {1, 2, 3, 4, 5, 6, 7};
      shuffleArray(dupeCheck);

      for (int i = 2; i < 7; i++)
      {
        bag[i] = dupeCheck[i];
      }
    }
  }

  static void shuffleArray(int[] ar)
  {
    Random rnd = ThreadLocalRandom.current();
    for (int i = ar.length - 1; i > 0; i--)
    {
      int index = rnd.nextInt(i + 1);
      int a = ar[index];
      ar[index] = ar[i];
      ar[i] = a;
    }
  }

  public void setRandomShape()
  {
    inarow++;

    for (int i = 1; i < 8; i++)
    {
      bag[(i - 1)] =  bag[i];
    }

    if (inarow > 5)
    {
      setNewShapeBag(2);
    }

    setShape(nextPieceShape);
    nextPieceShape = values[bag[1]];
  }

  public int minX()
  {
    int m = coords[0][0];
    for (int i=0; i < 4; i++)
    {
      m = Math.min(m, coords[i][0]);
    }
    return m;
  }


  public int minY()
  {
    int m = coords[0][1];
    for (int i=0; i < 4; i++)
    {
      m = Math.min(m, coords[i][1]);
    }
    return m;
  }

  public Shape rotateLeft()
  {
    if (pieceShape == Tetrominoes.SquareShape)
    {
      return this;
    }

    int[] bagCase;
    int lastInarow = inarow;
    bagCase = new int[7];

    for (int i = 0; i < 6; i++)
    {
      bagCase[i] = bag[i];
    }

    Shape result = new Shape();
    result.pieceShape = pieceShape;

    for (int i = 0; i < 4; ++i)
    {
      result.setX(i, y(i));
      result.setY(i, -x(i));
    }

    result.setNewShapeBag(1);
    for (int i = 0; i < 6; i++)
    {
      result.bag[i] = bagCase[i];
    }

    result.nextPieceShape = values[bagCase[1]];
    result.inarow = lastInarow;

    return result;
  }

  public Shape rotateRight()
  {
    if (pieceShape == Tetrominoes.SquareShape)
    {
      return this;
    }

    int[] bagCase;
    int lastInarow = inarow;
    bagCase = new int[7];

    for (int i = 0; i < 6; i++)
    {
      bagCase[i] = bag[i];
    }

    Shape result = new Shape();
    result.pieceShape = pieceShape;

    for (int i = 0; i < 4; ++i)
    {
      result.setX(i, -y(i));
      result.setY(i, x(i));
    }

    result.setNewShapeBag(1);
    for (int i = 0; i < 6; i++)
    {
      result.bag[i] = bagCase[i];
    }

    result.nextPieceShape = values[bagCase[1]];
    result.inarow = lastInarow;

    return result;
  }
}
