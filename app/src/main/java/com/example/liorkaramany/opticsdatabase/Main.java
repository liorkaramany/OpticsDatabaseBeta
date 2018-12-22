package com.example.liorkaramany.opticsdatabase;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class Main extends AppCompatActivity {

    ListView list;
    DatabaseReference ref;
    List<Customer> customerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = (ListView) findViewById(R.id.list);
        customerList = new ArrayList<>();

        ref = FirebaseDatabase.getInstance().getReference("customers");

        list.setOnCreateContextMenuListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                customerList.clear();
                for (DataSnapshot customerSnapshot :  dataSnapshot.getChildren())
                {
                    Customer customer = customerSnapshot.getValue(Customer.class);

                    customerList.add(customer);
                }

                CustomerList adapter = new CustomerList(Main.this, customerList);
                list.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        menu.setHeaderTitle("Options");
        menu.add("Edit");
        menu.add("Delete");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int index = info.position;
        Customer customer = customerList.get(index);

        String option = item.getTitle().toString();
        if (option.equals("Edit"))
        {
            /*if (customer.getUrl().isEmpty()) {
                Intent t = new Intent(this, Text.class);
                t.putExtra("sign", 1);

                t.putExtra("id", customer.getId());
                t.putExtra("name", customer.getName());
                t.putExtra("age", customer.getAge());
                t.putExtra("left", customer.getLeft());
                t.putExtra("right", customer.getRight());
                t.putExtra("price", customer.getPrice());

                startActivity(t);
            }
            else
            {
                /*Intent t = new Intent(this, Camera.class);
                t.putExtra("sign", 1);

                t.putExtra("id", customer.getId());
                t.putExtra("url", customer.getUrl());
                startActivity(t);
            } */
        }
        else if (option.equals("Delete"))
        {
            String id = customer.getId();
            /*if (!customer.getUrl().equals("")) {
                StorageReference r = FirebaseStorage.getInstance().getReferenceFromUrl(customer.getUrl());
                r.delete();
            }*/
            ref.child(id).removeValue();
            Toast.makeText(this, "Customer has been deleted", Toast.LENGTH_SHORT).show();
        }

        return super.onContextItemSelected(item);
    }

    public void add(View view) {
        Intent t = new Intent(this, Input.class);
        startActivity(t);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add("Credits");

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getTitle().toString().equals("Credits"))
        {
            Intent t = new Intent(this, Credits.class);
            startActivity(t);
        }

        return super.onOptionsItemSelected(item);
    }
}
