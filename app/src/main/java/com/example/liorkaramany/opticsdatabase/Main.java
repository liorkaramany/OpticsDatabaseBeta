package com.example.liorkaramany.opticsdatabase;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Main extends AppCompatActivity {

    ListView list;
    DatabaseReference ref;
    DatabaseReference imgRef;
    List<Customer> customerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = (ListView) findViewById(R.id.list);
        customerList = new ArrayList<>();

        ref = FirebaseDatabase.getInstance().getReference("customers");
        imgRef = FirebaseDatabase.getInstance().getReference("images");

        list.setOnCreateContextMenuListener(this);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Customer customer = customerList.get(position);

                AlertDialog.Builder adb = new AlertDialog.Builder(Main.this);

                adb.setTitle("Customer");

                LayoutInflater inflater = Main.this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.customer_layout, null);
                adb.setView(dialogView);

                TextView fname = (TextView) dialogView.findViewById(R.id.fname);
                TextView lname = (TextView) dialogView.findViewById(R.id.lname);
                TextView customerID = (TextView) dialogView.findViewById(R.id.customerID);
                TextView address = (TextView) dialogView.findViewById(R.id.address);
                TextView city = (TextView) dialogView.findViewById(R.id.city);
                TextView phone = (TextView) dialogView.findViewById(R.id.phone);
                TextView mobile = (TextView) dialogView.findViewById(R.id.mobile);
                TextView opendate = (TextView) dialogView.findViewById(R.id.opendate);
                CheckBox glasses = (CheckBox) dialogView.findViewById(R.id.glasses);
                CheckBox lens = (CheckBox) dialogView.findViewById(R.id.lens);

                fname.setText("" + customer.getfName());
                lname.setText("" + customer.getlName());
                customerID.setText("" + customer.getCustomerID());
                address.setText("" + customer.getAddress());
                city.setText("" + customer.getCity());
                phone.setText("" + customer.getPhone());
                mobile.setText("" + customer.getMobile());
                opendate.setText("" + customer.getOpenDate());

                int typeID = customer.getTypeID();
                if (typeID >= 2)
                    lens.setChecked(true);
                if (typeID == 1 || typeID == 3)
                    glasses.setChecked(true);

                adb.setView(dialogView);

                adb.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editCustomer(customer);
                    }
                });
                adb.setNeutralButton("View Document", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showDocument(customer);
                    }
                });
                adb.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog ad = adb.create();
                ad.show();
            }
        });

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // The request code used in ActivityCompat.requestPermissions()
            // and returned in the Activity's onRequestPermissionsResult()
            int PERMISSION_ALL = 1;
            String[] PERMISSIONS = {
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.CAMERA
            };

            if(!hasPermissions(this, PERMISSIONS)){
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
            }
        }
    }


    public void editCustomer(Customer customer)
    {
        Intent t = new Intent(Main.this, Input.class);
        t.putExtra("sign", 1);

        t.putExtra("id", customer.getId());
        t.putExtra("fname", customer.getfName());
        t.putExtra("lname", customer.getlName());
        t.putExtra("customerID", customer.getCustomerID());
        t.putExtra("address", customer.getAddress());
        t.putExtra("city", customer.getCity());
        t.putExtra("phone", customer.getPhone());
        t.putExtra("mobile", customer.getMobile());
        t.putExtra("openDate", customer.getOpenDate());
        t.putExtra("typeID", customer.getTypeID());

        startActivity(t);
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
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
        menu.add("View document");
        menu.add("Edit details");
        menu.add("Delete");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int index = info.position;
        Customer customer = customerList.get(index);

        String option = item.getTitle().toString();
        if (option.equals("Edit details"))
        {
            editCustomer(customer);
        }
        else if (option.equals("Delete"))
        {
            String id = customer.getId();
            ref.child(id).removeValue();
            imgRef.child(id).removeValue();
            StorageReference r = FirebaseStorage.getInstance().getReference("customers").child(id);
            r.delete();
            Toast.makeText(this, "Customer has been deleted", Toast.LENGTH_SHORT).show();
        }
        else if (option.equals("View document"))
        {
            showDocument(customer);
        }

        return super.onContextItemSelected(item);
    }

    public void showDocument(Customer customer)
    {
        final String id = customer.getId();
        imgRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot customerSnapshot : dataSnapshot.getChildren()) {
                    Image image = customerSnapshot.getValue(Image.class);

                    if (id.equals(image.getId())) {
                        AlertDialog.Builder adb = new AlertDialog.Builder(Main.this);

                        LayoutInflater inflater = Main.this.getLayoutInflater();
                        View dialogView = inflater.inflate(R.layout.img_layout, null);
                        adb.setView(dialogView);

                        ImageView document = dialogView.findViewById(R.id.document);
                        TextView date = dialogView.findViewById(R.id.date);

                        final String url = image.getUrl();
                        String d = image.getOpenDate();
                        date.setText(d);
                        Picasso.get().load(url).into(document);

                        adb.setTitle("Document");
                        adb.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        adb.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent t = new Intent(Main.this, Document.class);
                                t.putExtra("url", url);
                                t.putExtra("id", id);
                                t.putExtra("sign", 1);
                                startActivity(t);
                            }
                        });

                        AlertDialog ad = adb.create();
                        ad.show();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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



    public void sortList(int option)
    {
        Query query;
        switch (option)
        {
            case 0:
                query = ref.orderByChild("fName");

                query.addValueEventListener(new ValueEventListener() {
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

                break;

            case 1:
                query = ref.orderByChild("lName");

                query.addValueEventListener(new ValueEventListener() {
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

                break;

            case 2:
                query = ref;

                query.addValueEventListener(new ValueEventListener() {
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

                break;
        }
    }

    public void sortFName(View view) {
        sortList(0);
    }

    public void sortLName(View view) {
        sortList(1);
    }

    public void sortDate(View view) {
        sortList(2);
    }
}
