package com.ime.music.net.parse;

import android.util.Log;

import com.ime.music.CLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AudioSourceParser implements Parser {
    @Override
    public boolean Parse(String response, ArrayList<Map<String, Object>> result) {

        try{
            CLog.d(response);
            JSONObject jsonObject = new JSONObject(response);

            JSONObject joData = jsonObject.getJSONObject("data");
            String url = joData.getString("address");//如果没有会发生异常
            CLog.d(url);
            Map<String, Object> map = new HashMap<>();
            map.put("AudioUrl", url);
            map.put("PlayUrl", url);
            result.add(map);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("clog_d fail", ": song info failed");
            return false;
        }
        return true;
    }
}
