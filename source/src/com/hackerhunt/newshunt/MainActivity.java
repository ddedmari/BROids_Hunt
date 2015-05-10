/**
 * Copyright (c) 2014-present, Facebook, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by Facebook.
 *
 * As with any software that integrates with the Facebook platform, your use of
 * this software is subject to the Facebook Developer Principles and Policies
 * [http://developers.facebook.com/policy/]. This copyright notice shall be
 * included in all copies or substantial portions of the software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.hackerhunt.newshunt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

public class MainActivity extends FragmentActivity {

	private static final String PERMISSION = "user_likes";
	private final String PENDING_ACTION_BUNDLE_KEY =
			"com.facebook.samples.hellofacebook:PendingAction";

	private PendingAction pendingAction = PendingAction.NONE;
	private boolean canPresentShareDialog;
	private boolean canPresentShareDialogWithPhotos;
	private CallbackManager callbackManager;
	private ProfileTracker profileTracker;
	private ShareDialog shareDialog;
	private FacebookCallback<Sharer.Result> shareCallback = new FacebookCallback<Sharer.Result>() {
		@Override
		public void onCancel() {
			Log.d("HelloFacebook", "Canceled");
		}

		@Override
		public void onError(FacebookException error) {
			Log.d("HelloFacebook", String.format("Error: %s", error.toString()));
			String title = getString(R.string.error);
			String alertMessage = error.getMessage();
			showResult(title, alertMessage);
		}

		@Override
		public void onSuccess(Sharer.Result result) {
			Log.d("HelloFacebook", "Success!");
			if (result.getPostId() != null) {
				String title = getString(R.string.success);
				String id = result.getPostId();
				String alertMessage = getString(R.string.successfully_posted_post, id);
				showResult(title, alertMessage);
			}
		}

		private void showResult(String title, String alertMessage) {
			new AlertDialog.Builder(MainActivity.this)
			.setTitle(title)
			.setMessage(alertMessage)
			.setPositiveButton(R.string.ok, null)
			.show();
		}
	};

	private enum PendingAction {
		NONE,
		POST_PHOTO,
		POST_STATUS_UPDATE,
		GET_LIKES
	}
	Button emailSignin;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FacebookSdk.sdkInitialize(this.getApplicationContext());
		callbackManager = CallbackManager.Factory.create();
		getActionBar().setTitle("");
		if (hasLikesPermission()) {
			getLikesPermission();
		}

		LoginManager.getInstance().registerCallback(callbackManager,
				new FacebookCallback<LoginResult>() {
			@Override
			public void onSuccess(LoginResult loginResult) {
				handlePendingAction();
				onClickGetLikes();
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), Categories.class);
				startActivity(intent);
				updateUI();
			}

			@Override
			public void onCancel() {
				if (pendingAction != PendingAction.NONE) {
					showAlert();
					pendingAction = PendingAction.NONE;
				}
				updateUI();
			}

			@Override
			public void onError(FacebookException exception) {
				if (pendingAction != PendingAction.NONE
						&& exception instanceof FacebookAuthorizationException) {
					showAlert();
					pendingAction = PendingAction.NONE;
				}
				updateUI();
			}

			private void showAlert() {
				new AlertDialog.Builder(MainActivity.this)
				.setTitle(R.string.cancelled)
				.setMessage(R.string.permission_not_granted)
				.setPositiveButton(R.string.ok, null)	
				.show();
			}
		});


		setContentView(R.layout.main);

		emailSignin = (Button) findViewById(R.id.btnEmail);
		Typeface font = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf" );
		emailSignin.setTypeface(font);
		emailSignin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent("com.hackerhunt.newshunt.brijesh");
				startActivity(intent);
			}
		});

		shareDialog = new ShareDialog(this);
		shareDialog.registerCallback(
				callbackManager,
				shareCallback);

		if (savedInstanceState != null) {
			String name = savedInstanceState.getString(PENDING_ACTION_BUNDLE_KEY);
			pendingAction = PendingAction.valueOf(name);
		}

		profileTracker = new ProfileTracker() {
			@Override
			protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
				updateUI();
				// It's possible that we were waiting for Profile to be populated in order to
				// post a status update.
				handlePendingAction();
			}
		};

		// Can we present the share dialog for regular links?
		canPresentShareDialog = ShareDialog.canShow(ShareLinkContent.class);

		// Can we present the share dialog for photos?
		canPresentShareDialogWithPhotos = ShareDialog.canShow(
				SharePhotoContent.class);
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

	@Override
	protected void onResume() {
		super.onResume();

		// Call the 'activateApp' method to log an app event for use in analytics and advertising
		// reporting.  Do so in the onResume methods of the primary Activities that an app may be
		// launched into.
		AppEventsLogger.activateApp(this);

		updateUI();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putString(PENDING_ACTION_BUNDLE_KEY, pendingAction.name());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		callbackManager.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPause() {
		super.onPause();

		// Call the 'deactivateApp' method to log an app event for use in analytics and advertising
		// reporting.  Do so in the onPause methods of the primary Activities that an app may be
		// launched into.
		AppEventsLogger.deactivateApp(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		profileTracker.stopTracking();
	}

	private void updateUI() {
		boolean enableButtons = AccessToken.getCurrentAccessToken() != null;

	}

	private void handlePendingAction() {
		PendingAction previouslyPendingAction = pendingAction;
		// These actions may re-set pendingAction if they are still pending, but we assume they
		// will succeed.
		pendingAction = PendingAction.NONE;

		switch (previouslyPendingAction) {
		case NONE:
			break;
		case POST_PHOTO:
			postPhoto();
			break;
		case POST_STATUS_UPDATE:
			postStatusUpdate();
			break;
		case GET_LIKES:
			getLikes();
			break;
		}
	}
	private void onClickGetLikes() {
		//		performPublish(PendingAction.POST_STATUS_UPDATE, canPresentShareDialog);
		retrieveLikes(PendingAction.GET_LIKES, canPresentShareDialog);
	}
	private void getLikes() {
		GraphRequest.Callback callback = new GraphRequest.Callback() {

			@Override
			public void onCompleted(GraphResponse response) {
				ArrayList<String> categoryList = new ArrayList<String>();
				try {
					JSONObject jResponse = response.getJSONObject();
					JSONArray jData = jResponse.getJSONArray("data");
					String category;
					String name;
					StringBuilder builder = new StringBuilder();
					for (int j = 0; j < jData.length(); j++) {
						category = ((JSONObject) jData.get(j)).getString("category");
						name =  ((JSONObject) jData.get(j)).getString("name");
						categoryList.add(category);
						System.err.println(category+" " + name +"\n");
						builder.append(category+" " + name +"\n");
					}

					Intent intent = new Intent();
					intent.setClass(MainActivity.this, StaggeredGridActivity.class);
					intent.putStringArrayListExtra("category", categoryList);
					startActivity(intent);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		};
		AccessToken accessToken = AccessToken.getCurrentAccessToken();

		Set<String> fields = new HashSet<String>();
		String[] requiredFields = new String[]{
				"category",
				"name"
		};
		fields.addAll(Arrays.asList(requiredFields));

		GraphRequest request = GraphRequest.newGraphPathRequest(
				accessToken, "me/likes", null);
		Bundle parameters = request.getParameters();
		parameters.putString("fields", TextUtils.join(",", fields));


		GraphRequest request1 = new GraphRequest(accessToken, "me/likes", parameters, HttpMethod.GET, callback);
		GraphRequestAsyncTask task = new GraphRequestAsyncTask(request1);
		task.execute();
	}
	private void postStatusUpdate() {
		Profile profile = Profile.getCurrentProfile();
		ShareLinkContent linkContent = new ShareLinkContent.Builder()
		.setContentTitle("Hello Facebook")
		.setContentDescription(
				"The 'Hello Facebook' sample  showcases simple Facebook integration")
				.setContentUrl(Uri.parse("http://developers.facebook.com/docs/android"))
				.build();
		if (canPresentShareDialog) {
			shareDialog.show(linkContent);
		} else if (profile != null && hasPublishPermission()) {
			ShareApi.share(linkContent, shareCallback);
		} else {
			pendingAction = PendingAction.POST_STATUS_UPDATE;
		}
	}

	private void onClickPostPhoto() {
		performPublish(PendingAction.POST_PHOTO, canPresentShareDialogWithPhotos);
	}

	private void postPhoto() {
		Bitmap image = BitmapFactory.decodeResource(this.getResources(), R.drawable.icon);
		SharePhoto sharePhoto = new SharePhoto.Builder().setBitmap(image).build();
		ArrayList<SharePhoto> photos = new ArrayList<>();
		photos.add(sharePhoto);

		SharePhotoContent sharePhotoContent =
				new SharePhotoContent.Builder().setPhotos(photos).build();
		if (canPresentShareDialogWithPhotos) {
			shareDialog.show(sharePhotoContent);
		} else if (hasPublishPermission()) {
			ShareApi.share(sharePhotoContent, shareCallback);
		} else {
			pendingAction = PendingAction.POST_PHOTO;
		}
	}

	private boolean hasPublishPermission() {
		AccessToken accessToken = AccessToken.getCurrentAccessToken();
		return accessToken != null && accessToken.getPermissions().contains("publish_actions");
	}

	private boolean hasLikesPermission() {
		AccessToken accessToken = AccessToken.getCurrentAccessToken();
		return accessToken != null && accessToken.getPermissions().contains("user_likes");
	}


	private void getLikesPermission() {
		LoginManager.getInstance().logInWithReadPermissions(
				this,Arrays.asList(PERMISSION));
	}


	private void retrieveLikes(PendingAction action, boolean allowNoToken) {
		AccessToken accessToken = AccessToken.getCurrentAccessToken();
		if (accessToken != null) {
			pendingAction = action;
			if (hasLikesPermission()) {
				// We can do the action right away.
				handlePendingAction();
				return;
			} else {
				// We need to get new permissions, then complete the action when we get called back.
				LoginManager.getInstance().logInWithReadPermissions(
						this,
						Arrays.asList(PERMISSION));
				return;
			}
		}

		if (allowNoToken) {
			pendingAction = action;
			handlePendingAction();
		}
	}


	private void performPublish(PendingAction action, boolean allowNoToken) {
		AccessToken accessToken = AccessToken.getCurrentAccessToken();
		if (accessToken != null) {
			pendingAction = action;
			if (hasPublishPermission()) {
				// We can do the action right away.
				onClickGetLikes();
				return;
			} else {
				// We need to get new permissions, then complete the action when we get called back.
				LoginManager.getInstance().logInWithPublishPermissions(
						this,
						Arrays.asList(PERMISSION));
				return;
			}
		}

		if (allowNoToken) {
			pendingAction = action;
			handlePendingAction();
		}
	}
}
