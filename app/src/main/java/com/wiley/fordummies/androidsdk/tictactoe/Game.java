package com.wiley.fordummies.androidsdk.tictactoe;

import java.util.Locale;

import timber.log.Timber;

public class Game {
    private enum STATE {Inactive, Active, Won, Draw}
    private STATE mGameState;

    private Symbol mCurrentSymbol;

    private enum PLAYER {Player1, Player2}

    private PLAYER mCurrentPlayer = PLAYER.Player1;
    private PLAYER mWinningPlayer = PLAYER.Player1;

    private String mPlayerOneName = null, mPlayerTwoName = null;

    private final GameGrid mGameGrid;

    private int mPlayCount = 0;

    public Game() { // Constructor
        mGameGrid = new GameGrid();
        mGameState = STATE.Active;
        mCurrentSymbol = Symbol.SymbolXCreate();
    }

    public GameGrid getGameGrid() {
        return mGameGrid;
    }

    public void setPlayerNames(String FirstPlayer, String SecondPlayer) {
        mPlayerOneName = FirstPlayer;
        mPlayerTwoName = SecondPlayer;
    }

    public String getPlayerOneName() {
        return mPlayerOneName;
    }

    public String getPlayerTwoName() {
        return mPlayerTwoName;
    }

    public String getCurrentPlayerName() {
        if (mCurrentPlayer == PLAYER.Player1) return mPlayerOneName;
        else return mPlayerTwoName;
    }

    public String getWinningPlayerName() {
        if (mWinningPlayer == PLAYER.Player1) return mPlayerOneName;
        else return mPlayerTwoName;
    }

    public boolean play(int x, int y) {
        boolean successfulPlay = false;
        if ((mGameGrid.getValueAtLocation(x, y) == Symbol.SymbolBlankCreate())) {
            successfulPlay = true;
            mPlayCount = mPlayCount + 1;
            String infoLogStr = String.format(Locale.ENGLISH, "Player %s placed symbol %s at position (%d, %d)", mCurrentPlayer, mCurrentSymbol, x, y);
            Timber.tag("Game").i(infoLogStr);
            mGameGrid.setValueAtLocation(x, y, mCurrentSymbol);
            checkResultAndSetState();
            if (mGameState == STATE.Active) {// if the game is still active
                // Swap symbols and players
                if (mCurrentSymbol == Symbol.SymbolXCreate())
                    mCurrentSymbol = Symbol.SymbolOCreate();
                else
                    mCurrentSymbol = Symbol.SymbolXCreate();
                if (mCurrentPlayer == PLAYER.Player1) mCurrentPlayer = PLAYER.Player2;
                else mCurrentPlayer = PLAYER.Player1;
            }
        }
        return successfulPlay;
    }

    private void checkResultAndSetState() {
        if (mGameGrid.isRowFilled(0) ||
                mGameGrid.isRowFilled(1) ||
                mGameGrid.isRowFilled(2) ||
                mGameGrid.isColumnFilled(0) ||
                mGameGrid.isColumnFilled(1) ||
                mGameGrid.isColumnFilled(2) ||
                mGameGrid.isLeftToRightDiagonalFilled() ||
                mGameGrid.isRightToLeftDiagonalFilled()) {
            mWinningPlayer = mCurrentPlayer;
            mGameState = STATE.Won;
        } else if (mPlayCount == 9) {
            mGameState = STATE.Draw;
        } /* else, leave state as is */
    }

    public boolean isActive() {
        return mGameState == STATE.Active;
    }

    public boolean isWon() {
        return mGameState == STATE.Won;
    }

    public boolean isDrawn() {
        return mGameState == STATE.Draw;
    }

    public int getPlayCount() {
        return mPlayCount;
    }

	public void resetPlayCount() { mPlayCount = 0;}
}
