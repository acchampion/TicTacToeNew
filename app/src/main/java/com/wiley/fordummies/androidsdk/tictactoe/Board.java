package com.wiley.fordummies.androidsdk.tictactoe;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.content.ContextCompat;
import timber.log.Timber;


public class Board extends View {

    private final GameSessionActivity mGameSessionActivity;    // game context (parent)
    private float mBlockWidth;        // mBlockWidth of one block
    private float mBlockHeight;    // will be same as mBlockWidth;
    private GameGrid mGameGrid = null;
    private boolean mIsEnabled = true;

    private Paint mBackgroundPaint;
    private Paint mDarkPaint;
    private Paint mLightPaint;
    private Paint mDitherPaint;

    static Bitmap sSymX = null, sSymO = null, sSymBlank = null;
    static boolean sDrawablesInitialized = false;

    private final String TAG = getClass().getSimpleName();

    public Board(Context context, AttributeSet attributes) {
        super(context, attributes);
        this.mGameSessionActivity = (GameSessionActivity) context;

        setFocusable(true);
        setFocusableInTouchMode(true);

        // Allocate Paint objects to save memory.
        mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaint.setColor(ContextCompat.getColor(context, R.color.white));
        mDarkPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDarkPaint.setColor(ContextCompat.getColor(context, R.color.dark));

        float strokeWidth = 2;
        mDarkPaint.setStrokeWidth(strokeWidth);
        mLightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLightPaint.setColor(ContextCompat.getColor(context, R.color.light));
        mLightPaint.setStrokeWidth(strokeWidth);
        Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(ContextCompat.getColor(context, R.color.dark));

        float lineWidth = 10;
        linePaint.setStrokeWidth(lineWidth);
        linePaint.setStrokeCap(Cap.ROUND);
        mDitherPaint = new Paint();
        mDitherPaint.setDither(true);
    }

    public void setGrid(GameGrid aGrid) {
        mGameGrid = aGrid;
    }

    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        mBlockWidth = width / 3f;
        mBlockHeight = height / 3f;

        if (width < height) {
            mBlockHeight = Math.min(mBlockWidth, mBlockHeight);
        } else {
            mBlockWidth = Math.min(mBlockHeight, mBlockWidth);
        }

        super.onSizeChanged(width, height, oldWidth, oldHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float canvasWidth = getWidth();
        float canvasHeight = getHeight();

        if (canvasWidth < canvasHeight) {
            canvasHeight = Math.min(canvasHeight, canvasWidth);
        }
        else {
            canvasWidth = Math.min(canvasHeight, canvasWidth);
        }

        canvas.drawRect(0, 0, canvasWidth, canvasHeight, mBackgroundPaint);

        // Drawing lines
        for (int i = 0; i < GameGrid.SIZE - 1; i++) {
            canvas.drawLine(0, (i + 1) * mBlockHeight, canvasWidth, (i + 1) * mBlockHeight, mDarkPaint);
            canvas.drawLine(0, (i + 1) * mBlockHeight + 1, canvasWidth, (i + 1) * mBlockHeight + 1, mLightPaint);
            canvas.drawLine((i + 1) * mBlockHeight, 0, (i + 1) * mBlockHeight, canvasHeight, mDarkPaint);
            canvas.drawLine((i + 1) * mBlockHeight + 1, 0, (i + 1) * mBlockHeight + 1, canvasHeight, mLightPaint);
        }

        float offsetX;
        float offsetY;
        for (int i = 0; i < GameGrid.SIZE; i++) {
            for (int j = 0; j < GameGrid.SIZE; j++) {
                Bitmap symSelected = getBitmapForSymbol(mGameGrid.getValueAtLocation(i, j));
                offsetX = (int) (((mBlockWidth - symSelected.getWidth()) / 2) + (i * mBlockWidth));
                offsetY = (int) (((mBlockHeight - symSelected.getHeight()) / 2) + (j * mBlockHeight));
                canvas.drawBitmap(symSelected, offsetX, offsetY, mDitherPaint);
            }
        }
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!this.mIsEnabled) {
            Timber.d(TAG, "Board.onTouchEvent(): Board not mIsEnabled");
            return false;
        }

        int posX = 0;
        int posY = 0;
        int action = event.getAction();

        if (action == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();
            Timber.d(TAG, "Coordinates: " + x + ", " + y);
            if (x > mBlockWidth && x < mBlockWidth * 2) posX = 1;
            if (x > mBlockWidth * 2 && x < mBlockWidth * 3) posX = 2;

            if (y > mBlockHeight && y < mBlockHeight * 2) posY = 1;
            if (y > mBlockHeight * 2 && y < mBlockHeight * 3) posY = 2;

            performClick();
            mGameSessionActivity.humanTakesATurn(posX, posY);
        }
        return super.onTouchEvent(event);
    }

    protected void placeSymbol(int x, int y) {
        Timber.d(TAG,"Thread ID in Board.placeSymbol: %s", Thread.currentThread().getId());
        invalidateBlock(x, y);
    }

    public void invalidateBlock(int x, int y) {
        Rect selBlock = new Rect((int) (x * mBlockWidth), (int) (y * mBlockHeight), (int) ((x + 1) * mBlockWidth), (int) ((y + 1) * mBlockHeight));
        invalidate();
    }

    protected void disableInput() {
        this.mIsEnabled = false;
        Timber.d(TAG,"Board.disableInput(): Board not mIsEnabled");
    }

    protected void enableInput() {
        this.mIsEnabled = true;
        Timber.d(TAG,"Board.enableInput(): Board mIsEnabled");
    }

    public Bitmap getBitmapForSymbol(Symbol aSymbol) {

        if (!sDrawablesInitialized) {
            try {
                Resources res = getResources();
                sSymX = BitmapFactory.decodeResource(res, R.drawable.x);
                sSymO = BitmapFactory.decodeResource(res, R.drawable.o);
                sSymBlank = BitmapFactory.decodeResource(res, R.drawable.blank);
                sDrawablesInitialized = true;
            } catch (OutOfMemoryError ome) {
                Timber.d(TAG, "Ran out of memory decoding bitmaps");
                ome.printStackTrace();
            }
        }


        Bitmap symSelected = sSymBlank;

        if (aSymbol == Symbol.SymbolXCreate())
            symSelected = sSymX;
        else if (aSymbol == Symbol.SymbolOCreate())
            symSelected = sSymO;
        return symSelected;
    }
}

