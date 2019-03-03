package com.example.thekidself.chat.helper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.thekidself.R;

import java.util.ArrayList;

public class ThreadAdapter extends RecyclerView.Adapter<ThreadAdapter.ViewHolder> {

    private String code_user;
    private Context context;

    //Tag for tracking self message
    private int SELF = 786;

    //ArrayList of messages object containing all the messages in the thread
    private ArrayList<Message> messages;

    //Constructor
    public ThreadAdapter(Context context, ArrayList<Message> messages, String code_user){ //code_user - actual user of this device
        this.code_user = code_user;
        this.messages = messages;
        this.context = context;
    }

    //IN this method we are tracking the self message
    @Override
    public int getItemViewType(int position) {
        //getting message object of current position
        Message message = messages.get(position);

        //If its owner id is  equals to the logged in user id
        if (messages.get(position).getCode_user().equals(code_user)) {
            return SELF;
        }
        return position;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == SELF) {
            //Inflating the layout self
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_thread, parent, false);
        } else {
            //else inflating the layout others
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_thread_other, parent, false);
        }
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //Adding messages to the views
        Message message = messages.get(position);
        holder.textViewMessage.setText(message.getMessage());
        String text = message.getUsername() + ", " + message.getCreated_on();
        holder.textViewTime.setText(text);
    }


    @Override
    public int getItemCount() {
        return messages.size();
    }

    //Initializing views
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewMessage;
        public TextView textViewTime;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewMessage = (TextView) itemView.findViewById(R.id.textViewMessage);
            textViewTime = (TextView) itemView.findViewById(R.id.textViewTime);
        }
    }
}
