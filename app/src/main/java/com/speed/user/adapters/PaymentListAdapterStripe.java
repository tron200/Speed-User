package com.speed.user.adapters;

/**
 * Created by Amit on 11/02/17.
 */

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.speed.user.R;
import com.speed.user.models.CardDetails;
import com.speed.user.utills.MyTextView;

import java.util.List;


public class PaymentListAdapterStripe extends RecyclerView.Adapter<PaymentListAdapterStripe.MyViewHolder> {


    private List<CardDetails> cardDetailsList;

    public PaymentListAdapterStripe(List<CardDetails> cardDetailsList) {
        this.cardDetailsList = cardDetailsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.payment_list_item_strpe, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        CardDetails cardDetails = cardDetailsList.get(position);
        try {
            if (cardDetails.getBrand().equalsIgnoreCase("MASTERCARD")) {
                holder.paymentTypeImg.setImageResource(R.drawable.credit_card);
            } else if (cardDetails.getBrand().equalsIgnoreCase("MASTRO")) {
                holder.paymentTypeImg.setImageResource(R.drawable.visa_payment_icon);
            } else if (cardDetails.getBrand().equalsIgnoreCase("Visa")) {
                holder.paymentTypeImg.setImageResource(R.drawable.visa);
            }
            holder.cardNumber.setText("xxxx - xxxx - xxxx - " + cardDetailsList.get(position).getLast_four());


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, year, genre;
        public ImageView paymentTypeImg, tickImg;
        public MyTextView cardNumber;

        public MyViewHolder(View view) {
            super(view);
            paymentTypeImg = itemView.findViewById(R.id.paymentTypeImg);
            cardNumber = itemView.findViewById(R.id.cardNumber);
            tickImg = itemView.findViewById(R.id.img_tick);
        }
    }
}




