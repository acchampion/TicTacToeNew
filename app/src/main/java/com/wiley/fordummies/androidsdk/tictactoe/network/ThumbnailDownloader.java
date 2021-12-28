package com.wiley.fordummies.androidsdk.tictactoe.network;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import timber.log.Timber;

public class ThumbnailDownloader<T> extends HandlerThread implements LifecycleObserver {

	private final int MESSAGE_DOWNLOAD = 0;
	private final String TAG = ThumbnailDownloader.class.getSimpleName();

	private boolean mHasQuit = false;
	private Looper mLooper;
	private Handler mRequestHandler, mResponseHandler;
	public LifecycleObserver mFragmentLifecycleObserver, mViewLifecycleObserver;
	private ThumbnailDownloadListener<T> mThumbnailDownloadListener;
	private ConcurrentMap<T, String> mRequestMap = new ConcurrentHashMap<>();
	private FlickrFetchr mFlickrFetchr = new FlickrFetchr();

	public interface ThumbnailDownloadListener<T> {
		void onThumbnailDownloaded(T holder, Bitmap bitmap);
	}

	public ThumbnailDownloader(Handler responseHandler) {
		super("ThumbnailDownloader");
		mResponseHandler = responseHandler;

		mFragmentLifecycleObserver = new LifecycleObserver() {
			@OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
			public void setup() {
				Timber.tag(TAG).i("Starting background thread");
				if (!isAlive()) {
					start();
				}
				mLooper = getLooper();
			}

			@OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
			public void tearDown() {
				Timber.tag(TAG).i("Destroying background thread");
				quit();
			}
		};

		mViewLifecycleObserver = new LifecycleObserver() {
			@OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
			private void clearQueue() {
				Timber.tag(TAG).i("Clearing all requests from queue");
				mRequestHandler.removeMessages(MESSAGE_DOWNLOAD);
				mRequestMap.clear();
			}
		};
	}

	@Override
	protected void onLooperPrepared() {
		super.onLooperPrepared();
		mRequestHandler = new Handler(Looper.myLooper()) {
			@Override
			public void handleMessage(@NonNull Message msg) {
				super.handleMessage(msg);
				if (msg.what == MESSAGE_DOWNLOAD) {
					T target = (T) msg.obj;
					Timber.tag(TAG).i("Got a request for URL: %s", mRequestMap.get(target));
					handleRequest(target);
				}
			}
		};
	}

	public void setThumbnailDownloadListener(ThumbnailDownloadListener<T> listener) {
		mThumbnailDownloadListener = listener;
	}

	public void queueThumbnail(T target, String url) {
		Timber.tag(TAG).i("Got a URL: %s", url);
		mRequestMap.put(target, url);
		mRequestHandler.obtainMessage(MESSAGE_DOWNLOAD, target).sendToTarget();
	}

	@Override
	public boolean quit() {
		mHasQuit = true;
		return super.quit();
	}

	private void handleRequest(T target) {
		String url = mRequestMap.get(target);
		Bitmap bitmap = mFlickrFetchr.fetchPhoto(url);

		mResponseHandler.post(() -> {
			String theUrl = mRequestMap.get(target);
			if (!Objects.requireNonNull(theUrl).equals(url) || mHasQuit) {
				return;
			}
			mRequestMap.remove(target);
			mThumbnailDownloadListener.onThumbnailDownloaded(target, bitmap);
		});
	}
}
