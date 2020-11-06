package com.myaddictometer.markattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    TinyDB quickDatabase;
    static int FIRST_USE = 99;
    static int NOT_FIRST_USE = 100;

//    ConstraintLayout iplLayout;
//    ConstraintLayout matchALayout;
//    ConstraintLayout matchBLayout;
//    TextView matchANumber;
//    TextView matchBNumber;
//    ImageView matchATeamA;
//    ImageView matchATeamB;
//    ImageView matchBTeamA;
//    ImageView matchBTeamB;
//    ConstraintLayout winnerALayout, winnerBLayout;
//    ImageView winnerA, winnerB, loserA, loserB;
//    TextView scoreATextView, scoreBTextView;
//    ProgressBar resultProgressBar;
//    TextView todayTextView, resultTextView;

    List<Integer> matchesIndicesResult;
    String resultMatch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createNotificationChannel();

        TextView welcomeTextView = findViewById(R.id.welcomeTextView);
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE - MMM dd, yyyy");
        String message = getString(R.string.welcome_message)  + "\n\n" + sdf.format(System.currentTimeMillis());
        welcomeTextView.setText(message);

        quickDatabase = new TinyDB(this);
        Button login = findViewById(R.id.btn_mark_attendance);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quickDatabase.getString(getString(R.string.key_username)).equals("")){
                    setUpUserDetails(FIRST_USE);
                } else {
                    startActivity(WebActivity.start(MainActivity.this));
                }
            }
        });

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()//.build();
                .addTestDevice("31CEEF2EE3DB8042E939736CA6E38142")
                .build();
        mAdView.loadAd(adRequest);

        //IPL mania
//        iplLayout = findViewById(R.id.iplLayout);
//        boolean notFirstTimeIPL = quickDatabase.getBoolean(getString(R.string.key_notFirstTimeIPLUpdate));
//        boolean showIPLLayout;
//
//        if (!notFirstTimeIPL) {
//            showIPLLayout = true;
//        } else {
//            showIPLLayout = quickDatabase.getBoolean(getString(R.string.pref_key_ipl_updates));
//        }
//
//        if (showIPLLayout) {
//            matchALayout = findViewById(R.id.matchALayout);
//            matchBLayout = findViewById(R.id.matchBLayout);
//            matchATeamA = findViewById(R.id.matchATeamA);
//            matchATeamB = findViewById(R.id.matchATeamB);
//            matchBTeamA = findViewById(R.id.matchBTeamA);
//            matchBTeamB = findViewById(R.id.matchBTeamB);
//            winnerALayout = findViewById(R.id.winnerALayout);
//            winnerBLayout = findViewById(R.id.winnerBLayout);
//            winnerA = findViewById(R.id.winnerA);
//            winnerB = findViewById(R.id.winnerB);
//            loserA = findViewById(R.id.loserA);
//            loserB = findViewById(R.id.loserB);
//            scoreATextView = findViewById(R.id.scoreATextView);
//            scoreBTextView = findViewById(R.id.scoreBTextView);
//            resultProgressBar = findViewById(R.id.resultProgressBar);
//            todayTextView = findViewById(R.id.todayTextView);
//            resultTextView = findViewById(R.id.resultTextView);
//
//            String[] iplTeams = getResources().getStringArray(R.array.ipl_team_names);
//            try {
//                JSONObject object = new JSONObject(getString(R.string.ipl_schedule));
//                JSONArray array = object.getJSONArray("matches");
//                List<Integer> matchesIndices = new ArrayList<Integer>();
//                matchesIndicesResult = new ArrayList<Integer>();
//                String date = new SimpleDateFormat("yyyy-MM-dd").format(System.currentTimeMillis());
//                Calendar todayD = Calendar.getInstance();
//                todayD.add(Calendar.DAY_OF_MONTH, -1);
//                String dateResult = new SimpleDateFormat("yyyy-MM-dd").format(todayD.getTimeInMillis());
//                for (int i = 0; i < array.length(); i++) {
//                    JSONObject obj = array.getJSONObject(i);
//                    if (date.equals(obj.getString("date").split("T")[0]) && Arrays.asList(iplTeams).indexOf(obj.getString("team-1")) > -1) {
//                        matchesIndices.add(i);
//                    }
//                    if (dateResult.equals(obj.getString("date").split("T")[0]) && Arrays.asList(iplTeams).indexOf(obj.getString("team-1")) > -1) {
//                        matchesIndicesResult.add(i);
//                    }
//                }
//                //Settings up dates
//                todayTextView.setText(String.format("Matches on %s", date.substring(8) + "-" + date.substring(5, 7) + "-" + date.substring(0, 4)));
//                resultTextView.setText(String.format("Result of matches on %s", dateResult.substring(8) + "-" + dateResult.substring(5, 7) + "-" + dateResult.substring(0, 4)));
//                //upcoming matches
//                if (matchesIndices.size() == 0) {
//                    matchALayout.setVisibility(View.GONE);
//                    matchBLayout.setVisibility(View.GONE);
//                    TextView upcomingMatchesTextView = findViewById(R.id.todayTextView);
//                    upcomingMatchesTextView.setText("No Matches today");
//                } else if (matchesIndices.size() == 1) {
//                    matchALayout.setVisibility(View.VISIBLE);
//                    matchBLayout.setVisibility(View.GONE);
//                    matchATeamA.setImageDrawable(getResources().obtainTypedArray(R.array.ipl_eam_logos).getDrawable(Arrays.asList(iplTeams).indexOf(array.getJSONObject(matchesIndices.get(0)).getString("team-1"))));
//                    matchATeamB.setImageDrawable(getResources().obtainTypedArray(R.array.ipl_eam_logos).getDrawable(Arrays.asList(iplTeams).indexOf(array.getJSONObject(matchesIndices.get(0)).getString("team-2"))));
//                } else if (matchesIndices.size() == 2) {
//                    matchALayout.setVisibility(View.VISIBLE);
//                    matchBLayout.setVisibility(View.VISIBLE);
//                    matchATeamA.setImageDrawable(getResources().obtainTypedArray(R.array.ipl_eam_logos).getDrawable(Arrays.asList(iplTeams).indexOf(array.getJSONObject(matchesIndices.get(0)).getString("team-1"))));
//                    matchATeamB.setImageDrawable(getResources().obtainTypedArray(R.array.ipl_eam_logos).getDrawable(Arrays.asList(iplTeams).indexOf(array.getJSONObject(matchesIndices.get(0)).getString("team-2"))));
//                    matchBTeamA.setImageDrawable(getResources().obtainTypedArray(R.array.ipl_eam_logos).getDrawable(Arrays.asList(iplTeams).indexOf(array.getJSONObject(matchesIndices.get(1)).getString("team-1"))));
//                    matchBTeamB.setImageDrawable(getResources().obtainTypedArray(R.array.ipl_eam_logos).getDrawable(Arrays.asList(iplTeams).indexOf(array.getJSONObject(matchesIndices.get(1)).getString("team-2"))));
//                }
//                //result
//
//                String unique_id = "";
//                String baseURL = "https://cricapi.com/api/cricketScore?apikey=cPGZrTye3sQLQhhivz1SkWacbqh1&unique_id=";
//                String resultOfMatch;
//                if (matchesIndicesResult.size() == 0) {
//                    matchALayout.setVisibility(View.GONE);
//                    matchBLayout.setVisibility(View.GONE);
//                    TextView resultMatchesTextView = findViewById(R.id.resultTextView);
//                    resultMatchesTextView.setText("No Matches for result");
//                } else if (matchesIndicesResult.size() == 1) {
//                    winnerALayout.setVisibility(View.VISIBLE);
//                    winnerBLayout.setVisibility(View.GONE);
//                    resultMatch = "A";
//                    resultProgressBar.setVisibility(View.VISIBLE);
//                    unique_id = array.getJSONObject(matchesIndicesResult.get(0)).getString("unique_id");
//                    resultOfMatch = quickDatabase.getString(unique_id);
//                    if (resultOfMatch.isEmpty()) {
//                        new GetScore(unique_id, resultMatch).execute(baseURL + unique_id);
//                    } else {
//                        scoreATextView.setText(Html.fromHtml(resultOfMatch));
//                        resultProgressBar.setVisibility(View.GONE);
//                    }
//                    //                matchATeamA.setImageDrawable(getResources().obtainTypedArray(R.array.ipl_eam_logos).getDrawable(Arrays.asList(iplTeams).indexOf(array.getJSONObject(matchesIndicesResult.get(0)).getString("team-1"))));
//                    //                matchATeamB.setImageDrawable(getResources().obtainTypedArray(R.array.ipl_eam_logos).getDrawable(Arrays.asList(iplTeams).indexOf(array.getJSONObject(matchesIndicesResult.get(0)).getString("team-2"))));
//                } else if (matchesIndicesResult.size() == 2) {
//                    winnerALayout.setVisibility(View.VISIBLE);
//                    winnerBLayout.setVisibility(View.VISIBLE);
//                    resultProgressBar.setVisibility(View.VISIBLE);
//                    resultMatch = "A";
//                    unique_id = array.getJSONObject(matchesIndicesResult.get(0)).getString("unique_id");
//                    resultOfMatch = quickDatabase.getString(unique_id);
//                    if (resultOfMatch.isEmpty()) {
//                        new GetScore(unique_id, resultMatch).execute(baseURL + unique_id);
//                    } else {
//                        scoreATextView.setText(Html.fromHtml(resultOfMatch));
//                        resultProgressBar.setVisibility(View.GONE);
//                    }
//                    resultMatch = "B";
//                    unique_id = array.getJSONObject(matchesIndicesResult.get(1)).getString("unique_id");
//                    resultOfMatch = quickDatabase.getString(unique_id);
//                    if (resultOfMatch.isEmpty()) {
//                        new GetScore(unique_id, resultMatch).execute(baseURL + unique_id);
//                    } else {
//                        scoreBTextView.setText(Html.fromHtml(resultOfMatch));
//                        resultProgressBar.setVisibility(View.GONE);
//                    }
//                    //                matchATeamA.setImageDrawable(getResources().obtainTypedArray(R.array.ipl_eam_logos).getDrawable(Arrays.asList(iplTeams).indexOf(array.getJSONObject(matchesIndicesResult.get(0)).getString("team-1"))));
//                    //                matchATeamB.setImageDrawable(getResources().obtainTypedArray(R.array.ipl_eam_logos).getDrawable(Arrays.asList(iplTeams).indexOf(array.getJSONObject(matchesIndicesResult.get(0)).getString("team-2"))));
//                    //                matchBTeamA.setImageDrawable(getResources().obtainTypedArray(R.array.ipl_eam_logos).getDrawable(Arrays.asList(iplTeams).indexOf(array.getJSONObject(matchesIndicesResult.get(1)).getString("team-1"))));
//                    //                matchBTeamB.setImageDrawable(getResources().obtainTypedArray(R.array.ipl_eam_logos).getDrawable(Arrays.asList(iplTeams).indexOf(array.getJSONObject(matchesIndicesResult.get(1)).getString("team-2"))));
//                }
//
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } else {
//            iplLayout.setVisibility(View.GONE);
//
//        }

    }

    public class GetScore extends AsyncTask<String, Void, String> {

        String uniqueID;
        String matchNumber;

        GetScore(String id, String match) {
            this.uniqueID = id;
            this.matchNumber = match;
        }

        @Override
        protected String doInBackground(String... urls) {

            String output = "";
            URL url;
            HttpURLConnection httpURLConnection;
            try {
                url = new URL(urls[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream in = httpURLConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data != -1) {
                    char current = (char) data;
                    output += current;
                    data = reader.read();
                }
                return output;
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
//            resultProgressBar.setVisibility(View.GONE);
//            try {
//                JSONObject jsonObject = new JSONObject(s);
//                String resultAndScore = "";
//               if (this.matchNumber.equals("A")) {
//                   resultAndScore = String.format("<b>%s</b><br>%s",
//                           jsonObject.getString("stat"),
//                           jsonObject.getString("score"));
//                   scoreATextView.setText(Html.fromHtml(resultAndScore));
//                   quickDatabase.putString(this.uniqueID, resultAndScore);
//                } else if (this.matchNumber.equals("B")) {
//                   resultAndScore = String.format("<b>%s</b><br>%s",
//                           jsonObject.getString("stat"),
//                           jsonObject.getString("score"));
//                   scoreBTextView.setText(Html.fromHtml(resultAndScore));
//                   quickDatabase.putString(this.uniqueID, resultAndScore);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                String errMessage = "Couldn't fetch result!";
//                scoreATextView.setText(errMessage);
//                scoreBTextView.setText(errMessage);
////                Toast.makeText(MainActivity.this, errMessage, Toast.LENGTH_SHORT).show();
//            }
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings){
            startActivity(SettingsActivity.start(this));
//            setUpUserDetails(NOT_FIRST_USE);
        } else if (id == R.id.action_privacy_policy){
            startActivity(PrivacyActivity.start(this));
//            String privacy_text = getString(R.string.privacy_policy_text);
//            Spanned text_html = Html.fromHtml(privacy_text);
//            new AlertDialog.Builder(this)
//                    .setTitle(R.string.action_privacy)
//                    .setMessage(text_html)
//                    .setPositiveButton(R.string.okay, null)
//                    .show();
        } else if (id == R.id.action_share) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text));
            String title = getResources().getString(R.string.share_chooser_title);
            startActivity(Intent.createChooser(shareIntent, title));
        }
        return true;
    }

    private void setUpUserDetails(final int calling_function){
        View myLayout = View.inflate(this, R.layout.login_page_layout, null);
        final EditText user = myLayout.findViewById(R.id.ed_emd_id);
        final EditText pwd = myLayout.findViewById(R.id.ed_pwd);
        if (!quickDatabase.getString(getString(R.string.key_username)).equals("")){
            user.setText(quickDatabase.getString(getString(R.string.key_username)));
            pwd.setText(quickDatabase.getString(getString(R.string.key_password)));
        }
        new AlertDialog.Builder(this)
                .setView(myLayout)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        quickDatabase.putString(getString(R.string.key_username), user.getText().toString());
                        quickDatabase.putString(getString(R.string.key_password), pwd.getText().toString());
                        Toast.makeText(getApplicationContext(), getString(R.string.username_pwd_set_toast_alert_first_time), Toast.LENGTH_SHORT).show();
                        if (calling_function == FIRST_USE){
                            startActivity(WebActivity.start(MainActivity.this));
                        }
                    }
                })
                .show();
    }


    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "AttendanceChannel";
            String description = "Miscellaneous notifications of the app.";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(getString(R.string.CHANNEL_ID), name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = this.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
