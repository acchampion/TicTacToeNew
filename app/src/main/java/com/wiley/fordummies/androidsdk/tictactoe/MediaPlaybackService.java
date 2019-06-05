package com.wiley.fordummies.androidsdk.tictactoe;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;

import timber.log.Timber;

public class MediaPlaybackService extends Service {
    MediaPlayer player;

    private final String TAG = getClass().getSimpleName();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        player = MediaPlayer.create(this, R.raw.sample_audio);
        player.setLooping(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Bundle extras = intent.getExtras();
        if (extras != null) {
            String audioFileURIString = extras.getString("URIString");
            Uri audioFileURI = Uri.parse(audioFileURIString);
            Timber.d(TAG, "URI = %s", audioFileURI.toString());
            try {
                player.reset();
                player.setDataSource(this.getApplicationContext(), audioFileURI);
                player.prepare();
                player.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        player.stop();
    }

}
