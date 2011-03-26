package org.sukey.android.cascade.helpers;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.sukey.android.cascade.Contact;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

public abstract class ContactAccessor {
	private static ContactAccessor sInstance;

	public static ContactAccessor getInstance() {
		if (sInstance == null) {
			int sdkVersion = Integer.parseInt(Build.VERSION.SDK);
			if (sdkVersion < Build.VERSION_CODES.ECLAIR) {
				sInstance = new ContactAccessorOldApi();
			} else {
				sInstance = new ContactAccessorNewApi();
			}
		}
		return sInstance;
	}

	protected abstract List<Contact> getContactsSelection(Context context,
			String selection, String[] selectionArgs);

	public abstract Intent getContactPickerIntent();

	public abstract List<Contact> getContactList(Context context);

	public abstract Contact[] getContactsFromIds(Context context, String[] ids);

	public Contact[] getContactsFromIds(Context context, Set<String> ids) {
		return getContactsFromIds(context, ids.toArray(new String[] {}));
	}

	protected static String createInClause(String[] pColl) {
		if (pColl == null || pColl.length == 0)
			return "()";
		StringBuilder oBuilder = new StringBuilder("('");
		int i = 0, len = pColl.length;
		oBuilder.append(pColl[i].replace("'", "\\'"));
		while (++i < len)
			oBuilder.append("', '").append(pColl[i].replace("'", "\\'"));
		return oBuilder.append("')").toString();
	}

	protected static String createInClause(Iterable<? extends Object> pColl) {
		Iterator<? extends Object> oIter;
		if (pColl == null || (!(oIter = pColl.iterator()).hasNext()))
			return "()";
		StringBuilder oBuilder = new StringBuilder("('").append(String.valueOf(oIter.next()).replace("'", "\\'"));
		while (oIter.hasNext())
			oBuilder.append("', '").append(String.valueOf(oIter.next()).replace("'", "\\'"));
		return oBuilder.append("')").toString();
	}
}
