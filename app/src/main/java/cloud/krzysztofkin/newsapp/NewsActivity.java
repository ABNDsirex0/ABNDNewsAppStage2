package cloud.krzysztofkin.newsapp;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity implements LoaderCallbacks<List<Article>> {
    ArticlesAdapter articleListAdapter;
    //private static String API_KEY = "test";
    static String API_KEY = BuildConfig.ApiKey;
    private static final String QUERY_URL = "http://content.guardianapis.com/search";
    ProgressBar progressBar;
    TextView errorMessageView;
    ListView articleListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        //find elements of main activity
        progressBar = findViewById(R.id.progress_bar);
        errorMessageView = findViewById(R.id.error_message_view);
        articleListView = findViewById(R.id.article_list);
        //set visibility elements on main activity
        progressBar.setVisibility(View.VISIBLE);
        errorMessageView.setVisibility(View.GONE);

        //setup listview elements
        articleListAdapter = new ArticlesAdapter(NewsActivity.this, new ArrayList<Article>());
        articleListView.setAdapter(articleListAdapter);
        articleListView.setEmptyView(errorMessageView);
        articleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Article currentArticle = articleListAdapter.getItem(position);
                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri uri = Uri.parse(currentArticle.getWebUrl());
                // Create a new intent to view the URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, uri);
                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });


        //set connectivity manager and check network connection:
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (isConnected) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(1, null, this);
        } else {
            progressBar.setVisibility(View.GONE);
            errorMessageView.setText(R.string.no_internet);
        }

    }

    @Override
    //  Initialize activity options menu
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the Options Menu we specified in XML
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    //select menu item
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<List<Article>> onCreateLoader(int i, Bundle bundle) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        // getString retrieves a String value from the preferences. The second parameter is the default value for this preference.
        String pageSize = sharedPrefs.getString(
                getString(R.string.settings_page_size_key),
                getString(R.string.settings_page_size_default));
        String order = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );
        String topic = sharedPrefs.getString(
                getString(R.string.settings_topic_key),
                getString(R.string.settings_topic_default));
        // parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(QUERY_URL);
        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();

        // Append query parameter and its value.
        uriBuilder.appendQueryParameter("api-key", API_KEY);
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        if(!topic.equals("all")){
            uriBuilder.appendQueryParameter("section",topic);
        }
        uriBuilder.appendQueryParameter("page-size", pageSize);
        uriBuilder.appendQueryParameter("order-by", order);

        Log.v("URL", uriBuilder.toString());
        return new ArticlesLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Article>> loader, List<Article> articles) {
        progressBar.setVisibility(View.GONE);
        articleListAdapter.clear();
        if (articles != null && !articles.isEmpty()) {
            articleListAdapter.addAll(articles);
        }
        errorMessageView.setText(R.string.no_data);
    }

    @Override
    public void onLoaderReset(Loader<List<Article>> loader) {
        articleListAdapter.clear();
    }
}
