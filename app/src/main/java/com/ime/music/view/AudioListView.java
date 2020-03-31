package com.ime.music.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class AudioListView extends ListView {

    private AudioListAdapter mAdapter;

//    public SongListAdapter adapter()
//   不要使用setAdapter
    private void init(Context context) {
        mAdapter = new AudioListAdapter(context);//为了保证所有ListView中都有adapter
        setAdapter(mAdapter);
    }

    public AudioListView(Context context) {
        super(context);
        init(context);
    }

    public AudioListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
}
