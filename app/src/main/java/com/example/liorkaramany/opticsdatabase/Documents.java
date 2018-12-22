package com.example.liorkaramany.opticsdatabase;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class Documents extends AppCompatActivity {

    ListView list;

    //Gets 2 ArrayLists through intents containing the dates and the URLs of the images to display.
    ArrayList<Image> images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents);

        list = (ListView) findViewById(R.id.list);

        Intent gt = getIntent();
        images = (ArrayList<Image>) gt.getSerializableExtra("images");

        if (images == null)
            images = new ArrayList<>();

        ImageList adapter = new ImageList(Documents.this, images);

        list.setAdapter(adapter);
    }

    public void add(View view) {

    }

    public void save(View view) {
        Intent back = getIntent();
        back.putExtra("images", images);
        setResult(RESULT_OK, back);
        finish();
    }
}
