/*
    Pomodoro is a simple Pomodoro Technique app for Android
    Copyright (C) 2015 Matteo Taroli <contact@matteotaroli.be>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package be.matteotaroli.pomodoro;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Handles the visual circular representation of the timer.
 */

public class CircleTimerView extends SurfaceView implements SurfaceHolder.Callback {
    public static final String TAG = "CircleTimerView";

    /* Arc angles */
    private static final float START_ANGLE = 0;
    private float sweepAngle = 360;

    /* Drawing thread */
    private DrawCircleThread mDrawingThread;

    private boolean mVisible;

    public CircleTimerView(Context context) {
        super(context);
        init();
    }

    public CircleTimerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleTimerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mVisible = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mVisible = false;
    }

    /**
     * Sets the basics.
     */
    private void init() {
        getHolder().addCallback(this);
        setZOrderOnTop(true);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
    }

    public void setSweepAngle(float angle) {
        sweepAngle = angle;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mDrawingThread = new DrawCircleThread(holder);
        mDrawingThread.setRunning(true);
        mDrawingThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mDrawingThread.setRunning(false);
        boolean retry = true;
        while (retry) {
            try {
                mDrawingThread.join();
                retry = false;
            } catch (InterruptedException e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    /**
     * Draws the circle on the given canvas.
     *
     * @param canvas Surface on which to draw
     */
    private void drawCircle(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        float height = canvas.getHeight();
        float width = canvas.getWidth();

        float centerX = width / 2;
        float centerY = height / 2;

        if (width > height) {
            height *= 0.8f;
            width = height;
        } else {
            width *= 0.8f;
            height = width;
        }

        RectF oval = new RectF(centerX - width / 2, centerY - height / 2,
                centerX + width / 2, centerY + height / 2);

        Paint paint = new Paint();
        paint.setColor(getResources().getColor(android.R.color.white));
        paint.setStrokeWidth(10);

        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setAntiAlias(true);

        canvas.drawArc(oval, START_ANGLE, sweepAngle, false, paint);
    }

    /**
     * Handles the drawing in another thread
     */
    class DrawCircleThread extends Thread {
        private final SurfaceHolder mSurfaceHolder;
        private boolean mRunning = false;

        public DrawCircleThread(SurfaceHolder surfaceHolder) {
            this.mSurfaceHolder = surfaceHolder;
        }

        public void setRunning(boolean running) {
            mRunning = running;
        }

        @Override
        public void run() {
            Canvas canvas;

            while (mRunning) {
                if (mVisible) {
                    canvas = null;
                    try {
                        canvas = this.mSurfaceHolder.lockCanvas(null);
                        synchronized (this.mSurfaceHolder) {
                            drawCircle(canvas);
                        }
                    } finally {
                        if (canvas != null) {
                            this.mSurfaceHolder.unlockCanvasAndPost(canvas);
                        }
                    }
                }
            }
        }
    }
}
