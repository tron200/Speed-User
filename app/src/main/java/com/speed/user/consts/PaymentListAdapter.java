package com.speed.user.consts;

/**
 * Created by Amit on 11/02/17.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.speed.user.R;
import com.speed.user.models.CardDetails;
import com.speed.user.utills.MyTextView;

import java.util.ArrayList;

public class PaymentListAdapter extends ArrayAdapter<CardDetails> {

    public ArrayList<CardDetails> list;
    int vg;
    Context context;

    public PaymentListAdapter(Context context, int vg, ArrayList<CardDetails> list) {

        super(context, vg, list);

        this.context = context;

        this.vg = vg;

        this.list = list;

    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(vg, parent, false);

        ImageView paymentTypeImg = itemView.findViewById(R.id.paymentTypeImg);
        RadioButton radioButton = itemView.findViewById(R.id.radioButton);

        MyTextView cardNumber = itemView.findViewById(R.id.cardNumber);

        ImageView tickImg = itemView.findViewById(R.id.img_tick);

        try {
            if (list.get(position).getBrand().equalsIgnoreCase("MASTERCARD")) {
                paymentTypeImg.setImageResource(R.drawable.credit_card);
            } else if (list.get(position).getBrand().equalsIgnoreCase("MASTRO")) {
                paymentTypeImg.setImageResource(R.drawable.visa_payment_icon);
            } else if (list.get(position).getBrand().equalsIgnoreCase("Visa")) {
                paymentTypeImg.setImageResource(R.drawable.visa);
            }
            cardNumber.setText("xxxx - xxxx - xxxx - " + list.get(position).getLast_four());

            if (list.get(position).getIs_default().equals("1")) {
                radioButton.setChecked(true);
                radioButton.setClickable(false);
            }

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
