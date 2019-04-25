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
public class Board {

  private final int nMines;
  private final int nRows;
  private final int nCols;
  private final int allCells;
  private final int EMPTY_CELL = 0;
  private final int MINE_CELL = 9;
  /**
   * tile values 0 - open, 1 - closed,<br>
   * 2 - question, 3 - mine
   */
  private Tile[][] tiles;
  /** mine and clue values, 9 - mine, 0-8 clue values */
  private int[][] mines;

  private int[][] keys;
  /** Level 2 - game status win, lose, play */
  private Status status;
  /**
   * default constructor<br>
   * board size 10 x 10<br>
   * create mines and tile arrays<br>
   * place mines<br>
   * calculate clues<br>
   * (*)set game status to play<br>
   */
  Board() {
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
    allCells = nRows * nCols;
    initGame();
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
    Tile tile = tiles[r][c];
    int mine = mines[r][c];
    if (tile == Tile.CLOSED) {
      if (status == Status.LOSE && mine == MINE_CELL) {
        return '*';
      } else {
        return '_';
      }
    } else if (tile == Tile.FLAG) {
      return 'F';
    } else if (tile == Tile.OPEN) {
      if (mine == EMPTY_CELL) {
        return ' ';
      } else if (mine == MINE_CELL) {
        return '@';
      } else if (mine > EMPTY_CELL && mine < MINE_CELL) {
        return (char) ('0' + mine);
      }
    }

    return '?';
  }

  /**
   * Level 2 - game status
   *
   * @return "play", "win", or "lose"
   */
  public String getStatus() {
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
      return mines[r][c];
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
      return tiles[r][c];
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
        Tile current_t = tiles[r][c];
        if (current_t == Tile.CLOSED) {
          tiles[r][c] = Tile.OPEN;
          int mine = mines[r][c];
          if (mine == EMPTY_CELL) {
            for (int dr = -1; dr <= 1; dr++) {
              for (int dc = -1; dc <= 1; dc++) {
                tileOpen(r + dr, c + dc);
              }
            }
          } else if (mine == MINE_CELL) {
            status = Status.LOSE;
          }
        }
      }
    }
  }

  public void tileFlag(int r, int c) {
    if (validIndex(r, c)) {
      if (status == Status.PLAY) {
        Tile current_t = tiles[r][c];
        if (current_t == Tile.CLOSED) {
          tiles[r][c] = Tile.FLAG;
        } else if (current_t == Tile.FLAG) {
          tiles[r][c] = Tile.CLOSED;
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
        sb.append(mines[r][c]);
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
        sb.append(String.format("%03d, ", keys[r][c]));
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
        sb.append(tiles[r][c].toString().charAt(0));
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
    mines = new int[nRows][nCols];
    tiles = new Tile[nRows][nCols];
    keys = new int[nRows][nCols];

    // init tiles array
    resetTiles();

    // place mines
    placeMines();

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
        tiles[r][c] = Tile.CLOSED;
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
        mines[r][c] = EMPTY_CELL;
      }
    }

    int i = 0;
    while (i < nMines) {
      int position = random.nextInt(allCells);
      int r = position / nCols;
      int c = position % nCols;
      if ((mines[r][c] != MINE_CELL)) {
        mines[r][c] = MINE_CELL;
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
  private void calculateClues() {
    //		add your code here
  }
  /**
   * calculates clue values and updates clue values in mines array<br>
   * integer value 9 represents a mine<br>
   * clue values will be 0 ... 8<br>
   */
  private void calculateClue(int r, int c) {
    if (r >= 0 && r < nRows && c >= 0 && c < nCols) {
      if (mines[r][c] != MINE_CELL) {
        mines[r][c]++;
      }
    }
  }

  private void placeKeys() {
    for (int r = 0; r < nRows; r++) {
      for (int c = 0; c < nCols; c++) {
        keys[r][c] = allCells;
      }
    }
    for (int r = 0; r < nRows; r++) {
      for (int c = 0; c < nCols; c++) {
        spreadKey(r, c, r * nCols + c, true);
      }
    }
  }

  private void spreadKey(int r, int c, int key, boolean empty) {
    if (validIndex(r, c) && keys[r][c] > key) {
      boolean empty_cur = mines[r][c] == EMPTY_CELL;
      if (empty || mines[r][c] == EMPTY_CELL) {
        keys[r][c] = key;
        for (int rn = r - 1; rn <= r + 1; rn++) {
          for (int cn = c - 1; cn <= c + 1; cn++) {
            spreadKey(rn, cn, key, empty_cur);
          }
        }
      }
    }
  }

  private void checkEmptyKey(int r, int c) {
    if (validIndex(r, c)) {
      if (mines[r][c] == EMPTY_CELL) {

        int minKey = allCells;
        for (int rn = r - 1; rn <= r + 1; rn++) {
          for (int cn = c - 1; cn <= c + 1; cn++) {
            if (validIndex(rn, cn)) {
              minKey = Math.min(minKey, keys[rn][cn]);
            }
          }
        }
        for (int rn = r - 1; rn <= r + 1; rn++) {
          for (int cn = c - 1; cn <= c + 1; cn++) {
            if (validIndex(rn, cn)) {
              keys[rn][cn] = minKey;
            }
          }
        }
      }
    }
  }

  private enum Status {
    LOSE,
    PLAY,
    WAIT,
    WIN
  }

  private enum Tile {
    CLOSED,
    FLAG,
    OPEN
  }
}
