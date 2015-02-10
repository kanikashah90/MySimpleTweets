package com.codepath.apps.mysimpletweets;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.codepath.apps.mysimpletweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import com.codepath.apps.mysimpletweets.adapters.TweetsArrayAdapter;

public class TimelineActivity extends ActionBarActivity {

    private TwitterClient client;
    private ListView lvTweets;
    private ArrayList<Tweet> tweets;
    private ArrayAdapter<Tweet> atweets;
    private long maxIdVal;
    private boolean isPagination;
    private EditText writeTweet;
    private String userName;
    private String userScreenName;
    private String userImageUrl;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //findViewById(R.id.lvTweets).requestFocus();
        maxIdVal = 0;
        isPagination = false;
        lvTweets = (ListView) findViewById(R.id.lvTweets);
        tweets = new ArrayList<Tweet>();
        atweets = new TweetsArrayAdapter(this, tweets);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isPagination = false;
                maxIdVal = 0;
                populateTimeline();
            }
        });
        lvTweets.setAdapter(atweets);
        lvTweets.setOnScrollListener(new EndlessScrollListener(4) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                isPagination = true;
                populateTimeline();
            }
        });
        client = TwitterApp.getRestClient(); // singleton
        userName = "";
        userScreenName = "";
        userImageUrl = "";
        populateTimeline();
        getUserDetails();
        writeTweet = (EditText) findViewById(R.id.etTweet);
        writeTweet.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //Toast.makeText(getBaseContext(), "Hi", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getBaseContext(), CreateTweet.class);
                i.putExtra("userName", userName);
                i.putExtra("screenName", userScreenName);
                i.putExtra("imageUrl", userImageUrl);
                startActivityForResult(i, 10);
            }
        });
    }

    //Calls the Rest Api
    //Show the listView
    private void populateTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // DESERIALIZE JSON
                // CREATE MODELS
                // LOAD MODELS DATA INTO LIST VIEW
                ArrayList<Tweet> tweets = Tweet.fromJSONArray(response);
                maxIdVal = Tweet.getLargestTweetId(tweets);
                if(!isPagination) {
                    if(!atweets.isEmpty()) {
                        atweets.clear();
                    }
                }
                atweets.addAll(tweets);
                atweets.notifyDataSetChanged();
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                throwable.printStackTrace();
            }
        }, isPagination, maxIdVal);

    }

    // Calls the Rest Api
    // Get the users information
    private void getUserDetails() {
        client.getUserDetails(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //Deserialize the response
                //Get the information needed
                try {
                    userName = response.getString("name");
                    userScreenName = response.getString("screen_name");
                    userImageUrl = response.getString("profile_image_url");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 10) {
            if(resultCode == RESULT_OK) {
                isPagination = false;
                maxIdVal = 0;
                populateTimeline();
            }
        }
    }
}
