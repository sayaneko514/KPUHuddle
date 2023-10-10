package com.example.kpuhuddle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static java.sql.DriverManager.println;

public class Create extends AppCompatActivity implements View.OnClickListener{
    private DatePickerDialog datePickerDialog;
    private Button createEvent;
    private TextView selectTime, dateButton;
    private ImageView banner;
    private Spinner eventType;
    private EditText editTextEventName, editTextEventLocation,editTextEventDesc;
    int timeHour, timeMinute;
    private String userid;
    private FirebaseUser user;

    Event event = new Event();
    AuditLog log = new AuditLog();

    FirebaseDatabase rootNode;
    DatabaseReference reference, aLog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);


        initDatePicker();

        dateButton = findViewById(R.id.CreateDateBtn);
        dateButton.setText(getToday());

        selectTime = findViewById(R.id.CreateTime);
        selectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                TimePickerDialog timePickerDialog = new TimePickerDialog(Create.this, new TimePickerDialog.OnTimeSetListener()
                    {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int h, int m) {
                            timeHour = h;
                            timeMinute = m;
                            Calendar calendar2 = Calendar.getInstance();
                            calendar2.set(0, 0, 0, timeHour, timeMinute);
                            selectTime.setText(DateFormat.format("hh:mm aa", calendar2));
                        }
                    }, 12, 0,false
                );
                timePickerDialog.updateTime(timeHour,timeMinute);
                timePickerDialog.show();
            }
        });

        createEvent = findViewById(R.id.CreateBtn);
        createEvent.setOnClickListener(this);

        banner = findViewById(R.id.CreateBanner);
        banner.setOnClickListener(this);

        eventType = findViewById(R.id.CreateVenueType);
        ArrayAdapter arrayAdapter = ArrayAdapter.createFromResource(this, R.array.eventList,R.layout.color_spinner_layout);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        eventType.setAdapter(arrayAdapter);

        editTextEventName = findViewById(R.id.CreateEventName);
        editTextEventLocation = findViewById(R.id.CreateLocation);
        editTextEventDesc = findViewById(R.id.CreateDesc);

        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference().child("Events");

        user = FirebaseAuth.getInstance().getCurrentUser();
        userid = user.getUid();
    }

    private void initDatePicker()
    {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day)
            {
                month = month + 1;
                String date = makeDateString(day, month, year);
                dateButton.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;
        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
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

    public void openDatePicker(View view)
    {
        datePickerDialog.show();
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

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.CreateBtn:
                publishEvent();
                break;
            case R.id.CreateBanner:
                startActivity(new Intent(Create.this, MainDash.class));
                break;
        }
    }

    private void publishEvent()
    {
        final String eName = editTextEventName.getText().toString().trim();
        final String eLocation = editTextEventLocation.getText().toString().trim();
        final String eDesc = editTextEventDesc.getText().toString().trim();
        final String eTime = selectTime.getText().toString();
        final String eType = eventType.getSelectedItem().toString();
        final String eDate = dateButton.getText().toString();
        final String eHost = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String pUrl;
        if (eType.equals("Study Group"))
            pUrl = "https://firebasestorage.googleapis.com/v0/b/kpuhuddle.appspot.com/o/studygroup.jpg?alt=media&token=662688c1-6a33-4c22-8603-460553e21dd7";
        else if (eType.equals("Work Shop"))
            pUrl = "https://firebasestorage.googleapis.com/v0/b/kpuhuddle.appspot.com/o/workshop.jpg?alt=media&token=cc29a95e-d942-4d95-b3c3-f46e1ed5dbba";
        else if (eType.equals("Club Gathering"))
            pUrl = "https://firebasestorage.googleapis.com/v0/b/kpuhuddle.appspot.com/o/gathering.jpg?alt=media&token=a9ccf97a-10c0-485a-b33f-54599094b3e6";
        else if (eType.equals("Job Fair"))
            pUrl = "https://firebasestorage.googleapis.com/v0/b/kpuhuddle.appspot.com/o/jobfair.jpg?alt=media&token=76e0727b-acf9-48e3-95de-98264e2907b8";
        else
            pUrl = "Null";


        if (eName.isEmpty())
        {
            editTextEventName.setError("Event name cannot be blank!");
            editTextEventName.requestFocus();
            return;
        }

        if (eLocation.isEmpty())
        {
            editTextEventLocation.setError("Event location cannot be blank!");
            editTextEventLocation.requestFocus();
            return;
        }

        if (eDesc.isEmpty())
        {
            editTextEventDesc.setError("Event description cannot be blank!");
            editTextEventDesc.requestFocus();
            return;
        }

        event.setEventLocation(eLocation);
        event.setEventType(eType);
        event.setEventDate(eDate);
        event.setEventCount("1");
        event.setEventDesc(eDesc);
        event.setEventHost(eHost);
        event.setEventTime(eTime);
        event.setpUrl(pUrl);
        event.setEventName(eName);
        event.setEventID(Long.toString(System.currentTimeMillis())+eHost);

        reference.child(event.getEventID()).setValue(event).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    Toast.makeText(Create.this, "Event published!", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(Create.this, MainDash.class));
                }

                else
                {
                    Toast.makeText(Create.this, "Event publish failed, please try again", Toast.LENGTH_LONG).show();
                }

            }
        });
        logCreate(event.getEventID());
    }

    public void logCreate(String eventID)
    {
        long currentTime = System.currentTimeMillis();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String logTimeStamp = dateFormat.format(currentTime);
        String logDesc = "User Creates New Event " + eventID;
        log.setLogDesc(logDesc);
        log.setLogTimeStamp(logTimeStamp);
        log.setLogUser(userid);
        log.setLogID("LOG"+Long.toString(System.currentTimeMillis()));
        aLog.child(log.getLogID()).setValue(log);
    }
}