package pl.pwr.mipo.mipoo;

/**
 * Created by CitrusPanc on 5/2/2015.
 */
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.pwr.mipo.mipoo.app.AppConfig;
import pl.pwr.mipo.mipoo.app.AppController;
import pl.pwr.mipo.mipoo.db.SessionManager;
import pl.pwr.mipo.mipoo.zxing.IntentIntegrator;
import pl.pwr.mipo.mipoo.zxing.IntentResult;

public class ScanActivity extends ActionBarActivity {

    private TextView tvbarcode;
    private TextView tvtyp;
    SessionManager session;
    EditText inputPrice;

    private View positiveAction;
    private float priceFloat;

    private ProgressDialog pDialog;
    private List<ScannedProduct> productList = new ArrayList<ScannedProduct>();

    private String barcode = null;
    private String typ = null;

    public static final String KEY_LIST = "list";
    public static final String KEY_PRICE = "price";

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null)
        {
            barcode = scanResult.getContents();
            typ = scanResult.getFormatName();
//            StringBuilder stringBuilder = new StringBuilder();
//            Log.i((String) "App", (String) stringBuilder.append("Code = ").append(string).toString());
//            StringBuilder stringBuilder2 = new StringBuilder();
//            Log.i((String) "App", (String) stringBuilder2.append("Format = ").append(string2).toString());
//            TextView textView = (TextView) this.findViewById(2131427405);
//            TextView textView2 = (TextView) this.findViewById(2131427407);
//            textView.setText((CharSequence) string);
//            textView2.setText((CharSequence) string2);

            tvbarcode.setText("Barcode " + barcode);


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

    public void onClick(View view) {
//        this.fragmentTransaction = this.getSupportFragmentManager().beginTransaction();
        switch (view.getId()) {
            default: {
                return;
            }
            case R.id.btn_scan: {
                IntentIntegrator intentIntegrator = new IntentIntegrator(this);
                intentIntegrator.initiateScan();
                break;
            }
        }

    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.setContentView(R.layout.activity_btnscan);

        tvbarcode = (TextView) findViewById(R.id.textView);
        tvtyp = (TextView) findViewById(R.id.textView2);

        tvbarcode.setText("Barcode " + barcode);


    }

    private void checkCode(final String barcode) {
        // Tag used to cancel the request
        String tag_string_req = "req_scan";

//        pDialog.setMessage("Logging in ...");
//        showDialog();

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();

        JsonArrayRequest strReq = new JsonArrayRequest(Request.Method.POST,
                AppConfig.URL_REGISTER,new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("TAG", "barcode: " + barcode);
                Log.d("TAG", "Login Response: " + response.toString());
//                hideDialog();
//                Log.d("RESPONSE", response.toString());
                hidePDialog();

                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject obj = response.getJSONObject(i);
                        boolean error = obj.getBoolean("error");


                        // Check for error node in json
                        if (!error) {

                            ScannedProduct scanInfo = new ScannedProduct();

//                        scanInfo.setBarcode(obj.getString("barcode"));
                           scanInfo.setProductName(obj.getString("name"));
                           scanInfo.setPrice(Float.parseFloat(obj.getString("price")));
                           scanInfo.setStoreName(obj.getString("shop"));


                           productList.add(scanInfo);
                           // user successfully logged in
                           // Create login session

//                        session.setLogin(true);

//                        String errorMsg = "Wszystko jest zajebiscie";
//                        Toast.makeText(getApplicationContext(),
//                                errorMsg, Toast.LENGTH_LONG).show();
//                        Launch main activity
                            tvtyp.setText(" success ");
//                        finish();
//                        startActivity(getIntent());

//                        showAccountDialog(true);



                        } else {
                            // Error in login. Get the error message

                            String errorMsg = obj.getString("error_msg");
                            tvtyp.setText(" Error: " + errorMsg);
                            Toast.makeText(getApplicationContext(),
                                    "An error occured " + errorMsg, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        // JSON error
                        e.printStackTrace();
                    }
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
                params.put("tag", "scan");
                params.put("barcode", barcode);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);




    }

    private void showRegisterDialog() {


        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.enter_price)
                .customView(R.layout.dialog_price, true)
                .positiveText(R.string.ok)
                .negativeText(android.R.string.cancel)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {

                        priceFloat = Float.parseFloat(inputPrice.getText().toString());

                        Intent intent = new Intent(ScanActivity.this, ScanResultActivity.class);
                        intent.putExtra(KEY_PRICE, priceFloat);
                        intent.putExtra(KEY_LIST, (ArrayList<ScannedProduct>) productList);
                        startActivity(intent);

                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {

                    }

                }).build();

        inputPrice = (EditText) dialog.getCustomView().findViewById(R.id.price);
//        inputRegEmail = (EditText) dialog.getCustomView().findViewById(R.id.email);
//        inputRegPassword = (EditText) dialog.getCustomView().findViewById(R.id.password);
        positiveAction = dialog.getActionButton(DialogAction.POSITIVE);

        inputPrice.addTextChangedListener(new TextWatcher() {
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
        positiveAction.setEnabled(false); // disabled by default

    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }
//    public boolean onCreateOptionsMenu(Menu menu) {
//        this.getMenuInflater().inflate(2131689472, menu);
//        return true;
//    }
//
//    public boolean onOptionsItemSelected(MenuItem menuItem) {
//        if (menuItem.getItemId() == 2131427454) {
//            return true;
//        }
//        return super.onOptionsItemSelected(menuItem);
//    }  /* * Failed to analyse overrides */
//    public static class AfterScanFragment extends Fragment {
//        private String current = "";
//        private EditText etPrice;
//        private TextView tvBarcodeFormat;
//        private TextView tvBarcodeId;
//
//        public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
//            View view = layoutInflater.inflate(2130903065, viewGroup, false);
//            this.tvBarcodeId = (TextView) view.findViewById(2131427435);
//            this.tvBarcodeFormat = (TextView) view.findViewById(2131427439);
//            this.etPrice = (EditText) view.findViewById(2131427422);
//            this.etPrice.setRawInputType(3);
//            return view;
//        }
//
//        public void updateTextBarcodeID(String string, String string2) {
//            this.tvBarcodeId.setText((CharSequence) string);
//            this.tvBarcodeFormat.setText((CharSequence) string2);
//        }
//    }  /* * Failed to analyse overrides */

//    public static class PlaceholderFragment extends Fragment {
//        public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
//            return layoutInflater.inflate(2130903066, viewGroup, false);
//        }
//    }
}
