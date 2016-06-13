package com.shoelaceman.yatc.highscore;

import java.io.Serializable;

public class Score  implements Serializable
{
  static final long serialVersionUID = -7586138168126270505L;

  private int score;
  private String naam;

  public int getScore()
  {
    return score;
  }

  public String getNaam()
  {
    return naam;
  }

  public Score(String naam, int score)
  {
    this.score = score;
    this.naam = naam;
  }
}
