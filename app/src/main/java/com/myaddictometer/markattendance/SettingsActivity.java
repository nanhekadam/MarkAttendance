package com.myaddictometer.markattendance;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.telephony.SubscriptionInfo;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;
import java.util.PropertyResourceBundle;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    public static Intent start(Context context) {
        return new Intent(context, SettingsActivity.class);
    }

    /* Below is the SETTINGS FRAGMENT */
    public static class SettingsFragment extends PreferenceFragmentCompat {

        TinyDB quickDatabase;
        static final int NOTIFICATION_ID = 123;
        static final int ALARM_REQUEST_CODE = 321;
        Context context;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preference_main, rootKey);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            quickDatabase  = new TinyDB(getActivity());
            context = getActivity();

            boolean timer_not_first_time = quickDatabase.getBoolean(getString(R.string.key_timer_first_timer));

            PreferenceManager.setDefaultValues(context, R.xml.preference_main, false);

            Preference pref_login = (Preference) getPreferenceManager().findPreference(getString(R.string.pref_key_login));
            Preference pref_notif_switch = (Preference) getPreferenceManager().findPreference(getString(R.string.pref_key_notify));
            final Preference pref_time = (Preference) getPreferenceManager().findPreference(getString(R.string.pref_key_notif_time));
            Preference pref_ipl_updates = (Preference) getPreferenceManager().findPreference(getString(R.string.pref_key_ipl_updates));


            pref_login.setOnPreferenceClickListener(new androidx.preference.Preference.OnPreferenceClickListener(){
                @Override
                public boolean onPreferenceClick(androidx.preference.Preference preference) {
                    setUpUserDetails(getActivity());
                    return true;
                }
            });


            pref_notif_switch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (Boolean.parseBoolean(newValue.toString())) {
                        setAlarm(quickDatabase.getLong(getString(R.string.key_schedule_time), 0));
                    } else {
                        cancelPreviousAlarms();
                        Toast.makeText(context, "Notification has been disabled.", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
            });


            pref_time.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    setUPNotificationTime(pref_time);
                    return true;
                }
            });
            if (!timer_not_first_time) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, 9);
                calendar.set(Calendar.MINUTE, 0);
                quickDatabase.putLong(getString(R.string.key_schedule_time), calendar.getTimeInMillis());
                quickDatabase.putBoolean(getString(R.string.key_timer_first_timer), true);
            }
            pref_time.setSummary(getFormattedTime(quickDatabase.getLong(getString(R.string.key_schedule_time), 0)));

            pref_ipl_updates.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    quickDatabase.putBoolean(getString(R.string.pref_key_ipl_updates), Boolean.parseBoolean(newValue.toString()));
                    quickDatabase.putBoolean(getString(R.string.key_notFirstTimeIPLUpdate), true);
                    return true;
                }
            });
        }

        private void setAlarm(Long time) {

            SimpleDateFormat sdf = new SimpleDateFormat("HH");
            int hour = Integer.parseInt(sdf.format(time));
            sdf = new SimpleDateFormat("mm");
            int minute = Integer.parseInt(sdf.format(time));

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);

            if (Calendar.getInstance().after(calendar)) {
                calendar.add(Calendar.DATE, 1);
            }

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent alarm = new Intent(getActivity(), NotificationReceiver.class);
            alarm.putExtra(getString(R.string.EXTRA_TIME_KEY), calendar.getTimeInMillis());

            PendingIntent pendingAlarm = PendingIntent.getBroadcast(getActivity(), ALARM_REQUEST_CODE, alarm, 0);
            assert alarmManager != null;
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingAlarm);

            Toast.makeText(
                    context,
                    "You'll be notified to mark attendance at " + getFormattedTime(calendar.getTimeInMillis()),
                    Toast.LENGTH_LONG).show();

        }

        private void cancelPreviousAlarms() {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent alarm = new Intent(getActivity(), NotificationReceiver.class);
            PendingIntent pendingAlarm = PendingIntent.getBroadcast(getActivity(), ALARM_REQUEST_CODE, alarm, 0);
            assert alarmManager != null;
            alarmManager.cancel(pendingAlarm);
        }


        private void setUPNotificationTime(final Preference preference) {
            cancelPreviousAlarms();

            final View mLayout = View.inflate(getActivity(), R.layout.time_picker, null);
            final AlertDialog alertDialog = new AlertDialog.Builder(context).create();

            mLayout.findViewById(R.id.btn_set_time).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    TimePicker timePicker = (TimePicker) mLayout.findViewById(R.id.time_picker);

                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
                    calendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());

                    quickDatabase.putLong(getString(R.string.key_schedule_time), calendar.getTimeInMillis());
                    setAlarm(calendar.getTimeInMillis());
                    preference.setSummary(getFormattedTime(calendar.getTimeInMillis()));
                    alertDialog.dismiss();
                }
            });
            alertDialog.setView(mLayout);
            alertDialog.show();
        }


        private void setUpUserDetails(final Context context){

            View myLayout = View.inflate(context, R.layout.login_page_layout, null);
            final EditText user = myLayout.findViewById(R.id.ed_emd_id);
            final EditText pwd = myLayout.findViewById(R.id.ed_pwd);
            if (!quickDatabase.getString(context.getString(R.string.key_username)).equals("")){
                user.setText(quickDatabase.getString(context.getString(R.string.key_username)));
                pwd.setText(quickDatabase.getString(context.getString(R.string.key_password)));
            }
            new AlertDialog.Builder(context)
                    .setView(myLayout)
                    .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            quickDatabase.putString(context.getString(R.string.key_username), user.getText().toString());
                            quickDatabase.putString(context.getString(R.string.key_password), pwd.getText().toString());
                            Toast.makeText(context, context.getString(R.string.username_pwd_set_toast_alert), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .show();
        }

        private String getFormattedTime(Long timeInMillis) {
            SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("hh:mm a");
            return simpleTimeFormat.format(timeInMillis) + " daily";
        }

    }

}