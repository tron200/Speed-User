package com.speed.user.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.speed.user.R;
import com.speed.user.chat.UserChatActivity;
import com.speed.user.models.UserChat;
import com.speed.user.utills.Utils;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.util.ArrayList;

public class UserChatListAdapter extends RecyclerView.Adapter<UserChatListAdapter.ViewHolder> {
    private ArrayList<UserChat> userChatArrayList;
    private Context context;
    private String from;


    public UserChatListAdapter(Context context, ArrayList<UserChat> userChatArrayList) {
        this.context = context;
        this.userChatArrayList = userChatArrayList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_list_adapter, parent, false);

        // view.setOnClickListener(CareTakerListActivity.myOnClickListener);

        ViewHolder myViewHolder = new ViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        TextView textViewName = holder.textViewName;
        ImageView imageView = holder.imageView;
        CardView card_view = holder.card_view;
        TextView textViewDateTime = holder.textViewDateTime;


        card_view.setTag(position);


        try {
            from = Utils.getDateFormate(userChatArrayList.get(position).getCreated_at());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        textViewDateTime.setText(from);

        textViewName.setText(userChatArrayList.get(position).getUserFirstName() + "  " +
                userChatArrayList.get(position).getUserLastName());

        if (userChatArrayList.get(position).getMessage() != null && userChatArrayList.get(position).getMessage() != "") {
            holder.textViewChat.setText(userChatArrayList.get(position).getMessage());
        }

       /* if (!userChatArrayList.get(position).getAvatar().equalsIgnoreCase("null"))
            Picasso.get().load(URLHelper.image_url_signature+userChatArrayList.get(position).getAvatar())
                    .memoryPolicy(MemoryPolicy.NO_CACHE).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imageView);
        else*/
        Picasso.get().load(userChatArrayList.get(position).getAvatar())
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .placeholder(R.drawable.ic_dummy_user)
                .error(R.drawable.ic_dummy_user)
                .into(imageView);
    }

    @Override
    public int getItemCount() {
        return userChatArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        TextView textViewName, textViewDateTime, textViewChat;
        CardView card_view;

        public ViewHolder(View itemView) {
            super(itemView);

            card_view = itemView.findViewById(R.id.card_view);
            this.textViewName = itemView.findViewById(R.id.textViewName);
            this.textViewDateTime = itemView.findViewById(R.id.textViewDateTime);
            this.textViewChat = itemView.findViewById(R.id.textViewChat);
            this.imageView = itemView.findViewById(R.id.imageView);
            card_view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            int pos = (int) v.getTag();
            String requestId = userChatArrayList.get(pos).getRequest_id();
            Intent intent = new Intent(context, UserChatActivity.class);
            intent.putExtra("requestId", requestId);
            intent.putExtra("providerId", userChatArrayList.get(pos).getProvider_id());
            intent.putExtra("userId", userChatArrayList.get(pos).getUser_id());
            intent.putExtra("userName", userChatArrayList.get(pos).getUserFirstName() + " " + userChatArrayList.get(pos).getUserLastName());
            context.startActivity(intent);

        }
    }
}
