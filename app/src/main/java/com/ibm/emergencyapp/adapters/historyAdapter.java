package com.ibm.emergencyapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ibm.emergencyapp.HomeActivity;
import com.ibm.emergencyapp.NotificationActivity;
import com.ibm.emergencyapp.R;
import com.ibm.emergencyapp.util.NotificationItems;

import java.util.ArrayList;

/**
 * Created by Nigel on 06/25/2018.
 */

public class historyAdapter extends RecyclerView.Adapter<historyAdapter.ViewHolder> {
    private ArrayList<NotificationItems> dataItems = new ArrayList<>();
    public Context contextHistory;
    public static Boolean fromHistory = false;
    public static String date = "";
    public static String description = "";


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, date;
        Button exit;

        public ViewHolder(View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            exit = itemView.findViewById(R.id.notification_close_button);
            title = itemView.findViewById(R.id.notification_title);
            description = itemView.findViewById(R.id.notification_description);
        }
    }

    public historyAdapter(ArrayList<NotificationItems> items, Context context) {
        dataItems = items;
        contextHistory = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public historyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_view, parent, false);
        return new historyAdapter.ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.exit.setVisibility(View.GONE);
        holder.date.setVisibility(View.VISIBLE);
        holder.date.setText(dataItems.get(position).date);
        holder.title.setText(dataItems.get(position).name);
        holder.description.setText(dataItems.get(position).discription);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromHistory = true;
                Intent intent = new Intent(contextHistory, NotificationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("NOTIFICATION_ID", dataItems.get(position).id);
                date = dataItems.get(position).date;
                description = dataItems.get(position).discription;
                contextHistory.startActivity(intent);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dataItems.size();
    }
}