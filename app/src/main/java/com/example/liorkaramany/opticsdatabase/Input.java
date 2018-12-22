package com.example.liorkaramany.opticsdatabase;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Input extends AppCompatActivity {

    EditText fname, lname, customerID, address, city, phone, mobile;
    CheckBox glasses, lens;

    DatabaseReference dref;
    StorageReference sref;

    int typeID;
    String id, url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        fname = (EditText) findViewById(R.id.fname);
        lname = (EditText) findViewById(R.id.lname);
        customerID = (EditText) findViewById(R.id.customerID);
        address = (EditText) findViewById(R.id.address);
        city = (EditText) findViewById(R.id.city);
        phone = (EditText) findViewById(R.id.phone);
        mobile = (EditText) findViewById(R.id.mobile);

        glasses = (CheckBox) findViewById(R.id.glasses);
        lens = (CheckBox) findViewById(R.id.lens);

        dref = FirebaseDatabase.getInstance().getReference("customers");
        sref = FirebaseStorage.getInstance().getReference("customers");

        Intent gt = getIntent();
        typeID = gt.getIntExtra("typeID", -1);

        if (typeID != -1)
        {
            id = gt.getStringExtra("id");

            fname.setText("" + gt.getStringExtra("fname"));
            lname.setText("" + gt.getStringExtra("lname"));
            customerID.setText("" + gt.getStringExtra("customerID"));
            address.setText("" + gt.getStringExtra("address"));
            city.setText("" + gt.getStringExtra("city"));
            phone.setText("" + gt.getStringExtra("phone"));
            mobile.setText("" + gt.getStringExtra("mobile"));

            if (typeID >= 2)
                lens.setChecked(true);
            if (typeID == 1 || typeID == 3)
                glasses.setChecked(true);

            url = gt.getStringExtra("url");
        }
    }

    public void back(View view) {
        finish();
    }

    public void upload(View view) {
        String fn = fname.getText().toString();
        String ln = lname.getText().toString();
        String cID = customerID.getText().toString();
        String a = address.getText().toString();
        String c = city.getText().toString();
        String p = phone.getText().toString();
        String m = mobile.getText().toString();

        if (fn.isEmpty() || ln.isEmpty() || cID.isEmpty() || a.isEmpty() || c.isEmpty() || p.isEmpty() || m.isEmpty())
            Toast.makeText(this, "You haven't entered all the information", Toast.LENGTH_SHORT).show();
        else
        {
            int typeId = 0;
            if (glasses.isChecked() && lens.isChecked())
                typeId = 3;
            else if (glasses.isChecked())
                typeId = 1;
            else if (lens.isChecked())
                typeId = 2;

            Customer customer;
            if (id == null)
            {
                String newId = dref.push().getKey();
                customer = new Customer(newId, fn, ln, cID, a, c, p, m, typeId, url);
                dref.child(newId).child("object").setValue(customer);
                Toast.makeText(this, "Customer has been added", Toast.LENGTH_SHORT).show();
            }
            else
                {
                customer = new Customer(id, fn, ln, cID, a, c, p, m, typeId, url);
                dref.child(id).child("object").setValue(customer);
                Toast.makeText(this, "Customer has been edited", Toast.LENGTH_SHORT).show();
            }

            finish();
        }
    }
}
