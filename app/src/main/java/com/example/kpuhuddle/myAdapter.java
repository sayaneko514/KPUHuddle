package com.example.kpuhuddle;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class myAdapter extends RecyclerView.Adapter<myHolder> implements Filterable {

    Context c;
    ArrayList<Event> events, filterList;
    CustomFilter filter;
    AuditLog log = new AuditLog();

    private FirebaseUser user;
    private DatabaseReference aLog;
    private String userid;


    public myAdapter(Context c, ArrayList<Event> events)
    {
        this.c = c;
        this.events = events;
        this.filterList = events;
    }

    @NonNull
    @Override
    public myHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_row, parent, false);
        return new myHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myHolder holder, int position)
    {
        holder.eName.setText(events.get(position).getEventName());
        String fullDate = events.get(position).getEventDate();
        String getMonth = fullDate.substring(0, 3);
        String getDay = fullDate.substring(4, 6);
        holder.eMonth.setText(getMonth);
        holder.eDay.setText(getDay);
        holder.eLocation.setText(events.get(position).getEventLocation());
        holder.eCount.setText(events.get(position).getEventCount());
        Glide.with(holder.eImg.getContext()).load(events.get(position).getpUrl()).into(holder.eImg);

        holder.setItemClickListener(new ItemClickListener()
        {
            @Override
            public void onItemClickListener(View v, int position)
            {
                String dName = events.get(position).getEventName();
                String dLocation = events.get(position).getEventLocation();
                String dCount = events.get(position).getEventCount();
                String dPurl = events.get(position).getpUrl();
                String dDesc = events.get(position).getEventDesc();
                String dDate = events.get(position).getEventDate();
                String dHost = events.get(position).getEventHost();
                String dTime = events.get(position).getEventTime();
                String dID = events.get(position).getEventID();

                logEventClick(dID);

                Intent intent = new Intent(c, EventDetail.class);

                intent.putExtra("item_eventName", dName);
                intent.putExtra("item_eventLocation", dLocation);
                intent.putExtra("item_eventDate",dDate);
                intent.putExtra("item_eventTime", dTime);
                intent.putExtra("item_eventHost", dHost);
                intent.putExtra("item_eventDesc", dDesc);
                intent.putExtra("item_eventCount", dCount);
                intent.putExtra("item_eventPurl", dPurl);
                intent.putExtra("item_eventID",dID);
                c.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null){
            filter = new CustomFilter(filterList, this);

        }

        return filter;
    }

    public void logEventClick(String eventID)
    {
        aLog = FirebaseDatabase.getInstance().getReference().child("Logs");
        long currentTime = System.currentTimeMillis();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String logTimeStamp = dateFormat.format(currentTime);
        user = FirebaseAuth.getInstance().getCurrentUser();
        userid = user.getUid();
        String logDesc = "User Clicks On Event " + eventID;
        log.setLogDesc(logDesc);
        log.setLogTimeStamp(logTimeStamp);
        log.setLogUser(userid);
        log.setLogID("LOG"+Long.toString(System.currentTimeMillis()));
        aLog.child(log.getLogID()).setValue(log);
    }
}
