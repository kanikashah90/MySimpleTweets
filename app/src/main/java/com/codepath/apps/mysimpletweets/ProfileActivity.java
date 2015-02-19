package com.codepath.apps.mysimpletweets;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.fragments.HomeTimeLineFragment;
import com.codepath.apps.mysimpletweets.fragments.ProfileFragment;
import com.codepath.apps.mysimpletweets.fragments.TweetListFragment;
import com.codepath.apps.mysimpletweets.fragments.createTweetDialog;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

public class ProfileActivity extends ActionBarActivity implements TweetListFragment.OnScreenActivityListener{
    private Toolbar toolbar;
    private ProgressBar progress_bar;
    private TwitterClient client;
    private ImageView ivImage;
    private TextView tvName;
    private TextView tvTag;
    private TextView tvFollowersCount;
    private TextView tvFollowingCount;
    private long tweetId;
    private String originUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_profile);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);
        client = TwitterApp.getRestClient();

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_twitter);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        String screenName = getIntent().getStringExtra("screenName");
        String tag = getIntent().getStringExtra("tag");
        String imageUrl = getIntent().getStringExtra("imageUrl");
        int followers = getIntent().getIntExtra("followers", 0);
        int following = getIntent().getIntExtra("following", 0);
        setTitle(screenName);

        ivImage = (ImageView) findViewById(R.id.ivImage);
        tvName = (TextView) findViewById(R.id.tvName);
        tvTag = (TextView) findViewById(R.id.tvTagLine);
        tvFollowersCount = (TextView) findViewById(R.id.tvFollowersCount);
        tvFollowingCount = (TextView) findViewById(R.id.tvFollowingCount);

        ivImage.setImageResource(android.R.color.transparent);
        Picasso.with(this).load(imageUrl).into(ivImage);
        tvName.setText(screenName);
        tvTag.setText(tag);
        tvFollowingCount.setText(String.valueOf(following));
        tvFollowersCount.setText(String.valueOf(followers));

        ProfileFragment profileFragment = ProfileFragment.newInstance(screenName, tag, imageUrl, followers, following);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.flTimeline, profileFragment);
        ft.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
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

    // Should be called manually when an async task has started
    //@Override
    public void showProgressBar() {
        progress_bar.setVisibility(View.VISIBLE);
        //setSupportProgressBarIndeterminateVisibility(true);
        //setProgressBarIndeterminateVisibility(true);
    }

    // Should be called when an async task has finished
    //@Override
    public void hideProgressBar() {
        progress_bar.setVisibility(View.INVISIBLE);
        //setSupportProgressBarIndeterminateVisibility(false);
        //setProgressBarIndeterminateVisibility(false);
    }

    @Override
    public void replyTweet(final long tweetId, final String originUser) {
        this.tweetId = tweetId;
        this.originUser = originUser;
        client.getUserDetails(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //Deserialize the response
                //Get the information needed
                try {
                    //FragmentManager fm = getSupportFragmentManager();
                    String userName = response.getString("name");
                    String userScreenName = response.getString("screen_name");
                    String userImageUrl = response.getString("profile_image_url");
                    createDialog(userName, userScreenName, userImageUrl);
                    //createTweetDialog tweetDialog = createTweetDialog.newInstance(userName, userScreenName, userImageUrl, tweetId, originUser);
                    //tweetDialog.show(fm, "new_tweet");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.i("INFO","user information fetch failed");
            }
        });
    }

    public void createDialog(String userName, String userScreenName, String userImageUrl) {
        FragmentManager fm = getSupportFragmentManager();
        createTweetDialog tweetDialog = createTweetDialog.newInstance(userName, userScreenName, userImageUrl, tweetId, originUser);
        tweetDialog.show(fm, "new_tweet");
    }

    public void refreshProfileTimeline() {
        ProfileFragment profileTimeline = (ProfileFragment) getSupportFragmentManager().findFragmentById(R.id.flTimeline);
        profileTimeline.refreshTimeline();
    }
}
