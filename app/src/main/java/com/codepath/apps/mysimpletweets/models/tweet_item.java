package com.codepath.apps.mysimpletweets.models;

import android.database.Cursor;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.squareup.picasso.Cache;

import java.net.CacheRequest;
import java.util.List;

@Table(name="Tweets")
public class tweet_item extends Model {
    @Column(name =  "tweet")
    public String tweet;
    @Column(name = "userName")
    public String userName;
    @Column(name = "screenName")
    public String screenName;
    @Column(name = "creationTime")
    public String creationTime;

    public tweet_item(){
        super();
    }

    public tweet_item(String tweet, String userName, String screenName, String creationTime) {
        super();
        this.tweet = tweet;
        this.userName = userName;
        this.screenName = screenName;
        this.creationTime = creationTime;
    }

    public static List<tweet_item> getAll() {
        return new Select()
                .all()
                .from(tweet_item.class)
                .execute();
    }

}
