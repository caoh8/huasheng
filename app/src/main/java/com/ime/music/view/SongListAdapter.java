package com.ime.music.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.ime.music.R;
import com.ime.music.info.PlayInfo;
import com.ime.music.info.SongInfo;
import com.ime.music.net.parse.SongSourceParser;
import com.ime.music.play.PlayeItemHolder;
import com.ime.music.share.Share;
import com.ime.music.util.GeneratorUrl;
import com.tendcloud.tenddata.TCAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//import static com.ime.music.util.Notice.toastShort;

public class SongListAdapter extends PlayedAdapter {

    private ArrayList<SongInfo> data = new ArrayList<>();

    public SongListAdapter(Context context) {
        super(context);
    }

    @Override
    View getPlayedItem(PlayeItemHolder holder, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.song_item_full, null);
        /*得到各个控件的对象*/
        holder.display_name = view.findViewById(R.id.display_name);
//        holder.segment = view.findViewById(R.id.segment);
//            holder.play_btn = view.findViewById(R.id.play_btn);
        holder.share_btn = view.findViewById(R.id.share_btn);
        Drawable share = ContextCompat.getDrawable(context, R.drawable.share_std);
        share.setBounds(0,0,80,60);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M)
            holder.share_btn.setCompoundDrawables(share,null,null,null);
        return view;
    }

    @Override
    void init() {
//        data = new ArrayList<>();
        this.sourceParser = new SongSourceParser();
    }

    @Override
    void play(PlayInfo info) {
        Map kv = new HashMap();
        kv.put("歌手", ((SongInfo) info).getSingerName());
        kv.put("歌曲名", ((SongInfo) info).getSongName());
        TCAgent.onEvent(context, "音乐搜索-播放", ((SongInfo) info).getSongName() + " - " +
                ((SongInfo) info).getSingerName(), kv);
    }

    @Override
    void share(PlayInfo info, Share.ShareInfo sinfo) {
        sinfo.setAuthorName(((SongInfo)info).getSingerName());
        sinfo.setFileName(((SongInfo)info).getSongName());
//        Toast.makeText(context, "分享", Toast.LENGTH_SHORT).show();
        Map kv = new HashMap();
        kv.put("分享", "音乐搜索");
        TCAgent.onEvent(context, "音乐搜索-分享", ((SongInfo) info).getFileName(), kv);

        if (info.isPlayed()) {
            Map kv1 = new HashMap();
            kv1.put("分享", "音乐搜索-播放-分享");
            TCAgent.onEvent(context, "音乐搜索-播放-分享", ((SongInfo) info).getFileName(), kv1);
        }
    }

    @Override
    PlayInfo getDataIndex(int i) {
        return data.get(i);
    }

    @Override
    String getPlayURL(PlayInfo info) {
        return GeneratorUrl.SongSource(context, (SongInfo) info, 1);
    }

    @Override
    String getShareURL(PlayInfo info) {
        return GeneratorUrl.SongSource(context, (SongInfo) info, 2);
    }

    @Override
    void post2Service(int behavior, PlayInfo info) {

    }

    public ArrayList<SongInfo> getSongInfoData() {
        return data;
    }

    @Override
    public int getCount() {
        return data.size();
    }
}
