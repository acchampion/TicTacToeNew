package com.wiley.fordummies.androidsdk.tictactoe;

public class Square {
    private final int mX;
    private final int mY;

    public Square(int x, int y) {
        this.mX = x;
        this.mY = y;
    }

    public int getX() {
        return mX;
    }

    public int getY() {
        return mY;
    }
}
