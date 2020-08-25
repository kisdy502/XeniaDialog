package tv.fengmang.xeniadialog.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import tv.fengmang.xeniadialog.R;


public class JumpingView extends View {
    private static final String TAG = JumpingView.class.getSimpleName();

    private static final int NUM_NOTES = 4;
    private static final int INCREMENT = 2;
    private static final int DIF = 12;

    private Paint mPaint;

    private boolean isRunning;
    private boolean hasInitSize;

    private float mHeight;

    private int mNoteWidth;
    private int baseX = 0;
    private int mSmallWidth;
    private int mSmallHeight;
    private int mDefaultColor;

    private List<FPoint> mStartPoints;

    private static final int[] difs = new int[]{0, DIF * 3, DIF, DIF * 5};

    public JumpingView(Context context) {
        this(context, null);
    }

    public JumpingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JumpingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        initPaint();
        initValues(mSmallWidth, mSmallHeight);
        initPoints();
        hasInitSize = true;
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setColor(mDefaultColor);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(2);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.orange_JumpingView);
        mSmallWidth = ta.getDimensionPixelOffset(R.styleable.orange_JumpingView_orange_small_width, 0);
        mSmallHeight = ta.getDimensionPixelOffset(R.styleable.orange_JumpingView_orange_small_height, 0);
        mDefaultColor = ta.getColor(R.styleable.orange_JumpingView_orange_default_color, Color.WHITE);
        ta.recycle();
    }

    private void initPoints() {
        mStartPoints = new ArrayList<>();
        for (int i = 0; i < NUM_NOTES; i++) {
            FPoint startPoint = new FPoint();
            float x = (float) (((2 * i) + 0.5) * mNoteWidth);
            startPoint.set(x, 0);
            mStartPoints.add(startPoint);
        }
    }

    private void initValues(int width, int height) {
        mNoteWidth = width / (NUM_NOTES * 2 - 1);
        mPaint.setStrokeWidth(mNoteWidth * 0.9f);
        mHeight = (float) height;
    }

    class RenderTask extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                if (!hasInitSize) {
                    Thread.sleep(150);
                    initPoints();
                }
                if (mStartPoints == null) initPoints();
                drawUI();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void drawUI() throws InterruptedException {
        synchronized (this) {
            while (isRunning) {
                Log.d(TAG, Thread.currentThread().getName() + "drawUI");
                postInvalidate();
                Thread.sleep(50);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        try {
            if (mStartPoints == null || mStartPoints.size() != NUM_NOTES) {
                return;
            }
            for (int i = 0; i < NUM_NOTES; i++) {
                baseX += INCREMENT;
                canvas.drawLine(mStartPoints.get(i).x,
                        mHeight,
                        mStartPoints.get(i).x,
                        mHeight * 3 / 4 - getHeight(difs[i] + baseX) * mHeight * 3 / 4,
                        mPaint);
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception : " + e.getMessage());
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mSmallWidth, mSmallHeight);
    }

    private float getHeight(int x) {
        return (float) (Math.sin(Math.PI * (double) x / 100) + 1) / 2;
    }

    public void setColor(int color) {
        if (mPaint.getColor() != color) {
            mPaint.setColor(color);
        }
    }

    public void show() {
        if (getVisibility() != VISIBLE) {
            setVisibility(VISIBLE);
        }
    }

    public void hide() {
        if (getVisibility() != GONE) {
            setVisibility(GONE);
        }
    }

    public void startJump() {
        setVisibility(VISIBLE);
        if (!isRunning) {
            isRunning = true;
            RenderTask task = new RenderTask();
            task.setName("RenderTask" + new Random().nextInt(10000));
            task.start();
        }
    }

    public void stopJump() {
        setVisibility(GONE);
        isRunning = false;
        mStartPoints = null;
    }

    @Override
    protected void onDetachedFromWindow() {
        isRunning = false;
        super.onDetachedFromWindow();
    }

   static class FPoint {
        float x;
        float y;

        public float getX() {
            return x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }

        /**
         * Set the point's x and y coordinates
         */
        void set(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }
}