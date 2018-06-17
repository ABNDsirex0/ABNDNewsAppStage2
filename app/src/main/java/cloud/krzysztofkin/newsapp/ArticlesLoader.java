package cloud.krzysztofkin.newsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Loader for downloading data in the background
 */
class ArticlesLoader extends AsyncTaskLoader<List<Article>> {

    private String query;

    ArticlesLoader(Context context, String queryURL) {
        super(context);
        query = queryURL;
    }

    @Override
    public List<Article> loadInBackground() {
        return DataUtils.getData(query);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }
}
