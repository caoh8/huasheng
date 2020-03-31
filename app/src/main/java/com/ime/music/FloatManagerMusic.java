package com.ime.music;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;

import com.ime.music.net.post.Report;
import com.ime.music.util.DeviceInfo;
import com.ime.music.util.Tools;
import com.ime.music.view.FloatWindowView;
import com.yhao.floatwindow.FloatWindow;
import com.yhao.floatwindow.MoveType;
import com.yhao.floatwindow.PermissionListener;
import com.yhao.floatwindow.Screen;
import com.yhao.floatwindow.ViewStateListener;

import cn.sharesdk.framework.Platform;

public class FloatManagerMusic {
    private final String TAG = "FloatManagerMusic";
    private final Context context;
    private boolean isBallInitialed = false;
    private boolean isWindowInitialed = false;
    private static final String TAG_BALL = "ball";
    private static final String TAG_WINDOW = "window";
    private int currentX;
    private int currentY;

    public FloatManagerMusic(Context context) {
        this.context = context;
        init();
    }

    public void init() {
        initBall();
        initWindow();

        FloatWindow.get(TAG_BALL).show();
        FloatWindow.get(TAG_WINDOW).hide();
    }

    private void initWindow() {
        if (isWindowInitialed) {
            return;
        }
        isWindowInitialed = true;

        createWindow();
    }

    private final int WidthWindow = 278;

    private int getWindowWidth() {
        int width = FloatWindow.get(TAG_WINDOW).getView().getMeasuredWidth();
        if (width == 0) width = Tools.dip2px(WidthWindow);
        return width;
    }

    private int getWindowHeight() {
        int height = FloatWindow.get(TAG_WINDOW).getView().getMeasuredHeight();
        if (height == 0) height = Tools.dip2px(410);
        return height;
    }

    private void updateWindowX(int x) {
        if (x < 0) FloatWindow.get(TAG_WINDOW).updateX(0);
        else if (x + getWindowWidth() > DeviceInfo.getDeviceWidth(context))
            FloatWindow.get(TAG_WINDOW).updateX(DeviceInfo.getDeviceWidth(context) - getWindowWidth());
        else
            FloatWindow.get(TAG_WINDOW).updateX(x);
    }

    private void updateWindowY(int y) {
        if (y < 0) FloatWindow.get(TAG_WINDOW).updateY(0);
        else if (y + getWindowHeight() > DeviceInfo.getDeviceHeight(context))
            FloatWindow.get(TAG_WINDOW).updateY(DeviceInfo.getDeviceHeight(context) - getWindowHeight());
        else
            FloatWindow.get(TAG_WINDOW).updateY(y);
    }

    public View getWindowView() {
        return FloatWindow.get(TAG_WINDOW).getView();
    }

    private void createWindow() {
        View view = new FloatWindowView(context);
        FloatWindow
                .with(context)
                .setTag(TAG_WINDOW)
                .setView(view)
                .setWidth(Tools.dip2px(WidthWindow))                               //设置控件宽高
//                .setHeight(Screen.width,0.5f)
//                .setX(100)                                   //设置控件初始位置
//                .setY(Screen.height, 0.5f)
                .setDesktopShow(true)                        //桌面显示
                .setViewStateListener(new ViewStateListener() {
                    @Override
                    public void onPositionUpdate(int i, int i1) {
                        currentX = i;
                        currentY = i1;
//                        CLog.e("Update2: " + i + " " + i1);
                        if (i < 0) updateWindowX(0);
                        else if (i + getWindowWidth() > DeviceInfo.getDeviceWidth(context))
                            updateWindowX(DeviceInfo.getDeviceWidth(context) - getWindowWidth());
                        if (i1 < 0) updateWindowY(0);
                        if (i1 + getWindowHeight() > DeviceInfo.getDeviceHeight(context))
                            updateWindowY(DeviceInfo.getDeviceHeight(context) - getWindowHeight());
                    }

                    @Override
                    public void onShow() {

                    }

                    @Override
                    public void onHide() {

                    }

                    @Override
                    public void onDismiss() {

                    }

                    @Override
                    public void onMoveAnimStart() {

                    }

                    @Override
                    public void onMoveAnimEnd() {

                    }

                    @Override
                    public void onBackToDesktop() {

                    }
                })    //监听悬浮控件状态改变
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onSuccess() {
                        CLog.e("悬浮窗权限");
                    }

                    @Override
                    public void onFail() {

                        CLog.e("悬浮窗权限失败");
                    }
                })  //监听权限申请结果.setMoveType(MoveType.slide)
                .setMoveType(MoveType.active)
                .setMoveStyle(50, new AccelerateInterpolator())  //贴边动画时长为500ms，加速插值器
                .setFilter(true)
                .build();
    }

    private ImageView qiu;

    public void changBall(Drawable drawable) {
        qiu.post(() -> qiu.setBackground(drawable));
    }

    private void createBall() {

        View view = LayoutInflater.from(context).inflate(R.layout.layout_float_ball, null);
        qiu = view.findViewById(R.id.layout_float_ball_tv);
        qiu.getBackground().setAlpha(230);

        FloatWindow
                .with(context)
                .setTag(TAG_BALL)
                .setView(view)
//                .setWidth(100)                               //设置控件宽高
//                .setHeight(Screen.width, 0.5f)
//                .setX(Screen.height, 0.5f)                                   //设置控件初始位置
                .setY(Screen.height, 0.3f)
                .setDesktopShow(true)                        //桌面显示
                .setViewStateListener(new ViewStateListener() {
                    @Override
                    public void onPositionUpdate(int i, int i1) {
                        currentX = i;
                        currentY = i1;
                        if (i < 0) FloatWindow.get(TAG_BALL).updateX(0);
                        else if (i + FloatWindow.get(TAG_BALL).getView().getMeasuredWidth() > DeviceInfo.getDeviceWidth(context))
                            FloatWindow.get(TAG_BALL).updateX(DeviceInfo.getDeviceWidth(context) - FloatWindow.get(TAG_BALL).getView().getMeasuredWidth());
                        if (i1 < 0) FloatWindow.get(TAG_BALL).updateY(0);
                        if (i1 + FloatWindow.get(TAG_BALL).getView().getMeasuredHeight() > DeviceInfo.getDeviceHeight(context))
                            FloatWindow.get(TAG_BALL).updateY(DeviceInfo.getDeviceHeight(context) - FloatWindow.get(TAG_BALL).getView().getMeasuredHeight());
                    }

                    @Override
                    public void onShow() {

                    }

                    @Override
                    public void onHide() {

                    }

                    @Override
                    public void onDismiss() {

                    }

                    @Override
                    public void onMoveAnimStart() {

                    }

                    @Override
                    public void onMoveAnimEnd() {

                    }

                    @Override
                    public void onBackToDesktop() {

                    }
                })    //监听悬浮控件状态改变
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onSuccess() {
                        CLog.e("悬浮窗权限");
                    }

                    @Override
                    public void onFail() {

                        CLog.e("悬浮窗权限失败");
                    }
                })  //监听权限申请结果.setMoveType(MoveType.slide)
                .setMoveType(MoveType.active)
//                .setMoveStyle(50, new AccelerateInterpolator())  //贴边动画时长为500ms，加速插值器
                .setFilter(false)
                .build();
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        qiu.setFocusable(false);
        view.setFocusableInTouchMode(false);

        view.setOnClickListener(v -> {

            Report.category();
            initWindow();

            hideBall();
            showWindow();

            SharedPreferences preferences = v.getContext().getSharedPreferences(TAG, Context.MODE_PRIVATE);
            if (!preferences.getBoolean("windowFirst", false)) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("windowFirst", true);
                editor.apply();
                showHelpAlert();
            }
        });
    }

    public void closeFloat() {
        Tools.vibrate();
        Tools.notice();
        showOpenAlert();
        hideWindow();
        hideBall();
    }

    private void initBall() {
        if (isBallInitialed) {
            FloatWindow.get(TAG_BALL).show();
            return;
        }
        isBallInitialed = true;

        createBall();
    }

    private void hideBall() {
        FloatWindow.get(TAG_BALL).hide();
    }

    public void hideWindow() {
        FloatWindow.get(TAG_WINDOW).hide();
    }

    public void showBall() {
        FloatWindow.get(TAG_BALL).show();
        FloatWindow.get(TAG_BALL).updateX(currentX);
        FloatWindow.get(TAG_BALL).updateY(currentY);
    }

    private void showWindow() {
        ((FloatWindowView) FloatWindow.get(TAG_WINDOW).getView()).flushAndCheckedLianXiang();
        FloatWindow.get(TAG_WINDOW).show();
        updateWindowX(currentX);
        updateWindowY(currentY);
    }

    public void showHeadSetAlert() {
        ((FloatWindowView) FloatWindow.get(TAG_WINDOW).getView()).startHeadSetCheck();
    }

    private void showOpenAlert() {
        final String TAG = "OpenAlert";
        if (null == FloatWindow.get(TAG)) {
            View view = LayoutInflater.from(context).inflate(R.layout.layout_open_ball_tip, null);
            FloatWindow
                    .with(context)
                    .setTag(TAG)
                    .setView(view)
                    .setWidth(Tools.dip2px(250))
                    .setHeight(Tools.dip2px(140))
                    .setX(DeviceInfo.getDeviceWidth(context) / 2 - Tools.dip2px(250) / 2)
                    .setY(DeviceInfo.getDeviceHeight(context) / 2 - Tools.dip2px(140) / 2)
                    .setMoveType(MoveType.inactive)
                    .build();

            view.setFocusableInTouchMode(false);

            view.findViewById(R.id.layout_open_ball_ok).setOnClickListener(v -> {
                FloatWindow.get(TAG).hide();
                FloatWindow.destroy(TAG);
            });
        }
        FloatWindow.get(TAG).show();
    }

    public void showHelpAlert() {
        final String TAG = "HelpAlert";
        if (null == FloatWindow.get(TAG)) {
            View view = LayoutInflater.from(context).inflate(R.layout.layout_help_tip, null);
            FloatWindow
                    .with(context)
                    .setTag(TAG)
                    .setView(view)
                    .setWidth(Tools.dip2px(300))
                    .setHeight(Tools.dip2px(280))
                    .setX(DeviceInfo.getDeviceWidth(context) / 2 - Tools.dip2px(300) / 2)
                    .setY(DeviceInfo.getDeviceHeight(context) / 2 - Tools.dip2px(280) / 2)
                    .setMoveType(MoveType.inactive)
                    .build();

            view.setFocusableInTouchMode(false);

            view.findViewById(R.id.layout_help_ok).setOnClickListener(v -> {
                FloatWindow.get(TAG).hide();
                FloatWindow.destroy(TAG);
            });
        }
        FloatWindow.get(TAG).show();
    }

    public void showUpVolumeTip() {
        ((FloatWindowView) FloatWindow.get(TAG_WINDOW).getView()).startVolumeCheck();
    }
}
