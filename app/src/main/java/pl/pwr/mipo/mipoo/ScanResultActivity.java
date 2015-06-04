package pl.pwr.mipo.mipoo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    String yourPrice;
    String yourStore;
    float allPrices = 0;
    float amountOfProducts = 0;
    float avgPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanresult);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView tvName = (TextView) findViewById(R.id.tvName);
        TextView tvBarcode = (TextView) findViewById(R.id.tvBarcode);
        TextView tvStore = (TextView) findViewById(R.id.tvStore);
        TextView tvPrice = (TextView) findViewById(R.id.tvPrice);
        ImageView imgThumbs = (ImageView) findViewById(R.id.imgThumbs);
        ListView lv = (ListView) findViewById(R.id.listView);

        mExtras = getIntent().getExtras();
        sproducts = (ArrayList<ScannedProduct>) getIntent().getSerializableExtra(ScanActivity.KEY_LIST);
        yourPrice = mExtras.getString(ScanActivity.KEY_PRICE);
        yourStore = mExtras.getString(ScanActivity.KEY_STORE);

        for(int i = 0; i < sproducts.size(); i++){
            Log.d("SCAN_RESULT", "My Scanned Response item: " + i + ", details "
                    + sproducts.get(i).getProductName() + " "
                    + sproducts.get(i).getBarcode() + " "
                    + sproducts.get(i).getStoreName() + " "
                    + sproducts.get(i).getPrice() + " "
                    + sproducts.get(i).getProductName());
        }

        for(ScannedProduct sp : sproducts){
            allPrices += sp.getPrice();
            amountOfProducts += 1;
        }

        avgPrice = allPrices/amountOfProducts;
        Float price = Float.parseFloat(yourPrice);

        if(avgPrice > price){
            imgThumbs.setImageResource(R.drawable.thumbs_up);
        }
        else{
            imgThumbs.setImageResource(R.drawable.thumbs_down);
        }

        tvName.setText(sproducts.get(0).getProductName());
        tvBarcode.setText("" + sproducts.get(0).getBarcode());
        tvStore.setText(yourStore);
        tvPrice.setText("" + yourPrice + " zl");

        adapter = new ScanProductListAdapter(this, sproducts);
        lv.setAdapter(adapter);
        lv.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.activity_cursor, menu);
        return (super.onCreateOptionsMenu(menu));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
                return (true);
        }
        return (super.onOptionsItemSelected(item));
    }

}
