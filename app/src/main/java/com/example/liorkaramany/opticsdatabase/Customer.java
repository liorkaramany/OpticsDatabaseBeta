package com.example.liorkaramany.opticsdatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Lior Karamany
 * @version 1.0
 * @since 1.0
 */
public class Customer {

    public String id;
    public String fName;
    public String lName;
    public String customerID;
    public String address;
    public String city;
    public String phone;
    public String mobile;
    public String openDate;
    public int typeID;
    public String url;

    public Customer()
    {

    }

    public Customer(String id, String fName, String lName, String customerID, String address, String city, String phone, String mobile, String url) {
        this.id = id;
        this.fName = fName;
        this.lName = lName;
        this.customerID = customerID;
        this.address = address;
        this.city = city;
        this.phone = phone;
        this.mobile = mobile;

        Calendar date = Calendar.getInstance();
        openDate = date.get(Calendar.DAY_OF_MONTH) + "/" + (date.get(Calendar.MONTH)+1) +"/" + date.get(Calendar.YEAR);

        this.typeID = 0;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public String getfName() {
        return fName;
    }

    public String getlName() {
        return lName;
    }

    public String getCustomerID() {
        return customerID;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getPhone() {
        return phone;
    }

    public String getMobile() {
        return mobile;
    }

    public String getOpenDate() {
        return openDate;
    }

    public int getTypeID() {
        return typeID;
    }

    public String getUrl() {
        return url;
    }
}
