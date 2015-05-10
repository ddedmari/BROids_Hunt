package com.hackerhunt.newshunt;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.SearchView;
import android.widget.Toast;

import com.etsy.android.grid.StaggeredGridView;

public class StaggeredGridActivity extends Activity implements  AbsListView.OnScrollListener, AbsListView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private static final String TAG = "StaggeredGridActivity";
    public static final String SAVED_DATA_KEY = "SAVED_DATA";

    private StaggeredGridView mGridView;
    private boolean mHasRequestedMore;
    private SampleAdapter mAdapter;

    private ArrayList<String> mData;
    ArrayList<ArticleListItem>  mCurrentDirectoryList;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sgv);
        mCurrentDirectoryList = new ArrayList<ArticleListItem>();
        
        Intent intent = getIntent();
        String category = intent.getStringExtra("key");
        getActionBar().setTitle("");
        try {
			new CategoryApi().execute(category).get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
        mGridView = (StaggeredGridView) findViewById(R.id.grid_view);

        LayoutInflater layoutInflater = getLayoutInflater();

     /*   View header = layoutInflater.inflate(R.layout.list_item_header_footer, null);
        View footer = layoutInflater.inflate(R.layout.list_item_header_footer, null);
        TextView txtHeaderTitle = (TextView) header.findViewById(R.id.txt_title);
        TextView txtFooterTitle =  (TextView) footer.findViewById(R.id.txt_title);
       txtHeaderTitle.setText("THE HEADER!");
        txtFooterTitle.setText("THE FOOTER!")

        mGridView.addHeaderView(header);
        mGridView.addFooterView(footer);*/
//        mCurrentDirectoryList.add(new ArticleListItem("test", "brijesh","test"));
        mAdapter = new SampleAdapter(this, R.layout.grid_item_layout, mCurrentDirectoryList);
        
        // do we have saved data?
        if (savedInstanceState != null) {
            mData = savedInstanceState.getStringArrayList(SAVED_DATA_KEY);
        }

        if (mData == null) {
            mData = SampleData.generateSampleData();
        }
        
        mGridView.setAdapter(mAdapter);
        mGridView.setOnScrollListener(this);
        mGridView.setOnItemClickListener(this);
        mGridView.setOnItemLongClickListener(this);
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(SAVED_DATA_KEY, mData);
    }

    @Override
    public void onScrollStateChanged(final AbsListView view, final int scrollState) {
        Log.d(TAG, "onScrollStateChanged:" + scrollState);
    }

    @Override
    public void onScroll(final AbsListView view, final int firstVisibleItem, final int visibleItemCount, final int totalItemCount) {
        Log.d(TAG, "onScroll firstVisibleItem:" + firstVisibleItem +
                            " visibleItemCount:" + visibleItemCount +
                            " totalItemCount:" + totalItemCount);
        // our handling
        if (!mHasRequestedMore) {
            int lastInScreen = firstVisibleItem + visibleItemCount;
            if (lastInScreen >= totalItemCount) {
                Log.d(TAG, "onScroll lastInScreen - so load more");
                mHasRequestedMore = true;
//                onLoadMoreItems();
            }
        }
    }

    /*private void onLoadMoreItems() {
        final ArrayList<String> sampleData = SampleData.generateSampleData();
        for (String data : sampleData) {
            mAdapter.add(data);
        }
        // stash all the data in our backing store
        mData.addAll(sampleData);
        // notify the adapter that we can update now
        mAdapter.notifyDataSetChanged();
        mHasRequestedMore = false;
    }*/

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Toast.makeText(this, "Item Clicked: " + position, Toast.LENGTH_SHORT).show();
        
        
        Intent intent = new Intent();
        intent.setClass(StaggeredGridActivity.this, ArticleDetailActivity.class);
        intent.putExtra("data", mCurrentDirectoryList.get(position));
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
    {
        Toast.makeText(this, "Item Long Clicked: " + position, Toast.LENGTH_SHORT).show();
        return true;
    }
    
    
    private class CategoryApi extends AsyncTask<String, String, ArrayList<ArticleListItem>> {
	     ArrayList<ArticleListItem> resultMap = new ArrayList<ArticleListItem>();

	    @Override
	    protected ArrayList<ArticleListItem> doInBackground(String... params) {
	        String urlString = params[0]; // URL to call
	        InputStream in = null;
	        String response;
	        try {
	            // http client
	            DefaultHttpClient httpClient = new DefaultHttpClient();
	            HttpEntity httpEntity = null;
	            HttpResponse httpResponse = null;
	            HttpGet httpPost = new HttpGet("http://192.168.5.80:8984/solr/newshunter/select?q=category%3A*" + urlString + "*&wt=json&indent=true&fl=id,article_text,main_image,article_text,title");
	            httpResponse = httpClient.execute(httpPost);
	            httpEntity = httpResponse.getEntity();
	            response = EntityUtils.toString(httpEntity);
	            JSONObject obj = new JSONObject(response);
	            JSONObject respObj = obj.getJSONObject("response");
	            if (respObj.getInt("numFound") > 0) {
	                JSONArray docsArr = respObj.getJSONArray("docs");
	                for (int i = 0; i < docsArr.length(); i++) {
	                    JSONObject tmpObj = docsArr.getJSONObject(i);
	                    String id = tmpObj.getString("id");
	                    String articleText = tmpObj.getString("article_text").replace("[\"", "").replace("\"]", "");
	                    String title = tmpObj.getString("title").replace("[\"", "").replace("\"]", "");
	                    String main_image = tmpObj.getString("main_image").replace("[\"", "").replace("\"]", "").replace("..", "");
	                    
	                    ArticleListItem ai = new ArticleListItem(title, articleText, main_image);
	                    resultMap.add(ai);
	                }
	            }
	        } catch (Exception e) {
	            System.out.println(e.getMessage());
	        }
	        return resultMap;
	    }
	    
	    @Override
	    protected void onPostExecute(ArrayList<ArticleListItem> result) {
	    	super.onPostExecute(result);
	    	mCurrentDirectoryList = result;
	    	mGridView.setAdapter(new SampleAdapter(getApplicationContext(), R.layout.grid_item_layout, result));
	    }
	}
    
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        
        
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search)
                .getActionView();
        if (null != searchView) {
            searchView.setSearchableInfo(searchManager
                    .getSearchableInfo(getComponentName()));
            searchView.setIconifiedByDefault(false);
        }
        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            public boolean onQueryTextChange(String newText) {
                // this is your adapter that will be filtered
                return true;
            }

            public boolean onQueryTextSubmit(String query) {
                //Here u can get the value "query" which is entered in the search box.
                try {
					new SearchResultApi().execute(query).get();
				} catch (InterruptedException | ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                return true;
            }
        };
        searchView.setOnQueryTextListener(queryTextListener);
        return true;
    }
    
    
    private class SearchResultApi extends AsyncTask<String, String, ArrayList<ArticleListItem>> {
    	 ArrayList<ArticleListItem> resultMap = new ArrayList<ArticleListItem>();

 	    @Override
 	    protected ArrayList<ArticleListItem> doInBackground(String... params) {
 	        String urlString = params[0]; // URL to call
 	        InputStream in = null;
 	        String response;
 	        try {
 	            // http client
 	            DefaultHttpClient httpClient = new DefaultHttpClient();
 	            HttpEntity httpEntity = null;
 	            HttpResponse httpResponse = null;
 	            HttpGet httpPost = new HttpGet("http://192.168.5.80:8984/solr/newshunter/select?q=title%3A*" + urlString + "*+OR+article_text%3A*+" + urlString + "+*&wt=json&indent=true&fl=id,article_text,main_image,article_text,title");
 	            httpResponse = httpClient.execute(httpPost);
 	            httpEntity = httpResponse.getEntity();
 	            response = EntityUtils.toString(httpEntity);
 	            JSONObject obj = new JSONObject(response);
 	            JSONObject respObj = obj.getJSONObject("response");
 	            if (respObj.getInt("numFound") > 0) {
 	                JSONArray docsArr = respObj.getJSONArray("docs");
 	                for (int i = 0; i < docsArr.length(); i++) {
 	                    JSONObject tmpObj = docsArr.getJSONObject(i);
 	                    String id = tmpObj.getString("id");
 	                    String articleText = tmpObj.getString("article_text").replace("[\"", "").replace("\"]", "");
 	                    String title = tmpObj.getString("title").replace("[\"", "").replace("\"]", "");
 	                    String main_image = tmpObj.getString("main_image").replace("[\"", "").replace("\"]", "").replace("..", "");
 	                    
 	                    ArticleListItem ai = new ArticleListItem(title, articleText, main_image);
 	                    resultMap.add(ai);
 	                }
 	            }
 	        } catch (Exception e) {
 	            System.out.println(e.getMessage());
 	            e.printStackTrace();
 	        }
 	        return resultMap;
        }
 	    
 	    @Override
 	    protected void onPostExecute(ArrayList<ArticleListItem> result) {
 	    	super.onPostExecute(result);
 	    	mCurrentDirectoryList = result;
	    	mGridView.setAdapter(new SampleAdapter(getApplicationContext(), R.layout.grid_item_layout, result));
 	    }
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage("com.github.pedrovgs.sample");
			startActivity(LaunchIntent);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

    
    
}
