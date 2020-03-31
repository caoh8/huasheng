package com.ime.music.net.shower;

import android.widget.HeaderViewListAdapter;
import android.widget.Toast;

import com.ime.music.CLog;
import com.ime.music.info.SongInfo;
import com.ime.music.view.SongListAdapter;
import com.ime.music.view.SongListView;

import java.util.ArrayList;
import java.util.Map;

public class SongListShower implements Shower {
    private final SongListView listView;
    private final SongListAdapter adapter;
    private boolean mode;

    public SongListShower(SongListView listView, boolean clear) {
        this.listView = listView;
        this.mode = clear;
//        this.adapter = (SongListAdapter) listView.getAdapter();
        this.adapter = (SongListAdapter) ((HeaderViewListAdapter)listView.getAdapter()).getWrappedAdapter();
    }

    synchronized private void notifyListView() {
        listView.post(new Runnable() {
            @Override
            public void run() {
                listView.loadComplete();
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    synchronized public void init() {
        listView.post(new Runnable() {
            @Override
            public void run() {
                synchronized (adapter) {
                    if (mode) {
                        listView.setOnScrollListener(null);
                        adapter.getSongInfoData().clear();
                        notifyListView();
                    }
                }
            }
        });

    }

    @Override
    synchronized public void info(String msg) {
        listView.post(new Runnable() {
            @Override
            public void run() {
//                Toast.makeText(listView.getContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    synchronized public void error(String msg, Error e) {
        listView.post(new Runnable() {
            @Override
            public void run() {
                listView.loadComplete();
//                Toast.makeText(listView.getContext(), msg, Toast.LENGTH_SHORT).show();

                CLog.e("歌曲列表获取失败 错误码：" + e);
                switch (e) {
                    case net:
                        listView.showReload();
                        break;
                    case parse:
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
                if (result.isEmpty()) {
//                    Toast.makeText(listView.getContext(), "没有结果", Toast.LENGTH_SHORT).show();
                    return;
                }
                synchronized (adapter) {
                    listView.hidReload();
                    ArrayList<SongInfo> data = adapter.getSongInfoData();
                    if (mode) {//clear模式
                        CLog.d("clear songs list");
                        listView.setOnScrollListener(null);
//                notifyListView();
                        data.clear();
                        notifyListView();
                    }
                    for (int i = 0; i < result.size(); ++i) {
                        CLog.d("renew songs list");
                        notifyListView();
                        SongInfo info = new SongInfo();
                        info.setFileHash((String) result.get(i).get("FileHash"));
                        info.setFileName((String) result.get(i).get("FileName"));
                        info.setAlbumAudioID((String) result.get(i).get("AlbumAudioID"));
                        info.setExtname((String) result.get(i).get("ExtName"));
//                info.setFeetype((String) result.get(i).get("FeeType"));
                        info.setSingerName((String) result.get(i).get("SingerName"));
                        info.setSongName((String) result.get(i).get("SongName"));
                        info.setAlbumID((String) result.get(i).get("AlbumID"));
                        info.setSegment((String) result.get(i).get("Segment"));
                        data.add(info);
                    }
                    notifyListView();
                    listView.setOnScrollListener(listView);
                }
            }
        });
    }
}
