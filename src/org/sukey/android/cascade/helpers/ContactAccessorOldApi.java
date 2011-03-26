package org.sukey.android.cascade.helpers;

import java.util.ArrayList;
import java.util.List;

import org.sukey.android.cascade.Contact;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.Contacts.People;
import android.provider.Contacts.Phones;

@SuppressWarnings("deprecation")
public class ContactAccessorOldApi extends ContactAccessor {

	@Override
	public Intent getContactPickerIntent() {
		return new Intent(Intent.ACTION_PICK, People.CONTENT_URI);
	}

	@Override
	protected List<Contact> getContactsSelection(Context context,
			String selection, String[] selectionArgs) {

		ContentResolver cr = context.getContentResolver();
		List<Contact> contacts = new ArrayList<Contact>();
		Cursor cur = cr.query(People.CONTENT_URI, new String[] { People._ID,
				People.NAME, People.TYPE, People.NUMBER }, selection,
				selectionArgs, People.DEFAULT_SORT_ORDER);
		while (cur.moveToNext()) {
			String id = cur.getString(cur.getColumnIndexOrThrow(People._ID));
			String name = cur.getString(cur.getColumnIndexOrThrow(People.NAME));
			int type = cur.getInt(cur.getColumnIndexOrThrow(People.TYPE));
			String number = cur.getString(cur
					.getColumnIndexOrThrow(People.NUMBER));
			String label = (String) Phones.getDisplayLabel(context, type,
					"Other");

			Contact contact = new Contact(id, name, type, number, label);
			contacts.add(contact);
		}
		cur.close();

		return contacts;
	}

	@Override
	public List<Contact> getContactList(Context context) {
		return getContactsSelection(context, People.NUMBER + " is not null",
				null);
	}

	@Override
	public Contact[] getContactsFromIds(Context context, String[] ids) {
		return getContactsSelection(
				context,
				People.NUMBER + " is not null AND " + People._ID + " IN "
						+ createInClause(ids), null).toArray(new Contact[] {});
	}

}
