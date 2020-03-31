package com.ime.music.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.ime.music.CLog;
import com.ime.music.R;

public class pullAddListView extends ListView implements AbsListView.OnScrollListener {

    private View bottomView;
    private int lastVisibleItemIndex;
    private int totalItemCount;
    private boolean isMoveUp;
    private int bottomHeight;
    private boolean isLoading = false;
    private LoadListener loadListener;
    private int yload;

    public pullAddListView(Context context) {
        super(context);
        init(context);
    }

    public pullAddListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        bottomView = LinearLayout.inflate(context, R.layout.list_view_fresh_bottom, null);
        bottomView.measure(0, 0);
        bottomHeight = bottomView.getMeasuredHeight();
        bottomView.setPadding(0, -bottomHeight, 0, 0);
        this.addFooterView(bottomView);
//        this.setOnScrollListener(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                yload =(int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int moveY=(int) ev.getY();
                int paddingY = 50 + moveY - yload;
                if(paddingY<0) {
                    isMoveUp = true;
//                    bottomView.setPadding(0, 0, 0, 0);
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {
        CLog.d("scroll state: " + i);
        if(totalItemCount <= lastVisibleItemIndex &&i==SCROLL_STATE_IDLE && isMoveUp){
            if(!isLoading && null != loadListener){
                isLoading=true;
                bottomView.setPadding(0, 0, 0, 0);
                loadListener.pullLoad();
            }
        }
    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {
        CLog.d("onScroll" + i + " " + i1 + " " + i2);
//        firstVisibleItemIndex = i;
        lastVisibleItemIndex = i + i1;
        totalItemCount = i2;
    }

    public void setOnLoadListener(LoadListener loadListener) {
        this.loadListener = loadListener;
    }

    //接口回调
    public interface LoadListener{
        void pullLoad();
    }
    //加载完成
    public void loadComplete(){
        isLoading=false;
        bottomView.setPadding(0, -bottomHeight, 0, 0);
    }
}
