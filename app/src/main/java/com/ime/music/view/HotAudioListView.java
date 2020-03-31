package com.ime.music.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ime.music.CLog;
import com.ime.music.R;
import com.ime.music.net.parse.HotAudioParser;
import com.ime.music.net.search.Searcher;
import com.ime.music.net.shower.HotAudioListShower;
import com.ime.music.util.ConstantUtil;
import com.ime.music.util.Tools;

//public class HotAudioListView extends pullAddListView {

public class HotAudioListView extends ListView {
    private HotAudioListAdapter mAdapter;
    private HistoryAudioListAdapter adapterHistory;

    private View reloadView;
    private TextView textViewRefresh;
    private ProgressBar progressBarReload;
    private LinearLayout layoutReload;
    int height;

//    public SongListAdapter adapter()
//   不要使用setAdapter
    private void init(Context context) {
        mAdapter = new HotAudioListAdapter(context);
        setAdapter(mAdapter);

        adapterHistory = new HistoryAudioListAdapter(context);
//        setAdapter(adapterHistory);

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
                Searcher.search_new(ConstantUtil.http_audio_hot, new HotAudioParser(),
                        new HotAudioListShower(HotAudioListView.this, true), 2);
            }
        });
    }

    public void showReload() {
        if (isHistoryList) return;
        if (mAdapter.getCount() != 0) return;
        layoutReload.setVisibility(VISIBLE);
        progressBarReload.setVisibility(GONE);
//        int h = getMeasuredHeight();
//        CLog.d("h: " + h);
//        int pedding = h > 0 ? h / 2  : Tools.dip2px(400);
        int pedding = Tools.dip2px(200);
        CLog.d("pedding: " + pedding);
        reloadView.setPadding(0, pedding, 0, 0);
    }

    public void hidReload() {
        //界面
        progressBarReload.setVisibility(GONE);
        layoutReload.setVisibility(VISIBLE);
        reloadView.setPadding(0, -height, 0, 0);
    }

    public HotAudioListView(Context context) {
        super(context);
        init(context);
    }

    public HotAudioListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private boolean isHistoryList;

    public void showHot() {
        post(new Runnable() {
            @Override
            public void run() {
                Searcher.search_new(ConstantUtil.http_audio_hot, new HotAudioParser(),
                        new HotAudioListShower(HotAudioListView.this, true));
                setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                isHistoryList = false;
            }
        });
    }

    public void showHistory() {
        post(new Runnable() {
            @Override
            public void run() {
                hidReload();
                adapterHistory.freshData();
                setAdapter(adapterHistory);
                adapterHistory.notifyDataSetChanged();
                isHistoryList = true;
                if(adapterHistory.getCount() == 0) {
//                    Toast.makeText(getContext(), "没有历史数据", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public HistoryAudioListAdapter getHistoryAdapter() {
        return adapterHistory;
    }

    public HotAudioListAdapter getHotAdapter() {
        return mAdapter;
    }

//    private void setAdapter(ListAdapter adapter) {
//        super.setAdapter(adapter);
//    }
}
