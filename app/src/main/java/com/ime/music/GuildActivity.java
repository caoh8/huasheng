package com.ime.music;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.viewpager.widget.ViewPager;

import com.tendcloud.tenddata.TCAgent;

import java.util.ArrayList;
import java.util.List;

public class GuildActivity extends Activity implements ViewPager.OnPageChangeListener{

    private ViewPager vP;
    private int []imageArray;

    private ViewGroup viewGroup;
    private List<View> viewsList;

    private ImageView iv_point;
    private ImageView[] iv_PointArray;

    private Button button;

    protected void onPause() {
        super.onPause();
        TCAgent.onPageEnd(GuildActivity.this,"首次启动引导页");
    }

    protected void onResume() {
        super.onResume();
        TCAgent.onPageStart(GuildActivity.this,"首次启动引导页");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //获取当前窗口，并调用windowsManager来控制窗口
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //去除顶部标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_guild);



        button = findViewById(R.id.startButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override//设置监听，当滑动结束后点击按钮跳转到App主页面
            public void onClick(View view) {
                startActivity(new Intent(GuildActivity.this,SettingActivity.class));
                finish();
                overridePendingTransition(R.anim.anim_in,R.anim.anim_out);
                SharedPreferences preferences = getSharedPreferences("GuildActivity", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                    // 将登录标志位设置为false，下次登录时不在显示引导页
                    editor.putBoolean("firstStart", false);
                    editor.apply();
            }
        });
        //初始化要加载的页面，即滑动页面
        initViewPager();
        //初始化活动要加载的点，即滑动时的小圆点
        initPoint();
    }

    private void initViewPager(){
        //加载第一张启动页面
        vP = (ViewPager)findViewById(R.id.viewpager_launcher);
        //滑动页面放到一个imageArray数组中
        imageArray = new int[]{R.drawable.welcom_01_std,R.drawable.welcom_02_std,R.drawable.welcom_03_std};
        viewsList = new ArrayList<>();
        LinearLayout.LayoutParams params= new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
        //获取imageArray数组的长度，现实当前页面，当数组中还有页面时，继续添加到viewList中显示
        int len = imageArray.length;
        for (int i = 0;i<len;i++){
            ImageView IV = new ImageView(this);
            IV.setLayoutParams(params);
            IV.setBackgroundResource(imageArray[i]);
            viewsList.add(IV);
        }
        //实例化GuildPagerAdapter
        vP.setAdapter(new GuildPagerAdapter(viewsList));
        //设置滑动监听
        vP.addOnPageChangeListener(this);
    }

    private  void  initPoint() {
        viewGroup = (ViewGroup)findViewById(R.id.dot);
        iv_PointArray = new ImageView[viewsList.size()];
        int size = viewsList.size();
        for(int i = 0; i <size;i++){
            iv_point = new ImageView(this);
            //实例化圆点，设置圆点的参数大小，位置
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(60,60);
            lp.leftMargin = 20;
            lp.rightMargin = 20;
            iv_point.setLayoutParams(lp);


            iv_PointArray[i] = iv_point;
            if(i == 0){
                iv_point.setBackgroundResource(R.drawable.dot);
            }else{
                iv_point.setBackgroundResource(R.drawable.black_dot);
            }
            viewGroup.addView(iv_PointArray[i]);
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

    /**
     * This method will be invoked when a new page becomes selected. Animation is not
     * necessarily complete.
     *
     * @param position Position index of the new selected page.
     */
    @Override
    public void onPageSelected(int position) {

        int lenth = imageArray.length;
        for (int i = 0; i < lenth; i++) {
            iv_PointArray[i].setBackgroundResource(R.drawable.dot);
            if (position != i) {
                iv_PointArray[i].setBackgroundResource(R.drawable.black_dot);
            }
            if (position == lenth - 1) {
                button.setVisibility(View.VISIBLE);
            } else {
                button.setVisibility(View.GONE);
            }
        }

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

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
