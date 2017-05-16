package com.martimbang.mawan.instagram;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {

    TextView changesSignupModeTextView;
    EditText passwordEditText;

    Boolean signUpModeActive = true;

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
            signUp(v);
        }
        return false;
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == changesSignupModeTextView.getId()) {
//            Log.i("AppInfo", "Change Signup Mode");
            Button signupButton = (Button) findViewById(R.id.signupButton);
            if (signUpModeActive) {
                signUpModeActive = false;
                signupButton.setText("LOGIN");
                changesSignupModeTextView.setText("Or, Signup");
            } else {
                signUpModeActive = true;
                signupButton.setText("SIGN UP");
                changesSignupModeTextView.setText("Or, Login");
            }

        }

        if (v.getId() == R.id.backgroundConstraintLayout || v.getId() == R.id.logoImageView) {
            Log.i("onClick", "Clicking background");
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }

    }

    public void showUserList() {
        Intent intent = new Intent(getApplicationContext(), userlistActivity.class);
        startActivity(intent);
    }

    public void signUp(View view) {
        EditText usernameEditText = (EditText)findViewById(R.id.usernameEditText);

        Log.i("Signup", "username length: " + usernameEditText.getText().toString().length());
        Log.i("Signup", "password length: " + passwordEditText.getText().toString().length());


        if (usernameEditText.getText().toString().matches("") || passwordEditText.getText().toString().matches("")) {
            Toast.makeText(this, "A username and password are required", Toast.LENGTH_SHORT).show();
        } else {

            if (signUpModeActive) {
                ParseUser user = new ParseUser();
                user.setUsername(usernameEditText.getText().toString());
                user.setPassword(usernameEditText.getText().toString());

                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.i("Signup", "Successful");
                            showUserList();
                        } else {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                ParseUser.logInInBackground(usernameEditText.getText().toString(), passwordEditText.getText().toString(), new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if (user != null) {
                            Log.i("Signup", "Login successful");
                            showUserList();
                        } else {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
            .server("http://ec2-54-169-88-212.ap-southeast-1.compute.amazonaws.com:80/parse/")
            .applicationId("a1955b70fe94a33951d12388959e5cc364a9ea98")
            .build());

        changesSignupModeTextView = (TextView) findViewById(R.id.changesSignupModeTextView);
        changesSignupModeTextView.setOnClickListener(this);

        ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.backgroundConstraintLayout);
        ImageView imageView = (ImageView) findViewById(R.id.logoImageView);

        constraintLayout.setOnClickListener(this);
        imageView.setOnClickListener(this);

        passwordEditText = (EditText) findViewById(R.id.passwordEditText);

        passwordEditText.setOnKeyListener(this);

        if (ParseUser.getCurrentUser() != null) {
            Log.i("current user", ParseUser.getCurrentUser().getUsername());
            showUserList();
        }

        ParseAnalytics.trackAppOpenedInBackground(getIntent());

    }

}
