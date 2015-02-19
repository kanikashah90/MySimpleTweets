package com.codepath.apps.mysimpletweets.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.ProfileActivity;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.fragments.TweetListFragment;
import com.codepath.apps.mysimpletweets.fragments.createTweetDialog;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by kanikash on 2/7/15.
 */
public class TweetsArrayAdapter extends ArrayAdapter<Tweet> {

    private TweetListActionsCallback callback;

    private static class ViewHolder {
        ImageView ivUserImage;
        TextView tvUserName;
        TextView tvTweet;
        TextView tvScreenName;
        TextView tvTime;
        TextView tvReply;
        TextView tvReTweet;
        TextView tvReTweetCount;
        TextView tvFavourite;
        TextView tvFavouriteCount;
    }

    public TweetsArrayAdapter(Context context, List<Tweet> tweets) {
        super(context, R.layout.item_tweet, tweets);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 1. get the tweet
        final Tweet tweet = getItem(position);
        final ViewHolder viewHolder;
        // 2. Inflate the template
        if(convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tweet, parent, false);
            // 3. Find the subviews in the template to populate data
            viewHolder.ivUserImage = (ImageView) convertView.findViewById(R.id.ivUserImage);
            viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
            viewHolder.tvTweet = (TextView) convertView.findViewById(R.id.tvTweet);
            viewHolder.tvScreenName = (TextView) convertView.findViewById(R.id.tvScreenName);
            viewHolder.tvTime = (TextView) convertView.findViewById(R.id.tvTime);
            viewHolder.tvReply = (TextView) convertView.findViewById(R.id.tvReply);
            viewHolder.tvReTweet = (TextView) convertView.findViewById(R.id.tvReTweet);
            viewHolder.tvReTweetCount = (TextView) convertView.findViewById(R.id.tvReTweetCount);
            viewHolder.tvFavourite = (TextView) convertView.findViewById(R.id.tvFavourite);
            viewHolder.tvFavouriteCount = (TextView) convertView.findViewById(R.id.tvFavouriteCount);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // 4. Populate data in subviews from the tweet
        viewHolder.tvUserName.setText(tweet.getUser().getName());
        viewHolder.tvTweet.setText(Html.fromHtml(tweet.getText()));
        viewHolder.tvScreenName.setText("@" + tweet.getUser().getScreenName());
        viewHolder.tvTime.setText(tweet.getCreationTime());
        viewHolder.ivUserImage.setImageResource(android.R.color.transparent); // clear out the old image for the recycled view
        Picasso.with(getContext()).load(tweet.getUser().userImageUrl).into(viewHolder.ivUserImage);
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fontawesome-webfont.ttf");
        viewHolder.tvReply.setTypeface(font);
        viewHolder.tvReTweet.setTypeface(font);
        viewHolder.tvFavourite.setTypeface(font);
        viewHolder.tvReply.setTex   t(getContext().getResources().getString(R.string.fa_reply));
        viewHolder.tvReTweet.setText(getContext().getResources().getString(R.string.fa_retweet));
        viewHolder.tvReTweetCount.setText(String.valueOf(tweet.getReTweetCount()));
        viewHolder.tvFavourite.setText(getContext().getResources().getString(R.string.fa_star_o));
        viewHolder.tvFavouriteCount.setText(String.valueOf(tweet.getFavouritesCount()));

        // Set the click handlers
        viewHolder.ivUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), ProfileActivity.class);
                i.putExtra("screenName", tweet.getUser().getScreenName());
                i.putExtra("imageUrl", tweet.getUser().getUserImageUrl());
                i.putExtra("followers", tweet.getUser().getFollowersCount());
                i.putExtra("following", tweet.getUser().getFriendsCount());
                i.putExtra("tag", tweet.getUser().getTag());
                getContext().startActivity(i);
            }
        });

        viewHolder.tvReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(callback != null){
                    callback.replyTweet(tweet.getUid(), tweet.getUser().getScreenName());
                }
            }
        });

        // 5. Return view to be inserted into the list
        return convertView;
    }

    public void setCallback(TweetListActionsCallback callback) {
        this.callback = callback;
    }

    public interface TweetListActionsCallback {
        public void replyTweet(long id, String originUser);
    }
}
