package com.ime.music.service;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;

import com.ime.music.CLog;
import com.ime.music.R;
import com.ime.music.net.parse.AudiosParser;
import com.ime.music.net.search.Searcher;
import com.ime.music.util.ConstantUtil;
import com.ime.music.util.Tools;

public class LianXiangService extends AccessibilityService {

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
//        CLog.e("启动啦");
    }

    /**
     * Callback for {@link AccessibilityEvent}s.
     *
     * @param event The new event. This event is owned by the caller and cannot be used after
     *              this method returns. Services wishing to use the event after this method returns should
     *              make a copy.
     */
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int type = event.getEventType();
        switch (type) {
            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
                String keyWord = event.getText().toString();
                keyWord = Tools.ModifyStr(keyWord);
                if (null != keyWord && !keyWord.isEmpty()) {
                    Searcher.search(ConstantUtil.http_audio_filter + "keyword=" + keyWord,
                            new AudiosParser(), LianXiangResultManager.get());
                } else {
                    LianXiangResultManager.get().failed();
                }
                break;
        }
    }

    /**
     * Callback for interrupting the accessibility feedback.
     */
    @Override
    public void onInterrupt() {
        CLog.e("accessibility: onInterrupt");
    }
}
