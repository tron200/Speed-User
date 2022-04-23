package com.speed.user.consts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.speed.user.R;
import com.speed.user.helper.SharedHelper;
import com.speed.user.utills.MyBoldTextView;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Amit on 25/02/17.
 */

public class CouponListAdapter extends ArrayAdapter<JSONObject> {

    public ArrayList<JSONObject> list;
    int vg;
    Context context;

    public CouponListAdapter(Context context, int vg, ArrayList<JSONObject> list) {

        super(context, vg, list);

        this.context = context;

        this.vg = vg;

        this.list = list;

    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(vg, parent, false);

        MyBoldTextView discount = itemView.findViewById(R.id.discount);

        MyBoldTextView promo_code = itemView.findViewById(R.id.promo_code);

        MyBoldTextView expires = itemView.findViewById(R.id.expiry);


        try {
            discount.setText(list.get(position).optJSONObject("promocode").optString("discount")+""+SharedHelper.getKey(context, "currency") + " " + context.getString(R.string.off));
            promo_code.setText(context.getString(R.string.the_applied_coupon) + " " + list.get(position).optJSONObject("promocode").optString("promo_code") + ".");
            String date = list.get(position).optJSONObject("promocode").optString("expiration");
            expires.setText(context.getString(R.string.valid_until) + " " + getDate(date) + " " + getMonth(date) + " " + getYear(date));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return itemView;

    }


    private String getMonth(String date) throws ParseException {
        Date d = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String monthName = new SimpleDateFormat("MMM").format(cal.getTime());
        return monthName;
    }

    private String getDate(String date) throws ParseException {
        Date d = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String dateName = new SimpleDateFormat("dd").format(cal.getTime());
        return dateName;
    }

    private String getYear(String date) throws ParseException {
        Date d = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String yearName = new SimpleDateFormat("yyyy").format(cal.getTime());
        return yearName;
    }

}
