package com.codepath.apps.mysimpletweets;

import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.mysimpletweets.fragments.HomeTimeLineFragment;
import com.codepath.apps.mysimpletweets.fragments.MentionTimeLineFragment;
import com.codepath.apps.mysimpletweets.fragments.ProfileFragment;
import com.codepath.apps.mysimpletweets.fragments.TweetListFragment;
import com.codepath.apps.mysimpletweets.fragments.createTweetDialog;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

public class TimelineActivity extends ActionBarActivity implements TweetListFragment.OnScreenActivityListener {
    private HomeTimeLineFragment homeTimelineFragment;
    private MentionTimeLineFragment mentionTimeLineFragment;
    private ViewPager vpPager;
    private PagerSlidingTabStrip tabs;
    private String screenName;
    private String tag;
    private String imageUrl;
    private int followers;
    private int following;
    private TwitterClient client;
    private TweetPageAdapter tpAdapter;
    private Toolbar toolbar;
    private ProgressBar progress_bar;
    private long tweetId;
    private String originUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        //setTitle("");
        // To display logo
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setLogo(R.drawable.ic_twitter);
        //getSupportActionBar().setDisplayUseLogoEnabled(true);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);

        getSupportActionBar().setTitle("");
        // To display logo
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_twitter);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        tpAdapter = new TweetPageAdapter(getSupportFragmentManager());
        vpPager = (ViewPager) findViewById(R.id.viewpager);
        vpPager.setAdapter(tpAdapter);
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setTextColor(getResources().getColor(R.color.twitter_color));
        tabs.setViewPager(vpPager);
        client = TwitterApp.getRestClient();
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

    public class TweetPageAdapter extends FragmentPagerAdapter {
        private String tabTitles[] = {"Home", "Mentions"};

        public TweetPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position == 0) {
                return new HomeTimeLineFragment();
            } else if(position == 1) {
                return new MentionTimeLineFragment();
            } else {
                return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }
    }

    public void onProfileView(MenuItem mi) {
        // Launch the profile view
        client.getUserDetails(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //Deserialize the response
                //Get the information needed
                try {
                    screenName = response.getString("screen_name");
                    imageUrl = response.getString("profile_image_url");
                    followers = response.getInt("followers_count");
                    following = response.getInt("friends_count");
                    tag = response.getString("description");
                    Intent i = new Intent(getBaseContext(), ProfileActivity.class);
                    i.putExtra("screenName", screenName);
                    i.putExtra("imageUrl", imageUrl);
                    i.putExtra("followers", followers);
                    i.putExtra("following", following);
                    i.putExtra("tag", tag);
                    startActivity(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.i("INFO", String.valueOf(statusCode) + " " + responseString + "user information fetch failed");
            }
        });

    }

    public void onCreateTweet(MenuItem mi) {

        client.getUserDetails(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                replyTweet(-1, "");
                /*//Deserialize the response
                //Get the information needed
                try {
                    FragmentManager fm = getSupportFragmentManager();
                    String userName = response.getString("name");
                    String userScreenName = response.getString("screen_name");
                    String userImageUrl = response.getString("profile_image_url");
                    createTweetDialog tweetDialog = createTweetDialog.newInstance(userName, userScreenName, userImageUrl);
                    tweetDialog.show(fm, "new_tweet");
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.i("INFO","user information fetch failed");
            }
        });
    }

    public void refreshHomeTimeline() {
        HomeTimeLineFragment homeTimeline = (HomeTimeLineFragment) getSupportFragmentManager().findFragmentByTag(makeFragmentName(0));
        homeTimeline.refreshTimeline();
        MentionTimeLineFragment mentionTimeLine = (MentionTimeLineFragment) getSupportFragmentManager().findFragmentByTag(makeFragmentName(1));
        mentionTimeLine.refreshTimeline();
    }

    private String makeFragmentName( int index)
    {
        return "android:switcher:" + vpPager.getId() + ":" + index;
    }

    // Should be called manually when an async task has started
    //@Override
    public void showProgressBar() {
        progress_bar.setVisibility(View.VISIBLE);
    }

    // Should be called when an async task has finished
    //@Override
    public void hideProgressBar() {
        progress_bar.setVisibility(View.INVISIBLE);
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

}
