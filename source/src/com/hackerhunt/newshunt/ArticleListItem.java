package com.hackerhunt.newshunt;

import android.os.Parcel;
import android.os.Parcelable;

public class ArticleListItem implements Parcelable {

	public String getTitle() {
		return Title;
	}

	public void setTitle(String title) {
		Title = title;
	}

	public String getImageid() {
		return imageid;
	}

	public void setImageid(String imageid) {
		this.imageid = imageid;
	}

	public ArticleListItem(String title, String uri, String imageid) {
		super();
		Title = title;
		this.uri = uri;
		this.imageid = imageid;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	private String Title, uri;

	private String imageid;

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(Title);
		dest.writeString(uri);
		dest.writeString(imageid);
	}

	public static final Parcelable.Creator<ArticleListItem> CREATOR = new Parcelable.Creator<ArticleListItem>() {

		public ArticleListItem createFromParcel(Parcel in) {
			String Title = in.readString();
			String uri = in.readString();

			String imageid = in.readString();

			return new ArticleListItem(Title, uri, imageid);

		}

		public ArticleListItem[] newArray(int size) {
			return new ArticleListItem[size];
		}
	};

}
