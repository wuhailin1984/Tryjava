package com.example.tryjava;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class User_Login extends AppCompatActivity {

    private Context context;
    private ProgressDialog dialog;
    EditText editTextInputUserName = null;
    EditText editTextInputUserPassword = null;
    Button buttonLogin = null;

    // return data to main thread
    static Handler handler = new Handler();
    private String info;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user__login);

        context = this;
        buttonLogin=(Button) findViewById(R.id.button_Login);
        editTextInputUserName = (EditText)findViewById(R.id.editText_UserName);
        editTextInputUserPassword = (EditText)findViewById(R.id.editText_UserPassword);
    }

    public void button_Login_click(View v) {

        String stringUserName=editTextInputUserName.getText().toString();
        String stringUserPassword=editTextInputUserPassword.getText().toString();

        dialog = new ProgressDialog(this);
        dialog.setTitle("status");
        dialog.setMessage("Try to login in，please wait ...");
        dialog.setCancelable(false);
        dialog.show();
        new Thread(new MyThread()).start();
    }


    // sub thread for receiving data, main thread for modifying data
    public class MyThread implements Runnable {
        @Override
        public void run() {
            info = WebService.executeHttpGet(editTextInputUserName.getText().toString(), editTextInputUserPassword.getText().toString());
            handler.post(   new Runnable() {
                @Override
                public void run() {
                    if(info.equalsIgnoreCase("Successed")){
                        dialog.dismiss();
                        //sleep(1000);
                        Intent intent = new Intent(User_Login.this, MainActivity.class);
                        startActivity(intent);
                    }
                    else if(info.equalsIgnoreCase("Login Failed")){
                        dialog.dismiss();
                        editTextInputUserName.setText(info);
                    }
                }
            });
        }
    }


    private boolean checkNetwork() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager.getActiveNetworkInfo() != null) {
            return connManager.getActiveNetworkInfo().isAvailable();
        }
        return false;
    }

}
