package com.ime.music.play;

import android.widget.Button;
import android.widget.TextView;

import com.ime.music.info.PlayInfo;
import com.ime.music.share.ButtonShare;
import com.ime.music.view.PlayItem;

public class PlayeItemHolder {
    public PlayItem display_name;
    public TextView segment;
    public Button play_btn;
    public Button share_btn;
    public int num;//标识为第几行的

    public void display(PlayInfo info) {
        display_name.setText(info.getName());
        display_name.setSelected(true);
    }
}
