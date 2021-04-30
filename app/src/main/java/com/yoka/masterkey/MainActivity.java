package com.yoka.masterkey;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.yoka.masterkey.library.ConnectionHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MainActivity extends AppCompatActivity {

EditText usernameTxt, passwordTxt;
Button login;
SharedPreferences sharedPreferences;
ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        usernameTxt = findViewById(R.id.usernameTxt);
        passwordTxt = findViewById(R.id.passwordTxt);

        progressBar = findViewById(R.id.pbbar);
        progressBar.setVisibility(View.GONE);

        login = findViewById(R.id.loginBtn);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DoLogin(view);
            }
        });
    }

    private class DoLoginForUser extends AsyncTask<String, Void, String> {
        String username, password;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            username = usernameTxt.getText().toString();
            password = passwordTxt.getText().toString();
            progressBar.setVisibility(View.VISIBLE);
            login.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                ConnectionHelper con = new ConnectionHelper();
                Connection connect = ConnectionHelper.CONN();

                String query = "Select * from AccountUsers where Username='" + username + "'";
                PreparedStatement ps = connect.prepareStatement(query);

                Log.e("query", query);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    String passcode = rs.getString("password");
                    connect.close();
                    rs.close();
                    ps.close();
                    if (passcode != null && !passcode.trim().equals("") && passcode.equals(password))
                        return "success";
                    else
                        return "Invalid Credentials";

                } else
                    return "User does not exists.";
            } catch (SQLException e) {
                return "Error:" + e.getMessage().toString();
            } catch (Exception e) {
                return "Error:" + e.getMessage().toString();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);
            login.setVisibility(View.VISIBLE);
            if (result.equals("success")) {
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("userdetails", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString("email", usernameTxt.getText().toString());

                editor.commit();

                Intent i = new Intent(MainActivity.this, DashboardActivity.class);
                startActivity(i);
            }
        }
    }

    public void DoLogin(View v)
    {
        DoLoginForUser login = new DoLoginForUser();
        login.execute("");
    }
}