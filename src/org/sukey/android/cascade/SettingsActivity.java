package org.sukey.android.cascade;

import org.sukey.android.cascade.helpers.ContactAccessor;
import org.sukey.android.cascade.helpers.ContactStorage;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceClickListener;

public class SettingsActivity extends PreferenceActivity implements
		OnPreferenceClickListener {
	Preference mEnabled;
	Preference mSelectContacts;
	Preference mTestService;
	Preference mDoIt;
	ContactAccessor mContactAccessor = ContactAccessor.getInstance();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.prefs);

		mEnabled = (Preference) findPreference("enabled");
		mEnabled.setOnPreferenceClickListener(this);

		mSelectContacts = (Preference) findPreference("buddy_list");
		mSelectContacts.setOnPreferenceClickListener(this);

		mTestService = (Preference) findPreference("test");
		if (mTestService != null)
			mTestService.setOnPreferenceClickListener(this);

		mDoIt = (Preference) findPreference("do_it");
		if (mDoIt != null)
			mDoIt.setOnPreferenceClickListener(this);
	}

	public boolean onPreferenceClick(Preference preference) {
		if (preference == mEnabled) {
			return true;
		} else if (preference == mSelectContacts) {
			startActivity(new Intent(this, SelectContactsActivity.class));
			return true;
		} else if (preference == mTestService) {
			Contact[] contacts = new Contact[] {
					new Contact("1234", "Test contact 1", 1, "5554", "Label1"),
					new Contact("7413", "Test contact 2", 2, "5556", "Label2"),
					new Contact("4789", "Test contact 3", 2, "5560", "Label3") };

			Intent intent = new Intent(this, CascadeService.class);
			intent.setAction(CascadeService.ACTION_BROADCAST);
			intent.putExtra(CascadeService.EXTRA_MESSAGE,
					"This is a test from Sukey.");
			intent.putExtra(CascadeService.EXTRA_CONTACTS, contacts);
			startService(intent);
			return true;
		} else if (preference == mDoIt) {
			Contact[] contacts = ContactStorage.getContacts(this);

			Intent intent = new Intent(this, CascadeService.class);
			intent.setAction(CascadeService.ACTION_BROADCAST);
			intent.putExtra(CascadeService.EXTRA_MESSAGE,
					"This is a test from Sukey Cascade.");
			intent.putExtra(CascadeService.EXTRA_CONTACTS, contacts);
			startService(intent);
			return true;
		}
		return false;
	}
}