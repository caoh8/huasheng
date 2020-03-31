package com.ime.music.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ime.music.CLog;
import com.ime.music.R;
import com.ime.music.info.AudioInfo;
import com.ime.music.info.PlayInfo;
import com.ime.music.net.parse.AudioSourceParser;
import com.ime.music.net.search.Searcher;
import com.ime.music.net.shower.Shower;
import com.ime.music.play.MusicPlayer;
import com.ime.music.play.PlayeItemHolder;
import com.ime.music.share.ButtonShare;
import com.ime.music.share.Share;
import com.ime.music.util.GeneratorUrl;
import com.ime.music.util.HistoryManager;
import com.tendcloud.tenddata.TCAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.ime.music.net.search.Searcher.search_new;
//import static com.ime.music.util.Notice.toastShort;

public class HotAudioListAdapter extends AudioListAdapter {
    public HotAudioListAdapter(Context context) {
        super(context);
    }
}
