package com.multisweeper.server.logic;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

class Cell implements Serializable {
  private static final long serialVersionUID = -5808216196821592318L;
  private static final boolean DEFAULT_MINE = false;
  private static final int DEFAULT_COUNT = 0;
  @Expose private Tile tile;
  private int key;
  @Expose private boolean bomb;
  private boolean mine;
  @Expose private int bombCount;
  private int mineCount;

  Cell() {
    tile = Tile.CLOSED;
    key = -1;
    bomb = Cell.DEFAULT_MINE;
    mine = Cell.DEFAULT_MINE;
    bombCount = Cell.DEFAULT_COUNT;
    mineCount = Cell.DEFAULT_COUNT;
  }

  boolean isEmpty() {
    return mineCount == Cell.DEFAULT_COUNT;
  }

  boolean isMine() {
    return mine;
  }

  boolean isOpen() {
    return tile == Tile.OPEN;
  }

  boolean isFlag() {
    return tile == Tile.FLAG;
  }

  void setMine() {
    mine = true;
    mineCount = 9;
  }

  void addCount() {
    mineCount++;
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
    if (tile == Tile.OPEN) {
      bomb = mine;
      bombCount = mineCount;
    } else {
      bomb = Cell.DEFAULT_MINE;
      bombCount = Cell.DEFAULT_COUNT;
    }
  }

  int getCount() {
    return mineCount;
  }
}
