package org.kamsoft.ilschool.login;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.kamsoft.ilschool.BackgroundMail;
import org.kamsoft.ilschool.R;
import org.kamsoft.ilschool.mail.SendMailTask;
import org.kamsoft.ilschool.utils.AlertDialogManager;
import org.kamsoft.ilschool.utils.ConnectivityReceiver;
import org.kamsoft.ilschool.utils.DB;
import org.kamsoft.ilschool.utils.MyApplication;
import org.kamsoft.ilschool.utils.SessionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityLostPassword extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    Button send;
    static final int READ_BLOCK_SIZE = 100;
    EditText etxt_lost_password_NID ,etxt_lost_password_email  ;
    TextInputLayout Layout_lost_password_etxt_NID,Layout_lost_password_etxt_email;

    boolean isConnected;
    AlertDialogManager alert = new AlertDialogManager();
    Connection connect;
    PreparedStatement stmt ;
    ResultSet rs;
    SessionManager session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_password);

        session = new SessionManager(getApplicationContext());
        //session.checkLogin();

        Layout_lost_password_etxt_NID = (TextInputLayout) findViewById(R.id.lost_password_layout_NID);
        Layout_lost_password_etxt_email = (TextInputLayout) findViewById(R.id.lost_password_layout_email);

        etxt_lost_password_NID = (EditText) findViewById(R.id.editText_lost_password_NID);
        etxt_lost_password_email = (EditText) findViewById(R.id.editText_lost_password_Email);


        send = (Button)findViewById(R.id.btn_send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!checkConnection())
                {
                    return;
                }

                if (supmitform()) {
                    try {

                       connect = DB.CONN("univ", "univ", "School", "195.246.40.141");

                        String query = "SELECT   Password  FROM     Users  WHERE NationalID = '"+ etxt_lost_password_NID.getText().toString()+"'  AND  Active = '1' ";

                        Statement statement = connect.createStatement();
                        rs = statement.executeQuery(query);
                        List<Map<String, String>> data = null;
                        data = new ArrayList<Map<String, String>>();
                        while (rs.next()) {
                            Map<String, String> datanum = new HashMap<String, String>();
                            datanum.put("A", rs.getString("Password"));
                            data.add(datanum);
                        }
                        String[] from = { "A" };
//
                        if(data.size() > 0) {
                            sendEmail(data.get(0).get("A"));
                        }


                        //Toast.makeText(getBaseContext(), "Send successfully!",Toast.LENGTH_SHORT).show();

                    } catch (Exception e) {
                        alert.showAlertDialog(ActivityLostPassword.this, "Server Error ..", "Error While Get Information", false);
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    protected void sendEmail(String pass) {
        Log.i("Send email", "");

        String TO = etxt_lost_password_email.getText().toString().trim();


//            BackgroundMail.newBuilder(this)
//                    .withUsername("ahmed.fathy553@gmail.com")
//                    .withPassword("AHMEDNOTPASS")
//                    .withMailto(TO)
//                    .withSubject("استعادة كلمة المرور (مدرسة اللغات الاسلامية)")
//                    .withBody(pass)
//                    .withOnSuccessCallback(new BackgroundMail.OnSuccessCallback() {
//                        @Override
//                        public void onSuccess() {
//                            finish();
//                        }
//                    })
//                    .withOnFailCallback(new BackgroundMail.OnFailCallback() {
//                        @Override
//                        public void onFail() {
//                            //do some magic
//                        }
//                    })
//                    .send();

    }

    private void lostpasswordvalidate() {
        if (!validateN_ID()) {
            return;
        }

        if (!validateEmail()) {
            return;
        }

        final String N_ID = etxt_lost_password_NID.getText().toString();
        final String Email = etxt_lost_password_email.getText().toString();


     }

        //Adding request to request queue

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
    // Validating name
    private boolean validateN_ID() {
        if (etxt_lost_password_NID.getText().toString().trim().isEmpty()) {
            Layout_lost_password_etxt_NID.setError(getString(R.string.err_msg_nationalID));
            requestFocus(etxt_lost_password_NID);
            return false;
        } else {
            Layout_lost_password_etxt_NID.setErrorEnabled(false);
        }

        return true;
    }

    // Validating email
    private boolean validateEmail() {
        String email = etxt_lost_password_email.getText().toString().trim();

        if (email.isEmpty()  || !isValidEmail(email) ) {
            Layout_lost_password_etxt_email.setError(getString(R.string.err_msg_password));
            requestFocus(etxt_lost_password_email);
            return false;
        } else {
            Layout_lost_password_etxt_email.setErrorEnabled(false);
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
        MyApplication.getInstance().setConnectivityListener(ActivityLostPassword.this);
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
        if (etxt_lost_password_NID.getText().toString().trim().isEmpty()) {
            etxt_lost_password_NID.setError("ادخل الرقم القومي");
            return false;
        }
        if (etxt_lost_password_email.getText().toString().trim().isEmpty()) {
            etxt_lost_password_email.setError("ادخل الايميل");
            return false;
        }

        return true;
    }


}

