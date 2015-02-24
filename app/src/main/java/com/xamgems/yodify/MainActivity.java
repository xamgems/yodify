package com.xamgems.yodify;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;


public class MainActivity extends ActionBarActivity {

    private EditText mEditQuery;
    private TextView mTextResponse;
    private Button mButtonSubmit;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUp();

        mButtonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setIsLoading(true);
                new YodaQueryTask().execute(mEditQuery.getText().toString());
            }
        });
    }

    private void setUp() {
        mEditQuery = (EditText) findViewById(R.id.edit_query);
        mTextResponse = (TextView) findViewById(R.id.text_response);
        mButtonSubmit = (Button) findViewById(R.id.button_submit);
        mProgressBar = (ProgressBar) findViewById(R.id.progress);
    }

    /**
     * Displays appropriate UI to indicate that some work is being
     * done.
     * @param isLoading {@code true} if progress UI should be displayed and
     *                  {@code false} if otherwise.
     */
    private void setIsLoading(boolean isLoading) {
        if (isLoading) {
            mTextResponse.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mTextResponse.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class YodaQueryTask extends AsyncTask<String, Void, String> {

        private static final String API_ENDPOINT = "https://yoda.p.mashape.com/yoda?sentence=";
        /** Never actually post your keys online... **/
        private static final String MASHAPE_KEY = "eCeSEFG362msh8iuNHYN6DRzQWNup1WtcDwjsn2R0PJxdidDvF";
        private static final String ACCEPT_TEXT = "text/plain";

        @Override
        protected String doInBackground(String... params) {
            URL yodaURL = null;
            BufferedReader reader = null;
            StringBuilder builder = new StringBuilder();
            try {
                // Setup URL to API endpoint
                String encodedQuery = URLEncoder.encode(params[0], "utf-8");
                yodaURL = new URL(API_ENDPOINT + encodedQuery);

                HttpURLConnection connection = (HttpURLConnection) yodaURL.openConnection();
                connection.setRequestProperty("X-Mashape-Key", MASHAPE_KEY);
                connection.setRequestProperty("Accept", ACCEPT_TEXT);
                connection.getResponseCode();

                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = reader.readLine();
                while (line != null) {
                    builder.append(line);
                    line = reader.readLine();
                }
            } catch (IOException e) {
                return e.toString();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        return "Could not close reader";
                    }
                }
            }

            return builder.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            mTextResponse.setText(result);
            setIsLoading(false);
        }
    }
}
