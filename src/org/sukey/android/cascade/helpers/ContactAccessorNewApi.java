package org.sukey.android.cascade.helpers;

import java.util.ArrayList;
import java.util.List;

import org.sukey.android.cascade.Contact;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;

public class ContactAccessorNewApi extends ContactAccessor {

	@Override
	public Intent getContactPickerIntent() {
		return new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);
	}

	@Override
	protected List<Contact> getContactsSelection(Context context,
			String selection, String[] selectionArgs) {

		List<Contact> contacts = new ArrayList<Contact>();

		ContentResolver cr = context.getContentResolver();
		Cursor cur = cr.query(Contacts.CONTENT_URI,
				null, selection, selectionArgs, Contacts.DISPLAY_NAME);
		while (cur.moveToNext()) {
			String id = cur.getString(cur
					.getColumnIndex(Contacts._ID));
			String name = cur.getString(cur
					.getColumnIndex(Contacts.DISPLAY_NAME));
			Cursor pCur = cr.query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
					new String[] { id }, null);
			while (pCur.moveToNext()) {
				String number = pCur
						.getString(pCur
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				int type = pCur
						.getInt(pCur
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));

				String label = Resources.getSystem().getString(
						ContactsContract.CommonDataKinds.Phone
								.getTypeLabelResource(type));

				Contact contact = new Contact(id, name, type, number, label);
				contacts.add(contact);
			}
			pCur.close();
		}
		cur.close();

		return contacts;
	}

	@Override
	public List<Contact> getContactList(Context context) {
		return getContactsSelection(context, Contacts.HAS_PHONE_NUMBER
				+ " = 1", null);
	}

	@Override
	public Contact[] getContactsFromIds(Context context, String[] ids) {
		return getContactsSelection(
				context,
				Contacts.HAS_PHONE_NUMBER + " = 1 AND " + Contacts._ID + " IN "
						+ createInClause(ids), null).toArray(new Contact[] {});
	}
}
