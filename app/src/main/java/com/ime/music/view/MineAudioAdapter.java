package com.ime.music.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.ime.music.R;
import com.ime.music.info.AudioHistoryInfo;
import com.ime.music.info.PlayInfo;
import com.ime.music.net.parse.AudioSourceParser;
import com.ime.music.net.post.Report;
import com.ime.music.play.PlayeItemHolder;
import com.ime.music.share.Share;
import com.ime.music.util.GeneratorUrl;
import com.ime.music.util.HistoryManager;
import com.tendcloud.tenddata.TCAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MineAudioAdapter extends PlayedAdapter {

    private ArrayList<AudioHistoryInfo> data = new ArrayList<>();

    public MineAudioAdapter(Context context) {
        super(context);
    }

    public void freshData() {
        ArrayList<AudioHistoryInfo> data = HistoryManager.getAudioHistoryFromFile(
                context.getString(R.string.audio_history_file), context);

        playingItem = -1;
        if (data == null) {
            this.data.clear();
            return;
        }
        this.data = data;
    }

    @Override
    String getPlayURL(PlayInfo info) {
        return GeneratorUrl.AudioSource((AudioHistoryInfo) info, 1);
    }

    @Override
    String getShareURL(PlayInfo info) {
        return GeneratorUrl.AudioSource((AudioHistoryInfo) info, 2);
    }

    @Override
    void post2Service(int behavior, PlayInfo info) {
        Report.behavior(behavior, ((AudioHistoryInfo) info).getId(), null, null);
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
    View getPlayedItem(PlayeItemHolder holder, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.audio_item_float2, null);
        holder.display_name = view.findViewById(R.id.audio_item_float2_tv_file_name);
//        holder.share_btn = view.findViewById(R.id.share_btn);
//        Drawable share = ContextCompat.getDrawable(context, R.drawable.shoucang2);
//        share.setBounds(0,0,80,60);
//        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M)
//            holder.share_btn.setCompoundDrawables(share,null,null,null);
        return view;
    }

    @Override
    void init() {
        this.sourceParser = new AudioSourceParser();
    }

    @Override
    void share(PlayInfo info, Share.ShareInfo sinfo) {
    }

    @Override
    void play(PlayInfo info) {
        Map<Object, Object> kv = new HashMap<>();
        kv.put("播放", "我的");
        TCAgent.onEvent(context, "我的音频-播放", ((AudioHistoryInfo) info).getAudioName(), kv);
    }

}
