package com.codepath.apps.mysimpletweets.fragments;

import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.mysimpletweets.ProfileActivity;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TimelineActivity;
import com.codepath.apps.mysimpletweets.TwitterApp;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONObject;

/**
 * Created by kanikash on 2/16/15.
 */
public class createTweetDialog extends android.support.v4.app.DialogFragment {

    private TextView tvCharCount;
    private TextView tvUserName;
    private EditText etText;
    private TwitterClient client;
    private Button btn_send;

    public static createTweetDialog newInstance(String userName, String screenName, String imageUrl, long tweetId, String originUser ) {
        createTweetDialog frag = new createTweetDialog();
        Bundle args = new Bundle();
        args.putString("userName", userName);
        args.putString("screenName", screenName);
        args.putString("imageUrl", imageUrl);
        args.putLong("tweetId", tweetId);
        args.putString("originUser", originUser);
        frag.setArguments(args);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_tweet, container);

        getDialog().requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

        etText = (EditText) view.findViewById(R.id.etText);
        client = TwitterApp.getRestClient();


        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_dialog_titlebar);
        tvUserName = (TextView) getDialog().findViewById(R.id.tvUserName);
        tvCharCount = (TextView) getDialog().findViewById(R.id.tvCharCount);
        tvUserName.setText(getArguments().getString("screenName"));

        String replyUser = getArguments().getString("originUser");
        if(!replyUser.isEmpty()) {
            etText.setText("@" + replyUser + " ");
            int remainChars = 140 - etText.getText().toString().length();
            tvCharCount.setText(String.valueOf(remainChars));
        }
        etText.requestFocus();

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

        btn_send = (Button) getDialog().findViewById(R.id.btn_send);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tweet = etText.getText().toString();
                if(tweet.length() < 140) {
                    client.postTweet(new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            // Refresh the contents of the homeTimeline fragment
                            if(getActivity() instanceof TimelineActivity ) {
                                ((TimelineActivity)getActivity()).refreshHomeTimeline();
                            } else if(getActivity() instanceof ProfileActivity) {
                                ((ProfileActivity)getActivity()).refreshProfileTimeline();
                            }
                            dismiss();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            Toast.makeText(getActivity().getBaseContext(), "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }, tweet, getArguments().getLong("tweetId"));
                }
            }
        });
    }
}
