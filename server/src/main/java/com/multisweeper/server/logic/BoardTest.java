package com.multisweeper.server.logic;

import java.util.Random;

public class BoardTest {
  public static void main(String[] args) {
    InitBoardFile.main(new String[1]);
    Board board = Board.fromFile("");
    int nRows = board.getRows();
    int nCols = board.getCols();
    System.out.println(board.getCols());
    System.out.println("board");
    System.out.println(board.toStringBoard());
    System.out.println("mine");
    System.out.println(board.toStringMines());

    //    System.out.println("tile");
    //    System.out.println(board.toStringTiles());
    //    System.out.println("key");
    //    System.out.println(board.toStringKeys());
    BoardTest.testBoard(board, nRows, nCols);
    BoardTest.printBoard(board, nRows, nCols);
    BoardTest.printBoard(board, nRows, nCols);
    BoardTest.printBoard(board, nRows, nCols);
    BoardTest.printBoard(board, nRows, nCols);
    BoardTest.printBoard(board, nRows, nCols);
    System.out.println(board.toJson());
  }

  private static void printBoard(Board board, int nRows, int nCols) {
    Random rand = new Random();
    int r1 = rand.nextInt(nRows);
    int r2 = rand.nextInt(nRows);
    int c1 = rand.nextInt(nCols);
    int c2 = rand.nextInt(nCols);
    System.out.println(String.format("open(%d,%d)", r1, c1));
    board.tileOpen(r1, c1);
    System.out.println(board.getStatus());
    System.out.println(board.toStringBoard());
    System.out.println(String.format("flag(%d,%d)", r2, c2));
    board.tileFlag(r2, c2);

    System.out.println(board.getStatus());
    System.out.println(board.toStringBoard());
  }

  private static void testBoard(Board board, int nRows, int nCols) {
    Random rand = new Random();
    int r1 = rand.nextInt(nRows);
    int c1 = rand.nextInt(nCols);

    System.out.println(String.format("flag(%d,%d)", r1, c1));
    board.tileFlag(r1, c1);
    System.out.println(board.getStatus());
    System.out.println(board.toStringBoard());

    System.out.println(String.format("open(%d,%d)", r1, c1));
    board.tileOpen(r1, c1);
    System.out.println(board.getStatus());
    System.out.println(board.toStringBoard());

    System.out.println(String.format("flag(%d,%d)", r1, c1));
    board.tileFlag(r1, c1);
    System.out.println(board.getStatus());
    System.out.println(board.toStringBoard());

    System.out.println(String.format("open(%d,%d)", r1, c1));
    board.tileOpen(r1, c1);
    System.out.println(board.getStatus());
    System.out.println(board.toStringBoard());
  }
}
