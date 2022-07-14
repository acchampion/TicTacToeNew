package com.wiley.fordummies.androidsdk.tictactoe.concurrency;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import timber.log.Timber;

/**
 * Class that runs code in background using Java's Executors and a Handler to update the UI thread.
 * Why? AsyncTask is deprecated, and Google recommends using Java's standard concurrency classes
 * (see the java.util.concurrent package).
 *
 * If we were using Kotlin, we'd use coroutines and dispatchers.
 *
 * Source: https://tedblob.com/asynctask-deprecated-alternative-android
 *
 * @author acc
 */
public class ExecutorRunner {
	// Executor instance
	private final Executor mExecutor = Executors.newSingleThreadExecutor();
	// Handler for updating the UI thread
	private final Handler mHandler = new Handler(Looper.getMainLooper());

	private final String TAG = getClass().getSimpleName();

	public interface Callback<R> {
		void onComplete(R r);
		void onError(Exception e);
	}

	public <R> void execute(Callable<R> callable, Callback<R> callback) {
		mExecutor.execute(() -> {
			final R result;

			try {
				Timber.tag(TAG).d("Invoking call() on callable");
				result = callable.call();
				Timber.tag(TAG).d("Posting onComplete()");
				mHandler.post(() -> callback.onComplete(result));

			} catch (Exception e) {
				Timber.tag(TAG).e("Error running Executor");
				e.printStackTrace();
				mHandler.post(() -> callback.onError(e));
			}
		});
	}
}
