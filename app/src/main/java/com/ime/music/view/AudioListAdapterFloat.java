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

import com.ime.music.CLog;
import com.ime.music.R;
import com.ime.music.info.AudioInfo;
import com.ime.music.info.PlayInfo;
import com.ime.music.net.parse.AudioSourceParser;
import com.ime.music.net.post.Report;
import com.ime.music.play.MusicPlayer;
import com.ime.music.play.PlayeItemHolder;
import com.ime.music.share.Share;
import com.ime.music.util.ConstantUtil;
import com.ime.music.util.GeneratorUrl;
import com.ime.music.util.HistoryManager;
import com.ime.music.util.MineManager;
import com.tendcloud.tenddata.TCAgent;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class AudioListAdapterFloat extends PlayedAdapter {

    private class AudioViewHolder extends PlayeItemHolder {
//        private TextView time_tv;
        private TextView delay;
        private ImageView shoucang;
    }

    @Override
    protected void playItemClicked(PlayeItemHolder holder) {
        super.playItemClicked(holder);
        MusicPlayer.getInstance().setHoldShower(((AudioViewHolder) holder).delay);
        MusicPlayer.getInstance().setHold(true);
    }

    public void setData(ArrayList<AudioInfo> data) {
        if (data == null) {
            this.data = new ArrayList<>();
            return;
        }
        if (data != this.data) {
            this.data = data;
        }
        notifyDataSetChanged();
    }

    private ArrayList<AudioInfo> data = new ArrayList<>();

    public AudioListAdapterFloat(Context context) {
        super(context);
    }

    @Override
    protected void display(PlayeItemHolder holder, PlayInfo info) {
        super.display(holder, info);
//        ((AudioViewHolder) holder).time_tv.setText(((AudioInfo) info).getDuration() + "\"");
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    PlayInfo getDataIndex(int i) {
        return data.get(i);
    }

    private Drawable shoucangDrawable = context.getDrawable(R.drawable.shoucang);
    private Drawable shoucangDrawable2 = context.getDrawable(R.drawable.shoucang2);
    @Override
    View getPlayedItem(PlayeItemHolder holder, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.audio_item_float2, null);
        holder.display_name = view.findViewById(R.id.audio_item_float2_tv_file_name);
//        ((AudioViewHolder) holder).time_tv = view.findViewById(R.id.audio_item_tv_time);
        ((AudioViewHolder) holder).delay = view.findViewById(R.id.audio_item_float2_tv_delay);
        ((AudioViewHolder) holder).shoucang = view.findViewById(R.id.audio_item_float2_iv_shoucang);
        ((AudioViewHolder) holder).delay.setVisibility(View.GONE);

        ImageView s = ((AudioViewHolder) holder).shoucang;
        AudioInfo info = (AudioInfo) getDataIndex(position);

        initShouCang(info, s);
        return view;
    }

    @Override
    protected void reused(PlayeItemHolder holder, int position) {
        super.reused(holder, position);
        initShouCang((AudioInfo) getDataIndex(position), ((AudioViewHolder) holder).shoucang);
    }

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

    }

    public ArrayList<AudioInfo> getSongInfoData() {
        return data;
    }

}
