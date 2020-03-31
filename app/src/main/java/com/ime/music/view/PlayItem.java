package com.ime.music.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.ime.music.CLog;
import com.ime.music.play.MusicPlayer;

public class PlayItem extends TextView {
    final private int lessLong = 0;
    private Context context;
    //定义一个paint
    private Paint paint;
    private String text = "";
    //绘制时控制文本绘制的范围
    private Rect bound;
    private int max_right;

    private int x_down;
    private int x_newDown;
    private int x_current = 0;//进度条当前位置
    private volatile boolean isScrolling = false;//进度条在运行
    private int x_move;
//    Drawable drawable = new ColorDrawable(Color.parseColor("#4466EE"));

    private int playerLength = 0;
    private OnSeekListener moveListener = null;
    private OnPlayClickListener playClickListener = null;

    private int play = -1;//标记正在第几行播放

    private Paint linePaint;
    private Paint seekPaint;
    private Rect seek;
//
//    @Override
//    public boolean isFocused() {
//        return true;
//    }

    private void initPaint() {
        linePaint = new Paint();
        linePaint.setColor(Color.parseColor("#4466EE"));
        linePaint.setStyle(Paint.Style.STROKE);//画笔属性是空心圆
        linePaint.setStrokeWidth(4);//设置画笔粗细

        seekPaint = new Paint();
        seekPaint.setColor(Color.parseColor("#4466EE"));
//        seekPaint.setStyle(Paint.Style.STROKE);//画笔属性是空心圆
        seekPaint.setStrokeWidth(1);//设置画笔粗细
        seekPaint.setAlpha(25);
    }


    public void setPlayClickListener(OnPlayClickListener playClickListener) {
        this.playClickListener = playClickListener;
    }

    public boolean isScrolling() {
        return isScrolling;
    }

    public int getPlay() {
        return play;
    }

    public void setPlay(int play) {
        this.play = play;
    }

    public interface OnSeekListener {
        void onMove();
        void onSeek(int position);
    }

    public interface OnPlayClickListener {
        void onClick();
    }

    public void setOnSeekListener(OnSeekListener l) {
        moveListener = l;
    }

    //将时长转换为进度
    private int toSeekPosition(int position) {
        if (playerLength == 0) return 0;
        if (position <= 0) return 0;
        if (position > playerLength) return 0;
//        CLog.d("max: " + max_right);
//        CLog.d("position: " + position);
//        CLog.d("playerLength: " + playerLength);
        return max_right * position / playerLength;
    }

    //将进度转换为时长
    private int toMusicPositon(int position) {
        if (max_right <= 0) return 0;
        if (position <= 0) return 0;
//        CLog.d("max: " + max_right);
//        CLog.d("position: " + position);
//        CLog.d("playerLength: " + playerLength);
        return playerLength * position / max_right;
    }

    private Handler handlerSeekBar = new Handler();
    //每--ms更新seekbar
    private Runnable runnableRenewSeekBar = new Runnable() {
        @Override
        public void run() {
            int p = toSeekPosition(MusicPlayer.getInstance().getPosition());
            if (p < lessLong) p = lessLong;
            CLog.d("running: " + p);
            setSeekProgress(p);
            handlerSeekBar.postDelayed(runnableRenewSeekBar, 200);
        }
    };

    //设置seekbar进度
    private void setSeekProgress(int position) {
        x_current = position;
        invalidate();
    }

    public void start() {
        CLog.d("ui start: " + this.toString());
//        playerLength = MusicPlayer.getPosition();
        isScrolling = true;
        handlerSeekBar.post(runnableRenewSeekBar);
    }

    public void stop() {
        CLog.d("ui stop: " + this.toString());
//        playInit();
        isScrolling = false;
        handlerSeekBar.removeCallbacks(runnableRenewSeekBar);
        invalidate();
    }

    public void playInit() {
        playerLength = 0;
        x_current = 0;
    }

    private void init(Context context) {
        this.context = context;
        initPaint();
        bound = new Rect();
        paint = new Paint();
        paint.setTextSize(60);
        paint.getTextBounds(text, 0, text.length(), bound);
//        drawable.setBounds(0,0,0,0);
//        drawable.setAlpha(25);

        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return true;
            }
        });
    }

    public PlayItem(Context context) {
        super(context);
        init(context);
    }

    public PlayItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    public PlayItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawPlaySeek(canvas);
    }

    private void drawText(Canvas canvas) {
        paint.setColor(Color.RED);
        canvas.drawText(text, 10, getHeight() / 2 + bound.height() / 2, paint);
    }

    private void drawPlaySeek(Canvas canvas) {
        int left = getLeft();
        int top = getTop();
        int bottom = getBottom();
        int right = x_current;
        if (left > right) right = left = 0;
        if (right<0) right = 0;
        if (right>max_right) right = max_right;
        x_move = 0;
        seek = new Rect(left, 0, right, bottom);
        if (right == 0) return;
        canvas.drawRect(seek, seekPaint);
        canvas.drawLine(right, 0, right, bottom, linePaint);
    }

    @Override
    /*
    wrap_content不能使用
     */
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int minimumWidth = getSuggestedMinimumWidth();
        final int minimumHeight = getSuggestedMinimumHeight();
//        CLog.d("---minimumWidth = " + minimumWidth + "");
//        CLog.d("---minimumHeight = " + minimumHeight + "");
        int width = measureWidth(minimumWidth, widthMeasureSpec);
        max_right = width;
        int height = measureHeight(minimumHeight, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    private int measureWidth(int defaultWidth, int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
//        CLog.d("---speSize = " + specSize + "");

        switch (specMode) {
            case MeasureSpec.AT_MOST:
                defaultWidth = (int) paint.measureText(text) + getPaddingLeft() + getPaddingRight();
//                CLog.d("---speMode = AT_MOST");
                break;
            case MeasureSpec.EXACTLY:
//                CLog.d("---speMode = EXACTLY");
                defaultWidth = specSize;
                break;
            case MeasureSpec.UNSPECIFIED:
//                CLog.d("---speMode = UNSPECIFIED");
                defaultWidth = Math.max(defaultWidth, specSize);
        }
        return defaultWidth;
    }

    private int measureHeight(int defaultHeight, int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
//        CLog.d("---speSize = " + specSize + "");
        switch (specMode) {
            case MeasureSpec.AT_MOST:
                defaultHeight = (int) (-paint.ascent() + paint.descent()) + getPaddingTop() + getPaddingBottom();
//                defaultHeight = specSize;
//                CLog.d("---speMode = AT_MOST");
                break;
            case MeasureSpec.EXACTLY:
                defaultHeight = specSize;
//                CLog.d("---speSize = EXACTLY");
                break;
            case MeasureSpec.UNSPECIFIED:
                defaultHeight = Math.max(defaultHeight, specSize);
//                CLog.d("---speSize = UNSPECIFIED");
                break;
        }
        return defaultHeight;

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x_down = (int) event.getX();
                x_newDown = x_down;
                x_move = 0;
                CLog.d("x:" + String.valueOf(x_down));
                break;
            case MotionEvent.ACTION_MOVE:
                x_move = (int) (event.getX()) - x_newDown;
                if (x_move > 10 || x_move < -10) {
                    if (null != moveListener) {
                        getParent().requestDisallowInterceptTouchEvent(true);//父控件不监控事件
                        stop();//停止更新ui
                        moveListener.onMove();
                        CLog.d("m:" + String.valueOf(x_move));
                        x_current = (int) (seek.right + x_move);
                        x_newDown += x_move;
                        invalidate();
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
                CLog.d("mu:" + String.valueOf(x_move));
                if (null != moveListener && x_newDown - x_down != 0) {
//                    int length = x_current - x_down;//移动的距离
                    int musicPosition = toMusicPositon(x_current);
                    moveListener.onSeek(musicPosition);
                    start();
                }

                if (x_down == x_newDown && null != playClickListener) {
                    playClickListener.onClick();
                }
//                invalidate();
                break;
            default: break;
        }

//        return true;
        return super.onTouchEvent(event);
    }

    public void setPlayerLength(int playerLength) {
        this.playerLength = playerLength;
    }
}
