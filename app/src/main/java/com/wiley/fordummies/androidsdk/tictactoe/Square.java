package com.wiley.fordummies.androidsdk.tictactoe;

class Square {
    private int mX;
    private int mY;

    Square(int x, int y) {
        this.mX = x;
        this.mY = y;
    }

    int getX() {
        return mX;
    }

    int getY() {
        return mY;
    }
}
