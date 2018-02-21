package org.kamsoft.ilschool.login;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.kamsoft.ilschool.HomeActivity;
import org.kamsoft.ilschool.R;
import org.kamsoft.ilschool.utils.AlertDialogManager;
import org.kamsoft.ilschool.utils.ConnectivityReceiver;
import org.kamsoft.ilschool.utils.DB;
import org.kamsoft.ilschool.utils.MyApplication;
import org.kamsoft.ilschool.utils.SessionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ActivityRegister extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    Button signup;
    static final int READ_BLOCK_SIZE = 100;
    EditText etxt_register_NID ,etxt_register_email , etxt_register_phone ;
    TextInputLayout Layout_register_etxt_NID,Layout_register_etxt_email,Layout_register_etxt_phone;

    boolean isConnected;
    AlertDialogManager alert = new AlertDialogManager();
    Connection connect;
    PreparedStatement stmt ;
    ResultSet rs;
    SessionManager session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        session = new SessionManager(getApplicationContext());
        //session.checkLogin();

        Layout_register_etxt_NID = (TextInputLayout) findViewById(R.id.register_layout_NID);
        Layout_register_etxt_email = (TextInputLayout) findViewById(R.id.register_layout_email);
        Layout_register_etxt_phone = (TextInputLayout) findViewById(R.id.register_layout_phone);
        etxt_register_NID = (EditText) findViewById(R.id.editText_register_NID);
        etxt_register_email = (EditText) findViewById(R.id.editText_register_Email);
        etxt_register_phone = (EditText) findViewById(R.id.editText_register_phone);

        signup = (Button)findViewById(R.id.btn_signup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!checkConnection())
                {
                    return;
                }

                if (supmitform()) {
                    connect = DB.CONN("univ", "univ", "School", "195.246.40.141");

                    String query = "Insert Into Users  ( NationalID, Password, E_Mail,Mobile ) VALUES ('"+etxt_register_NID.getText().toString()+"','123','"+etxt_register_email.getText().toString()+"','"+etxt_register_phone.getText().toString()+"')   ";
                        try {

                            Statement statement = connect.createStatement();
                            rs = statement.executeQuery(query);

                        } catch (SQLException e) {
                            e.printStackTrace();
                            alert.showAlertDialog(ActivityRegister.this, "Server Error ..", "Error While Get Information", false);
                        }
                       Intent intent = new Intent(getApplicationContext(), Activitylogin.class);

                       //session.createLoginSession("","123","",etxt_register_NID.getText().toString(),"");

                        startActivity(intent);
                        finish();

                }
            }
        });
    }



    private void registervalidate() {
        if (!validateN_ID()) {
            return;
        }

        if (!validateEmail()) {
            return;
        }
        if (!validatephone()) {
            return;
        }
        final String N_ID = etxt_register_NID.getText().toString();
        final String Email = etxt_register_email.getText().toString();
        final String phone = etxt_register_phone.getText().toString();

                try {

                    // check for error flag
                    //if (obj.getBoolean("error") == false) {
                    if (1 == 1) {

                        session.createLoginSession("","","",N_ID,"");
                        // storing user in shared preferences

                        // start main activity
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        finish();

                    } else {
                        // login error - simply toast the message
                        //Toast.makeText(getApplicationContext(), "" + obj.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                   // Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "Json parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

        //Adding request to request queue

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
    // Validating name
    private boolean validateN_ID() {
        if (etxt_register_NID.getText().toString().trim().isEmpty()) {
            Layout_register_etxt_NID.setError(getString(R.string.err_msg_nationalID));
            requestFocus(etxt_register_NID);
            return false;
        } else {
            Layout_register_etxt_NID.setErrorEnabled(false);
        }

        return true;
    }

    // Validating email
    private boolean validateEmail() {
        String email = etxt_register_email.getText().toString().trim();

        if (email.isEmpty()  || !isValidEmail(email) ) {
            Layout_register_etxt_email.setError(getString(R.string.err_msg_password));
            requestFocus(etxt_register_email);
            return false;
        } else {
            Layout_register_etxt_email.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatephone() {
        String phone = etxt_register_phone.getText().toString().trim();
        if (phone.isEmpty() ) {
            Layout_register_etxt_phone.setError(getString(R.string.err_msg_password));
            requestFocus(etxt_register_phone);
            return false;
        } else {
            Layout_register_etxt_phone.setErrorEnabled(false);
        }
        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.editText_register_NID:
                    validateN_ID();
                    break;
                case R.id.editText_register_Email:
                    validateEmail();
                    break;
                case R.id.editText_register_phone:
                    validatephone();
                    break;
            }
        }
    }



    // Method to manually check connection status
    private boolean checkConnection() {
        if (ConnectivityReceiver.isConnected()) {
            isConnected = true;
            showSnack(isConnected);
            return true;
        } else {
            isConnected = false;
            showSnack(isConnected);
            return false;
        }
    }



    // Showing the status in Snackbar
    private void showSnack(boolean isConnected) {
        String message;
        int color;
        if (!isConnected) {
            message = "يرجى التحقق من الاتصال بالانترنت";
            color = Color.WHITE;

            Snackbar snackbar = Snackbar.make(findViewById(R.id.registerlayout), message, Snackbar.LENGTH_LONG);

            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(color);
            textView.setBackgroundColor(Color.DKGRAY);
            snackbar.show();
        }
//        else {
//            message = "Good! Connected to Internet";
//            color = Color.WHITE;
//        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        // register connection status listener
        MyApplication.getInstance().setConnectivityListener(ActivityRegister.this);
    }

    /**
     * Callback will be triggered when there is change in
     * network connection
     */

    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

    private boolean supmitform() {
        if(!Checkinputs())
        {
            return false;
        }
        return true;
    }

    private boolean Checkinputs() {
        if (etxt_register_NID.getText().toString().trim().isEmpty()) {
            etxt_register_NID.setError("ادخل الرقم القومي");
            return false;
        }
        if (etxt_register_email.getText().toString().trim().isEmpty()  || !isValidEmail(etxt_register_email.getText().toString().trim()) ) {
            etxt_register_email.setError("ادخل الايميل");
            requestFocus(etxt_register_email);
            return false;
        }
        if (etxt_register_phone.getText().toString().trim().isEmpty()) {
            etxt_register_phone.setError("ادخل رقم الموبايل");
            return false;
        }
        return true;
    }


}

