package org.sukey.android.cascade;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.Parcelable;
import android.telephony.SmsManager;
import android.util.Log;

public class CascadeService extends Service {
	public static final String ACTION_BROADCAST = "org.sukey.cascade.broadcast_sms";
	public static final String EXTRA_CONTACTS = "org.sukey.cascade.contacts";
	public static final String EXTRA_MESSAGE = "org.sukey.cascade.message";

	protected NotificationManager nm;
	protected Notification notification;
	protected PendingIntent notificationContentIntent;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		handleStart(intent, startId);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		handleStart(intent, startId);
		return START_REDELIVER_INTENT;
	}

	protected void updateNotification(int cur, int max) {
		String title = getResources().getString(R.string.app_name);
		String text = getResources().getString(
				R.string.notification_cascading_message,
				new Object[] { (Integer) cur, (Integer) max });
		notification.setLatestEventInfo(getApplicationContext(), title, text,
				notificationContentIntent);
		notification.number = max - cur;
		nm.notify(0, notification);
	}

	protected void handleStart(Intent intent, int startId) {
		// actually do some work
		if (!intent.getAction().equals(ACTION_BROADCAST)) {
			stopSelf();
			return;
		}
		String message = intent.getStringExtra(EXTRA_MESSAGE);

		Parcelable[] p_contacts = intent
				.getParcelableArrayExtra(EXTRA_CONTACTS);

		Contact[] contacts = new Contact[p_contacts.length];
		for (int i = 0; i < p_contacts.length; ++i) {
			contacts[i] = (Contact) p_contacts[i];
		}

		nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		new SendSmsTask().execute(contacts, message);
	}

	public static class ProgressMessage {
		// TODO
	}

	public class SendSmsTask extends AsyncTask<Object, ProgressMessage, Void> {

		@Override
		protected Void doInBackground(Object... args) {
			SmsManager sms = SmsManager.getDefault();

			if (args.length != 2) {
				throw new IllegalArgumentException(
						"Expecting exactly two arguments: an array of contacts and a message");
			}
			Contact[] contacts = (Contact[]) args[0];
			String message = (String) args[1];
			int length = contacts.length;
			Log.d("CascadeService", "Cascading \"" + message + "\" to "
					+ length + " recipients");
			for (int i = 0; i < length; ++i) {
				updateNotification(i + 1, length);
				Log.d("CascadeService", "Sending message to "
						+ contacts[i].getNumber());
				sms.sendTextMessage(contacts[i].getNumber(), null, message,
						null, null);
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			notification = new Notification(R.drawable.icon,
					"Cascading messages", System.currentTimeMillis());
			notification.flags |= Notification.FLAG_ONGOING_EVENT
					| Notification.FLAG_NO_CLEAR;
			Intent intent = new Intent(CascadeService.this,
					CascadeService.class);
			notification.contentIntent = notificationContentIntent = PendingIntent
					.getBroadcast(CascadeService.this, 0, intent, 0);
		}

		@Override
		protected void onProgressUpdate(ProgressMessage... values) {
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			nm.cancel(0);
			CascadeService.this.stopSelf();
		}

	}

}
