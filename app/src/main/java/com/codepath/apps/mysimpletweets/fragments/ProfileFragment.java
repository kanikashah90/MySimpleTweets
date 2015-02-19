package com.codepath.apps.mysimpletweets.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApp;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by kanikash on 2/16/15.
 */
public class ProfileFragment extends  TweetListFragment {
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

    public static ProfileFragment newInstance(String screenName, String tag, String imageUrl, int followers, int following) {
        ProfileFragment fragmentDemo = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString("ScreenName", screenName);
        /*args.putString("Tag", tag);
        args.putString("ImageURL", imageUrl);
        args.putInt("Followers", followers);
        args.putInt("Following", following);*/
        fragmentDemo.setArguments(args);
        return fragmentDemo;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        maxIdVal = 0;
        isPagination = false;
        client = TwitterApp.getRestClient();  // singleton
        populateTimeline();
        populateUserDetails();
    }

    private void populateUserDetails() {

    }

    //Calls the Rest Api
    //Show the listView
    private void populateTimeline() {
        if(isNetworkAvailable()) {
            listener.showProgressBar();
            /*client.callLimit(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.i("INFO", "kdjshkd" + response.toString());
                    //notifyRefreshState(false);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.i("INFO", "Mention Error is " + String.valueOf(statusCode) + " " + errorResponse.toString());
                }
            });*/

            String screenName = getArguments().getString("ScreenName");
            client.getUserTimeline(screenName, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    // DESERIALIZE JSON
                    // CREATE MODELS
                    // LOAD MODELS DATA INTO LIST VIEW
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
                    Log.i("INFO", "Profile *****" + String.valueOf(statusCode) + " " + errorResponse.toString());
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
