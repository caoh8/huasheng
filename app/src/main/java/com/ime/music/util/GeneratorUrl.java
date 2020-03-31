package com.ime.music.util;

import android.content.Context;
import com.ime.music.info.AudioInfo;
import com.ime.music.info.SongInfo;
import com.ime.music.info.TipInfo2;

public class GeneratorUrl {
    static public String SongSource(Context context, SongInfo info, int behavior) {
        String ret = "";
        ret += ConstantUtil.http_song_source;
        ret += "extname=" + info.getExtname();
        ret += "&";
        ret += "hash=" + info.getFileHash();
        ret += "&";
        ret += "album_audio_id=" + info.getAlbumAudioID();
        ret += "&";
        ret += "clienttime=" + DeviceInfo.getClienttIME();
        ret += "&";
        ret += "mid=" + DeviceInfo.getMID();
        ret += "&";
        ret += "clientver=" + DeviceInfo.getClientver();
        ret += "&";
        ret += "feetype=" + info.getFeetype();
        ret += "&";
        ret += "behavior=" + behavior;
        return ret;
    }


    static public String AudioSource(AudioInfo info, int behavior) {
        String ret = "";
        ret += ConstantUtil.http_audio_source;
        ret += "id=" + info.getId();
        ret += "&";
        ret += "hash=" + info.getFileHash();
        ret += "&";
        ret += "behavior=" + behavior;
        return ret;
    }

    static public String AudioSource(TipInfo2 info, int behavior) {
        String ret = "";
        ret += ConstantUtil.http_audio_source;
        ret += "id=" + info.getId();
        ret += "&";
        ret += "hash=" + info.getFileHash();
        ret += "&";
        ret += "behavior=" + behavior;
        return ret;
    }
}
