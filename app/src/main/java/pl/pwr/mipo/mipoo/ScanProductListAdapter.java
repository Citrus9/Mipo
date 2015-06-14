package pl.pwr.mipo.mipoo;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Comparator;
import java.util.List;

/**
 * Created by CitrusPanc on 3/21/2015.
 */
public class ScanProductListAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<ScannedProduct> festItems;
    private Drawable img1, img2, im3;
//    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public ScanProductListAdapter(Activity activity, List<ScannedProduct> festList) {
        this.activity = activity;
        this.festItems = festList;
    }

    @Override
    public int getCount() {
        return festItems.size();
    }

    @Override
    public Object getItem(int location) {
        return festItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.item, null);

//        if (imageLoader == null)
//            imageLoader = AppController.getInstance().getImageLoader();
//        NetworkImageView thumbNail = (NetworkImageView) convertView
//                .findViewById(R.id.imgFullPhoto);
        TextView store = (TextView) convertView.findViewById(R.id.tvStore);
        TextView date = (TextView) convertView.findViewById(R.id.tvDate);

        TextView price = (TextView) convertView.findViewById(R.id.tvPrice);

        // getting movie data for the row
        ScannedProduct m = festItems.get(position);

        // thumbnail image
//        thumbNail.setImageUrl(m.getThumbNailUrl(), imageLoader);

        // name
//        name.setText(m.getProductName());

        // store
        store.setText(String.valueOf(m.getStoreName()));

        // date
//        date.setText("Where: " + String.valueOf(m.getWhere()));

        //price
        price.setText(String.valueOf(m.getPriceString()) + " zł");

        return convertView;
    }



}
