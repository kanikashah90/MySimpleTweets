package com.codepath.apps.mysimpletweets.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by kanikash on 2/8/15.
 */
public class TwitterUrl {
    private String url;
    private int startIndex;
    private int endIndex;

    public String getUrl() {
        return url;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public static ArrayList<TwitterUrl> urlsFromJsonArray(JSONArray jsonArray) {
        ArrayList<TwitterUrl> urls = new ArrayList<TwitterUrl>();
        for(int i = 0 ; i < jsonArray.length(); i++) {
            JSONObject jsonUrl;
            try {
                TwitterUrl turl = new TwitterUrl();
                jsonUrl = jsonArray.getJSONObject(i);
                turl.url = jsonUrl.getString("url");
                turl.startIndex = (Integer)jsonUrl.getJSONArray("indices").get(0);
                turl.endIndex = (Integer)jsonUrl.getJSONArray("indices").get(1);
                urls.add(turl);
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }

        }
        return urls;
    }

}
