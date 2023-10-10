package com.example.kpuhuddle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.HashMap;

public class EventDetail extends AppCompatActivity implements View.OnClickListener
{

    private TextView detaileName, detaileDate, detailLocation, detaileTime, detaileHost, detaileDesc, detaileCount;
    private ImageView detailePurl, detailCancelBtn;
    private Button detailJoinBtn, detailQuitBtn, detailUpdateBtn;
    AuditLog log = new AuditLog();

    private DatabaseReference reference, reference_event, aLog;
    private FirebaseUser user;
    private String userid;

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        detaileName = findViewById(R.id.detailEventName);
        detailLocation = findViewById(R.id.detailEventLocation);
        detaileDate = findViewById(R.id.detailEventDate);
        detaileTime = findViewById(R.id.detailEventTime);
        detaileHost = findViewById(R.id.detailEventHost);
        detaileDesc = findViewById(R.id.detailEventDesc);
        detaileCount =findViewById(R.id.detailCount);
        detailePurl = findViewById(R.id.detailEventPurl);

        detailCancelBtn = findViewById(R.id.detailCancelBtn);
        detailCancelBtn.setOnClickListener(this);

        detailJoinBtn = findViewById(R.id.detailJoinBtn);
        detailJoinBtn.setOnClickListener(this);

        detailQuitBtn = findViewById(R.id.detailQuitBtn);
        detailQuitBtn.setOnClickListener(this);

        detailUpdateBtn = findViewById(R.id.detailUpdateBtn);
        detailUpdateBtn.setOnClickListener(this);

        Intent intent = getIntent();
        String mName = intent.getStringExtra("item_eventName");
        String mLocation = intent.getStringExtra("item_eventLocation");
        String mDate = intent.getStringExtra("item_eventDate");
        String mTime = intent.getStringExtra("item_eventTime");
        String mHost = intent.getStringExtra("item_eventHost");
        String mDesc = intent.getStringExtra("item_eventDesc");
        String mCount = intent.getStringExtra("item_eventCount");
        String mPurl = intent.getStringExtra("item_eventPurl");
        String mID = intent.getStringExtra("item_eventID");
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(mHost);
        reference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                User hostName= new User();
                hostName.setFirstName(snapshot.getValue(User.class).getFirstName());
                hostName.setLastName(snapshot.getValue(User.class).getLastName());
                detaileHost.setText(hostName.getFirstName()+" "+hostName.getLastName());
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });

        reference_event = FirebaseDatabase.getInstance().getReference().child("Events").child(mID);

        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(mHost))
        {
            detailJoinBtn.setVisibility(View.GONE);
            detailQuitBtn.setVisibility(View.GONE);
            detailUpdateBtn.setVisibility(View.VISIBLE);
        }
        else {
            reference_event.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()) {
                        detailJoinBtn.setVisibility(View.VISIBLE);
                        detailQuitBtn.setVisibility(View.GONE);
                        detailUpdateBtn.setVisibility(View.GONE);
                    } else {
                        detailJoinBtn.setVisibility(View.GONE);
                        detailQuitBtn.setVisibility(View.VISIBLE);
                        detailUpdateBtn.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        detaileName.setText(mName);
        detailLocation.setText(mLocation);
        detaileCount.setText(mCount);
        detaileDesc.setText(mDesc);
        detaileDate.setText(mDate);
        detaileTime.setText(mTime);
        Glide.with(this).load(mPurl).into(detailePurl);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.detailCancelBtn:
                startActivity(new Intent(EventDetail.this, MainDash.class));
                break;
            case R.id.detailQuitBtn:
                quitEvent();
                break;
            case R.id.detailJoinBtn:
                joinEvent();
                break;
            case R.id.detailUpdateBtn:
                updateEvent();
                break;
        }
    }

    private void joinEvent() {
        Intent intent = getIntent();
        String joinCount = intent.getStringExtra("item_eventCount");
        String eventID = intent.getStringExtra("item_eventID");
        reference_event = FirebaseDatabase.getInstance().getReference().child("Events").child(eventID);
        aLog = FirebaseDatabase.getInstance().getReference().child("Logs");
        int counter = Integer.parseInt(joinCount);
        counter++;
        String updateCount = Integer.toString(counter);
        HashMap hashMap = new HashMap();
        hashMap.put("eventCount", updateCount);

        reference_event.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task)
            {
                if (task.isSuccessful())
                {
                    reference_event.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue("true").addOnCompleteListener(new OnCompleteListener<Void>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(EventDetail.this, "Event joined successful", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }startActivity(new Intent(EventDetail.this, MainDash.class));
            }
        });
        logEventJoin(eventID);
    }

    private void quitEvent() {
        Intent intent = getIntent();
        String joinCount = intent.getStringExtra("item_eventCount");
        String eventID = intent.getStringExtra("item_eventID");
        reference_event = FirebaseDatabase.getInstance().getReference().child("Events").child(eventID);
        aLog = FirebaseDatabase.getInstance().getReference().child("Logs");
        int counter = Integer.parseInt(joinCount);
        counter--;
        String updateCount = Integer.toString(counter);
        HashMap hashMap = new HashMap();
        hashMap.put("eventCount", updateCount);
        reference_event.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task)
            {
                if (task.isSuccessful())
                {
                    reference_event.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(EventDetail.this, "Event quited", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }startActivity(new Intent(EventDetail.this, MainDash.class));

            }
        });
        logEventLeave(eventID);
    }

    private void updateEvent()
    {
        Intent intent = getIntent();
        String mName = intent.getStringExtra("item_eventName");
        String mLocation = intent.getStringExtra("item_eventLocation");
        String mDate = intent.getStringExtra("item_eventDate");
        String mTime = intent.getStringExtra("item_eventTime");
        String mHost = intent.getStringExtra("item_eventHost");
        String mDesc = intent.getStringExtra("item_eventDesc");
        String mID = intent.getStringExtra("item_eventID");

        Intent update = new Intent(EventDetail.this, Update.class);

        update.putExtra("item_eventName", mName);
        update.putExtra("item_eventLocation", mLocation);
        update.putExtra("item_eventDate", mDate);
        update.putExtra("item_eventTime", mTime);
        update.putExtra("item_eventHost", mHost);
        update.putExtra("item_eventDesc", mDesc);
        update.putExtra("item_eventID", mID);
        startActivity(update);
    }

    public void logEventJoin(String eventID)
    {
        long currentTime = System.currentTimeMillis();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String logTimeStamp = dateFormat.format(currentTime);
        user = FirebaseAuth.getInstance().getCurrentUser();
        userid = user.getUid();
        String logDesc = "User Joins Event " + eventID;
        log.setLogDesc(logDesc);
        log.setLogTimeStamp(logTimeStamp);
        log.setLogUser(userid);
        log.setLogID("LOG"+Long.toString(System.currentTimeMillis()));
        aLog.child(log.getLogID()).setValue(log);
    }

    public void logEventLeave(String eventID)
    {
        long currentTime = System.currentTimeMillis();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String logTimeStamp = dateFormat.format(currentTime);
        user = FirebaseAuth.getInstance().getCurrentUser();
        userid = user.getUid();
        String logDesc = "User Leaves Event " + eventID;
        log.setLogDesc(logDesc);
        log.setLogTimeStamp(logTimeStamp);
        log.setLogUser(userid);
        log.setLogID("LOG"+Long.toString(System.currentTimeMillis()));
        aLog.child(log.getLogID()).setValue(log);
    }
}