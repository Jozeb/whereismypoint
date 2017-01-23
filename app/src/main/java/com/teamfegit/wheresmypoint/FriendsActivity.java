package com.teamfegit.wheresmypoint;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.teamfegit.wheresmypoint.AdapterPackage.FriendsAdapter;
import com.teamfegit.wheresmypoint.DatabasePackage.DbSource;

public class FriendsActivity extends Activity {

    ListView listView;
    DbSource database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);


        database = new DbSource(this);
        listView = (ListView) findViewById(R.id.listview);


        Button btn_addFriends = (Button) findViewById(R.id.btn_add);
        btn_addFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FriendsActivity.this, AddContacts.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        listView.setAdapter(new FriendsAdapter(this, database.getFriends()));
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        database.close();
        super.onDestroy();
    }
}
