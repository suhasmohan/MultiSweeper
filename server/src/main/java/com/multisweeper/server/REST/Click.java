package com.multisweeper.server.REST;

public class Click {

  private int row;
  private int col;
  private String type;

  public Click(int row, int col, String type) {
    this.row = row;
    this.col = col;
    this.type = type;
  }

  int getRow() {
    return row;
  }

  int getCol() {
    return col;
  }

  public boolean isOpen() {
    return type.equalsIgnoreCase("open");
  }

  public boolean isFlag() {
    return type.equalsIgnoreCase("flag");
  }

  public boolean isRestart() {
    return type.equalsIgnoreCase("restart");
  }

  public String getType() {
    return type;
  }
}
