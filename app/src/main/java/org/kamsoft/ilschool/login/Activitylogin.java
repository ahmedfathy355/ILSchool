package org.kamsoft.ilschool.login;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.kamsoft.ilschool.HomeActivity;
import org.kamsoft.ilschool.utils.AlertDialogManager;
import org.kamsoft.ilschool.R;
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

public class Activitylogin extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    Button login,sign_up;
    static final int READ_BLOCK_SIZE = 100;
    EditText etxt_NID ,etxt_password ;
    TextView lostpassword_link;
    TextInputLayout Layout_etxt_NID,Layout_etxt_password;
    CheckBox remember;
    boolean isConnected;
    AlertDialogManager alert = new AlertDialogManager();
    Connection connect;
    PreparedStatement stmt ;
    ResultSet rs;
    SessionManager session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        /**
         * Check for login session. It user is already logged in
         * redirect him to main activity
         * */
//        if (MyApplication.getInstance().getPrefManager().getUser() != null) {
//            startActivity(new Intent(this, HomeActivity.class));
//            finish();
//        }


        session = new SessionManager(getApplicationContext());
        //session.checkLogin();
        try {

            //session.checkLogin();
            HashMap<String, String> user = session.getUserDetails();
            if(!user.get(SessionManager.NationalID).isEmpty()) {
                Intent in3 = new Intent(Activitylogin.this, HomeActivity.class);
                startActivity(in3);
                finish();
            }
        } catch (Exception e) {
            return;
        }

        Layout_etxt_NID = (TextInputLayout) findViewById(R.id.input_layout_NID);
        Layout_etxt_password = (TextInputLayout) findViewById(R.id.input_layout_pass);
        etxt_NID = (EditText) findViewById(R.id.editText_NID);
        etxt_password = (EditText) findViewById(R.id.editText_Password);
        remember = (CheckBox) findViewById(R.id.Remember) ;
        login = (Button)findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!checkConnection())
                {
                    return;
                }

                if (supmitform()) {
                    try {
//                        HashMap<String, String> server = session.getServerDetails();
//                        String server_ip = server.get(SessionManager.Server_IP);
//                        if(ss == null)
//                        {
//                            alert.showAlertDialog(Activitylogin.this, "Setting is incorrect..", "ادخل بيانات السيرفر", false);
//                            return;
//                            }
//                        else
//                        {
                           connect = DB.CONN("univ", "univ", "School", "195.246.40.141");
//                        }

                        String query = "SELECT  NationalID, Password, Mobile, E_Mail  FROM     Users WHERE NationalID = '"+etxt_NID.getText().toString()+"' AND Password = '"+etxt_password.getText().toString()+"'  AND  Active = '1' ";
                        //String query = "SELECT UserName, Password FROM     Users   WHERE  (UserName = N'Admin') AND (Password = N'iFVeAZQcVxh4LIfDTuqDG/Fr7WicTV7y')";
                        Statement statement = connect.createStatement();
                        rs = statement.executeQuery(query);
                        List<Map<String, String>> data = null;
                        data = new ArrayList<Map<String, String>>();
                        while (rs.next()) {
                            Map<String, String> datanum = new HashMap<String, String>();
                            datanum.put("A", rs.getString("NationalID"));
                            datanum.put("B", rs.getString("Password"));
                            datanum.put("C", rs.getString("Mobile"));
                            datanum.put("D", rs.getString("E_Mail"));
                            data.add(datanum);
                        }
                        String[] from = { "A" ,"B","C","D"};
//
                        if(data.size() > 0){
                           Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                            if(remember.isChecked())
                            {
                                session.createLoginSession("",data.get(0).get("B"),"",data.get(0).get("A"),"");
                            }
                            else
                            {
                                Bundle b = new Bundle();
                                b.putString("NationalID", data.get(0).get("A"));
                                intent.putExtras(b);
                            }
                            startActivity(intent);
                            finish();
                            Toast.makeText(getBaseContext(), "Login successfully!",Toast.LENGTH_SHORT).show();
                        }else{
                            alert.showAlertDialog(Activitylogin.this, "Login failed..", "يرجى التوجة للمدرسة للإشتراك فى الخدمة أولا", false);
                        }
                    } catch (Exception e) {
                        alert.showAlertDialog(Activitylogin.this, "Server Error ..", "Error While Get Information", false);
                        e.printStackTrace();
                    }
                }
            }
        });



        sign_up = (Button) findViewById(R.id.btn_sign_up);
        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newaccount = new Intent(Activitylogin.this  , ActivityRegister.class);
                startActivity(newaccount);
            }
        });

        lostpassword_link = (TextView)findViewById(R.id.lostpassword_link);
        String htmlString="<u>نسيت كلمة السر ؟</u>";
        lostpassword_link.setText(Html.fromHtml(htmlString));
        lostpassword_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newpass = new Intent(Activitylogin.this  , ActivityLostPassword.class);
                startActivity(newpass);
            }
        });
    }



    private void loginvalidate() {
        if (!validateN_ID()) {
            return;
        }

        if (!validatepassword()) {
            return;
        }

        final String N_ID = etxt_NID.getText().toString();
        final String Pass = etxt_password.getText().toString();

                try {

                    // check for error flag
                    //if (obj.getBoolean("error") == false) {
                    if (1 == 1) {

                        session.createLoginSession("",Pass,"",N_ID,"");
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
        if (etxt_NID.getText().toString().trim().isEmpty()) {
            Layout_etxt_NID.setError(getString(R.string.err_msg_nationalID));
            requestFocus(etxt_NID);
            return false;
        } else {
            Layout_etxt_NID.setErrorEnabled(false);
        }

        return true;
    }

    // Validating email
    private boolean validatepassword() {
        String pass = etxt_password.getText().toString().trim();
//|| !isValidEmail(email)
        if (pass.isEmpty() ) {
            Layout_etxt_password.setError(getString(R.string.err_msg_password));
            requestFocus(etxt_password);
            return false;
        } else {
            Layout_etxt_password.setErrorEnabled(false);
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
                case R.id.editText_NID:
                    validateN_ID();
                    break;
                case R.id.editText_Password:
                    validatepassword();
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

            Snackbar snackbar = Snackbar.make(findViewById(R.id.loginlayout), message, Snackbar.LENGTH_LONG);

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
        MyApplication.getInstance().setConnectivityListener(Activitylogin.this);
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
        if (etxt_NID.getText().toString().trim().isEmpty()) {
            etxt_NID.setError("ادخل الرقم القومي");
            return false;
        }
        if (etxt_password.getText().toString().trim().isEmpty()) {
            etxt_password.setError("ادخل كلمة المرور");
            return false;
        }
        return true;
    }


}

