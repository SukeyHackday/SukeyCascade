package org.sukey.android.cascade;

import org.sukey.android.cascade.R;
import org.sukey.android.cascade.helpers.ContactAccessor;
import org.sukey.android.cascade.helpers.ContactStorage;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceClickListener;

public class SettingsActivity extends PreferenceActivity implements
		OnPreferenceClickListener {
	Preference mSelectContacts;
	Preference mTestService;
	ContactAccessor mContactAccessor = ContactAccessor.getInstance();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.prefs);

		mSelectContacts = (Preference) findPreference("buddy_list");
		mSelectContacts.setOnPreferenceClickListener(this);

		mTestService = (Preference) findPreference("test");
		mTestService.setOnPreferenceClickListener(this);
	}

	public boolean onPreferenceClick(Preference preference) {
		if (preference == mSelectContacts) {
			startActivity(new Intent(this, SelectContactsActivity.class));
			return true;
		} else if (preference == mTestService) {
			Contact[] contacts = new Contact[4];
			contacts[0] = new Contact("1234", "Test contact 1", 1, "+447810361502", "Label1");
			contacts[1] = new Contact("7413", "Test contact 2", 2, "+447810361502", "Label2");
			contacts[2] = new Contact("4789", "Test contact 3", 2, "+447810361502", "Label3");
			contacts[3] = new Contact("9102", "Test contact 4", 5, "+447810361502", "Label4");

			Intent intent = new Intent(this, CascadeService.class);
			intent.putExtra("org.sukey.cascade.contacts", contacts);
			startService(intent);
			return true;
		}
		return false;
	}
}