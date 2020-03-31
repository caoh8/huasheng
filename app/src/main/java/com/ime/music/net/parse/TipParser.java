package com.ime.music.net.parse;

import android.util.Log;
import android.widget.TextView;

import com.ime.music.CLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class TipParser implements Parser {
    private TextView mView;

    public TipParser(TextView view) {
        mView = view;
    }
    @Override
    public boolean Parse(String response, ArrayList<Map<String, Object>> result) {
        try {
            CLog.d(response);
            JSONObject jsonObject = new JSONObject(response);
            int errorCode = jsonObject.getInt("ErrorCode");

            if(0 == errorCode) {
                JSONObject joData = jsonObject.getJSONObject("data");
                return  show2View(joData.getJSONArray("search"), "hint") ||
                        show2View(joData.getJSONArray("song"), "filename") ||
                        show2View(joData.getJSONArray("singer"), "singername");
            } else {
                return false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("clog_d fail", ": song info failed");
            return false;
        }
    }

    private boolean show2View(JSONArray joArray, String tag) throws JSONException {
        if (0 != joArray.length()) {
            JSONObject data = joArray.getJSONObject(0);
            final String rec = data.getString(tag);
            mView.post(new Runnable() {
                @Override
                public void run() {
                    mView.setText(rec);
                }
            });
            return true;
        }
        return false;
    }
}
