package com.multisweeper.server.logic;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import java.io.*;
import java.util.Random;

/**
 * Board class provides the data and methods for a minesweeper game
 *
 * <p>Level 1 - data & methods to implement Mines, Clues, Tiles, basic Board characters, and
 * opening/marking tiles
 *
 * <p>Level 2 - Additional data & methods to support game status and extended Board characters
 *
 * <p>Board.java
 */
public class Board implements Serializable {

  private static final long serialVersionUID = -2188786541491928301L;
  @Expose private final int nMines;
  @Expose private final int nRows;
  @Expose private final int nCols;
  private final int allCells;
  private int nOpen;
  @Expose private int bomb;
  /**
   * tile values 0 - open, 1 - closed,<br>
   * 2 - question, 3 - mine
   */
  @Expose private Cell[][] cells;

  /** Level 2 - game status win, lose, play */
  @Expose private Status status;
  /**
   * default constructor<br>
   * board size 10 x 10<br>
   * create mines and tile arrays<br>
   * place mines<br>
   * calculate clues<br>
   * (*)set game status to play<br>
   */
  public Board() {
    this(10, 10, 0.15);
  }
  /**
   * alternate constructor use specifies board size<br>
   * create mines and tile arrays<br>
   * place mines<br>
   * calculate clues<br>
   * (*)set game status to play<br>
   *
   * @param nRows number of rows for grid<br>
   * @param nCols number of columns for grid<br>
   * @param rate rate of mines in field
   */
  private Board(int nRows, int nCols, double rate) {
    if (nRows <= 0 || nCols <= 0 || rate < 0.0 || rate > 1.0) {
      throw new IllegalArgumentException("Wrong board parameter");
    }
    this.nRows = nRows;
    this.nCols = nCols;
    nMines = (int) (nRows * nCols * rate);
    bomb = nMines;
    allCells = nRows * nCols;
    nOpen = 0;
    initGame();
  }

  public static Board fromFile() {
    try {
      FileInputStream fi = new FileInputStream(new File("board.txt"));
      ObjectInputStream oi = new ObjectInputStream(fi);

      // Read objects
      Board board = (Board) oi.readObject();

      oi.close();
      fi.close();
      return board;

    } catch (FileNotFoundException e) {
      System.out.println("File not found");
    } catch (IOException e) {
      System.out.println("Error initializing stream");
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Level 2 - game won status
   *
   * @return true if game won false if game not won
   */
  private boolean gameWon() {
    // add your code here
    return status == Status.WIN; // this line must be modified
  }

  /**
   * determines if x,y is valid position
   *
   * @param x row index
   * @param y column index
   * @return true if valid position on board, false if not valid board position
   */
  private boolean validIndex(int x, int y) {
    // add your code here
    if (x < 0 || y < 0 || x >= nRows || y >= nCols) {
      return false;
    }
    return true; // this line must be modified
  }

  /**
   * getBoard - determines current game board character for r,c position <br>
   * using the value of the mines[][] and tiles[][]array<br>
   * Note: Level 2 values are returned when <br>
   * game is over (ie. status is "win" or "lose")<br>
   * <br>
   * <br>
   * Level 1 values<br>
   * '1'-'8' opened tile showing clue value<br>
   * ' ' opened tile blank<br>
   * 'X' tile closed<br>
   * '?' tile closed marked with ?<br>
   * 'F' tile closed marked with flag<br>
   * '*' mine<br>
   * <br>
   * <br>
   * Level 2 values<br>
   * '-' if game lost, mine that was incorrectly flagged<br>
   * '!' if game lost, mine that ended game<br>
   * 'F' if game won, all mines returned with F <br>
   *
   * @return char representing game board at r,c
   */
  private char getBoard(int r, int c) {
    Cell cell = cells[r][c];
    Tile tile = cell.getTile();
    if (tile == Tile.CLOSED) {
      if (status == Status.LOSE && cell.isMine()) {
        return '*';
      } else {
        return '_';
      }
    } else if (tile == Tile.FLAG) {
      return 'F';
    } else if (tile == Tile.OPEN) {
      if (cell.isEmpty()) {
        return ' ';
      } else if (cell.isMine()) {
        return '@';
      } else if (!cell.isMine() && !cell.isEmpty()) {
        return (char) ('0' + cell.getCount());
      }
    }

    return '?';
  }

  /**
   * Level 2 - game status
   *
   * @return "play", "win", or "lose"
   */
  String getStatus() {
    return status.toString();
  }

  /**
   * number of rows for board
   *
   * @return number of rows
   */
  int getRows() {
    return nRows;
  }

  /**
   * number of columns for board
   *
   * @return number of columns
   */
  int getCols() {
    return nCols;
  }

  /**
   * value of the mines array at r,c<br>
   * -1 is returned if invalid r,c
   *
   * @param r row index
   * @param c column index
   * @return value of mines array, -1 if invalid
   */
  public int getMines(int r, int c) {
    if (validIndex(r, c)) {
      return cells[r][c].getCount();
    } else {
      throw new IllegalArgumentException("Invalid coordinate");
    }
  }

  /**
   * value of the tiles array at r,c -1 is returned if invalid r,c<br>
   *
   * @param r row index
   * @param c column index
   * @return value of tiles array, -1 if invalid
   */
  public Tile getTiles(int r, int c) {
    if (validIndex(r, c)) {
      return cells[r][c].getTile();
    } else {
      throw new IllegalArgumentException("Invalid coordinate");
    }
  }

  public int getKeys(int r, int c) {
    if (validIndex(r, c)) {
      return cells[r][c].getKey();
    } else {
      throw new IllegalArgumentException("Invalid coordinate");
    }
  }

  public int getSingleKeys(int r, int c) {
    if (validIndex(r, c)) {
      return r * nCols + c;
    } else {
      throw new IllegalArgumentException("Invalid coordinate");
    }
  }

  /**
   * mark tile - open tile, close tile, <br>
   * flag tile as mine<br>
   * <br>
   * Level 1 - Requirements<br>
   * - invalid r,c values must be ignored<br>
   * - a tile that is opened must stay open<br>
   * - a tile that is marked as a flag (ie. tile[][] value 3) can not be opened<br>
   * <br>
   * Level 2 - Requirements<br>
   * - tile values can only change when game status is "play"<br>
   * - game status must be updated after a tile is opened<br>
   * <br>
   *
   * @param r row index
   * @param c column index
   */
  void tileOpen(int r, int c) {
    if (validIndex(r, c)) {
      if (status == Status.PLAY) {
        Tile current_t = cells[r][c].getTile();
        if (current_t == Tile.CLOSED) {
          cells[r][c].setTile(Tile.OPEN);
          nOpen++;
          if (cells[r][c].isMine()) {
            status = Status.LOSE;
          } else {
            if (cells[r][c].isEmpty()) {
              for (int dr = -1; dr <= 1; dr++) {
                for (int dc = -1; dc <= 1; dc++) {
                  tileOpen(r + dr, c + dc);
                }
              }
            }
            checkWin();
          }
        }
      }
    }
  }

  private void checkWin() {
    if (nOpen == allCells - nMines) {
      status = Status.WIN;
    }
  }

  void tileFlag(int r, int c) {
    if (validIndex(r, c)) {
      if (status == Status.PLAY) {
        Tile current_t = cells[r][c].getTile();
        if (current_t == Tile.CLOSED) {
          cells[r][c].setTile(Tile.FLAG);
          bomb--;
        } else if (current_t == Tile.FLAG) {
          cells[r][c].setTile(Tile.CLOSED);
          bomb++;
        }
      }
    }
  }

  /**
   * mines array as String
   *
   * @return mines array as a String
   */
  String toStringMines() {
    StringBuilder sb = new StringBuilder();
    sb.append("\n");

    for (int r = 0; r < nRows; r++) {
      for (int c = 0; c < nCols; c++) {
        sb.append(cells[r][c].getCount());
      }

      sb.append("\n");
    }

    return sb.toString();
  }

  /**
   * keys array as String
   *
   * @return mines array as a String
   */
  String toStringKeys() {
    StringBuilder sb = new StringBuilder();
    sb.append("\n");

    for (int r = 0; r < nRows; r++) {
      for (int c = 0; c < nCols; c++) {
        sb.append(String.format("%03d, ", cells[r][c].getKey()));
      }

      sb.append("\n");
    }

    return sb.toString();
  }

  /**
   * tiles array as String
   *
   * @return mines array as a String
   */
  String toStringTiles() {
    StringBuilder sb = new StringBuilder();
    sb.append("\n");

    for (int r = 0; r < nRows; r++) {
      for (int c = 0; c < nCols; c++) {
        sb.append(cells[r][c].getTile().toString().charAt(0));
      }

      sb.append("\n");
    }

    return sb.toString();
  }

  /**
   * game board array as String
   *
   * @return game board as String
   */
  String toStringBoard() {
    StringBuilder sb = new StringBuilder();
    sb.append("\n");
    for (int r = 0; r < nRows; r++) {
      for (int c = 0; c < nCols; c++) {
        sb.append(getBoard(r, c));
      }
      sb.append("\n"); // advance to next line
    }

    return sb.toString();
  }

  /**
   * create mines & tiles array place mines<br>
   * update clues<br>
   */
  private void initGame() {
    // allocate space for mines and tiles array
    status = Status.WAIT;
    cells = new Cell[nRows][nCols];

    // place mines
    placeMines();
    // init tiles array
    resetTiles();

    // update clues
    // calculateClues();
    placeKeys();

    // set game status
    status = Status.PLAY;
  }

  /** Sets all tiles to 1 - closed */
  private void resetTiles() {
    for (int r = 0; r < nRows; r++) {
      for (int c = 0; c < nCols; c++) {
        cells[r][c].setTile(Tile.CLOSED);
      }
    }
  }

  /**
   * places mines randomly on grid integer value 9 represents a mine<br>
   * number of mines = (1 + number of columns * number rows) / 10<br>
   * minimum number of mines = 1<br>
   */
  private void placeMines() {
    //		add your code here

    Random random = new Random();
    int minesLeft = nMines;
    for (int r = 0; r < nRows; r++) {
      for (int c = 0; c < nCols; c++) {
        cells[r][c] = new Cell();
      }
    }

    int i = 0;
    while (i < nMines) {
      int position = random.nextInt(allCells);
      int r = position / nCols;
      int c = position % nCols;
      if (!cells[r][c].isMine()) {
        cells[r][c].setMine();
        i++;
        for (int rn = r - 1; rn <= r + 1; rn++) {
          for (int cn = c - 1; cn <= c + 1; cn++) {
            calculateClue(rn, cn);
          }
        }
      }
    }
  }

  /**
   * calculates clue values and updates clue values in mines array<br>
   * integer value 9 represents a mine<br>
   * clue values will be 0 ... 8<br>
   */
  private void calculateClue(int r, int c) {
    if (r >= 0 && r < nRows && c >= 0 && c < nCols) {
      if (!cells[r][c].isMine()) {
        cells[r][c].addCount();
      }
    }
  }

  private void placeKeys() {
    for (int r = 0; r < nRows; r++) {
      for (int c = 0; c < nCols; c++) {
        cells[r][c].setKey(allCells);
      }
    }
    for (int r = 0; r < nRows; r++) {
      for (int c = 0; c < nCols; c++) {
        spreadKey(r, c, r * nCols + c, true);
      }
    }
  }

  private void spreadKey(int r, int c, int key, boolean empty) {
    if (validIndex(r, c) && cells[r][c].getKey() > key) {
      boolean empty_cur = cells[r][c].isEmpty();
      if (empty || empty_cur) {
        cells[r][c].setKey(key);
        for (int rn = r - 1; rn <= r + 1; rn++) {
          for (int cn = c - 1; cn <= c + 1; cn++) {
            spreadKey(rn, cn, key, empty_cur);
          }
        }
      }
    }
  }

  String toJson() {
    Gson gson =
        new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
    return gson.toJson(this);
  }
}
