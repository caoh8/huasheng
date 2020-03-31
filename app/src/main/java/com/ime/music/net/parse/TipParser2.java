package com.ime.music.net.parse;

import android.util.Log;

import com.ime.music.CLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TipParser2 implements Parser {

    @Override
    public boolean Parse(String response, ArrayList<Map<String, Object>> result) {
        try {
            CLog.d(response);
            JSONObject jsonObject = new JSONObject(response);
            int errorCode = jsonObject.getInt("ErrorCode");

            if(0 == errorCode) {
                JSONObject joData = jsonObject.getJSONObject("data");
                return tip(result, joData.getJSONArray("search"), "hint") &&
                        tip(result, joData.getJSONArray("song"), "filename") &&
                        tip(result, joData.getJSONArray("singer"), "singername");
            } else {
                return false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("clog_d fail", ": Tip info failed");
            return false;
        }
    }

    private boolean tip(ArrayList<Map<String, Object>> result, JSONArray joArray, String tag) {
        for (int i=0; i<joArray.length(); ++i) {
            try {
                JSONObject data = joArray.getJSONObject(i);
                String tip = data.getString(tag);
                Map<String, Object> map = new HashMap<>();
                map.put("TipName", tip);
                result.add(map);
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
}
