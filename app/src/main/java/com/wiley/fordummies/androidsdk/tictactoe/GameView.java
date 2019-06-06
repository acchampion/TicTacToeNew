package com.wiley.fordummies.androidsdk.tictactoe;

import android.widget.TextView;

class GameView {
    private Board gameBoard = null;
    private TextView statusView = null;
    private TextView sessionScoresView = null;

    void setGameViewComponents(Board theBoard, TextView theStatusView, TextView theSessionScoresView) {
        gameBoard = theBoard;
        statusView = theStatusView;
        sessionScoresView = theSessionScoresView;
    }

    void setGameStatus(String message) {
        statusView.setText(message);
    }

    void showScores(String player1Name, int player1Score, String player2Name, int player2Score) {
        String scoreStr = player1Name + ":" + player1Score + "...." + player2Name + ":" + player2Score;
        sessionScoresView.setText(scoreStr);
    }

    void placeSymbol(int x, int y) {
        gameBoard.placeSymbol(x, y);
        gameBoard.invalidateBlock(x, y);
    }
}
