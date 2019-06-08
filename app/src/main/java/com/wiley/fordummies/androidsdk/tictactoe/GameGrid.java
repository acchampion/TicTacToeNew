package com.wiley.fordummies.androidsdk.tictactoe;

import java.util.ArrayList;

import timber.log.Timber;

class GameGrid {
    static final int SIZE = 3;
    private Symbol[][] mGrid;

    private final String TAG = getClass().getSimpleName();

    GameGrid() { // Constructor. Initializes the mGrid to blanks
        mGrid = new Symbol[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                mGrid[i][j] = Symbol.SymbolBlankCreate();
    }

    void setValueAtLocation(int x, int y, Symbol value) {
        if (((x >= 0) && (x < SIZE)) && ((y >= 0) && (y < SIZE)))
            mGrid[x][y] = value;
    }

    Symbol getValueAtLocation(int x, int y) {
        Symbol returnValue = null;
        if (((x >= 0) && (x < SIZE)) && ((y >= 0) && (y < SIZE))) returnValue = mGrid[x][y];
        return returnValue;
    }

    boolean isRowFilled(int row) { // Entire row has the same symbol
        boolean isFilled;
        boolean foundMismatch = false;
        for (int col = 0; (col < SIZE) && (!foundMismatch); col++) {
            if (mGrid[row][0] != mGrid[row][col])
                foundMismatch = true;
        }
        isFilled = (!foundMismatch) && (mGrid[row][0] != Symbol.SymbolBlankCreate());
        return isFilled;
    }

    boolean isColumnFilled(int column) { // Entire column has the same symbol
        boolean isFilled;
        boolean foundMismatch = false;
        for (int row = 0; (row < SIZE) && (!foundMismatch); row++) {
            if (mGrid[0][column] != mGrid[row][column]) foundMismatch = true;
        }
        isFilled = (!foundMismatch) && (mGrid[0][column] != Symbol.SymbolBlankCreate());
        return isFilled;
    }

    boolean isLeftToRightDiagonalFilled() { // Left diagonal has the same symbol
        boolean isFilled;
        boolean foundMismatch = false;
        for (int index = 0; (index < SIZE) && (!foundMismatch); index++) {
            if (mGrid[0][0] != mGrid[index][index]) foundMismatch = true;
        }
        isFilled = (!foundMismatch) && (mGrid[0][0] != Symbol.SymbolBlankCreate());
        return isFilled;
    }


    boolean isRightToLeftDiagonalFilled() { // Right diagonal has the same symbol
        int foundIndex = -1;
        boolean isFilled;
        boolean foundMismatch = false;
        Timber.d(TAG,"Entering isRightToLeftDiagonalFilled");
        for (int index = SIZE - 1; (index >= 0) && (!foundMismatch); index--) {
            final String logStr = ">" + mGrid[0][SIZE - 1].toString() + "<   >" + mGrid[index][index].toString() + "<";
            Timber.d(TAG, logStr);
            if (mGrid[0][SIZE - 1] != mGrid[SIZE - 1 - index][index]) {
                foundMismatch = true;
                foundIndex = index;
            }
        }
        isFilled = (!foundMismatch) && (mGrid[0][SIZE - 1] != Symbol.SymbolBlankCreate());
        final String leavingLogStr = "Leaving isRightToLeftDiagonalFilled" + foundMismatch + "index>" + foundIndex + "<>" + mGrid[0][SIZE - 1].toString() + "<";
        Timber.d(TAG, leavingLogStr);
        return isFilled;
    }

    ArrayList<Square> getEmptySquares() { // Get the unfilled squares
        ArrayList<Square> list = new ArrayList<>();
        for (int i = 0; i < GameGrid.SIZE; i++) {
            for (int j = 0; j < GameGrid.SIZE; j++) {
                if (mGrid[i][j] == Symbol.SymbolBlankCreate()) {
                    Square b = new Square(i, j);
                    list.add(b);
                }
            }
        }
        return list;
    }
}
