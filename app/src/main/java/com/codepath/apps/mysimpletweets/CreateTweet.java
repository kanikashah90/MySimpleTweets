package com.codepath.apps.mysimpletweets;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONObject;

public class CreateTweet extends ActionBarActivity {
    private String userName;
    private String screenName;
    private String imageUrl;
    private TextView tvUserName;
    private TextView tvScreenName;
    private TextView tvCharCount;
    private ImageView ivUserImage;
    private EditText etText;
    private TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        //Get the subviews
        tvUserName = (TextView) findViewById(R.id.tvUserName);
        tvScreenName = (TextView) findViewById(R.id.tvScreenName);
        tvCharCount = (TextView) findViewById(R.id.tvCharCount);
        ivUserImage = (ImageView) findViewById(R.id.ivUserImage);
        etText = (EditText) findViewById(R.id.etText);


        //Get all the User details from the Intent
        userName = getIntent().getStringExtra("userName");
        screenName = getIntent().getStringExtra("screenName");
        imageUrl = getIntent().getStringExtra("imageUrl");
        client = TwitterApp.getRestClient(); // singleton

        // Populate data in subviews
        tvUserName.setText(userName);
        tvScreenName.setText("@" + screenName);
        ivUserImage.setImageResource(android.R.color.transparent); // clear out the old image for the recycled view
        Picasso.with(getBaseContext()).load(imageUrl).into(ivUserImage);

        //update number of characters left while user is editing in edit box
        etText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Integer remainingChars = 140 - s.length();
                tvCharCount.setText(remainingChars.toString());
                //Toast.makeText(getBaseContext(), "Hi" + s.length(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tweet, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }

        return super.onOptionsItemSelected(item);
    }

    public void postTweet(View view) {
        // Get the data from the edit text
        String tweet = etText.getText().toString();
        if(tweet.length() < 140) {
            client.postTweet(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    // Finish this intent and trigger refresh in the parent
                    Intent i = new Intent();
                    setResult(RESULT_OK, i);
                    CreateTweet.this.finish();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Toast.makeText(getBaseContext(), "Failed", Toast.LENGTH_SHORT).show();
                }
            }, tweet);
        }
    }
}
