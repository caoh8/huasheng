package com.ime.music.net.shower;

import android.widget.Toast;

import com.ime.music.CLog;
import com.ime.music.info.AudioInfo;
import com.ime.music.util.Tools;
import com.ime.music.view.HotAudioListAdapter;
import com.ime.music.view.HotAudioListView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;

public class HotAudioListShower implements Shower {
    private HotAudioListView listView;
    private final HotAudioListAdapter adapter;
    private boolean mode;

    public HotAudioListShower(@NotNull HotAudioListView listView, boolean clear) {
        this.listView = listView;
        this.mode = clear;
        this.adapter = listView.getHotAdapter();
//        this.adapter = (HotAudioListAdapter) listView.getAdapter();
//        this.adapter = (HotAudioListAdapter)((HeaderViewListAdapter)listView.getAdapter()).getWrappedAdapter();
    }

    synchronized private void notifyListView() {
        listView.post(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    synchronized public void init() {
//        listView.post(new Runnable() {
//            @Override
//            public void run() {
//                synchronized (adapter) {
//                    adapter.getSongInfoData().clear();
//                    notifyListView();
//                }
//            }
//        });
    }

    @Override
    synchronized public void info(String msg) {
        listView.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(listView.getContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void error(String msg, Error e) {
        listView.post(new Runnable() {
            @Override
            public void run() {
                switch (e) {
                    case parse:
                        break;
                    case net:
                        listView.showReload();
                        break;
                }
            }
        });
    }

    @Override
    synchronized public void Show(final ArrayList<Map<String, Object>> result, boolean isOk) {
        listView.post(new Runnable() {
            @Override
            public void run() {
                synchronized (adapter) {
                    listView.hidReload();
                    if (!result.isEmpty()) {
                        ArrayList<AudioInfo> data = adapter.getSongInfoData();
                        if (mode && !data.isEmpty()) {//clear模式
                            CLog.d("clear audios list");
                            data.clear();
                            notifyListView();
                        }
                        Tools.hotAdd(result, data);
                        notifyListView();
//            listView.setOnScrollListener(listView);
                    } else {
                        CLog.d("no audios list");
//                        Toast.makeText(listView.getContext(), "没有更多了", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
