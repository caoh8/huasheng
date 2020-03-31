package com.ime.music.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class TipView extends ListView {
//    private TipViewAdapter mAdapter;

    public TipView(Context context) {
        super(context);
        init(context);
    }

    public TipView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TipView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public TipView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
//        mAdapter = new TipViewAdapter(context);//为了保证所有ListView中都有adapter
//        setAdapter(mAdapter);
    }
}
