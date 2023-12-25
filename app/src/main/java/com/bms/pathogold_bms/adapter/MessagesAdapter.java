package com.bms.pathogold_bms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bms.pathogold_bms.R;
import com.bms.pathogold_bms.model.chat.Messages;
import com.bms.pathogold_bms.utility.AllKeys;
import com.bms.pathogold_bms.utility.CommonMethods;

import java.util.ArrayList;
import java.util.Objects;

public class MessagesAdapter extends RecyclerView.Adapter {
    Context context;
    ArrayList<Messages> messagesArrayList;
    String mSenderID;
    int ITEM_SEND=1;
    int ITEM_RECIEVE=2;

    public MessagesAdapter(Context context, ArrayList<Messages> messagesArrayList,String mSenderID) {
        this.context = context;
        this.messagesArrayList = messagesArrayList;
        this.mSenderID = mSenderID;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==ITEM_SEND)
        {
            View view= LayoutInflater.from(context).inflate(R.layout.senderchatlayout,parent,false);
            return new SenderViewHolder(view);
        }
        else
        {
            View view= LayoutInflater.from(context).inflate(R.layout.recieverchatlayout,parent,false);
            return new RecieverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Messages messages=messagesArrayList.get(position);
        if(holder.getClass()==SenderViewHolder.class)
        {
            SenderViewHolder viewHolder=(SenderViewHolder)holder;
            viewHolder.textViewmessaage.setText(messages.getMessage());
            viewHolder.timeofmessage.setText(messages.getCurrenttime());
        }
        else
        {
            RecieverViewHolder viewHolder=(RecieverViewHolder)holder;
            viewHolder.textViewmessaage.setText(messages.getMessage());
            viewHolder.timeofmessage.setText(messages.getCurrenttime());
        }
    }


    @Override
    public int getItemViewType(int position) {
        Messages messages=messagesArrayList.get(position);
        if(Objects.requireNonNull(CommonMethods.Companion.getPrefrence(context, AllKeys.Companion.getGCM_TOKEN())).equals(messages.getSenderId()))
        {
            return  ITEM_SEND;
        }
        else
        {
            return ITEM_RECIEVE;
        }
    }

    @Override
    public int getItemCount() {
        return messagesArrayList.size();
    }


    class SenderViewHolder extends RecyclerView.ViewHolder
    {

        TextView textViewmessaage;
        TextView timeofmessage;


        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewmessaage=itemView.findViewById(R.id.sendermessage);
            timeofmessage=itemView.findViewById(R.id.timeofmessage);
        }
    }

    class RecieverViewHolder extends RecyclerView.ViewHolder
    {

        TextView textViewmessaage;
        TextView timeofmessage;


        public RecieverViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewmessaage=itemView.findViewById(R.id.sendermessage);
            timeofmessage=itemView.findViewById(R.id.timeofmessage);
        }
    }
}
