package com.example.datecount;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        //begin logic part
        TextView textView = findViewById(R.id.dateCount);
        String getDateDiff = this.getDateDiff();
        textView.setText(getDateDiff);

        @SuppressLint("HandlerLeak") final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String text = bundle.getString("key");
                TextView textView = findViewById(R.id.dateCount);
                textView.setText(text);
            }
        };

        checkBirthday();
        checkAnniversary();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Long endTime = System.currentTimeMillis() + Integer.MAX_VALUE;
                int count = 0;
                while (endTime > System.currentTimeMillis()) {
                    synchronized (this) {
                        try {
                            wait(1000);
                            count++;

                            Message message = handler.obtainMessage();
                            Bundle bundle = new Bundle();
                            String result = getDateDiff();

                            bundle.putCharSequence("key", result);
                            message.setData(bundle);
                            handler.sendMessage(message);

                            Log.i("ThreadLog", "" + count);
                        } catch (Exception e) {

                        }
                    }
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the about action
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //date difference from begin date
    public String getDateDiff() {
         String dateStart = "09/29/2018 16:29:00";

        //HH converts hour in 24 hours format (0-23), day calculation
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

        Date d1 = null;
        Date d2 = null;
        String result = "";
        try {
            d1 = format.parse(dateStart);
            d2 = new Date();

            //in milliseconds
            long diff = d2.getTime() - d1.getTime();

            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);

            System.out.print(diffDays + " days, ");
            System.out.print(diffHours + " hours, ");
            System.out.print(diffMinutes + " minutes, ");
            System.out.print(diffSeconds + " seconds.");
            result = "\t\t\t\t\t\t\t\t" + diffDays + " ngày\n" + diffHours + " giờ " + diffMinutes + " phút " + diffSeconds + " giây";

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    //check birthday
    private void checkBirthday() {
        String sDate1="01/06/2001";
        Date birthday = new Date();
        String wishes = "";
        try {
            birthday =new SimpleDateFormat("dd/MM/yyyy").parse(sDate1);
        } catch (Exception e) {

        }
        Calendar calendar = Calendar.getInstance();
        Calendar calendarBirth = Calendar.getInstance();
        Date today = new Date();
        calendar.setTime(today);
        calendarBirth.setTime(birthday);

        if ((calendar.get(Calendar.DATE) == calendarBirth.get(Calendar.DATE)) && (calendar.get(Calendar.MONTH) == calendarBirth.get(Calendar.MONTH))) {
            int yearDiff = calendar.get(Calendar.YEAR) - calendarBirth.get(Calendar.YEAR);
            wishes = "Chúc mừng sinh nhật thứ " + yearDiff +  " hihi :>";
            //play sound
            MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.combo);
            mp.start();
            //show alert
            showAlertDialog(wishes);
        }
    }

    private void checkAnniversary() {
        String sDate1="29/09/2018";
        Date start = new Date();
        String wishes = "";
        try {
            start =new SimpleDateFormat("dd/MM/yyyy").parse(sDate1);
        } catch (Exception e) {

        }
        Calendar calendar = Calendar.getInstance();
        Calendar calendarBirth = Calendar.getInstance();
        Date today = new Date();
        calendar.setTime(today);
        calendarBirth.setTime(start);

        if (calendar.get(Calendar.DATE) == calendarBirth.get(Calendar.DATE))  {
            //Toast.makeText(getApplicationContext(), "True date", Toast.LENGTH_LONG).show();
            int yearDiff = calendar.get(Calendar.YEAR) - calendarBirth.get(Calendar.YEAR);
            int monthDiff = calendar.get(Calendar.MONTH) - calendarBirth.get(Calendar.MONTH);
            int testMonthDiff = monthDiff;
            if (monthDiff  < 0) {
                monthDiff = 12 + monthDiff;
            }

            if (monthDiff % 12 == 0) {
                wishes = "Hôm nay là tròn " + yearDiff +  " năm yêu nhau nè :>";
            } else {
                if (yearDiff > 1) {
                    if (testMonthDiff > 0) {
                        wishes = "Hôm nay là tròn " + yearDiff +  " năm " + monthDiff + " tháng yêu nhau nè :>";
                    } else {
                        wishes = "Hôm nay là tròn " + (yearDiff - 1) +  " năm " + monthDiff + " tháng yêu nhau nè :>";
                    }
                } else  {
                    wishes = "Hôm nay là tròn " + monthDiff + " tháng yêu nhau nè :>";
                }

            }
            //play sound
            MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.combo);
            mp.start();
            //show alert
            showAlertDialog(wishes);
        }
    }

    public void showAlertDialog(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Hi :>");
        builder.setMessage(message + " Nhớ hong? :>");
        builder.setCancelable(false);
        builder.setPositiveButton("Nhớ chứ :>", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Toast.makeText(MainActivity.this, "Không thoát được", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Quên òi :<", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }
}
