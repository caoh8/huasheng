package com.ime.music.net.parse;

import android.util.Log;

import com.ime.music.CLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TipParser3 implements Parser {

    @Override
    public boolean Parse(String response, ArrayList<Map<String, Object>> result) {
        try {
            CLog.d(response);
            JSONObject jsonObject = new JSONObject(response);
            int status = jsonObject.getInt("status");

            if(200 == status) {
                JSONArray joData = jsonObject.getJSONArray("data");
                for (int i=0; i<joData.length(); ++i) {
                    JSONObject data = joData.getJSONObject(i);
                    String tip = data.getString("keyword");
                    int source = data.getInt("source");
                    Map<String, Object> map = new HashMap<>();
                    map.put("TipName", tip);
                    map.put("Source", source);
                    result.add(map);
                }
                return true;
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
