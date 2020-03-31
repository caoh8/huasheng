package com.ime.music.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.ime.music.CLog;
import com.ime.music.R;
import com.ime.music.info.TipInfo2;
import com.ime.music.net.parse.AudioSourceParser;
import com.ime.music.net.search.Searcher;
import com.ime.music.net.shower.Shower;
import com.ime.music.play.MusicPlayer;
import com.ime.music.share.Share;
import com.ime.music.util.GeneratorUrl;
import com.ime.music.util.HistoryManager;
import com.tendcloud.tenddata.TCAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.ime.music.net.search.Searcher.search_new;
//import static com.ime.music.util.Notice.toastShort;

public class TipItem extends RadioButton {

    private TipInfo2 info = null;
    private Button share;
    public TipItem(Context context) {
        super(context);
        init(context);
    }

    public void setShare(Button v) {
        share = v;

        Drawable d_share = ContextCompat.getDrawable(v.getContext(), R.drawable.share_std);
        d_share.setBounds(-5,0,63,51);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            ((Button)share).setCompoundDrawables(null,null,d_share,null);
        }


        v.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null == info) return ;
                search_new(GeneratorUrl.AudioSource(info, 2), new AudioSourceParser(), new Shower() {
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
                        if(isOk) {
                            String url = (String) result.get(0).get("AudioUrl");
                            //记录历史使用
                            info.setUrl(url);
                            HistoryManager.addAudioHistory(info, getContext());
                            //分享
                            Share.ShareInfo sinfo = new Share.ShareInfo();
                            sinfo.setMusic_url(url);
                            sinfo.setFileName(info.getAudioName());
                            sinfo.setAuthorName(info.getSource());
                            sinfo.setDuration(info.getDuration());
                            sinfo.setTitle_url(url);
//                            Share.showShare(view.getContext(), QQ.NAME, sinfo);
                            Share.showShare(getContext(), null, sinfo);
                            Map kv = new HashMap();
                            kv.put("分享", "音频联想bar");
                            TCAgent.onEvent(getContext(), "音频联想bar-分享", info.getAudioName(), kv);
                        }
                    }
                });
            }
        });
    }

    public TipItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void hideSelf() {
        setVisibility(GONE);

        if (null != share) share.setVisibility(GONE);
    }

    private void showSelf() {
        setVisibility(VISIBLE);

        if (null != share) share.setVisibility(VISIBLE);
    }

    public void changeShareButton() {
        if (this.isChecked()) {
            share.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.btn_blue50));
            Drawable w_share = ContextCompat.getDrawable(getContext(), R.drawable.share_white_std);
            w_share.setBounds(-10,-15,58,36);
            ((Button)share).setCompoundDrawables(w_share,null,null,null);
        } else {
            share.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.btn_white));
            Drawable w_share = ContextCompat.getDrawable(getContext(), R.drawable.share_std);
            w_share.setBounds(-40,-15,28,36);
            ((Button)share).setCompoundDrawables(w_share,null,null,null);
        }
    }

    private void init(Context context) {
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                CLog.d("---------- Tip Item Clicked --------");
                TipItem.this.setChecked(true);
                MusicPlayer.getInstance().clearUI();
                if(null == info) return ;
                if(info.getUrl().equals("")) {
//                    toastShort(view.getContext(), "正在获取播放资源");
                    Searcher.search_new(GeneratorUrl.AudioSource(info, 1),
                            new AudioSourceParser(), new Shower() {
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
                                    if(isOk){
//                                        toastShort(view.getContext(), "准备播放");
                                        String audioUrl = (String) result.get(0).get("AudioUrl");
                                        MusicPlayer.getInstance().play(audioUrl);
                                        info.setUrl(audioUrl);
                                        Map kv = new HashMap();
                                        kv.put("播放", "音频联想bar");
                                        TCAgent.onEvent(context, "音频联想bar-播放", info.getAudioName(), kv);
                                    } else {
//                                        toastShort(view.getContext(),"没有资源");
                                    }
                                }
                            });
                } else {
                    String audioUrl = info.getUrl();
                    MusicPlayer.getInstance().play(audioUrl);
                }

            }
        });

        this.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (null == info) return true;

                return true;
            }
        });
    }

    public void setInfo(TipInfo2 info) {
        this.info = info;
        String s = info.getAudioName();
        int t = info.getDuration();
        if (s.equals("")) hideSelf();
        else if (getVisibility() != VISIBLE) showSelf();
        int start = s.length();
        s += "  " + t + "\"";
        int end = s.length();
        Spannable string = new SpannableString(s);
        string.setSpan(new ForegroundColorSpan(Color.parseColor("#999999")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        setText(string);
    }

    public void reset() {
        this.info = null;
        setText("");
        hideSelf();
    }
}
