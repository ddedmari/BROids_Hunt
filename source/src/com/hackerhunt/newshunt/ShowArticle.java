package com.hackerhunt.newshunt;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;

public class ShowArticle extends Activity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		 try {
				Map<String, ArrayList<String>> result = new CategoryApi().execute("Sports").get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
	}
	
	
	
	private class CategoryApi extends AsyncTask<String, String, Map<String, ArrayList<String>>> {
        Map<String, ArrayList<String>> resultMap = new HashMap<String, ArrayList<String>>();

        @Override
        protected Map<String, ArrayList<String>> doInBackground(String... params) {
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
                        String articleText = tmpObj.getString("article_text");
                        String title = tmpObj.getString("title");
                        String main_image = tmpObj.getString("main_image");
                        ArrayList<String> al = new ArrayList<String>();
                        al.add(title);
                        al.add(articleText);
                        al.add(main_image);
                        resultMap.put(id, al);
                    }
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            return resultMap;
        }

    }
}
