package com.speed.user.consts;

/**
 * Created by Amit on 11/02/17.
 */

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.speed.user.R;
import com.speed.user.activities.Payment;
import com.speed.user.models.CardDetails;
import com.speed.user.utills.MyTextView;

import java.util.List;

public class NewPaymentListAdapter extends ArrayAdapter<CardDetails> {

    List<CardDetails> list;

    Context context;

    Activity activity;

    public NewPaymentListAdapter(Context context, List<CardDetails> list, Activity activity) {
        super(context, R.layout.payment_list_item, list);
        this.context = context;
        this.list = list;
        this.activity = activity;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.payment_list_item_strpe, parent, false);
        ImageView paymentTypeImg = itemView.findViewById(R.id.paymentTypeImg);
        MyTextView cardNumber = itemView.findViewById(R.id.cardNumber);
        RadioButton radioButton = itemView.findViewById(R.id.radioButton);
        radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ((Payment) context).callRadio(position);
            }
        });
        try {
            if (list.get(position).getBrand().equalsIgnoreCase("MASTER")) {
                paymentTypeImg.setImageResource(R.drawable.credit_card);
            } else if (list.get(position).getBrand().equalsIgnoreCase("MASTRO")) {
                paymentTypeImg.setImageResource(R.drawable.credit_card);
            } else if (list.get(position).getBrand().equalsIgnoreCase("Visa")) {
                paymentTypeImg.setImageResource(R.drawable.credit_card);
            } else {
                paymentTypeImg.setImageResource(R.drawable.credit_card);
            }
            cardNumber.setText("xxxx - xxxx - xxxx - " + list.get(position).getLast_four());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return itemView;
    }

    @Override
    public int getCount() {
        return list.size();
    }
}
