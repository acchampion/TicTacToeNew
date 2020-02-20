package com.wiley.fordummies.androidsdk.tictactoe;

import android.app.Application;

import timber.log.Timber;

public class TicTacToeApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if(BuildConfig.DEBUG){
            Timber.plant(new Timber.DebugTree());
        }
    }
}
