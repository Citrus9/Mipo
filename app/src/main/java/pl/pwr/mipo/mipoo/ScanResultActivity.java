package pl.pwr.mipo.mipoo;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by CitrusPanc on 5/16/2015.
 */
public class ScanResultActivity extends ActionBarActivity {

    ArrayList<ScannedProduct> sproducts;
    ScanProductListAdapter adapter;
    Bundle mExtras;
    float yourPrice;
    float allPrices = 0;
    float amountOfProducts = 0;
    float avgPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanresult);

        TextView tvName = (TextView) findViewById(R.id.tvName);
        TextView tvStore = (TextView) findViewById(R.id.tvStore);
        TextView tvPrice = (TextView) findViewById(R.id.tvPrice);
        ImageView imgThumbs = (ImageView) findViewById(R.id.imageView);
        ListView lv = (ListView) findViewById(R.id.listView);

        mExtras = getIntent().getExtras();
        sproducts = (ArrayList<ScannedProduct>) getIntent().getSerializableExtra(ScanActivity.KEY_LIST);
        yourPrice = mExtras.getFloat(ScanActivity.KEY_PRICE);

        for(ScannedProduct sp : sproducts){
            allPrices += sp.getPrice();
            amountOfProducts += 1;
        }

        avgPrice = allPrices/amountOfProducts;



        if(avgPrice > yourPrice){
            imgThumbs.setImageResource(R.drawable.thumbs_up);
        }
        else{
            imgThumbs.setImageResource(R.drawable.thumbs_down);
        }

        adapter = new ScanProductListAdapter(this, sproducts);
        lv.setAdapter(adapter);
        lv.setAdapter(adapter);


    }

}
