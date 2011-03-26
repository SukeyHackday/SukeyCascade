package org.sukey.android.cascade.helpers;

import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.sukey.android.cascade.Contact;

import android.content.Context;
import android.content.SharedPreferences;

public class ContactStorage {

	private ContactStorage() {
	}

	public static Set<String> getContactIds(Context context) {
		Set<String> contactIDs = new HashSet<String>();
		SharedPreferences pref = context.getSharedPreferences("cascade",
				Context.MODE_PRIVATE);
		if (pref.contains("contacts")) {
			JSONArray arry;
			try {
				arry = new JSONArray(pref.getString("contacts", null));
			} catch (JSONException e) {
				return contactIDs;
			}
			int l = arry.length();
			for (int i = 0; i < l; ++i) {
				try {
					contactIDs.add(arry.getString(i));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		return contactIDs;
	}

	public static Contact[] getContacts(Context context) {
		ContactAccessor ca = ContactAccessor.getInstance();
		return ca.getContactsFromIds(context, getContactIds(context));
	}

	public static void putContactIds(Context context, Set<String> contactIds) {
		JSONArray arry = new JSONArray(contactIds);
		SharedPreferences pref = context.getSharedPreferences("cascade",
				Context.MODE_PRIVATE);
		pref.edit().putString("contacts", arry.toString()).commit();
	}

}
