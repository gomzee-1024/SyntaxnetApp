package com.example.sysadmin.syntaxnetapp.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.sysadmin.syntaxnetapp.data.ChatMessage;
import com.example.sysadmin.syntaxnetapp.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by sysadmin on 26/7/16.
 */
public class ChatRecyclerViewAdapter extends RecyclerView.Adapter<ChatRecyclerViewAdapter.MyViewHolder> {

    ArrayList<ChatMessage> list;
    LayoutInflater inflater;
    Context context;

    public ChatRecyclerViewAdapter(Context context, ArrayList<ChatMessage> list){
        inflater = LayoutInflater.from(context);
        this.list=list;
        this.context= context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = inflater.inflate(R.layout.chat_item_msg_he,parent,false);
        MyViewHolder holder = new MyViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ChatMessage chatMessage;
        chatMessage = list.get(position);
        Log.d("isme", chatMessage.getMsg() + chatMessage.isItMe());
        if(chatMessage.isItMe()){
            holder.root.setGravity(Gravity.RIGHT);
            holder.cv.setImageResource(R.drawable.account_circle1);
            Drawable drawable = context.getResources().getDrawable(R.drawable.my_chat_text_bg);
            holder.tv.setBackground(drawable);
            holder.tv.setText(chatMessage.getMsg());
        }else{
            holder.root.setGravity(Gravity.LEFT);
            holder.cv.setImageResource(R.drawable.account_circle);
            Drawable drawable = context.getResources().getDrawable(R.drawable.my_chat_text_bg1);
            holder.tv.setBackground(drawable);
            holder.tv.setText(chatMessage.getMsg());

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void addMsg(ChatMessage chatMessage){
        list.add(chatMessage);
        notifyItemInserted(list.size());
    }

    public void refreshlastmsg(ChatMessage chatMessage) {
        list.remove(list.size()-1);
        notifyItemRemoved(list.size());
        list.add(chatMessage);
        notifyItemInserted(list.size());
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        CircleImageView cv;
        TextView tv;
        LinearLayout root;
        public MyViewHolder(View itemView) {
            super(itemView);
            cv = (CircleImageView) itemView.findViewById(R.id.chat_circleImageView);
            tv = (TextView) itemView.findViewById(R.id.chat_msg_text_view);
            root = (LinearLayout) itemView.findViewById(R.id.linear_layout_chat_item);
           /* tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ConfirmProductDialog dialog = new ConfirmProductDialog();
                    dialog.show(context.getFragmentManager(),"yDialog");

                }
            });*/
        }
    }

}
