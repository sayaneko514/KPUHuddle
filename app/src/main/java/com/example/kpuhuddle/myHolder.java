package com.example.kpuhuddle;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class myHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    ImageView eImg;
    TextView eDay, eMonth, eName, eLocation, eCount;
    ItemClickListener itemClickListener;
    public myHolder(@NonNull View itemView)
    {
        super(itemView);
        eImg = (ImageView)itemView.findViewById(R.id.dashImage);
        eDay = (TextView)itemView.findViewById(R.id.dashDay);
        eMonth = (TextView)itemView.findViewById(R.id.dashMonth);
        eName = (TextView)itemView.findViewById(R.id.dashEventName);
        eLocation = (TextView)itemView.findViewById(R.id.dashLocation);
        eCount = (TextView)itemView.findViewById(R.id.dashCounter);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        this.itemClickListener.onItemClickListener(view, getLayoutPosition());
    }

    public void setItemClickListener(ItemClickListener ic)
    {
        this.itemClickListener = ic;
    }
}
