package com.codepath.apps.mysimpletweets.models;

import android.text.format.DateUtils;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class Tweet {
    /*

        {
            "coordinates": null,
            "truncated": false,
            "created_at": "Tue Aug 28 21:16:23 +0000 2012",
            "favorited": false,
            "id_str": "240558470661799936",
            "in_reply_to_user_id_str": null,
            "entities": {
              "urls": [

              ],
              "hashtags": [

              ],
              "user_mentions": [

              ]
            },
            "text": "just another test",
            "contributors": null,
            "id": 240558470661799936,
            "retweet_count": 0,
            "in_reply_to_status_id_str": null,
            "geo": null,
            "retweeted": false,
            "in_reply_to_user_id": null,
            "place": null,
            "source": "<a href="//realitytechnicians.com\"" rel="\"nofollow\"">OAuth Dancer Reborn</a>",
            "user": {
              "name": "OAuth Dancer",
              "profile_sidebar_fill_color": "DDEEF6",
              "profile_background_tile": true,
              "profile_sidebar_border_color": "C0DEED",
              "profile_image_url": "http://a0.twimg.com/profile_images/730275945/oauth-dancer_normal.jpg",
              "created_at": "Wed Mar 03 19:37:35 +0000 2010",
              "location": "San Francisco, CA",
              "follow_request_sent": false,
              "id_str": "119476949",
              "is_translator": false,
              "profile_link_color": "0084B4",
              "entities": {
                "url": {
                  "urls": [
                    {
                      "expanded_url": null,
                      "url": "http://bit.ly/oauth-dancer",
                      "indices": [
                        0,
                        26
                      ],
                      "display_url": null
                    }
                  ]
                },
                "description": null
              },
              "default_profile": false,
              "url": "http://bit.ly/oauth-dancer",
              "contributors_enabled": false,
              "favourites_count": 7,
              "utc_offset": null,
              "profile_image_url_https": "https://si0.twimg.com/profile_images/730275945/oauth-dancer_normal.jpg",
              "id": 119476949,
              "listed_count": 1,
              "profile_use_background_image": true,
              "profile_text_color": "333333",
              "followers_count": 28,
              "lang": "en",
              "protected": false,
              "geo_enabled": true,
              "notifications": false,
              "description": "",
              "profile_background_color": "C0DEED",
              "verified": false,
              "time_zone": null,
              "profile_background_image_url_https": "https://si0.twimg.com/profile_background_images/80151733/oauth-dance.png",
              "statuses_count": 166,
              "profile_background_image_url": "http://a0.twimg.com/profile_background_images/80151733/oauth-dance.png",
              "default_profile_image": false,
              "friends_count": 14,
              "following": false,
              "show_all_inline_media": false,
              "screen_name": "oauth_dancer"
            },
            "in_reply_to_screen_name": null,
            "in_reply_to_status_id": null
        }
     */
    // list the attributes
    public String created_at;
    public long uid;
    public String text;
    public User user;
    public String creationTime;
    public ArrayList<TwitterUrl> urls;
    public int reTweetCount;
    public int favouritesCount;

    public int getReTweetCount() {
        return reTweetCount;
    }

    public int getFavouritesCount() {
        return favouritesCount;
    }

    public String getCreated_at() {
        return created_at;
    }

    public long getUid() {
        return uid;
    }

    public String getText() {
        return text;
    }

    public User getUser() {
        return user;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public ArrayList<TwitterUrl> getUrls() {
        return urls;
    }

    // Deserialize the JsonObject into type tweet
    public static Tweet fromJson(JSONObject jsonObject) {
        Tweet tweet = new Tweet();
        try {
            tweet.created_at = jsonObject.getString("created_at");
            tweet.uid = jsonObject.getLong("id");
            tweet.text = jsonObject.getString("text");
            tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
            tweet.urls = TwitterUrl.urlsFromJsonArray(jsonObject.getJSONObject("entities").getJSONArray("urls"));
            tweet.creationTime = tweet.getRelativeTimeAgo(jsonObject.getString("created_at"));
            tweet.reTweetCount = jsonObject.getInt("retweet_count");
            tweet.favouritesCount = jsonObject.getInt("favorite_count");
            String urlEmbeddedtext = tweet.createText(tweet.getText(), tweet.getUrls());
            if(!urlEmbeddedtext.isEmpty()) {
                tweet.text = urlEmbeddedtext;
            }
            tweet_item tItem = new tweet_item();
            tItem.tweet = tweet.getText();
            tItem.userName = tweet.getUser().getName();
            tItem.screenName = tweet.getUser().getScreenName();
            tItem.creationTime = tweet.creationTime;
            tItem.save();
            return tweet;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static ArrayList<Tweet> fromJSONArray(JSONArray jsonArray) {
        ArrayList<Tweet> atweets = new ArrayList<Tweet>();
        for(int i = 0 ; i < jsonArray.length(); i++ ) {
            try {
                JSONObject tweetJson = jsonArray.getJSONObject(i);
                Tweet tweet = Tweet.fromJson(tweetJson);
                if(tweet != null) {
                    atweets.add(tweet);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }
        return atweets;
    }

    public static long getLargestTweetId(ArrayList<Tweet> tweetList) {
        long minId = 1;
        for(int i = 0; i < tweetList.size(); i++) {
            if(i == 0) {
                minId = tweetList.get(i).getUid();
            } else {
                if(tweetList.get(i).getUid() < minId) {
                    minId = tweetList.get(i).getUid();
                }
            }
        }

        return minId;
    }

    private String createText(String text, ArrayList<TwitterUrl> urls) {
        String tweetText = "";
        int prevStartIndex = 0;
        for(int i = 0; i < urls.size(); i++) {
            TwitterUrl tUrl = urls.get(i);
            tweetText += text.substring(prevStartIndex, tUrl.getStartIndex()) + "<a href=\"" + tUrl.getUrl() + "\">" + tUrl.getUrl() + "</a>";
            prevStartIndex = tUrl.getEndIndex();
        }
        return tweetText;
    }

    private String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            //long dateMillis = sf.parse(rawJsonDate).getTime();
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }
}
