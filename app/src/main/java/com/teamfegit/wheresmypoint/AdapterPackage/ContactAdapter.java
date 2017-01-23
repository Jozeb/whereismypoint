package com.teamfegit.wheresmypoint.AdapterPackage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.teamfegit.wheresmypoint.R;
import com.teamfegit.wheresmypoint.StructurePackage.FriendsClass;

import java.util.ArrayList;

/**
 * Created by Eustace on 06-Dec-16.
 */

public class ContactAdapter extends BaseAdapter {


    ArrayList<FriendsClass> numbers;
    LayoutInflater layoutInflater;

    public ContactAdapter(Context context, ArrayList<FriendsClass> numbers) {


        layoutInflater = LayoutInflater.from(context);
        this.numbers = numbers;


    }

    @Override
    public int getCount() {
        return numbers.size();
    }

    @Override
    public Object getItem(int position) {
        return numbers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View r;
        if (convertView == null) {
            r = layoutInflater.inflate(R.layout.row_contacts, null);
        } else {
            r = convertView;
        }

        TextView tname = (TextView) r.findViewById(R.id.text_name);
        TextView tnumber = (TextView) r.findViewById(R.id.text_number);
        ImageView image = (ImageView) r.findViewById(R.id.imageview);

        tname.setText(numbers.get(position).name);
        tnumber.setText(numbers.get(position).number);




        return r;
    }


}
