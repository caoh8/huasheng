package com.ime.music.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ime.music.R;
import com.ime.music.net.parse.SongsParser;
import com.ime.music.net.search.Searcher;
import com.ime.music.net.shower.SongListShower;
import com.ime.music.util.ConstantUtil;

public class SongListView extends pullAddListView {

    private SongListAdapter mAdapter;
    private View reloadView;
    private TextView textViewRefresh;
    private ProgressBar progressBarReload;
    private LinearLayout layoutReload;
    int height;

//    public SongListAdapter adapter()

    private void init(Context context) {
        mAdapter = new SongListAdapter(context);
        setAdapter(mAdapter);
        setOnScrollListener(null);

        reloadView = LinearLayout.inflate(context, R.layout.web_missing, null);
        reloadView.measure(0, 0);
        height = reloadView.getMeasuredHeight();
        reloadView.setPadding(0, -height, 0, 0);
        this.addHeaderView(reloadView);

        textViewRefresh = reloadView.findViewById(R.id.tv_refresh);
        progressBarReload = reloadView.findViewById(R.id.web_miss_progressbar);
        layoutReload = reloadView.findViewById(R.id.layout_reload);
        textViewRefresh.setOnClickListener(new OnClickListener() {
            /**
             * Called when a view has been clicked.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                //界面
                progressBarReload.setVisibility(VISIBLE);
                layoutReload.setVisibility(GONE);
                //搜索
//                String keyWord = Trime.getService().getKeyWord();
                String keyWord = "";
                if (null != keyWord) {
                    String url = ConstantUtil.http_search_song + "keyword=" + keyWord;
                    Searcher.search_new(url, new SongsParser(), new SongListShower(SongListView.this, true), 5);
                }
            }
        });
    }

    public void showReload() {
        layoutReload.setVisibility(VISIBLE);
        progressBarReload.setVisibility(GONE);
        reloadView.setPadding(0, 125, 0, 0);
    }

    public void hidReload() {
        //界面
        progressBarReload.setVisibility(GONE);
        layoutReload.setVisibility(VISIBLE);
        reloadView.setPadding(0, -height, 0, 0);
    }

    public SongListView(Context context) {
        super(context);
        init(context);
    }

    public SongListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
}
