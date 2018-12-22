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

import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageList extends ArrayAdapter<Image> {

    private Activity context;
    private List<Image> imageList;

    public ImageList(Activity context, List<Image> imageList)
    {
        super(context, R.layout.imglist_layout, imageList);
        this.context = context;
        this.imageList = imageList;
    }

    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.imglist_layout, null, true);

        TextView date = (TextView) listViewItem.findViewById(R.id.date);
        ImageView img = (ImageView) listViewItem.findViewById(R.id.img);

        Image image = imageList.get(position);

        date.setText(image.getOpenDate());

        Picasso.get().load(image.getUrl()).into(img);

        return listViewItem;
    }
}
