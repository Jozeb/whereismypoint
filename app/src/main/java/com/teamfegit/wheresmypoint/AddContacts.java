package com.teamfegit.wheresmypoint;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.teamfegit.wheresmypoint.DatabasePackage.DbSource;
import com.teamfegit.wheresmypoint.StructurePackage.FriendsClass;

public class AddContacts extends AppCompatActivity {

    DbSource database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contacts);

        database = new DbSource(this);

        final EditText edit_name = (EditText) findViewById(R.id.edit_name);
        final EditText edit_number = (EditText) findViewById(R.id.edit_number);


        Button btn_register = (Button) findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edit_name.getText().toString().length() > 0) {

                    if (edit_number.getText().toString().length() > 0) {
                        String name = edit_name.getText().toString();
                        String number = edit_number.getText().toString();

                        database.addFriend(new FriendsClass(name, number));

                        Toast.makeText(AddContacts.this, "Friend Inserted", Toast.LENGTH_SHORT).show();
                        edit_name.setText("");
                        edit_number.setText("");
                        //finish();

                    } else {
                        edit_number.setError("Cannot be Empty!");
                    }
                } else {
                    edit_name.setError("Cannot be empty!");
                }

            }
        });


        Button btn_cancel = (Button) findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        database.close();
        super.onDestroy();
    }
}
