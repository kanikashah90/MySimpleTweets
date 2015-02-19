package com.codepath.apps.mysimpletweets.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.codepath.apps.mysimpletweets.TwitterApp;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by kanikash on 2/15/15.
 */
public class MentionTimeLineFragment extends TweetListFragment {
    private TwitterClient client;
    private long maxIdVal;
    private boolean isPagination;

    @Override
    public void changeControlVariables(Boolean isPagination, Boolean setMaxIdVal) {
        this.isPagination = isPagination;
        if(setMaxIdVal) {
            this.maxIdVal = 0;
        }
        populateTimeline();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        maxIdVal = 0;
        isPagination = false;
        client = TwitterApp.getRestClient();  // singleton
        populateTimeline();
    }

    //Calls the Rest Api
    //Show the listView
    private void populateTimeline() {
        if(isNetworkAvailable()) {
            listener.showProgressBar();
            /*client.callLimit(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.d("INFO", response.toString());
                    //notifyRefreshState(false);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.i("INFO", "Mention Error is " + String.valueOf(statusCode) + " " + errorResponse.toString());
                    throwable.printStackTrace();
                }
            });*/
            client.getMentionTimeline(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    // DESERIALIZE JSON
                    // CREATE MODELS
                    // LOAD MODELS DATA INTO LIST VIEW
                    Log.d("INFO", String.valueOf(response.toString().length()));
                    ArrayList<Tweet> tweets = Tweet.fromJSONArray(response);
                    maxIdVal = Tweet.getLargestTweetId(tweets) - 1;
                    if (!isPagination) {
                        if (!getTweetsAdapter().isEmpty()) {
                            getTweetsAdapter().clear();
                        }
                    }
                    getTweetsAdapter().addAll(tweets);
                    getTweetsAdapter().notifyDataSetChanged();
                    notifyRefreshState(false);
                    listener.hideProgressBar();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.i("INFO", "Mention Error is 2 " + String.valueOf(statusCode) + " " + errorResponse.toString());
                    listener.hideProgressBar();
                    throwable.printStackTrace();
                }
            }, isPagination, maxIdVal);
        } else {
            Toast.makeText(getActivity().getBaseContext(), "Check Internet Connection", Toast.LENGTH_LONG).show();
            Log.d("INFO", "check internet connection");
        }
    }
}
