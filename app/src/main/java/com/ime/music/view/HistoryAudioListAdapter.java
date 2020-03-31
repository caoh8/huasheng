package com.ime.music.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;

import com.ime.music.CLog;
import com.ime.music.R;
import com.ime.music.info.AudioHistoryInfo;
import com.ime.music.info.AudioInfo;
import com.ime.music.info.PlayInfo;
import com.ime.music.net.parse.AudioSourceParser;
import com.ime.music.net.post.Report;
import com.ime.music.net.search.Searcher;
import com.ime.music.net.shower.Shower;
import com.ime.music.play.MusicPlayer;
import com.ime.music.play.PlayeItemHolder;
import com.ime.music.share.Share;
import com.ime.music.util.GeneratorUrl;
import com.ime.music.util.HistoryManager;
import com.tendcloud.tenddata.TCAgent;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.Inflater;

import static com.ime.music.net.search.Searcher.search_new;

public class HistoryAudioListAdapter extends PlayedAdapter {

    private ArrayList<AudioHistoryInfo> data = new ArrayList<>();

    public HistoryAudioListAdapter(Context context) {
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
        View view = LayoutInflater.from(context).inflate(R.layout.audio_item,null);
        holder.display_name = view.findViewById(R.id.file_name);
        holder.share_btn = view.findViewById(R.id.share_btn);
        Drawable share = ContextCompat.getDrawable(context, R.drawable.share_std);
        share.setBounds(0,0,80,60);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M)
            holder.share_btn.setCompoundDrawables(share,null,null,null);
        return view;
    }

    @Override
    void init() {
        this.sourceParser = new AudioSourceParser();
    }

    @Override
    void share(PlayInfo info, Share.ShareInfo sinfo) {
        if (null != sinfo) {
            sinfo.setFileName(((AudioHistoryInfo) info).getAudioName());
            sinfo.setAuthorName(((AudioHistoryInfo) info).getSource());
            sinfo.setTitle_url(((AudioHistoryInfo) info).getUrl());
            sinfo.setDuration(((AudioHistoryInfo) info).getDuration());
        }
        HistoryManager.addAudioHistory((AudioHistoryInfo) info, context);
//        Share.showShare(context, null, sinfo);
        Map kv = new HashMap();
        TCAgent.onEvent(context, "历史音频-分享", ((AudioHistoryInfo) info).getAudioName(), kv);
    }

    @Override
    void play(PlayInfo info) {
        Map kv = new HashMap();
        TCAgent.onEvent(context, "历史音频-播放", ((AudioHistoryInfo) info).getAudioName(), kv);
    }

}
