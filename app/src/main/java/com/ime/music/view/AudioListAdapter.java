package com.ime.music.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.ime.music.R;
import com.ime.music.info.AudioInfo;
import com.ime.music.info.PlayInfo;
import com.ime.music.net.parse.AudioSourceParser;
import com.ime.music.net.post.Report;
import com.ime.music.play.PlayeItemHolder;
import com.ime.music.share.Share;
import com.ime.music.util.GeneratorUrl;
import com.ime.music.util.HistoryManager;
import com.ime.music.util.MineManager;
import com.tendcloud.tenddata.TCAgent;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AudioListAdapter extends PlayedAdapter {

    private class AudioViewHolder extends PlayeItemHolder {
//        private TextView time_tv;
        private TextView number;
        private ImageView shoucang;
    }

    private ArrayList<AudioInfo> data = new ArrayList<>();

    public AudioListAdapter(Context context) {
        super(context);
    }

    @Override
    protected void display(PlayeItemHolder holder, PlayInfo info) {
        super.display(holder, info);
//        ((AudioViewHolder) holder).time_tv.setText(((AudioInfo) info).getDuration() + "\"");
        ((AudioViewHolder) holder).number.setText(holder.num + 1 + " ");
        if (holder.num == 0) {
            ((AudioViewHolder) holder).number.setTextColor(Color.parseColor("#0938F7"));
        } else if (holder.num == 1) {
            ((AudioViewHolder) holder).number.setTextColor(Color.parseColor("#0938F7"));
        } else if (holder.num == 2) {
            ((AudioViewHolder) holder).number.setTextColor(Color.parseColor("#0938F7"));
        } else {
            ((AudioViewHolder) holder).number.setTextColor(Color.parseColor("#333333"));
        }

    }


    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    PlayInfo getDataIndex(int i) {
        return data.get(i);
    }


    @Override
    protected void reused(PlayeItemHolder holder, int position) {
        super.reused(holder, position);
        initShouCang((AudioInfo) getDataIndex(position), ((AudioListAdapter.AudioViewHolder) holder).shoucang);
    }

    @Override
    View getPlayedItem(PlayeItemHolder holder, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.audio_item2, null);
        holder.display_name = view.findViewById(R.id.file_name);
        holder.share_btn = view.findViewById(R.id.share_btn);
        Drawable share = ContextCompat.getDrawable(context, R.drawable.share_std);
        share.setBounds(0, 0, 48, 32);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M)
            holder.share_btn.setCompoundDrawables(share, null, null, null);
//        ((AudioViewHolder) holder).time_tv = view.findViewById(R.id.audio_item_tv);
        ((AudioViewHolder) holder).number = view.findViewById(R.id.number);
        holder.share_btn.setCompoundDrawables(share, null, null, null);

        ((AudioListAdapter.AudioViewHolder) holder).shoucang = view.findViewById(R.id.audio_item2_btn_mine);
        ImageView s = ((AudioListAdapter.AudioViewHolder) holder).shoucang;
        AudioInfo info = (AudioInfo) getDataIndex(position);
        initShouCang(info, s);
        return view;
    }

    private Drawable shoucangDrawable = context.getDrawable(R.drawable.shoucang);
    private Drawable shoucangDrawable2 = context.getDrawable(R.drawable.shoucang2);

    private void initShouCang(AudioInfo info, ImageView s) {
        if (MineManager.isMine(info)) {
            s.setBackground(shoucangDrawable2);
        } else {
            s.setBackground(shoucangDrawable);
        }
        s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (info.getMine()) {
                    s.setBackground(shoucangDrawable);
                    MineManager.removeMineAudio(info);
                } else {
                    s.setBackground(shoucangDrawable2);
                    MineManager.addMineAudio(info);
                }
            }
        });
    }

    @Override
    void init() {
        this.sourceParser = new AudioSourceParser();
    }

    @Override
    String getPlayURL(PlayInfo info) {
        return GeneratorUrl.AudioSource((AudioInfo) info, 1);
    }

    @Override
    String getShareURL(PlayInfo info) {
        return GeneratorUrl.AudioSource((AudioInfo) info, 2);
    }

    @Override
    void post2Service(int behavior, PlayInfo info) {
        Report.behavior(behavior, ((AudioInfo) info).getId(), null, null);
    }

    @Override
    protected PlayeItemHolder getHolder() {
        return new AudioViewHolder();
    }

    @Override
    void play(PlayInfo info) {
        Report.behavior(1, ((AudioInfo) info).getId(), null, null);
        Map kv = new HashMap();
        kv.put("播放", "音频联想bar-更多");
        TCAgent.onEvent(context, "音频联想bar-更多-播放", ((AudioInfo) info).getAudioName(), kv);
    }

    @Override
    void share(PlayInfo info, @NotNull Share.ShareInfo sinfo) {
        sinfo.setFileName(((AudioInfo) info).getAudioName());
        sinfo.setTitle_url(info.getPlayUrl());
        sinfo.setAuthorName(((AudioInfo) info).getSource());
        sinfo.setDuration(((AudioInfo) info).getDuration());

        HistoryManager.addAudioHistory((AudioInfo) info, context);
        Map kv = new HashMap();
        kv.put("分享", "音频联想bar-更多");
        TCAgent.onEvent(context, "音频联想bar-更多-分享", ((AudioInfo) info).getAudioName(), kv);
    }

    public ArrayList<AudioInfo> getSongInfoData() {
        return data;
    }

}
