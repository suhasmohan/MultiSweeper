package com.multisweeper.server.logic;

import java.io.Serializable;

class Cell implements Serializable {
  private static final long serialVersionUID = -5808216196821592318L;
  private Tile tile;
  private int key;
  private boolean bomb;
  private int bombCount;

  Cell() {
    tile = Tile.CLOSED;
    key = -1;
    bomb = false;
    bombCount = 0;
  }

  boolean isEmpty() {
    return bombCount == 0;
  }

  boolean isMine() {
    return bomb;
  }

  boolean isOpen() {
    return tile == Tile.OPEN;
  }

  boolean isFlag() {
    return tile == Tile.FLAG;
  }

  void setMine() {
    bomb = true;
    bombCount = 9;
  }

  void addCount() {
    bombCount++;
  }

  int getKey() {
    return key;
  }

  void setKey(int key) {
    this.key = key;
  }

  Tile getTile() {
    return tile;
  }

  void setTile(Tile tile) {
    this.tile = tile;
  }

  int getCount() {
    return bombCount;
  }
}
