package com.teamfegit.wheresmypoint.AdapterPackage;

import android.content.ContentProvider;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.teamfegit.wheresmypoint.DatabasePackage.DbSource;
import com.teamfegit.wheresmypoint.R;
import com.teamfegit.wheresmypoint.StructurePackage.FriendsClass;

import java.util.ArrayList;

/**
 * Created by Eustace on 06-Dec-16.
 */

public class FriendsAdapter extends BaseAdapter {


    ArrayList<FriendsClass> numbers;
    LayoutInflater layoutInflater;
    Context context;

    public FriendsAdapter(Context context, ArrayList<FriendsClass> numbers) {

        layoutInflater = LayoutInflater.from(context);
        this.numbers = numbers;
        this.context = context;

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
    public View getView(final int position, View convertView, ViewGroup parent) {

        View r;
        if (convertView == null) {
            r = layoutInflater.inflate(R.layout.row_friends, null);
        } else {
            r = convertView;
        }

        TextView tname = (TextView) r.findViewById(R.id.text_name);
        TextView tnumber = (TextView) r.findViewById(R.id.text_number);
        ImageView image = (ImageView) r.findViewById(R.id.imageview);
        Button btn_delete = (Button) r.findViewById(R.id.btn_delete);
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DbSource database = new DbSource(context);
                database.deleteFriendwithID(numbers.get(position).id);
                database.close();

            }
        });

        tname.setText(numbers.get(position).name);
        tnumber.setText(numbers.get(position).number);




        return r;
    }


}
