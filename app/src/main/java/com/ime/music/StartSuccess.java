package com.ime.music;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.ime.music.net.Download;
import com.ime.music.net.parse.HotAudioParser;
import com.ime.music.net.search.Searcher;
import com.ime.music.net.shower.HotAudioListShower;
import com.ime.music.prepare.FloatWindowManager;
import com.ime.music.prepare.Permission;
import com.ime.music.service.LianXiangService;
import com.ime.music.util.ConstantUtil;
import com.ime.music.view.HotAudioListView;
import com.tendcloud.tenddata.TCAgent;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

import static com.ime.music.net.RenewSoft.INSTALL_PERMISS_CODE;

public class StartSuccess extends Activity implements ViewPager.OnPageChangeListener {

    private ImageView mIvSetting;
    private FrameLayout share;
    private FrameLayout lianxiang;

    private ArrayList<View> pageview;
    private ImageView sb1;
    private ImageView sb4;

    // 滚动条初始偏移量
    private int offset = 0;
    //一倍滚动量
    private int one;


    protected void onPause() {
        super.onPause();
        TCAgent.onPageEnd(StartSuccess.this, "启动成功页");
    }

    protected void onResume() {
        super.onResume();
        TCAgent.onPageStart(StartSuccess.this, "启动成功页");

        // 开启悬浮球
        FloatManagerMusic floatManagerMusic = ConstantUtil.getFloatManagerMusic();
        floatManagerMusic.init();
//        RenewSoft soft = new RenewSoft(this);

        if (!FloatWindowManager.getInstance().checkPermission(StartSuccess.this)) {
            Intent intent = new Intent(StartSuccess.this, FloatWindowActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == INSTALL_PERMISS_CODE) {
//            Toast.makeText(this,"安装应用",Toast.LENGTH_SHORT).show();
            installAPK(ConstantUtil.softDownloadAddress, getCacheDir().getPath());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除顶部标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setStatusBarColor(Color.parseColor("#FFFFFF"));
        setContentView(R.layout.shouye);
        initViewPager();

        mIvSetting = findViewById(R.id.iv_setting);
        mIvSetting.setOnClickListener(view -> {
            Intent intent = new Intent(StartSuccess.this, ShezhiActivity.class);
            startActivity(intent);
            Map<Object, Object> kv = new HashMap<>();
            kv.put("点击", "启用成功页-设置");
            TCAgent.onEvent(StartSuccess.this, "启用成功页-设置点击", "", kv);
        });

        findViewById(R.id.shouye_iv_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartSuccess.this, SearchActivity.class));
                Map kv = new HashMap();
                kv.put("点击", "首页-搜索");
                TCAgent.onEvent(getApplicationContext(), "首页搜索按钮点击", "", kv);
            }
        });

        mIvSetting.post(this::requestRenew);
    }

    private void setGifClicked(GifImageView gif) {
        gif.setOnClickListener(v -> {
            try {
                GifDrawable drawable = (GifDrawable) gif.getDrawable();
                if (!drawable.isRunning()) {
                    drawable.start();
                } else {
                    drawable.stop();
                }
            } catch (Exception e) {
                CLog.e(e.getMessage());
            }

        });
    }

    HotAudioListView hotAudioListView;

    private void initViewPager() {
        ViewPager vpShouye = findViewById(R.id.viewpager_shouye);
        sb1 = findViewById(R.id.scroll_ball);
        sb4 = findViewById(R.id.scroll_ball4);
        LayoutInflater inflater = getLayoutInflater();
        View view1 = inflater.inflate(R.layout.zhinan, null);
        TextView vchat = view1.findViewById(R.id.tv_wechat);
        TextView qq = view1.findViewById(R.id.tv_qq);

        GifImageView gif = view1.findViewById(R.id.share_gif);
        GifImageView gif2 = view1.findViewById(R.id.share_gif2);
        setGifClicked(gif);
        setGifClicked(gif2);

        RadioButton rb1 = view1.findViewById(R.id.rb_1);
        RadioButton rb2 = view1.findViewById(R.id.rb_2);
        share = view1.findViewById(R.id.sv_share);
        lianxiang = view1.findViewById(R.id.sv_lianxiang);
        rb1.setChecked(true);
        share.setVisibility(View.VISIBLE);
        lianxiang.setVisibility(View.GONE);

        mTvZhinan = findViewById(R.id.tv_zhinan);
        sb1 = findViewById(R.id.scroll_ball);
        sb2 = findViewById(R.id.scroll_ball2);
        sb3 = findViewById(R.id.scroll_ball3);
        TextView close_ac = view1.findViewById(R.id.tv_zhinan_close_ac);
        close_ac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(intent);
                Toast.makeText(StartSuccess.this, "关闭花生语音包 - 无障碍权限", Toast.LENGTH_LONG).show();
            }
        });

        rb1.setOnClickListener(view -> {
            share.setVisibility(View.VISIBLE);
            lianxiang.setVisibility(View.GONE);
        });

        rb2.setOnClickListener(view -> {
            share.setVisibility(View.GONE);
            lianxiang.setVisibility(View.VISIBLE);
            close_ac.setText("点这里暂时关闭该功能（改进中）");

            if (!Permission.isAccessibilitySettingsOn(StartSuccess.this, LianXiangService.class.getCanonicalName())) {
                close_ac.setText(" ");
                Intent intent = new Intent(StartSuccess.this, LianXiangActivity.class);
                startActivity(intent);
            }
        });


        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Drawable wexin = ContextCompat.getDrawable(StartSuccess.this, R.drawable.weixin_std);
            if (wexin != null) {
                wexin.setBounds(0, 0, 72, 72);
            }
            vchat.setCompoundDrawables(wexin, null, null, null);


            Drawable d_qq = ContextCompat.getDrawable(StartSuccess.this, R.drawable.qq_std);
            if (d_qq != null) {
                d_qq.setBounds(0, 0, 72, 72);
            }
            qq.setCompoundDrawables(d_qq, null, null, null);
        }


        vchat.setOnClickListener(view -> getWechatApi());

        qq.setOnClickListener(view -> getQQApi());
        View view2 = inflater.inflate(R.layout.remen, null);
        hotAudioListView = view2.findViewById(R.id.hot_audio_list);
        Searcher.search_new(ConstantUtil.http_audio_hot, new HotAudioParser(),
                new HotAudioListShower(hotAudioListView, true), 2);


        pageview = new ArrayList<>();
        pageview.add(view1);
        pageview.add(view2);
        //数据适配器
        PagerAdapter mPagerAdapter = new PagerAdapter() {

            @Override
            //获取当前窗体界面数
            public int getCount() {
                // TODO Auto-generated method stub
                return pageview.size();
            }

            @Override
            //判断是否由对象生成界面
            public boolean isViewFromObject(View arg0, Object arg1) {
                // TODO Auto-generated method stub
                return arg0 == arg1;
            }

            //使从ViewGroup中移出当前View
            public void destroyItem(ViewGroup arg0, int arg1, Object arg2) {
                arg0.removeView(pageview.get(arg1));
            }

            //返回一个对象，这个对象表明了PagerAdapter适配器选择哪个对象放在当前的ViewPager中
            public Object instantiateItem(ViewGroup arg0, int arg1) {
                arg0.addView(pageview.get(arg1));
                return pageview.get(arg1);
            }
        };
        vpShouye.setAdapter(mPagerAdapter);
        SharedPreferences preferences = getSharedPreferences("StartSuccess", MODE_PRIVATE);
        if (!preferences.getBoolean("showHot", false)) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("showHot", true);
            editor.apply();
            vpShouye.setCurrentItem(0);
        } else {
            vpShouye.setCurrentItem(1);
            initPagesHot();
        }
        //添加切换界面的监听器
        vpShouye.addOnPageChangeListener(this);

        // 获取滚动条的宽度
        // 滚动条宽度
//        int bmpW = dip2px(getApplicationContext(), 24);
        //为了获取屏幕宽度，新建一个DisplayMetrics对象
        DisplayMetrics displayMetrics = new DisplayMetrics();
        //将当前窗口的一些信息放在DisplayMetrics类中
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        //得到屏幕的宽度
//        int screenW = displayMetrics.widthPixels;
        //计算出滚动条初始的偏移量
        offset = dip2px(getApplicationContext(), 25);
        //计算出切换一个界面时，滚动条的位移量
        one = dip2px(getApplicationContext(), 22);
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        //将滚动条的初始位置设置成与左边界间隔一个offset
        sb1.setImageMatrix(matrix);
    }


    private void requestRenew() {
        OkHttpClient client = new OkHttpClient.Builder()
                .callTimeout(1, TimeUnit.SECONDS)
                .readTimeout(1, TimeUnit.SECONDS)
                .build();

        String url = ConstantUtil.checkSoftUrl + "hash=" + ConstantUtil.softHash;
//        CLog.e(url);
        Request request = new Request.Builder()
                .url(url)
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                CLog.d("更新请求失败");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String ret = response.body().string();

                try {
                    JSONObject o = new JSONObject(ret);

                    int status = o.getInt("status");

                    if (200 == status) {
                        JSONObject jData = o.getJSONObject("data");
                        boolean update = jData.getBoolean("update");
                        if (update) {
                            String hash = jData.getString("hash");
                            String version = jData.getString("version");
                            String address = jData.getString("address");
                            ConstantUtil.softHash = hash;
                            ConstantUtil.softDownloadAddress = address;
                            ConstantUtil.hasNew = true;
                            ConstantUtil.version = version;

                            mIvSetting.post(() -> showRenewDialog(version));
                        } else if (ConstantUtil.hasNew) {
                            mIvSetting.post(() -> showRenewDialog(ConstantUtil.version));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private String url;
    private String saveDir;
    private Dialog dialogDowning;

    private void showRenewDialog(String content) {
        AlertDialog.Builder renewDialog =
                new AlertDialog.Builder(StartSuccess.this);
        final View dialogView = LayoutInflater.from(StartSuccess.this)
                .inflate(R.layout.request_renew_ayout, null);
        TextView textViewVersion = dialogView.findViewById(R.id.request_renew_tv);
        if (null != content)
            textViewVersion.setText(content);
        renewDialog.setView(dialogView);
        renewDialog.setPositiveButton("暂不", (dialog, which) -> CLog.i("取消更新"))
                .setNegativeButton("下载",
                        (dialog, which) -> {
                            CLog.i("开始下载更新");
                            CLog.e(ConstantUtil.softDownloadAddress);
                            url = ConstantUtil.softDownloadAddress;
                            saveDir = getCacheDir().getPath();
//                    CLog.e(saveDir);
//                        DownloadAPK.download(ConstantUtil.softDownloadAddress, saveDir);

                            AlertDialog.Builder builder = new AlertDialog.Builder(StartSuccess.this);
                            View view = View.inflate(StartSuccess.this, R.layout.download_progress, null);
                            TextView textView = view.findViewById(R.id.download_tv);
                            builder.setView(view);
                            builder.setTitle("正在下载...");
                            builder.setCancelable(false);
                            dialogDowning = builder.create();
                            dialogDowning.show();
                            Download.get().download(url, saveDir, new Download.OnDownloadListener() {
                                @Override
                                public void onDownloadSuccess() {
                                    dialogDowning.dismiss();
                                    CLog.e("下载完成");
                                    mIvSetting.post(() -> setInstallPermission());
                                }

                                @Override
                                public void onDownloading(int progress) {
//                                CLog.e("下载进度： " + progress);
                                    textView.post(() -> {
                                        String str = "下载进度: " + progress + "%";
                                        textView.setText(str);
                                    });

                                }

                                @Override
                                public void onDownloadFailed() {
                                    dialogDowning.dismiss();
                                    CLog.e("下载失败");
                                }
                            });
                        });
        renewDialog.create().show();
    }

    private void installAPK(String url, String saveDir) {
        File file = new File(saveDir, url.substring(url.lastIndexOf("/") + 1));
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri contentUri = FileProvider.getUriForFile(getApplicationContext(),
                    "com.ime.music.fileProvider", file);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        startActivity(intent);
    }

    private void setInstallPermission() {
        boolean haveInstallPermission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //先判断是否有安装未知来源应用的权限
            haveInstallPermission = getPackageManager().canRequestPackageInstalls();
            if (!haveInstallPermission) {
                //弹框提示用户手动打开
                AlertDialog.Builder dialog = new AlertDialog.Builder(StartSuccess.this);
                dialog.setTitle("安装权限");
                dialog.setMessage("需要打开允许来自此来源，请去设置中开启此权限");
                dialog.setPositiveButton("确定", (dialog1, which) -> toInstallPermissionSettingIntent());
                dialog.create().show();
//                dialog.show();
            } else {
                installAPK(url, saveDir);
            }
        } else {
            installAPK(url, saveDir);
        }
    }


    /**
     * 开启安装未知来源权限
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void toInstallPermissionSettingIntent() {
        Uri packageURI = Uri.parse("package:" + getPackageName());
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
        startActivityForResult(intent, INSTALL_PERMISS_CODE);
    }

    private void getWechatApi() {
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            ComponentName cmp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(cmp);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // TODO: handle exception
            Toast.makeText(this, "检查到您手机没有安装微信，请安装后使用该功能", Toast.LENGTH_SHORT).show();
        }
    }

    private void getQQApi() {
        try {
//                第一种方式：是可以的跳转到qq主页面，不能跳转到qq聊天界面
            Intent intent = getPackageManager().getLaunchIntentForPackage("com.tencent.mobileqq");
            startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "检查到您手机没有安装QQ，请安装后使用该功能", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * This method will be invoked when the current page is scrolled, either as part
     * of a programmatically initiated smooth scroll or a user initiated touch scroll.
     *
     * @param position             Position index of the first page currently being displayed.
     *                             Page position+1 will be visible if positionOffset is nonzero.
     * @param positionOffset       Value from [0, 1) indicating the offset from the page at position.
     * @param positionOffsetPixels Value in pixels indicating the offset from position.
     */
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    void initPagesHot() {
        mTvZhinan.setText("热门音频");
        sb1.setVisibility(View.GONE);
        sb2.setVisibility(View.GONE);
        sb3.setVisibility(View.VISIBLE);
        sb4.setVisibility(View.VISIBLE);
        mTvZhinan.post(new Runnable() {
            @Override
            public void run() {
                freshHot();
            }
        });

    }

    private void freshHot() {
        if (hotAudioListView.getHotAdapter().getCount() == 0) {
            Searcher.search_new(ConstantUtil.http_audio_hot, new HotAudioParser(),
                    new HotAudioListShower(hotAudioListView, true), 2);
        }
    }

    TextView mTvZhinan;
    ImageView sb2;
    ImageView sb3;

    /**
     * This method will be invoked when a new page becomes selected. Animation is not
     * necessarily complete.
     *
     * @param position Position index of the new selected page.
     */
    @Override
    public void onPageSelected(int position) {
        Animation animation = null;
        sb4.setVisibility(View.GONE);
        switch (position) {
            case 0:
                mTvZhinan.setText("发声指南");
                sb1.setVisibility(View.VISIBLE);
                sb2.setVisibility(View.VISIBLE);
                sb3.setVisibility(View.GONE);
                animation = new TranslateAnimation(one, 0, 0, 0);
                break;
            case 1:
                mTvZhinan.setText("热门音频");
                sb1.setVisibility(View.GONE);
                sb2.setVisibility(View.GONE);
                sb3.setVisibility(View.VISIBLE);
                animation = new TranslateAnimation(offset, one, 0, 0);
                freshHot();
                break;
        }

        //arg0为切换到的页的编码
        // 当前页编号
        // 将此属性设置为true可以使得图片停在动画结束时的位置
        if (animation != null) {
            animation.setFillAfter(true);
            //动画持续时间，单位为毫秒
            animation.setDuration(200);
        }
        //滚动条开始动画
        sb1.startAnimation(animation);

    }

    /**
     * Called when the scroll state changes. Useful for discovering when the user
     * begins dragging, when the pager is automatically settling to the current page,
     * or when it is fully stopped/idle.
     *
     * @param state The new scroll state.
     * @see ViewPager#SCROLL_STATE_IDLE
     * @see ViewPager#SCROLL_STATE_DRAGGING
     * @see ViewPager#SCROLL_STATE_SETTLING
     */
    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
