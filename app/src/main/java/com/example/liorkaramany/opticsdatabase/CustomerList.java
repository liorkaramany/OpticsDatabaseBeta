package com.example.liorkaramany.opticsdatabase;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CustomerList extends ArrayAdapter<Customer> {

    private Activity context;
    private List<Customer> customerList;

    public CustomerList(Activity context, List<Customer> customerList)
    {
        super(context, R.layout.list_layout, customerList);
        this.context = context;
        this.customerList = customerList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.list_layout, null, true);

        TextView fname = (TextView) listViewItem.findViewById(R.id.fname);
        TextView lname = (TextView) listViewItem.findViewById(R.id.lname);
        TextView customerID = (TextView) listViewItem.findViewById(R.id.customerID);
        TextView address = (TextView) listViewItem.findViewById(R.id.address);
        TextView city = (TextView) listViewItem.findViewById(R.id.city);
        TextView phone = (TextView) listViewItem.findViewById(R.id.phone);
        TextView mobile = (TextView) listViewItem.findViewById(R.id.mobile);
        TextView opendate = (TextView) listViewItem.findViewById(R.id.opendate);

        Customer customer = customerList.get(position);

        fname.setText(customer.getfName());
        lname.setText(""+customer.getlName());
        customerID.setText(""+customer.getCustomerID());
        address.setText(""+customer.getAddress());
        city.setText(""+customer.getCity());
        phone.setText(""+customer.getPhone());
        mobile.setText(""+customer.getMobile());
        opendate.setText(""+customer.getOpenDate());


        return listViewItem;
    }
}
