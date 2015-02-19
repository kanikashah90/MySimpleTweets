package com.codepath.apps.mysimpletweets.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.codepath.apps.mysimpletweets.EndlessScrollListener;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApp;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.adapters.TweetsArrayAdapter;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by kanikash on 2/15/15.
 */
public abstract class TweetListFragment extends Fragment implements TweetsArrayAdapter.TweetListActionsCallback{
    private ListView lvTweets;
    private ArrayList<Tweet> tweets;
    private TweetsArrayAdapter atweets;
    private SwipeRefreshLayout swipeContainer;
    protected OnScreenActivityListener listener;
    private TwitterClient client;
    private String userName;
    private String userScreenName;
    private String userImageUrl;
    private EditText writeTweet;


    public abstract void changeControlVariables(Boolean isPagination, Boolean setMaxIdVal);

    public interface OnScreenActivityListener {
        public void showProgressBar();
        public void hideProgressBar();
        public void replyTweet(long tweetId, String originUser);
    }

    // 1. inflation logic
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tweet_list, parent, false);
        lvTweets = (ListView) v.findViewById(R.id.lvTweets);
        lvTweets.setAdapter(atweets);
        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                String test = this.getClass().getName();
                changeControlVariables(false, true);
            }
        });
        lvTweets.setOnScrollListener(new EndlessScrollListener(4) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                changeControlVariables(true, false);
            }
        });
        return v;
    }


    // 2. Creation lifecycle event

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tweets = new ArrayList<Tweet>();
        atweets = new TweetsArrayAdapter(getActivity(), tweets);
        atweets.setCallback(this);
        //atweets.set
        client = TwitterApp.getRestClient();  // singleton
        //findViewById(R.id.lvTweets).requestFocus();
        userName = "";
        userScreenName = "";
        userImageUrl = "";
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnScreenActivityListener) {
            listener = (OnScreenActivityListener) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement MyListFragment.OnItemSelectedListener");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 10) {
            if(resultCode == getActivity().RESULT_OK) {
                changeControlVariables(false, true);
            }
        }
    }

    protected Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public ArrayAdapter<Tweet> getTweetsAdapter() {
        return atweets;
    }

    public void notifyRefreshState(boolean state) {
        swipeContainer.setRefreshing(state);
    }

    @Override
    public void replyTweet(long tweetId, String originUser) {
        listener.replyTweet(tweetId, originUser);
    }

    public void refreshTimeline() {
        changeControlVariables(false, true);
    }
}
