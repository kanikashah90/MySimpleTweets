package com.codepath.apps.mysimpletweets.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by kanikash on 2/7/15.
 */
public class TweetsArrayAdapter extends ArrayAdapter<Tweet> {

    public TweetsArrayAdapter(Context context, List<Tweet> tweets) {
        super(context, R.layout.item_tweet, tweets);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 1. get the tweet
        Tweet tweet = getItem(position);
        // 2. Inflate the template
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tweet, parent, false);
        }
        // 3. Find the subviews in the template to populate data
        ImageView ivUserImage = (ImageView) convertView.findViewById(R.id.ivUserImage);
        TextView tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
        TextView tvTweet = (TextView) convertView.findViewById(R.id.tvTweet);
        TextView tvScreenName = (TextView) convertView.findViewById(R.id.tvScreenName);
        TextView tvTime = (TextView) convertView.findViewById(R.id.tvTime);
        // 4. Populate data in subviews from the tweet
        tvUserName.setText(tweet.getUser().getName());
        tvTweet.setText(Html.fromHtml(tweet.getText()));
        tvScreenName.setText("@" + tweet.getUser().getName());
        tvTime.setText(tweet.getCreationTime());
        ivUserImage.setImageResource(android.R.color.transparent); // clear out the old image for the recycled view
        Picasso.with(getContext()).load(tweet.getUser().userImageUrl).into(ivUserImage);
        // 5. Return view to be inserted into the list
        return convertView;
    }
}
