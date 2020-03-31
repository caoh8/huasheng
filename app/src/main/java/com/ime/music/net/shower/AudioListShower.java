package com.ime.music.net.shower;

import android.widget.Toast;

import com.ime.music.CLog;
import com.ime.music.info.AudioInfo;
import com.ime.music.view.AudioListAdapter;
import com.ime.music.view.AudioListView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;

public class AudioListShower implements Shower {
    private AudioListView listView;
    private AudioListAdapter adapter;
    private boolean mode;

    public AudioListShower(@NotNull AudioListView listView, boolean clear) {
        this.listView = listView;
        this.mode = clear;
        this.adapter = (AudioListAdapter) listView.getAdapter();
//        this.adapter = (AudioListAdapter)((HeaderViewListAdapter)listView.getAdapter()).getWrappedAdapter();
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
        listView.post(new Runnable() {
            @Override
            public void run() {
                synchronized (adapter) {
                    adapter.getSongInfoData().clear();
                    notifyListView();
                }
            }
        });

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

    }

    @Override
    synchronized public void Show(final ArrayList<Map<String, Object>> result, boolean isOk) {
        listView.post(new Runnable() {
            @Override
            public void run() {
                synchronized (adapter) {
                    if(adapter != null && !result.isEmpty()) {
                        ArrayList<AudioInfo> data = adapter.getSongInfoData();
                        if (mode && !data.isEmpty()) {//clear模式
                            CLog.d("clear audios list");
                            notifyListView();
                            data.clear();
                            notifyListView();
                        }
                        for (int i=0; i<result.size(); ++i) {
                            CLog.d("renew audios list");
                            notifyListView();
                            AudioInfo info = new AudioInfo();
                            info.setDuration((int) result.get(i).get("Duration"));
                            info.setAudioName((String) result.get(i).get("AudioName"));
                            info.setFileHash((String) result.get(i).get("FileHash"));
                            info.setId((String) result.get(i).get("ID"));
                            info.setSource((String) result.get(i).get("Source"));
                            data.add(info);
                        }
                        notifyListView();
                    } else {
                        listView.post(new Runnable() {
                            @Override
                            public void run() {
                                CLog.d("no audios list");
//                                Toast.makeText(listView.getContext(), "没有更多了", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        });
    }
}
