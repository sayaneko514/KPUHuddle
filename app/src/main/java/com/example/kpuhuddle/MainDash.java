package com.example.kpuhuddle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

public class MainDash extends AppCompatActivity {

    NavigationView nav;
    DrawerLayout drawerLay;
    ActionBarDrawerToggle toggle;
    RecyclerView recyclerView;
    myAdapter adapter;
    SharedPreferences preferences;
    ArrayList<Event> list = new ArrayList<Event>();

    private FirebaseUser user;
    private DatabaseReference reference, reference_event;
    private String userid;
    private int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_dash);

        recyclerView = findViewById(R.id.dashRCView);
        preferences = this.getSharedPreferences("My_Pref", MODE_PRIVATE);

        nav = findViewById(R.id.navmenu);
        drawerLay = findViewById(R.id.drawer);

        toggle = new ActionBarDrawerToggle(this,drawerLay,R.string.open,R.string.close);
        drawerLay.addDrawerListener(toggle);
        toggle.syncState();

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference_event = FirebaseDatabase.getInstance().getReference().child("Events");
        userid = user.getUid();

        reference.child(userid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userInfo = snapshot.getValue(User.class);

                if(userInfo != null)
                {
                    NavigationView navigationView = findViewById(R.id.navmenu);
                    View headerView = navigationView.getHeaderView(0);
                    TextView navfirstName = headerView.findViewById(R.id.NavFirstName);
                    TextView navlastName = headerView.findViewById(R.id.NavLastName);
                    TextView navEmail = headerView.findViewById(R.id.NavEmail);

                    navfirstName.setText(userInfo.firstName);
                    navlastName.setText(userInfo.lastName);
                    navEmail.setText(userInfo.email);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                Toast.makeText(MainDash.this,"Something wrong with our server, please try again later", Toast.LENGTH_LONG).show();
            }
        });

        DatabaseReference mOptionReference = FirebaseDatabase.getInstance().getReference().child("Events");
        ValueEventListener optionListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                ArrayList<String> names = new ArrayList<>();
                for(DataSnapshot name : dataSnapshot.getChildren()) {
                    names.add(name.getKey());
                    Event option = name.getValue(Event.class);
                    try
                    {
                        if(dateCompare(option.getEventDate()) == true)
                        {
                            list.add(option);
                            counter++;
                        }
                    }
                    catch (ParseException e)
                    {
                        e.printStackTrace();
                    }
                }
                TextView totalEvent = findViewById(R.id.dashEventCounter);
                totalEvent.setText(counter+" events happening right now!");
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("LoadModel:onCancelled", databaseError.toException());
            }
        };
        mOptionReference.addValueEventListener(optionListener);

        getList();

        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
            {
                switch(menuItem.getItemId())
                {
                    case R.id.menuMainDash:
                        drawerLay.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.menuCreate:
                        startActivity(new Intent(MainDash.this, Create.class));
                        drawerLay.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.menuContactUs:
                        Toast.makeText(MainDash.this, "Please call phone number 604-599-2116, or email at servicedesk@kpu.ca", Toast.LENGTH_LONG).show();
                        drawerLay.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.menuLogout:
                        drawerLay.closeDrawer(GravityCompat.START);
                        startActivity(new Intent(MainDash.this, Login.class));
                        break;
                }
                return true;
            }
        });
    }
    @Override
    public void onStart()
    {
        super.onStart();

    }

    @Override
    public void onStop()
    {
        super.onStop();

    }

    private void getList()
    {
        String mSortSetting = preferences.getString("Sort", "ascending");

        if (mSortSetting.equals("ascending")){
            Collections.sort(list, Event.By_NAME_ASCENDING);
        }
        else if (mSortSetting.equals("descending")){
            Collections.sort(list, Event.By_NAME_DESCENDING);
        }

        else if (mSortSetting.equals("tascending")){
            Collections.sort(list, Event.By_TYPE_ASCENDING);
        }

        else if (mSortSetting.equals("tdescending")){
            Collections.sort(list, Event.By_TYPE_DESCENDING);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new myAdapter(this, list);
        recyclerView.setAdapter(adapter);
    }

    public ArrayList<HashMap<String, Object>> recArrayList(DataSnapshot snapshot){

        ArrayList<HashMap<String, Object>> list = new ArrayList<>();

        if (snapshot == null){

            return list;
        }

        Object fieldsObj = new Object();

        HashMap fldObj;

        for (DataSnapshot shot : snapshot.getChildren())
        {

            try{
                fldObj = (HashMap)shot.getValue(fieldsObj.getClass());

            }
            catch (Exception ex)
            {
                continue;
            }

            fldObj.put("recKeyID", shot.getKey());

            list.add(fldObj);
        }

        return list;
    }

    private String makeDateString(int day, int month, int year)
    {
        return getMonthFormat(month) + "/" + modDayFormat(day)+ "/" + year;
    }

    private String modDayFormat(int day)
    {
        if (day == 1)
            return "01";
        else if (day == 2)
            return "02";
        else if (day == 3)
            return "03";
        else if (day == 4)
            return "04";
        else if (day == 5)
            return "05";
        else if (day == 6)
            return "06";
        else if (day == 7)
            return "07";
        else if (day == 8)
            return "08";
        else if (day == 9)
            return "09";
        else
        {
            String x = String.valueOf(day);
            return x;
        }
    }

    private String getMonthFormat(int month)
    {
        if (month == 1)
            return "JAN";
        else if (month == 2)
            return "FEB";
        else if (month == 3)
            return "MAR";
        else if (month == 4)
            return "APR";
        else if (month == 5)
            return "MAY";
        else if (month == 6)
            return "JUN";
        else if (month == 7)
            return "JUL";
        else if (month == 8)
            return "AUG";
        else if (month == 9)
            return "SEP";
        else if (month == 10)
            return "OCT";
        else if (month == 11)
            return "NOV";
        else if (month == 12)
            return "DEC";
        else
            return "Null";
    }

    private String getToday()
    {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        return makeDateString(day, month, year);
    }

    public boolean dateCompare(String date) throws ParseException
    {
        SimpleDateFormat formatter = new SimpleDateFormat("MMM/dd/yyyy");
        Date d1 = formatter.parse(date);
        Date d2 = formatter.parse(getToday());
        if (d1.compareTo(d2) >= 0)
            return true;
        else
            return false;
    }
}