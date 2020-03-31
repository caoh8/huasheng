package com.ime.music.view;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import com.ime.music.CLog;
import com.ime.music.util.ConstantUtil;

public class InputScrollView extends ScrollView {
    public InputScrollView(Context context) {
        super(context);
    }

    public InputScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InputScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        int heightSize = heightMeasureSpec;

        if (getChildCount() > 0) {
            final View child = getChildAt(0);
            int childHeight = child.getMeasuredHeight();

//            CLog.d("childHeight: " + childHeight);
//            CLog.d("dh: " + ConstantUtil.keyBoardDefaultHeight);
            if (ConstantUtil.keyBoardDefaultHeight != -1 && childHeight != 0)
                setMeasuredDimension(widthMeasureSpec, ConstantUtil.keyBoardDefaultHeight);
        }
    }
}
