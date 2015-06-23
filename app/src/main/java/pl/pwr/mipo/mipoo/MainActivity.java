package pl.pwr.mipo.mipoo;


import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.heinrichreimersoftware.materialdrawer.DrawerView;
import com.heinrichreimersoftware.materialdrawer.structure.DrawerHeaderItem;
import com.heinrichreimersoftware.materialdrawer.structure.DrawerItem;
import com.heinrichreimersoftware.materialdrawer.structure.DrawerProfile;
import com.heinrichreimersoftware.materialdrawer.theme.DrawerTheme;
import com.melnykov.fab.FloatingActionButton;
import com.mobeta.android.dslv.DragSortListView;
import com.mobeta.android.dslv.SimpleDragSortCursorAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.pwr.mipo.mipoo.app.AppConfig;
import pl.pwr.mipo.mipoo.app.AppController;
import pl.pwr.mipo.mipoo.db.ListsDatabaseAdapter;
import pl.pwr.mipo.mipoo.db.SessionManager;
import pl.pwr.mipo.mipoo.zxing.IntentIntegrator;
import pl.pwr.mipo.mipoo.zxing.IntentResult;


import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

public class MainActivity extends ActionBarActivity {

    ListsDatabaseAdapter db;
    SessionManager session;
    private DrawerView drawer;
    private ActionBarDrawerToggle drawerToggle;
    private MAdapter mMAdapter;
    private ListsDatabaseAdapter mDbHelper;
    private DragSortListView mDslv;
    private EditText input;
    private View positiveAction;
    private FloatingActionButton fab;
    private ImageView imgAvatar;
    private TextView textName;
    private TextView textEmail;
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private EditText inputRegFullName;
    private EditText inputRegEmail;
    private EditText inputRegPassword;

    private double priceFloat;
    private String nameString;
    private String storeString;
    private String priceString;

    EditText inputName;
    EditText inputPrice;
    EditText inputStore;

    private List<ScannedProduct> productList = new ArrayList<ScannedProduct>();
    private List<MyList> myList = new ArrayList<MyList>();
    private List<Product> myProds = new ArrayList<Product>();

    private String barcode = null;
    private String typ = null;

    public static final String KEY_LIST = "list";
    public static final String KEY_PRICE = "price";
    public static final String KEY_STORE = "store";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawer = (DrawerView) findViewById(R.id.drawer);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close
        ) {

            public void onDrawerClosed(View view) {
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu();
            }
        };

//        drawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.ColorPrimaryDark));
        drawerLayout.setDrawerListener(drawerToggle);
        drawerLayout.closeDrawer(drawer);
//        drawer.setBackgroundColor(getResources().getColor(R.color.drawer_background));
        drawer.setDrawerTheme(
                new DrawerTheme(this)
                        .setBackgroundColorRes(R.color.white)
                        .setTextColorPrimaryRes(R.color.input_login)
                        .setTextColorSecondaryRes(R.color.input_register_hint)
                        .setHighlightColorRes(R.color.drawer_highlighted)
        );

        drawer.addItem(new DrawerItem()
                        .setImage(getResources().getDrawable(R.mipmap.ic_shopping), DrawerItem.SMALL_AVATAR)
                        .setTextPrimary("Shopping Lists")
//                        .setTextSecondary("photo5 secondary", DrawerItem.THREE_LINE)
        );
        drawer.addItem(new DrawerItem()
                        .setImage(getResources().getDrawable(R.mipmap.ic_scan), DrawerItem.SMALL_AVATAR)
                        .setTextPrimary("Scan")
//                        .setTextSecondary("photo5 secondary", DrawerItem.THREE_LINE)
        );
//        drawer.addItem(new DrawerItem()
//                        .setImage(getResources().getDrawable(R.mipmap.ic_group), DrawerItem.SMALL_AVATAR)
//                        .setTextPrimary("Groups?")
////                        .setTextSecondary("photo5 secondary", DrawerItem.THREE_LINE)
//        );
        drawer.addDivider();
        drawer.selectItem(0);
        drawer.setOnItemClickListener(new DrawerItem.OnItemClickListener() {
            @Override
            public void onClick(DrawerItem item, long id, int position) {
                drawer.selectItem(position);
                if (position == 1) {

                    IntentIntegrator intentIntegrator = new IntentIntegrator(MainActivity.this);
                    intentIntegrator.initiateScan();
//                    Intent i = new Intent(MainActivity.this, ScanActivity.class);
//                    startActivity(i);
                }
                if (position == 2) {
                    Intent i = new Intent(MainActivity.this, GroupActivity.class);
                    startActivity(i);
                }
                Toast.makeText(MainActivity.this, "Clicked item #" + position, Toast.LENGTH_SHORT).show();
            }
        });

        drawer.setOnFixedItemClickListener(new DrawerItem.OnItemClickListener() {
            @Override
            public void onClick(DrawerItem item, long id, int position) {
                drawer.selectFixedItem(position);

                Toast.makeText(MainActivity.this, "Clicked fixed item #" + position, Toast.LENGTH_SHORT).show();
            }
        });

        db = new ListsDatabaseAdapter(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        if (session.isLoggedIn()) {
            // Fetching user details from sqlite
            HashMap<String, String> user = db.getUserDetails();


            String name = user.get("email");
            String email = user.get("uid");

            drawer.addProfile(new DrawerProfile()
                            .setId(1)
//                            .setRoundedAvatar(new BitmapDrawable (getResources(), generateCircleBitmap(this,getMaterialColor(name), 100, name)))
                            .setRoundedAvatar(new BitmapDrawable(getResources(), generateCircleBitmap(this, getMaterialColor(name), 100, name)))
                            .setBackground(getResources().getDrawable(R.drawable.acc_header))
                            .setName(name)
                            .setDescription(email)
            );
        }
        else{
            drawer.addProfile(new DrawerProfile()
                            .setId(1)
                            .setRoundedAvatar(new BitmapDrawable(getResources(), generateCircleBitmap(this, getMaterialColor("?"), 100, "?")))
                            .setBackground(getResources().getDrawable(R.drawable.acc_header))
                            .setName(getString(R.string.you_are_not_logged))
            );
        }

        drawer.setOnProfileClickListener(new DrawerProfile.OnProfileClickListener() {
            @Override
            public void onClick(DrawerProfile profile, long id) {
                showAccountDialog(session.isLoggedIn());
            }
        });



        mDbHelper = ListsDatabaseAdapter.getInstance(getBaseContext());
        mDbHelper.openConnection();

        displayItemList();
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.attachToListView(mDslv);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogView(0, "", false);
            }
        });
        fab.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null)
        {
            barcode = scanResult.getContents();
            typ = scanResult.getFormatName();
            checkCode(barcode);
            Log.d("SCAN RESULT", "Barcode " + barcode + " Name " + typ);
            return;
        }else {
            if (resultCode != 0) return;{
                Log.i((String) "App", (String) "Scan unsuccessful");
                return;
            }

        }
    }

    private void displayItemList() {
        // The desired columns to be bound
        String[] columns = new String[] { ListsDatabaseAdapter.ITEM_NAME,
                ListsDatabaseAdapter.ITEM_POSITION };

        // the XML defined views which the data will be bound to
        int[] ids = new int[] { R.id.item_name, R.id.item_position_list };

        // pull all items from database
        Cursor cursor = mDbHelper.getAllListRecords();

        mMAdapter = new MAdapter(this, R.layout.list_items, null, columns, ids,
                0);

        mDslv = (DragSortListView) findViewById(R.id.item_list);

        // set dslv profile for faster scroll speeds
        mDslv.setDragScrollProfile(ssProfile);

        mDslv.setAdapter(mMAdapter);
        mMAdapter.changeCursor(cursor);

        mDslv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view,
                                    int position, long id) {
                mMAdapter.persistChanges();
                displayItemList();
                // Get the cursor, positioned to the corresponding row in the
                // result set
                Cursor cursor = (Cursor) listView.getItemAtPosition(position);

                // Get the item name and details from this row in the database.
                long listId = cursor.getLong(cursor
                        .getColumnIndex(ListsDatabaseAdapter.ITEM_KEY_ROWID));
                long time = System.currentTimeMillis();
                android.util.Log.i("Time Class ", " Time value in millisecinds " + time);
                launchProductsActivity(listId);
//                launchProductsActivity(listId);
//				String itemDetails = cursor.getString(cursor
//						.getColumnIndex("item_details"));
//				Toast.makeText(getApplicationContext(),
//						itemName + ": " + itemDetails, Toast.LENGTH_SHORT)
//						.show();

            }
        });

        mDslv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> listView, View view,
                                           int position, long id) {
                mMAdapter.persistChanges();
                displayItemList();
                // Get the cursor, positioned to the corresponding row in the
                // result set
                Cursor cursor = (Cursor) listView.getItemAtPosition(position);

                // Get the item name and details from this row in the database.
                long listId = cursor.getLong(cursor
                        .getColumnIndex(ListsDatabaseAdapter.ITEM_KEY_ROWID));
                String listName = cursor.getString(cursor
                        .getColumnIndex(ListsDatabaseAdapter.ITEM_NAME));
                showDialogView(listId, listName, true);
                return true;
            }
        });
    }

    private void showDialogView(final long listId, String name, final boolean edit) {
        fab.hide();
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.listName)
                .customView(R.layout.dialog_lists, true)
                .positiveText(R.string.ok)
                .negativeText(android.R.string.cancel)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        if (edit) {
//                            if (!mDbHelper.isSyncedList(listId))
                                mDbHelper.updateListRecord(listId,
                                        input.getText().toString(), System.currentTimeMillis() + "");
//                            else {
//                                mDbHelper.updateListRecord(listId,
//                                        input.getText().toString(), System.currentTimeMillis() + "");
//
//                            }

                        } else {
                            mDbHelper.insertListRecord(
                                    input.getText().toString(), System.currentTimeMillis() + "");
                        }
                        fab.show();
                        mMAdapter.persistChanges();
                        displayItemList();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        fab.show();
                    }

                }).build();

        dialog.setOnDismissListener(new MaterialDialog.OnDismissListener(){
            @Override
            public void onDismiss(DialogInterface dialog) {
                fab.show();
            }
        });

        positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
        input = (EditText) dialog.getCustomView().findViewById(R.id.name);
        input.setText(name);

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                positiveAction.setEnabled(s.toString().trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        dialog.show();
        positiveAction.setEnabled(false);  // disabled by default
    }

    private void showAccountDialog(final boolean signed) {
        fab.hide();
        MaterialDialog dialog;
        if(signed){
            HashMap<String, String> user = db.getUserDetails();

            String name = user.get("email");
            String email = user.get("uid");
            dialog = new MaterialDialog.Builder(this)
                    .title(R.string.account)
                    .customView(R.layout.dialog_profile, true)
                    .positiveText(R.string.btn_logout)
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            logoutUser();
                        }

                        @Override
                        public void onNegative(MaterialDialog dialog) {
                            fab.show();
                        }

                    }).build();

            dialog.setOnDismissListener(new MaterialDialog.OnDismissListener(){
                @Override
                public void onDismiss(DialogInterface dialog) {
                    fab.show();
                }
            });

            imgAvatar = (ImageView) dialog.getCustomView().findViewById(R.id.imgAvatar);
            textName = (TextView) dialog.getCustomView().findViewById(R.id.textName);
            textEmail = (TextView) dialog.getCustomView().findViewById(R.id.textEmail);

//            imgAvatar.setImageBitmap(generateCircleBitmap(this,getMaterialColor("?"), 200, "?"));
            imgAvatar.setImageBitmap(generateCircleBitmap(this,getMaterialColor(name), 100, name));
            textName.setText(name);
            textEmail.setText(email);
        }
        else{

            dialog = new MaterialDialog.Builder(this)
                    .title(R.string.btn_login)
                    .customView(R.layout.dialog_login, true)
                    .positiveText(R.string.btn_login)
                    .negativeText(R.string.btn_register)
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            if (inputEmail.getText().toString().trim().length() <= 0
                                    || inputPassword.getText().toString().trim().length() <= 0) {
                                Toast.makeText(getApplicationContext(),
                                        "You haven't entered all the credentials", Toast.LENGTH_LONG).show();
                            } else {
                                String email = inputEmail.getText().toString();
                                String password = inputPassword.getText().toString();
                                checkLogin(email, password);
                            }
                        }

                        @Override
                        public void onNegative(MaterialDialog dialog) {
                            showRegisterDialog();
                        }

                    }).build();

            dialog.setOnDismissListener(new MaterialDialog.OnDismissListener(){
                @Override
                public void onDismiss(DialogInterface dialog) {
                    fab.show();
                }
            });

            positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
            inputEmail = (EditText) dialog.getCustomView().findViewById(R.id.email);
            inputPassword = (EditText) dialog.getCustomView().findViewById(R.id.password);

        }
        dialog.show();
    }

    private void showRegisterDialog() {
        fab.hide();
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.register)
                .customView(R.layout.dialog_register, true)
                .positiveText(R.string.btn_register)
                .negativeText(android.R.string.cancel)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {

                        if (inputRegFullName.getText().toString().trim().length() <= 0
                                || inputRegEmail.getText().toString().trim().length() <= 0
                                || inputRegPassword.getText().toString().trim().length() <= 0) {
                            Toast.makeText(getApplicationContext(),
                                    "You haven't entered all the credentials", Toast.LENGTH_LONG).show();
                        } else {
                            registerUser(inputRegFullName.getText().toString(),
                                    inputRegEmail.getText().toString(),
                                    inputRegPassword.getText().toString());
                            showAccountDialog(false);
                        }
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        fab.show();
                    }

                }).build();

        dialog.setOnDismissListener(new MaterialDialog.OnDismissListener(){
            @Override
            public void onDismiss(DialogInterface dialog) {
                fab.show();
            }
        });

        inputRegFullName = (EditText) dialog.getCustomView().findViewById(R.id.name);
        inputRegEmail = (EditText) dialog.getCustomView().findViewById(R.id.email);
        inputRegPassword = (EditText) dialog.getCustomView().findViewById(R.id.password);
        positiveAction = dialog.getActionButton(DialogAction.POSITIVE);



        dialog.show(); // disabled by default
    }

    private void showAskForLoginDialog(){
        new MaterialDialog.Builder(this)
                .content(R.string.login_to_add)
                .positiveText(R.string.agree)
                .negativeText(R.string.disagree)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {

                        showAccountDialog(session.isLoggedIn());

                    }
                })
                .show();
    }

    private void showDetailsDialog() {

        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.enter_price)
                .customView(R.layout.dialog_price, true)
                .positiveText(R.string.ok)
                .negativeText(android.R.string.cancel)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {


                        priceFloat = Float.parseFloat(inputPrice.getText().toString());
                        storeString = inputStore.getText().toString();
//                        DecimalFormat precision = new DecimalFormat("0.00");
                        NumberFormat nf = NumberFormat.getInstance();
                        nf.setMinimumFractionDigits(2);
                        nf.setMaximumFractionDigits(2);
                        String output = nf.format(priceFloat);
                        DecimalFormat df = new DecimalFormat("#.##");

                        output = ""+priceFloat;
                        priceString = output;

                            ScannedProduct scanInfo = new ScannedProduct();
                            scanInfo.setPrice(priceFloat);
                            scanInfo.setPriceString(priceString);
                            scanInfo.setStoreName(storeString);
                            scanInfo.setBarcode(Long.parseLong(barcode));
                            productList.add(scanInfo);
                        if (session.isLoggedIn()) {
                            sendNewPrice(scanInfo);
                        }
                        Intent intent = new Intent(MainActivity.this, ScanResultActivity.class);
                        intent.putExtra(KEY_PRICE, priceString);
                        intent.putExtra(KEY_STORE, storeString);
                        intent.putExtra(KEY_LIST, (ArrayList<ScannedProduct>) productList);
                        startActivity(intent);

                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {

                    }

                }).build();

        inputPrice = (EditText) dialog.getCustomView().findViewById(R.id.price);
        inputStore = (EditText) dialog.getCustomView().findViewById(R.id.store);
//        inputRegEmail = (EditText) dialog.getCustomView().findViewById(R.id.email);
//        inputRegPassword = (EditText) dialog.getCustomView().findViewById(R.id.password);
        positiveAction = dialog.getActionButton(DialogAction.POSITIVE);

        dialog.show();

    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    private void showAddProductDialog(){

        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.enter_product)
                .customView(R.layout.dialog_addproduct, true)
                .positiveText(R.string.ok)
                .negativeText(android.R.string.cancel)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {

                        if (inputName.getText().toString().trim().length() <= 0
                                || inputPrice.getText().toString().trim().length() <= 0
                                || inputStore.getText().toString().trim().length() <= 0) {
                            Toast.makeText(getApplicationContext(),
                                    "You haven't entered all the data", Toast.LENGTH_LONG).show();

                        } else {
                            priceFloat = Float.parseFloat(inputPrice.getText().toString());
                            storeString = inputStore.getText().toString();
                            nameString = inputName.getText().toString();
                                NumberFormat nf = NumberFormat.getInstance();
                                nf.setMinimumFractionDigits(2);
                                nf.setMaximumFractionDigits(2);
                                String output = nf.format(priceFloat);
                            priceString = output;
                                ScannedProduct scanInfo = new ScannedProduct();
                                scanInfo.setProductName(nameString);
                                scanInfo.setPrice(Float.parseFloat(priceString));
                            scanInfo.setPriceString(priceString);
                                scanInfo.setStoreName(storeString);
                                scanInfo.setBarcode(Long.parseLong(barcode));
                                productList.add(scanInfo);
                                sendNewProduct(scanInfo);

                            Intent intent = new Intent(MainActivity.this, ScanResultActivity.class);
                            intent.putExtra(KEY_PRICE, priceString);
                            intent.putExtra(KEY_STORE, storeString);
                            intent.putExtra(KEY_LIST, (ArrayList<ScannedProduct>) productList);
                            startActivity(intent);
                        }

                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {

                    }

                }).build();

        inputName = (EditText) dialog.getCustomView().findViewById(R.id.name);
        inputPrice = (EditText) dialog.getCustomView().findViewById(R.id.price);
        inputStore = (EditText) dialog.getCustomView().findViewById(R.id.store);
//        inputRegEmail = (EditText) dialog.getCustomView().findViewById(R.id.email);
//        inputRegPassword = (EditText) dialog.getCustomView().findViewById(R.id.password);
        positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
        dialog.show();// disabled by default
    }

    private void checkLogin(final String email, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

//        pDialog.setMessage("Logging in ...");
//        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG", "email: " + email + "password :" + password);
                Log.d("TAG", "Login Response: " + response.toString());
//                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {

                        HashMap<String, String> user = db.getUserDetails();
                        // user successfully logged in
                        // Create login session

//                        db.addUser(uid, name, email);

                        String uid = jObj.getString("idUser");
//                        if(!user.get("uid").equals(uid)) {
//                        JSONObject user1 = jObj.getJSONObject("user");
                            String name = jObj.getString("login");
                            String email = jObj.getString("email");

                            db.addUser(uid, name, email);
//                        }
                        session.setLogin(true, uid, name);
                        Log.d("LOGIN_IUD", uid);

//                        String errorMsg = "Wszystko jest zajebiscie";
//                        Toast.makeText(getApplicationContext(),
//                                errorMsg, Toast.LENGTH_LONG).show();
//                        Launch main activity
                        finish();
                        startActivity(getIntent());
                        showAccountDialog(true);
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                "An error occured " + errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "An error occured " + error.getMessage(), Toast.LENGTH_LONG).show();
//                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "login");
                params.put("email", email);
                params.put("password", password);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    /**
     * Function to store user in MySQL database will post params(tag, name,
     * email, password) to register url
     * */
    private void registerUser(final String name,final String password, final String email) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

//        pDialog.setMessage("Registering ...");
//        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG", "Register Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                        String uid = jObj.getString("idUser");

                        JSONObject user = jObj.getJSONObject("user");
                        String name = user.getString("login");
                        String email = user.getString("email");


                        // Inserting row in users table
                        db.addUser(uid, name, email);

                        showAccountDialog(false);
                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                "An error occured " + errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "An error occured " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "register");
                params.put("name", name);
                params.put("password", password);
                params.put("email", email);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    /**
     *
     * @param barcode
     */
    private void checkCode(final String barcode) {
        // Tag used to cancel the request
        String tag_string_req = "req_scan";

//        pDialog.setMessage("Logging in ...");
//        showDialog();

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("TAG", "barcode: " + barcode);
                Log.d("TAG", "Server scan Response: " + response.toString());
                hidePDialog();
                try {
                    JSONObject obj = new JSONObject(response);
                    boolean error = obj.getBoolean("error");
                    if (!error) {
                        JSONArray jArray = obj.getJSONArray("product");

                        for (int i = 0; i < jArray.length(); i++) {
                            JSONObject jObj = jArray.getJSONObject(i);
                            ScannedProduct scanInfo = new ScannedProduct();
//                        scanInfo.setBarcode(obj.getString("barcode"));
                            scanInfo.setProductName(jObj.getString("name"));
                            Log.d("FLOAT", jObj.getString("price"));
                            scanInfo.setPrice(Float.valueOf(jObj.getString("price")));
                            scanInfo.setPriceString(jObj.getString("price"));
                            scanInfo.setStoreName(jObj.getString("shop"));
                            scanInfo.setBarcode(Long.parseLong(barcode));

                            productList.add(scanInfo);
                        }

                        for(int i = 0; i < productList.size(); i++){
                            Log.d("TAG", "My Scanned Response item: " + i + ", details "
                                    + productList.get(i).getProductName() + " "
                                    + productList.get(i).getBarcode() + " "
                                    + productList.get(i).getStoreName() + " "
                                    + productList.get(i).getPrice() + " "
                                    + productList.get(i).getProductName());
                        }

                        showDetailsDialog();
                    } else {
                        if (obj.getString("error_msg").equals("Product is not in a db")){
                            Log.d("TAG", "Well, you got me");
                            if(session.isLoggedIn()){
                                showAddProductDialog();
                            }
                            else{
                                showAskForLoginDialog();
                            }
                        }
                        // Error in login. Get the error message
                        else {
                            String errorMsg = obj.getString("error_msg");
                            Log.d("TAG", "Scan Response: " + errorMsg);
//                            Toast.makeText(getApplicationContext(),
//                                    "An error occured " + errorMsg, Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }
//                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "Server Error: " + error.getMessage());
                hidePDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "scan");
                params.put("barcode", barcode);
                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void sync() {
        // Tag used to cancel the request
        String tag_string_req = "req_sync";


        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("TAG", "Server scan Response: " + response.toString());
                hidePDialog();
                try {
                    JSONObject obj = new JSONObject(response);
                    boolean error = obj.getBoolean("error");
                    if (!error) {
                        JSONArray jArray1 = obj.getJSONArray("lists");
                        JSONArray jArray2 = obj.getJSONArray("items");

                        for (int i = 0; i < jArray1.length(); i++) {
                            JSONObject jObj = jArray1.getJSONObject(i);
                            MyList l = new MyList(0, "n", 0);
                            l.setId(Long.parseLong(jObj.getString("idList")));
                            l.setName(jObj.getString("nameList"));

                            myList.add(l);
                        }

                        for (int i = 0; i < jArray2.length(); i++) {
                            JSONObject jObj = jArray2.getJSONObject(i);
                            Product p = new Product("n", 0, 0, 0, 0, 0);
                            p.setName(jObj.getString("name"));
                            p.setListid(Long.parseLong(jObj.getString("idList")));
                            p.setBought(Integer.parseInt(jObj.getString("bought")));

                            Log.d("DELETED:", "product:" + p.getName() + " " + p.getBought());
                            if(p.getId()!=-1)
                                myProds.add(p);
                        }

                        Cursor c = mDbHelper.getAllListRecords();
                        ArrayList<MyList> dataLists = new ArrayList<MyList>();
                        if (c != null) {
                            while (c.moveToNext()) {
                                MyList l = new MyList(0, "", 0);
                                l.setId(c.getInt(0));
                                l.setName(c.getString(1));
                                dataLists.add(l);
                            }
                            c.close();
                        }

                        Cursor c1 = mDbHelper.getAllProdRecordsByList();
                        ArrayList<Product> dataProds = new ArrayList<Product>();
                        if (c1 != null) {
                            while (c1.moveToNext()) {
                                Product p = new Product("n", 0, 0, 0, 0, 0);
                                p.setId(c1.getInt(0));
                                p.setName(c1.getString(1));
                                p.setListid(c1.getInt(4));
                                p.setBought(c1.getInt(3));
                                dataProds.add(p);
                            }
                            c1.close();
                        }
                        int listCount = 0;
                        if (dataLists.size() == 0) {
                            for (int i = 0; i < myList.size(); i++) {
                                mDbHelper.insertPreListRecord(myList.get(i).getName(), myList.get(i).getId());
                            }
                        } else {
                            for (int i = 0; i < myList.size(); i++) {
                                for (int j = 0; j < dataLists.size(); j++) {
                                    if (myList.get(i).getId() == dataLists.get(j).getId()) {
                                        myList.get(i).setCount(1);
                                    }
                                }
                            }
                            for (int i = 0; i < myList.size(); i++) {
                                if (myList.get(i).getCount() <= 0) {
                                    mDbHelper.insertPreListRecord(myList.get(i).getName(), myList.get(i).getId());
                                }
                            }
                        }


                        int prodCount = 0;
                        if (dataProds.size() == 0) {
                            for (int j = 0; j < myProds.size(); j++)
                                mDbHelper.insertProductRecord(myProds.get(j).getName(), myProds.get(j).getListid(), 0);
                        } else {

                            for (int i = 0; i < myProds.size(); i++) {
                                for (int k = 0; k < dataProds.size(); k++) {
                                    if (myProds.get(i).getName().equals(dataProds.get(k).getName()) &&
                                            myProds.get(i).getListid() == dataProds.get(k).getListid()) {
                                            myProds.get(i).setCount(1);
                                        if(myProds.get(i).getBought() != dataProds.get(k).getBought()) {
                                            Log.d("DELETED:", "product:" + dataProds.get(k).getName() + " " + dataProds.get(k).getId());
                                            mDbHelper.deleteProductRecord(dataProds.get(k).getId());

                                            myProds.get(i).setCount(0);
                                        }
                                        Log.d("LOL:", "product:" + myProds.get(i).getName());
                                    }
                                }
                            }

//                                for (int k = 0; k < dataProds.size(); k++) {
////                                    Log.d("PRODUCTS:", "id:" + dataProds.get(k).getId() + " name:" + dataProds.get(k).getName());
//                                    if(myProds.get(i).getName().equals(dataProds.get(k).getName())) {
//                                        Log.d("PRODUCTS_EQUAL_NAME:", "prods:" + myProds.get(i).getName() + " data:" + dataProds.get(k).getName());
//                                        if (myProds.get(i).getListid() != dataProds.get(k).getListid()) {
//                                            Log.d("PRODUCTS_NOT_EQUAL_ID:", "prods:" + myProds.get(i).getName() + " data:" + dataProds.get(k).getName());
//                                            mDbHelper.insertProductRecord(myProds.get(i).getName(), myProds.get(i).getListid(), 0);
//                                            i++;
//                                        }
//                                        else{
//                                            Log.d("PRODUCTS_EQUAL_ID:", "prods:" + myProds.get(i).getName() + " data:" + dataProds.get(k).getName());
//                                        }
//                                    } else {
//                                        Log.d("PRODS_NAME_NOT_EQUAL:", "prods:" + myProds.get(i).getName() + " data:" + dataProds.get(k).getName());
//                                        mDbHelper.insertProductRecord(myProds.get(i).getName(), myProds.get(i).getListid(), 0);
//                                    }
//                                }

                            for (int j = 0; j < myProds.size(); j++)
                                if(myProds.get(j).getCount()<=0) {

                                    Log.d("ADDED", myProds.get(j).getName() + " " + myProds.get(j).getBought());
                                    mDbHelper.insertProductRecord(myProds.get(j).getName(), myProds.get(j).getListid(), myProds.get(j).getBought());
                                }

                        }

                        for (int i = 0; i < myList.size(); i++) {
                            Log.d("TAG", "My Scanned Response item: " + i + ", details "
                                    + myList.get(i).getId() + " "
                                    + myList.get(i).getName());
                        }
                        mMAdapter.persistChanges();
                        displayItemList();

                    }else{

                            String errorMsg = obj.getString("error_msg");
                            Log.d("TAG", "Scan Response: " + errorMsg);
//                            Toast.makeText(getApplicationContext(),
//                                    "An error occured " + errorMsg, Toast.LENGTH_LONG).show();

                    }

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "Server Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "An error occured " + error.getMessage(), Toast.LENGTH_LONG).show();
//                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "sync");
                Log.d("LOGIN", session.getUserLogged());
                params.put("idUser", session.getUserLogged());
                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void sendNewProduct(final ScannedProduct sc) {
        // Tag used to cancel the request
        String tag_string_req = "req_newproduct";

//        pDialog.setMessage("Logging in ...");
//        showDialog();

        final String newName = sc.getProductName();
        final double newPrice = sc.getPrice();
        final String newPriceString = sc.getPriceString();
        final String newStore = sc.getStoreName();


        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("TAG", "barcode: " + barcode);
                Log.d("TAG", "Server scan Response: " + response.toString());
                hidePDialog();
                try {
                    JSONObject obj = new JSONObject(response);
                    boolean error = obj.getBoolean("error");
                    if (!error) {
                        Log.d("TAG", "Success");
                    } else {
                        String errorMsg = obj.getString("error_msg");
                        Log.d("TAG", "Scan Response: " + errorMsg);
//                      Toast.makeText(getApplicationContext(),
//                            "An error occured " + errorMsg, Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "Server Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "An error occured " + error.getMessage(), Toast.LENGTH_LONG).show();
//                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "addProduct");
                params.put("barcode", barcode);
                params.put("idUser", session.getUserLogged());
                params.put("shop", newStore);
                params.put("price", newPriceString);
                params.put("name", newName);
                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void sendNewPrice(final ScannedProduct sc) {
        // Tag used to cancel the request
        String tag_string_req = "req_newprice";

//        pDialog.setMessage("Logging in ...");
//        showDialog();

        final String newName = sc.getProductName();
        final double newPrice = sc.getPrice();
        final String newPriceString = sc.getPriceString();
        final String newStore = sc.getStoreName();
        Log.d("LOG_ADD_PRICE", "UID: " + session.getUserLogged());

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("TAG", "barcode: " + barcode);
                Log.d("TAG", "Server scan Response: " + response.toString());
                hidePDialog();
                try {
                    JSONObject obj = new JSONObject(response);
                    boolean error = obj.getBoolean("error");
                    if (!error) {
                        Log.d("TAG", "Success");
                    } else {
                        String errorMsg = obj.getString("error_msg");
                        Log.d("TAG", "Scan Response: " + errorMsg);
//                      Toast.makeText(getApplicationContext(),
//                            "An error occured " + errorMsg, Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "Server Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "An error occured " + error.getMessage(), Toast.LENGTH_LONG).show();
//                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "addPrice");
                params.put("barcode", barcode);
                params.put("idUser", session.getUserLogged());
                params.put("shop", newStore);
                params.put("price", newPriceString);

                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void launchProductsActivity(Long id) {
        Intent i = new Intent(this, ProductsActivity.class);
        Log.d("putExtra", "ID: " + id);
        i.putExtra(ListsDatabaseAdapter.ITEM_KEY_ROWID, id);
        startActivity(i);
    }

    private DragSortListView.DragScrollProfile ssProfile = new DragSortListView.DragScrollProfile() {
        @Override
        public float getSpeed(float w, long t) {
            if (w > 0.8f) {
                // Traverse all views in a millisecond
                return ((float) mMAdapter.getCount()) / 0.001f;
            } else {
                return 10.0f * w;
            }
        }
    };

    private class MAdapter extends SimpleDragSortCursorAdapter {
        public void persistChanges() {
            Cursor c = getCursor();
            c.moveToPosition(-1);
            while (c.moveToNext()) {
                int listPos = getListPosition(c.getPosition());
                if (listPos == REMOVED) {
                    mDbHelper
                            .deleteListRecord(c.getInt(c.getColumnIndex("_id")));
//                    persistChanges();
                } else if (listPos != c.getPosition()) {
                    mDbHelper.updateListPosition(
                            c.getInt(c.getColumnIndex("_id")), listPos);
//                    persistChanges();
                }
            }
        }

        public MAdapter(Context ctxt, int rmid, Cursor c, String[] cols,
                        int[] ids, int something) {
            super(ctxt, rmid, c, cols, ids, something);
            mContext = ctxt;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);

            Cursor cursor = (Cursor) mDslv.getItemAtPosition(position);
            long listId = cursor.getLong(cursor
                    .getColumnIndex(ListsDatabaseAdapter.ITEM_KEY_ROWID));

            ImageView img = (ImageView) v.findViewById(R.id.imageView);
            TextView tv = (TextView) v.findViewById(R.id.item_name);
            String name = tv.getText().toString();
            ImageView imgSync = (ImageView) v.findViewById(R.id.imageViewSync);
            if(mDbHelper.isSyncedList(listId)){
                imgSync.setVisibility(View.VISIBLE);
            }
            else{
                imgSync.setVisibility(View.INVISIBLE);
            }
            img.setImageBitmap(generateCircleBitmap(MainActivity.this,getMaterialColor(listId), 100, name));

            return v;
        }
    }

    public void openDrawerFrameLayout(View view) {
        Intent intent = new Intent(this, ListsActivity.class);
        startActivity(intent);
    }
//
//    public void openDrawerActivity(View view) {
//        Intent intent = new Intent(this, MainActivity3.class);
//        startActivity(intent);
//    }

    public static Bitmap generateCircleBitmap(Context context, int circleColor, float diameterDP, String text){
        final int textColor = 0xffffffff;

        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float diameterPixels = diameterDP * (metrics.densityDpi / 160f);
        float radiusPixels = diameterPixels/2;

        // Create the bitmap
        Bitmap output = Bitmap.createBitmap((int) diameterPixels, (int) diameterPixels,
                Bitmap.Config.ARGB_8888);

        // Create the canvas to draw on
        Canvas canvas = new Canvas(output);
        canvas.drawARGB(0, 0, 0, 0);

        // Draw the circle
        final Paint paintC = new Paint();
        paintC.setAntiAlias(true);
        paintC.setColor(circleColor);
        canvas.drawCircle(radiusPixels, radiusPixels, radiusPixels, paintC);
        String letter = Character.toString(text.charAt(0));
        // Draw the text
        if (letter != null && letter.length() > 0) {
            final Paint paintT = new Paint();
            paintT.setColor(textColor);
            paintT.setAntiAlias(true);
            paintT.setTextSize(radiusPixels/2+30);
            Typeface typeFace = Typeface.createFromAsset(context.getAssets(),"Roboto-Regular.ttf");
            paintT.setTypeface(typeFace);
            final Rect textBounds = new Rect();
            paintT.getTextBounds(letter, 0, letter.length(), textBounds);
            canvas.drawText(letter, radiusPixels - textBounds.exactCenterX(), radiusPixels - textBounds.exactCenterY(), paintT);
        }

        return output;
    }

    private static List<Integer> materialColors = Arrays.asList(
            0xff4fc3f7,
            0xff4dd0e1,
            0xff9575cd,
            0xffe57373,
            0xfff06292,
            0xffba68c8,
            0xff81c784,
            0xff7986cb,
            0xff64b5f6,
            0xffd4e157,
            0xffffd54f,
            0xffffb74d,
            0xff4db6ac,
            0xffaed581,
            0xffff8a65
    );

    private int getMaterialColor(Object key) {
        return materialColors.get(Math.abs(key.hashCode()) % materialColors.size());
    }

    private void logoutUser() {
        session.setLogout(false);

        db.deleteUsers();

        // Launching the login activity
        finish();
        startActivity(getIntent());
        showAccountDialog(false);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

//        switch (item.getItemId()) {
//            case R.id.action_github:
//                String url = "https://github.com/HeinrichReimer/material-drawer";
//                Intent i = new Intent(Intent.ACTION_VIEW);
//                i.setData(Uri.parse(url));
//                startActivity(i);
//                break;
//        }
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_synch) {
            mMAdapter.persistChanges();
            sync();
            displayItemList();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

}