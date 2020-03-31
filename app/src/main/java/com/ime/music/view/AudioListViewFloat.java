package com.ime.music.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class AudioListViewFloat extends ListView {
    private void init(Context context) {
    }

    public AudioListViewFloat(Context context) {
        super(context);
        init(context);
    }

    public AudioListViewFloat(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
}
