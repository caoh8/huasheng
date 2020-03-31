package com.ime.music;

import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ime.music.info.AudioInfo;
import com.ime.music.net.parse.AudiosParser;
import com.ime.music.net.post.Report;
import com.ime.music.net.search.Searcher;
import com.ime.music.net.shower.Shower;
import com.ime.music.util.ConstantUtil;
import com.ime.music.util.Tools;
import com.ime.music.view.AudioListAdapter;
import com.ime.music.view.AudioListAdapterSearch;
import com.ime.music.view.AudioListView;
import com.tendcloud.tenddata.TCAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.opengl.Matrix.length;

public class SearchActivity extends SlideBackActivity {

    InputMethodManager inputMethodManager;
    ListView audioListView;
    AudioListAdapterSearch adapter;
    LinearLayout lEmpty;
    View webMiss;
    EditText et;
    ImageView qingchu;

    protected void onPause() {
        super.onPause();
        TCAgent.onPageEnd(SearchActivity.this, "搜索页");
    }

    protected void onResume() {
        super.onResume();
        TCAgent.onPageStart(SearchActivity.this, "搜索页");}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除顶部标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setStatusBarColor(Color.parseColor("#FFFFFF"));
        setContentView(R.layout.activity_search);
        inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        findViewById(R.id.search_activity_tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(SearchActivity.this, StartSuccess.class));
                audioListView.setVisibility(View.GONE);
                et.setText("");
                finish();
            }
        });
        lEmpty = findViewById(R.id.search_activity_layout_empty);
        lEmpty.setVisibility(View.GONE);



        et = findViewById(R.id.search_activity_et);
        findViewById(R.id.search_activity_tv_feedback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Report.report(1, et.getText().toString(), null);
                Toast.makeText(getApplicationContext(), "已经收到", Toast.LENGTH_SHORT).show();
            }
        });

        qingchu = findViewById(R.id.iv_qingchu);
        if (et.getText().length()>0){
            qingchu.setVisibility(View.VISIBLE);
        }else {
            qingchu.setVisibility(View.GONE);
        }

        webMiss = findViewById(R.id.search_activity_web_miss);
        TextView fresh = webMiss.findViewById(R.id.tv_refresh);
        webMiss.setVisibility(View.GONE);
        fresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            Drawable search = ContextCompat.getDrawable(getApplicationContext(), R.drawable.search_std);
            search.setBounds(0, 0, 36, 36);
            et.setCompoundDrawables(search, null, null, null);
        }

        audioListView = findViewById(R.id.search_activity_audio_list_view);
        adapter = new AudioListAdapterSearch(getApplicationContext());
        audioListView.setAdapter(adapter);

        et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH:
                        inputMethodManager.hideSoftInputFromWindow(et.getWindowToken(), 0);
                        search();
                        break;
                }
                return true;
            }
        });
    }

    private void search() {
        String keyword = Tools.ModifyStr(et.getText().toString());
        Map kv = new HashMap();
        kv.put("点击", "搜索");
        TCAgent.onEvent(getApplicationContext(), "搜索内容", keyword, kv);
        Searcher.search_new(ConstantUtil.http_audio_search + "keyword=" + keyword, new AudiosParser(), new Shower() {
            @Override
            public void init() {

            }

            @Override
            public void info(String msg) {

            }

            @Override
            public void error(String msg, Error e) {
                audioListView.post(new Runnable() {
                    @Override
                    public void run() {
                        clearListView();
                        switch (e) {
                            case net:
                                showWebMissView();
                                hideListView();
                                break;
                            case parse:
                                showEmptyView();
                                hideListView();
                                break;
                        }
                    }
                });
            }

            @Override
            public void Show(ArrayList<Map<String, Object>> result, boolean isOk) {
                audioListView.post(new Runnable() {
                    @Override
                    public void run() {
                        if (isOk) {
                            hideWebMissView();
                            clearListView();
                            if (result.isEmpty()) {
                                showEmptyView();
                                return;
                            } else {
                                hideEmptyView();
                            }
                            for (int i = 0; i < result.size(); ++i) {
                                AudioInfo info = new AudioInfo();
                                info.setDuration((int) result.get(i).get("Duration"));
                                info.setAudioName((String) result.get(i).get("AudioName"));
                                info.setFileHash((String) result.get(i).get("FileHash"));
                                info.setId((String) result.get(i).get("ID"));
                                info.setSource((String) result.get(i).get("Source"));
                                adapter.getSongInfoData().add(info);
                            }
                            adapter.notifyDataSetChanged();
                            showListView();
                        }
                    }
                });
            }
        });
    }

    private void hideListView() {
        audioListView.setVisibility(View.GONE);
    }

    private void showListView() {
        audioListView.setVisibility(View.VISIBLE);
    }

    private void clearListView() {
        hideListView();
        ArrayList<AudioInfo> data = adapter.getSongInfoData();
        data.clear();
        adapter.notifyDataSetChanged();
    }

    private void showEmptyView() {
        hideListView();
        lEmpty.setVisibility(View.VISIBLE);
    }

    private void hideEmptyView() {
        lEmpty.setVisibility(View.GONE);
    }

    private void showWebMissView() {
        hideListView();
        webMiss.setVisibility(View.VISIBLE);
    }

    private void hideWebMissView() {
        webMiss.setVisibility(View.GONE);
    }
}
