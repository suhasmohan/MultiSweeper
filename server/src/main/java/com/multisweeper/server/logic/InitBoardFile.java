package com.multisweeper.server.logic;

import java.io.*;
import java.util.Random;

public class InitBoardFile {
  public static void main(String[] args) {
    Random rand = new Random();
    int nRow = Math.min((int) (Math.abs(rand.nextGaussian() * 30) + 10), 70);
    Board board = new Board(nRow);
    try {

      File file = new File("board.txt");
      file.createNewFile();

      FileOutputStream f = new FileOutputStream(file);
      ObjectOutputStream o = new ObjectOutputStream(f);

      // Write objects to file
      o.writeObject(board);

      o.close();
      f.close();

    } catch (FileNotFoundException e) {
      System.out.println("File not found");
    } catch (IOException e) {
      System.out.println("Error initializing stream");
      e.printStackTrace();
    }
  }
}
