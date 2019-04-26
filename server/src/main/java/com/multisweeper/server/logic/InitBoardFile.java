package com.multisweeper.server.logic;

import java.io.*;

public class InitBoardFile {
  public static void main(String[] args) {
    Board board = new Board();
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
