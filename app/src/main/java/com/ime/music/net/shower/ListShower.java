package com.ime.music.net.shower;

import android.widget.ListView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;

public abstract class ListShower implements Shower {
    protected ListView listView;
    protected boolean isClear = true;
    public abstract void clear();

    public ListShower(@NotNull ListView listView, boolean isClear) {
        this.listView = listView;
        this.isClear = isClear;
    }

    @Override
    public void init() {

    }

    @Override
    public void info(String msg) {
//        Notice.toastShort(listView.getContext(), msg);
    }

    @Override
    public abstract void Show(ArrayList<Map<String, Object>> result, boolean isOk);
}
