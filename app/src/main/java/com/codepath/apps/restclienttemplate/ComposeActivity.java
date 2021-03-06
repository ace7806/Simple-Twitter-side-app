package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {

    EditText etCompose;
    Button btnTweet;
    TwitterClient client;
    TextView charCount;
    public static final String TAG = "ComposeActivity";
    public static final int MAX_TWEET_LENGTH = 280;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        client = TwitterApp.getRestClient(this);
        charCount = findViewById(R.id.tvCharCount);
        etCompose = findViewById(R.id.etCompose);
        btnTweet = findViewById (R.id.btnTweet);
        etCompose.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Fires right before text is changing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Fires right as the text is being changed (even supplies the range of text)
                charCount.setText(String.valueOf(charSequence.length())+"/280");
                if(charSequence.length()>=280) charCount.setTextColor(Color.RED);
                else charCount.setTextColor(Color.GRAY);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Fires right after the text has changed
            }
        });
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tweetContent = etCompose.getText().toString();
                if(tweetContent.isEmpty()||tweetContent.length()>MAX_TWEET_LENGTH) Toast.makeText(ComposeActivity.this,"sorry this tweet is empty or either too full",Toast.LENGTH_SHORT).show();
                Toast.makeText(ComposeActivity.this,tweetContent.toString(),Toast.LENGTH_SHORT).show();
                //make an api call on twitter to publish tweet
                client.publishTweet(tweetContent, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG, "onSuccess: published tweet");
                        try {
                            Tweet tweet = Tweet.fromJson(json.jsonObject);
                            Log.i(TAG, "onSuccess: published tweet says"+tweet.body);
                            Intent i = new Intent();
                            i.putExtra("tweet", Parcels.wrap(tweet));
                            setResult(RESULT_OK);// set result code and bundle data for response
                            etCompose.clearComposingText();
                            finish(); // closes the activity, pass data to parent
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "onFailure: ",throwable );
                    }
                });
            }
        });
    }
}