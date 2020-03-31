package com.ime.music.net.post;

import com.ime.music.CLog;
import com.ime.music.net.parse.CategoryParser;
import com.ime.music.util.ConstantUtil;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Report {
    static public void behavior(int behavior, String audioID, String userID, String MID) {
        OkHttpClient client = new OkHttpClient.Builder()
                .build();

//        FormBody formBody = new FormBody.Builder()
//                .add("behavior", String.valueOf(behavior))
//                .add("audio_id", audioID)
//                .build();

        MediaType mediaType = MediaType.parse("application/json");
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("behavior", behavior);

            jsonObject.put("audio_id", new BigInteger(audioID));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(mediaType, jsonObject.toString());

        Request request = new Request.Builder()
                .post(requestBody)
                .url(ConstantUtil.pot_audio_behavior)
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                CLog.d("post fail in behavior");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                CLog.d("post success in behavior");
                CLog.e(call.toString());
                CLog.e(response.toString());

            }
        });
    }

    static public void report(int source, String content, String MID) {
        OkHttpClient client = new OkHttpClient.Builder()
                .build();

        MediaType mediaType = MediaType.parse("application/json");
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("source", source);

            jsonObject.put("content", content);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(mediaType, jsonObject.toString());

        Request request = new Request.Builder()
                .post(requestBody)
                .url(ConstantUtil.post_report)
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                CLog.d("post fail in behavior");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                CLog.d("post success in behavior");
                CLog.e(call.toString());
                CLog.e(response.toString());

            }
        });
    }

    static public void category() {
        if (ConstantUtil.CategoryResult != null) return;
        OkHttpClient client = new OkHttpClient.Builder()
                .build();

        MediaType mediaType = MediaType.parse("application/json");

        RequestBody requestBody = RequestBody.create(mediaType,
                "{\"category\":    [\"1\", \"2\", \"3\",\"4\", \"5\",\"6\"]}");

        Request request = new Request.Builder()
                .post(requestBody)
                .url(ConstantUtil.post_category)
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                CLog.d("post fail in category");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                CLog.d("post success in category");
                CLog.e(call.toString());
                String response1 = response.body().string();
                CategoryParser parser = new CategoryParser();
                ArrayList<Map<String, Object>> result = new ArrayList<>();
                if (parser.Parse(response1, result)) {
                    ConstantUtil.CategoryResult = result;
                }
            }
        });
    }

    private static Map<String, String> categoryResult;
}
