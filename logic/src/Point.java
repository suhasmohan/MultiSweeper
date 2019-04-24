class Point {
  private final int row;
  private final int col;

  Point(int row, int col) {
    this.row = row;
    this.col = col;
  }

  @Override
  public boolean equals(Object anObject) {
    if (this == anObject) {
      return true;
    }
    if (anObject instanceof Point) {
      Point aPoint = (Point) anObject;
      return row == aPoint.row && col == aPoint.col;
    }
    return false;
  }
}
