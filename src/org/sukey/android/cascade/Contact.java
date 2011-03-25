package org.sukey.android.cascade;

import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.ContactsContract;

public final class Contact implements Parcelable {
	protected String mId;
	protected int mType;
	protected String mName;
	protected String mNumber;
	protected boolean mSelected;

	public Contact(String id, String name, int type, String number) {
		this(id, name, type, number, false);
	}

	public Contact(String id, String name, int type, String number,
			boolean selected) {
		mId = id;
		mName = name;
		mType = type;
		mNumber = number;
		mSelected = selected;
	}

	public Contact(Parcel in) {
		readFromParcel(in);
	}

	public boolean getSelected() {
		return mSelected;
	}

	public void setSelected(boolean selected) {
		mSelected = selected;
	}

	public String getId() {
		return mId;
	}

	public void setId(String id) {
		mId = id;
	}

	public String getLabel() {
		return Resources.getSystem().getString(
				ContactsContract.CommonDataKinds.Phone
						.getTypeLabelResource(mType));
	}

	public int getType() {
		return mType;
	}

	public void setType(int type) {
		mType = type;
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		mName = name;
	}

	public String getNumber() {
		return mNumber;
	}

	public void setNumber(String number) {
		mNumber = number;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[Contact: ").append(mId).append(", \"").append(mName)
				.append("\", ").append(mType).append(", ").append(mNumber)
				.append("]");
		return sb.toString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	protected void readFromParcel(Parcel src) {
		mId = src.readString();
		mType = src.readInt();
		mName = src.readString();
		mNumber = src.readString();
		mSelected = src.readInt() == 1;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mId);
		dest.writeInt(mType);
		dest.writeString(mName);
		dest.writeString(mNumber);
		dest.writeInt(mSelected ? 1 : 0);
	}

	public static final Parcelable.Creator<Contact> CREATOR = new Parcelable.Creator<Contact>() {
		public Contact createFromParcel(Parcel in) {
			return new Contact(in);
		}

		public Contact[] newArray(int size) {
			return new Contact[size];
		}
	};
}
