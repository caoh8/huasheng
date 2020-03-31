package com.ime.music.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.ime.music.CLog;
import com.ime.music.LianXiangActivity;
import com.ime.music.R;
import com.ime.music.info.AudioInfo;
import com.ime.music.info.MineAudioInfo;
import com.ime.music.net.parse.HotAudioParser;
import com.ime.music.net.search.Searcher;
import com.ime.music.net.shower.Shower;
import com.ime.music.play.MusicPlayer;
import com.ime.music.prepare.Permission;
import com.ime.music.service.LianXiangResultManager;
import com.ime.music.service.LianXiangService;
import com.ime.music.share.Share;
import com.ime.music.util.ConstantUtil;
import com.ime.music.util.MineManager;
import com.ime.music.util.Tools;
import com.tendcloud.tenddata.TCAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.ime.music.R.drawable.btn_bb;
import static com.ime.music.R.drawable.btn_blue;

public class FloatWindowView extends LinearLayout {
    //    private Context context;
    private final String TAG = "FloatWindowView";
    private RadioGroup radioGroup;
    private AudioListAdapterFloat adapter;

    public FloatWindowView(Context context) {
        super(context);
        init(context);
    }

    public FloatWindowView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FloatWindowView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        adapter = new AudioListAdapterFloat(getContext());
        View view = LayoutInflater.from(context).inflate(R.layout.layout_float_window, null);

        initHead(view);
        initClassifier(view);
        initTail(view);

        addView(view);
    }

    TextView textViewDelay0s;
    TextView textViewDelay1s;
    TextView textViewDelay2s;
    TextView textViewDelay3s;
    TextView textViewDelay4s;
    TextView textViewDelayTime;

    private TextView textViewHeadSet;
    private TextView textViewVolume;

    private void initTail(View view) {
        textViewDelay0s = view.findViewById(R.id.layout_float_window_tv_0_delay);
        textViewDelay1s = view.findViewById(R.id.layout_float_window_tv_1_delay);
        textViewDelay2s = view.findViewById(R.id.layout_float_window_tv_2_delay);
        textViewDelay3s = view.findViewById(R.id.layout_float_window_tv_3_delay);
        textViewDelay4s = view.findViewById(R.id.layout_float_window_tv_4_delay);
        textViewDelayTime = view.findViewById(R.id.layout_float_window_tv_time_delay);
        setPlayDelay(textViewDelay0s, 0);
        setPlayDelay(textViewDelay1s, 1);
        setPlayDelay(textViewDelay2s, 2);
        setPlayDelay(textViewDelay3s, 3);
        setPlayDelay(textViewDelay4s, 4);

        SharedPreferences preferences = getContext().getSharedPreferences(TAG, Context.MODE_PRIVATE);

        int currentTime = preferences.getInt("time", ConstantUtil.PlayDelay);
        textViewDelayTime.setText(String.valueOf(currentTime));
        // 延时调整
        view.findViewById(R.id.layout_float_window_tv_set_delay).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 已经显示则消失
                if (textViewDelay1s.getVisibility() == VISIBLE) {
                    textViewDelay0s.setVisibility(GONE);
                    textViewDelay1s.setVisibility(GONE);
                    textViewDelay2s.setVisibility(GONE);
                    textViewDelay3s.setVisibility(GONE);
                    textViewDelay4s.setVisibility(GONE);
                    return;
                }
                textViewDelay0s.setVisibility(VISIBLE);
                textViewDelay1s.setVisibility(VISIBLE);
                textViewDelay2s.setVisibility(VISIBLE);
                textViewDelay3s.setVisibility(VISIBLE);
                textViewDelay4s.setVisibility(VISIBLE);
//                CharSequence text = textViewDelayTime.getText();
                if (textViewDelayTime.getText().equals("3")) {
                    textViewDelay3s.setBackgroundResource(btn_blue);
                    textViewDelay3s.setTextColor(Color.parseColor("#ffffff"));
                    textViewDelay2s.setBackgroundResource(btn_bb);
                    textViewDelay2s.setTextColor(Color.parseColor("#333333"));
                    textViewDelay1s.setBackgroundResource(btn_bb);
                    textViewDelay1s.setTextColor(Color.parseColor("#333333"));
                    textViewDelay4s.setBackgroundResource(btn_bb);
                    textViewDelay4s.setTextColor(Color.parseColor("#333333"));
                    textViewDelay0s.setBackgroundResource(btn_bb);
                    textViewDelay0s.setTextColor(Color.parseColor("#333333"));
                } else if (textViewDelayTime.getText().equals("2")) {
                    textViewDelay2s.setBackgroundResource(btn_blue);
                    textViewDelay2s.setTextColor(Color.parseColor("#ffffff"));
                    textViewDelay1s.setBackgroundResource(btn_bb);
                    textViewDelay1s.setTextColor(Color.parseColor("#333333"));
                    textViewDelay3s.setBackgroundResource(btn_bb);
                    textViewDelay3s.setTextColor(Color.parseColor("#333333"));
                    textViewDelay4s.setBackgroundResource(btn_bb);
                    textViewDelay4s.setTextColor(Color.parseColor("#333333"));
                    textViewDelay0s.setBackgroundResource(btn_bb);
                    textViewDelay0s.setTextColor(Color.parseColor("#333333"));
                } else if (textViewDelayTime.getText().equals("1")) {
                    textViewDelay1s.setBackgroundResource(btn_blue);
                    textViewDelay1s.setTextColor(Color.parseColor("#ffffff"));
                    textViewDelay2s.setBackgroundResource(btn_bb);
                    textViewDelay2s.setTextColor(Color.parseColor("#333333"));
                    textViewDelay3s.setBackgroundResource(btn_bb);
                    textViewDelay3s.setTextColor(Color.parseColor("#333333"));
                    textViewDelay4s.setBackgroundResource(btn_bb);
                    textViewDelay4s.setTextColor(Color.parseColor("#333333"));
                    textViewDelay0s.setBackgroundResource(btn_bb);
                    textViewDelay0s.setTextColor(Color.parseColor("#333333"));
                } else if (textViewDelayTime.getText().equals("0")) {
                    textViewDelay0s.setBackgroundResource(btn_blue);
                    textViewDelay0s.setTextColor(Color.parseColor("#ffffff"));
                    textViewDelay2s.setBackgroundResource(btn_bb);
                    textViewDelay2s.setTextColor(Color.parseColor("#333333"));
                    textViewDelay3s.setBackgroundResource(btn_bb);
                    textViewDelay3s.setTextColor(Color.parseColor("#333333"));
                    textViewDelay4s.setBackgroundResource(btn_bb);
                    textViewDelay4s.setTextColor(Color.parseColor("#333333"));
                    textViewDelay1s.setBackgroundResource(btn_bb);
                    textViewDelay1s.setTextColor(Color.parseColor("#333333"));
                } else if (textViewDelayTime.getText().equals("4")) {
                    textViewDelay4s.setBackgroundResource(btn_blue);
                    textViewDelay4s.setTextColor(Color.parseColor("#ffffff"));
                    textViewDelay2s.setBackgroundResource(btn_bb);
                    textViewDelay2s.setTextColor(Color.parseColor("#333333"));
                    textViewDelay3s.setBackgroundResource(btn_bb);
                    textViewDelay3s.setTextColor(Color.parseColor("#333333"));
                    textViewDelay0s.setBackgroundResource(btn_bb);
                    textViewDelay0s.setTextColor(Color.parseColor("#333333"));
                    textViewDelay1s.setBackgroundResource(btn_bb);
                    textViewDelay1s.setTextColor(Color.parseColor("#333333"));
                }
            }
        });

        textViewHeadSet = view.findViewById(R.id.layout_float_window_headset_tip);
        textViewVolume = view.findViewById(R.id.layout_float_window_volume);
        textViewHeadSet.setVisibility(GONE);
        textViewVolume.setVisibility(GONE);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            Drawable tishi = ContextCompat.getDrawable(getContext(), R.drawable.tishi_std);
            tishi.setBounds(0, 0, 36, 36);
            ((TextView) view.findViewById(R.id.layout_float_window_tip)).setCompoundDrawables(tishi, null, null, null);
        }

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            Drawable share = ContextCompat.getDrawable(getContext(), R.drawable.share_std2);
            share.setBounds(0, 0, 54, 48);
            ((TextView) view.findViewById(R.id.layout_float_window_share)).setCompoundDrawables(share, null, null, null);
        }

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            Drawable close = ContextCompat.getDrawable(getContext(), R.drawable.guanbi_std);
            close.setBounds(0, 0, 36, 36);
            ((TextView) view.findViewById(R.id.layout_float_window_close_float)).setCompoundDrawables(close, null, null, null);
        }

        view.findViewById(R.id.layout_float_window_close_float).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ConstantUtil.getFloatManagerMusic().closeFloat();
            }
        });

        view.findViewById(R.id.layout_float_window_tip).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ConstantUtil.getFloatManagerMusic().showHelpAlert();
                Map kv = new HashMap();
                kv.put("点击", "提示");
                TCAgent.onEvent(getContext(), "提示按钮点击", "", kv);
            }
        });

        view.findViewById(R.id.layout_float_window_share).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Share.ShareSoft(getContext());
                Map kv = new HashMap();
                kv.put("点击", "悬浮窗-分享");
                TCAgent.onEvent(getContext(), "悬浮窗分享按钮点击", "", kv);
            }
        });
    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (!Tools.isHeadSetON()) {
                textViewHeadSet.post(new Runnable() {
                    @Override
                    public void run() {
                        textViewHeadSet.setVisibility(GONE);
                    }
                });
            } else {
                handler.postDelayed(runnable, 200);
            }
        }
    };

    Handler handlerVolume = new Handler();
    Runnable runnableVolume = new Runnable() {
        @Override
        public void run() {
            if (!Tools.isMusicVolumeZero()) {
                textViewVolume.post(new Runnable() {
                    @Override
                    public void run() {
                        textViewVolume.setVisibility(GONE);
                    }
                });
            } else {
                handlerVolume.postDelayed(runnableVolume, 200);
            }
        }
    };

    public void startHeadSetCheck() {
        textViewHeadSet.setVisibility(VISIBLE);

        handler.post(runnable);
    }

    public void startVolumeCheck() {
        textViewVolume.setVisibility(VISIBLE);

        handlerVolume.post(runnableVolume);
    }

    private void setPlayDelay(View view, int time) {
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ConstantUtil.PlayDelay = time;

                textViewDelay1s.setVisibility(GONE);
                textViewDelay2s.setVisibility(GONE);
                textViewDelay3s.setVisibility(GONE);
                textViewDelay4s.setVisibility(GONE);
                textViewDelay0s.setVisibility(GONE);
                textViewDelayTime.setText(String.valueOf(time));

                SharedPreferences preferences = getContext().getSharedPreferences(TAG, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("time", time);
                editor.apply();
            }
        });
    }

    private final int LianXiang = 0;
    private final int OFFSET_ITEM = 0;
    private final int HI = 1 + OFFSET_ITEM;
    private final int Daily = 2 + OFFSET_ITEM;
    private final int Anger = 3 + OFFSET_ITEM;
    private final int Sad = 4 + OFFSET_ITEM;
    private final int Cute = 5 + OFFSET_ITEM;
    private final int Sweet = 6 + OFFSET_ITEM;
    ArrayList<AudioInfo> dataHot = new ArrayList<>();
    AudioListViewFloat hotAudioListView;
    RadioButton btnHot;
    RadioButton btnLianXiang;
    RadioButton btnCute;
    RadioButton btnAnge;
    RadioButton btnSweet;
    RadioButton btnHi;
    RadioButton btnDaily;
    RadioButton btnSad;
    RadioButton btnMine;
    View lianXiangMiss;
    View noShouCang;
    TextView noLianXiang;
    TextView noPermission;
    TextView learnMore;
    TextView xiaomi;

    private void initClassifier(View view) {
        hotAudioListView = view.findViewById(R.id.layout_float_window_audio_list);
        radioGroup = view.findViewById(R.id.layout_float_window_radio_group);
        btnHot = view.findViewById(R.id.layout_float_window_btn_hot);
        btnDaily = view.findViewById(R.id.layout_float_window_btn_daily);
        btnHi = view.findViewById(R.id.layout_float_window_btn_hi);
        btnSad = view.findViewById(R.id.layout_float_window_btn_sad);
        btnSweet = view.findViewById(R.id.layout_float_window_btn_sweet);
        btnAnge = view.findViewById(R.id.layout_float_window_btn_ange);
        btnCute = view.findViewById(R.id.layout_float_window_btn_cute);
        btnLianXiang = view.findViewById(R.id.layout_float_window_btn_lianxiang);
        lianXiangMiss = view.findViewById(R.id.layout_float_window_lianxiang_miss);
        noShouCang = view.findViewById(R.id.layout_float_window_frame_mine);
        noLianXiang = view.findViewById(R.id.tv_lianxiang);
        noPermission = view.findViewById(R.id.tv_wuzhangai);
        learnMore = view.findViewById(R.id.layout_float_window_tv_lear_more);
        xiaomi = view.findViewById(R.id.layout_float_window_xiaomi);
        btnMine = view.findViewById(R.id.layout_float_window_btn_mine);
        btnHot.setChecked(true);
        hotAudioListView.setAdapter(adapter);
        // 初始化
        initHot();

        learnMore.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConstantUtil.getAppContext(), LianXiangActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ConstantUtil.getAppContext().startActivity(intent);
            }
        });

        noPermission.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ConstantUtil.getAppContext().startActivity(intent);
                Toast.makeText(ConstantUtil.getAppContext(), "请开启花生语音包 - 无障碍权限", Toast.LENGTH_LONG).show();
            }
        });

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            Drawable lock = ContextCompat.getDrawable(getContext(), R.drawable.lock_std);
            lock.setBounds(0, 0, 42, 42);
            noPermission.setCompoundDrawables(lock, null, null, null);
        }


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                MusicPlayer.getInstance().stop();
                flush();
            }
        });
    }

    public void flushAndCheckedLianXiang() {
        if (LianXiangResultManager.get().isValid())
            btnLianXiang.setChecked(true);
        flush();
    }

    public void flush() {
        noShouCang.setVisibility(GONE);
        lianXiangMiss.setVisibility(GONE);
        int checkedId = radioGroup.getCheckedRadioButtonId();
        if (checkedId == btnHot.getId()) {
            initHot();
        } else if (checkedId == btnDaily.getId()) {
            connectID(btnDaily, Daily);
        } else if (checkedId == btnHi.getId()) {
            connectID(btnHi, HI);
        } else if (checkedId == btnAnge.getId()) {
            connectID(btnAnge, Anger);
        } else if (checkedId == btnCute.getId()) {
            connectID(btnCute, Cute);
        } else if (checkedId == btnSweet.getId()) {
            connectID(btnSweet, Sweet);
        } else if (checkedId == btnSad.getId()) {
            connectID(btnSad, Sad);
        } else if (checkedId == btnLianXiang.getId()) {
            if (!LianXiangResultManager.get().isValid()) {
                lianXiangMiss.setVisibility(VISIBLE);
                setData(new ArrayList<>());

                if (Permission.isAccessibilitySettingsOn(getContext(), LianXiangService.class.getCanonicalName())) {
                    noPermission.setVisibility(GONE);
                    learnMore.setVisibility(GONE);
                    noLianXiang.setVisibility(VISIBLE);
                    SharedPreferences preferences = getContext().getSharedPreferences("permission", Context.MODE_PRIVATE);
                    if (preferences.getBoolean("p", true)) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("p", false);
                        editor.apply();
                        Map<Object, Object> kv = new HashMap<>();
                        kv.put("权限", "无障碍权限");
                        TCAgent.onEvent(getContext(), "无障碍权限开启", "", kv);
                    }
                } else {
                    noPermission.setVisibility(VISIBLE);
                    learnMore.setVisibility(VISIBLE);
                    noLianXiang.setVisibility(GONE);

                    if (!Permission.canBackgroundStart(ConstantUtil.getAppContext())) {
                        xiaomi.setVisibility(VISIBLE);
                        noPermission.setVisibility(GONE);
                        learnMore.setVisibility(GONE);
                        noLianXiang.setVisibility(GONE);
                    }
                }
            } else {
                setData(LianXiangResultManager.get().getData());
            }
        } else if (checkedId == btnMine.getId()) {
            showMine();
        }
    }

    private void showMine() {
        ArrayList<AudioInfo> minfos = MineManager.getMineAudioFromFile();
        if (minfos == null || minfos.isEmpty()) {
            setData(new ArrayList<>());
            noShouCang.setVisibility(VISIBLE);
            return;
        }

        setData(minfos);
    }

    private void connectID(View view, int id) {
        view.post(new Runnable() {
            @Override
            public void run() {
                if (ConstantUtil.CategoryResult != null)
                    setData((ArrayList<AudioInfo>) ConstantUtil.CategoryResult.get(id - 1).get(String.valueOf(id)));
            }
        });
    }

    public void setData(ArrayList<AudioInfo> data) {
        this.post(new Runnable() {
            @Override
            public void run() {
                adapter.setData(data);
//                flush();
            }
        });
    }

    public void setDataAndFlush(ArrayList<AudioInfo> data) {
        this.post(new Runnable() {
            @Override
            public void run() {
                adapter.setData(data);
                flush();
            }
        });
    }

    private void initHead(View view) {
        TextView closeTextView = view.findViewById(R.id.layout_float_window_tv_close);
        closeTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ConstantUtil.getFloatManagerMusic().hideWindow();
                ConstantUtil.getFloatManagerMusic().showBall();
            }
        });
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            Drawable tv_close = ContextCompat.getDrawable(getContext(), R.drawable.heshang_std);
            tv_close.setBounds(0, 0, 36, 36);
            closeTextView.setCompoundDrawables(tv_close, null, null, null);
        }
    }

    private void initHot() {
        if (!dataHot.isEmpty()) {
            setData(dataHot);
            return;
        }

        Searcher.search_new(ConstantUtil.http_audio_hot, new HotAudioParser(),
                new Shower() {
                    @Override
                    public void init() {

                    }

                    @Override
                    public void info(String msg) {

                    }

                    @Override
                    public void error(String msg, Error e) {

                    }

                    @Override
                    public void Show(ArrayList<Map<String, Object>> result, boolean isOk) {
                        hotAudioListView.post(new Runnable() {
                            @Override
                            public void run() {
                                if (!result.isEmpty()) {
                                    dataHot.clear();
                                    Tools.hotAdd(result, dataHot);
                                    setData(dataHot);
                                } else {
                                    CLog.d("no audios list");
                                }
                            }
                        });
                    }
                }, 2);
    }
}
