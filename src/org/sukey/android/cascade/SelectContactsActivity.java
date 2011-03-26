package org.sukey.android.cascade;

import java.util.List;
import java.util.Set;

import org.sukey.android.cascade.R;
import org.sukey.android.cascade.helpers.ContactAccessor;
import org.sukey.android.cascade.helpers.ContactStorage;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

public class SelectContactsActivity extends ListActivity {
	ContactAccessor mContactAccessor = ContactAccessor.getInstance();
	Set<String> contactIDs;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		populateContactsLayout();
		contactIDs = ContactStorage.getContactIds(this);
	}

	protected class LoadContactsTask extends
			AsyncTask<Void, Void, ArrayAdapter<Contact>> {
		protected ProgressDialog mProgress;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgress = ProgressDialog.show(SelectContactsActivity.this, null,
					getString(R.string.loading_contacts), true, false);
		}

		@Override
		protected ArrayAdapter<Contact> doInBackground(Void... params) {
			List<Contact> contacts = mContactAccessor
					.getContactList(SelectContactsActivity.this);
			if (contactIDs.size() > 0) {
				for (Contact c : contacts) {
					c.setSelected(contactIDs.contains(c.getId()));
				}
			}
			return new ContactItemArrayAdapter(SelectContactsActivity.this,
					R.layout.select_contacts, contacts);
		}

		@Override
		protected void onPostExecute(ArrayAdapter<Contact> result) {
			super.onPostExecute(result);
			setListAdapter(result);
			mProgress.dismiss();
		}

	}

	@Override
	protected void onStop() {
		super.onStop();
		ContactStorage.putContactIds(this, contactIDs);
	}

	/** 
	 * 
	 */
	private void populateContactsLayout() {
		ListView listView = getListView();
		listView.setItemsCanFocus(false);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		listView.setTextFilterEnabled(true);
		queryContacts();
	}

	/**
	 * Utility method for querying contacts and displaying them
	 * 
	 * @param nameClause
	 */
	private void queryContacts() {
		new LoadContactsTask().execute();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Contact contact = (Contact) l.getItemAtPosition(position);
		CheckBox checkBox = (CheckBox) v.findViewById(R.id.selected);
		contact.setSelected(!contact.getSelected());
		l.setItemChecked(position, contact.getSelected());
		checkBox.setChecked(l.isItemChecked(position));
		if (l.isItemChecked(position)) {
			contactIDs.add(contact.getId());
		} else {
			contactIDs.remove(contact.getId());
		}
	}

	private static class ContactItemArrayAdapter extends ArrayAdapter<Contact> {
		private final int resource;

		/**
		 * 
		 * @param context
		 * @param resource
		 * @param textViewResourceId
		 * @param objects
		 */
		public ContactItemArrayAdapter(Context context, int resource,
				int textViewResourceId, List<Contact> objects) {
			super(context, resource, textViewResourceId, objects);
			this.resource = resource;
		}

		/**
		 * 
		 * @param context
		 * @param textViewResourceId
		 * @param objects
		 */
		public ContactItemArrayAdapter(Context context, int textViewResourceId,
				List<Contact> objects) {
			super(context, textViewResourceId, objects);
			this.resource = textViewResourceId;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if (view == null)
				view = View.inflate(getContext(), resource, null);
			Contact contact = (Contact) getItem(position);
			TextView nameView = (TextView) view.findViewById(R.id.name);
			nameView.setText(contact.getName());
			TextView labelView = (TextView) view.findViewById(R.id.label);
			labelView.setText(contact.getLabel() + ":");
			TextView numberView = (TextView) view.findViewById(R.id.number);
			numberView.setText(contact.getNumber());
			CheckBox checkBox = (CheckBox) view.findViewById(R.id.selected);
			checkBox.setTag(contact.getId());
			checkBox.setChecked(contact.getSelected());
			return view;
		}
	}
}