package com.ime.music.net.shower;

import java.util.ArrayList;
import java.util.Map;

/*
搜索结果 显示 提示
此类可能被子线程中调用。所有界面显示操作请保证在主线程中运行
 */
public interface Shower {
    public enum Error {
        net,
        parse
    }
    //进行显示界面初始化
    public void init();

    //用于提示用户
    public void info(String msg);

    //错误处理
    public void error(String msg, Error e);

    //isOk表示result是否有效,
    public void Show(ArrayList<Map<String, Object>> result, boolean isOk);
}
