package com.multisweeper.server.REST;

public class Click {

	int row;
	int col;
	String type;

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

	public boolean isOpen() {
		return type.equalsIgnoreCase("open");
	}

	public boolean isFlag() {
		return type.equalsIgnoreCase("flag");
	}

	public Click(int row, int col, String type) {
		this.row = row;
		this.col = col;
		this.type = type;
	}
}
