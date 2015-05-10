package com.hackerhunt.newshunt;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.hackerhunt.newshunt.ArticleDetailActivity.DownloadImagesTask;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SampleAdapter extends ArrayAdapter<ArticleListItem> {

	private Context mContext;
	private int mLayoutId;
	private List<ArticleListItem> mFileList;

	public SampleAdapter(Context context, int textViewResourceId,List<ArticleListItem> fileList) {
		super(context, textViewResourceId, fileList);
		mContext = context;
		mLayoutId = textViewResourceId;
		mFileList = fileList;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		ArticleListItem item = mFileList.get(position);
		holder = new ViewHolder();
		if (convertView == null) {
			
			LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = vi.inflate(mLayoutId, null);

			holder.textSize = (TextView) convertView.findViewById(R.id.txtCategory);
			holder.icon = (ImageView) convertView.findViewById(R.id.imgCategory);
			
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		if (null != item) {
			holder.textSize.setText(item.getTitle());
			
			try {
				holder.icon.setTag(item.getImageid().replace("\\/","/"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			new DownloadImagesTask().execute(holder.icon);
			
		}
		return convertView;
	}
	

	static class ViewHolder {
		TextView textSize;
		ImageView icon;
	}
	public class DownloadImagesTask extends AsyncTask<ImageView, Void, Bitmap> {

		ImageView imageView = null;

		@Override
		protected Bitmap doInBackground(ImageView... imageViews) {
		    this.imageView = imageViews[0];
		    return downloadBitmap((String)imageView.getTag());
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			imageView.setImageBitmap(result);
		}
		
		private Bitmap downloadBitmap(String url) {
			// initilize the default HTTP client object
			final DefaultHttpClient client = new DefaultHttpClient();

			//forming a HttoGet request 
			final HttpGet getRequest = new HttpGet(url);
			try {

				HttpResponse response = client.execute(getRequest);

				//check 200 OK for success
				final int statusCode = response.getStatusLine().getStatusCode();

				if (statusCode != HttpStatus.SC_OK) {
					Log.w("ImageDownloader", "Error " + statusCode + 
							" while retrieving bitmap from " + url);
					return null;

				}

				final HttpEntity entity = response.getEntity();
				if (entity != null) {
					InputStream inputStream = null;
					try {
						// getting contents from the stream 
						inputStream = entity.getContent();

						// decoding stream data back into image Bitmap that android understands
						final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

						return bitmap;
					} finally {
						if (inputStream != null) {
							inputStream.close();
						}
						entity.consumeContent();
					}
				}
			} catch (Exception e) {
				// You Could provide a more explicit error message for IOException
				getRequest.abort();
				Log.e("ImageDownloader", "Something went wrong while" +
						" retrieving bitmap from " + url + e.toString());
			} 

			return null;
		}
	
	}
}