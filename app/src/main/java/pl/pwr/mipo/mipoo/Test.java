package pl.pwr.mipo.mipoo;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

import pl.pwr.mipo.mipoo.R;
import pl.pwr.mipo.mipoo.db.ListsDatabaseAdapter;

public class Test extends ActionBarActivity {

    private ListView list ;
    private ArrayAdapter<String> adapter ;
    private ListsDatabaseAdapter mDbHelper;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        mDbHelper = ListsDatabaseAdapter.getInstance(getBaseContext());
        mDbHelper.openConnection();
      //  mDbHelper.insertGroupRecord("Moja grupa");
        list = (ListView) findViewById(R.id.listView1);

        String cars[] = {ListsDatabaseAdapter.GROUP_NAME,
                ListsDatabaseAdapter.GROUP_POSITION };

        ArrayList<String> carL = new ArrayList<String>();
        carL.addAll( Arrays.asList(cars) );

        adapter = new ArrayAdapter<String>(this, R.layout.row, carL);

        list.setAdapter(adapter);
    }



}
