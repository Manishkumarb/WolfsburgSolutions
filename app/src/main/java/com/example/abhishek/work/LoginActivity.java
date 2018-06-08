package com.example.abhishek.work;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.abhishek.work.ServerOperations.Authentication;
import com.example.abhishek.work.SupportClasses.NetworkStatusChecker;
import com.example.abhishek.work.SupportClasses.CustomEventListeners.ServerResponseListener.OnResponseReceiveListener;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int RC_SIGN_IN = 105;

    private Context context;

    //Check sign in status
    private Boolean isSignedIn = false;

    //Ui components
    private EditText mail_edittext, password_edittext;
    private Button signin_btn, signUp_link_btn;
    private SignInButton googleSignIn_btn;
    private ProgressDialog progressDialog;

    //GoogleSignIn components
    private GoogleSignInOptions googleSignInOptions;
    private GoogleSignInClient googleSignInClient;
    private GoogleSignInAccount googleSignInAccount;

    //User Data
    private String mail = "";
    private String password = "";

    //Check if connected to internet or not
    NetworkStatusChecker networkStatusChecker;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = LoginActivity.this;
        sharedPreferences = getApplicationContext().getSharedPreferences("userdata", MODE_PRIVATE);
        editor = sharedPreferences.edit();

/*
        networkStatusChecker = new NetworkStatusChecker(this);

        if (!networkStatusChecker.isConnected()) {
            //show dialog
            AlertDialog.Builder noNetworkDialog = new AlertDialog.Builder(this);
            noNetworkDialog.setTitle("No Internet Connection");
            noNetworkDialog.setMessage("Please connect to internet");
            noNetworkDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialogInterface, int i) {

                    Runnable waitTwoSec = new Runnable() {
                        @Override
                        public void run(){
                            if(networkStatusChecker.isConnected()){
                                dialogInterface.dismiss();
                                checkSignInStatus();
                            }
                        }
                    };
                    Handler h = new Handler();
                    h.postDelayed(waitTwoSec, 100);

                }
            });
            noNetworkDialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });

        } else {
            checkSignInStatus();
        }
*/


        checkSignInStatus();
        if (!isSignedIn) {
            setContentView(R.layout.activity_login);

            Log.e("mail_edittext",(mail_edittext==null ? "null" : "not null"));
            mail_edittext = (EditText) findViewById(R.id.email_edittext_login_id);
            Log.e("mail_edittext",(mail_edittext==null ? "null" : "not null"));
            password_edittext = (EditText) findViewById(R.id.password_edittext_login_id);
            signin_btn = (Button) findViewById(R.id.signIn_btn_id);
            signUp_link_btn = (Button) findViewById(R.id.sign_up_link_btn_id);
            googleSignIn_btn = (SignInButton) findViewById(R.id.google_signIn_btn_id);
            googleSignIn_btn.setSize(SignInButton.SIZE_STANDARD);

            signin_btn.setOnClickListener(this);
            signUp_link_btn.setOnClickListener(this);
            googleSignIn_btn.setOnClickListener(this);

            //Google Sign In Configuration
            googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
            googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);


        } else {

            startActivity(new Intent(LoginActivity.this, HomeActivity.class));

            //Deprecated
            //checkData();
        }

    }


    public void checkData() {
        if (mail != null || !mail.isEmpty()) {
            Authentication authentication = new Authentication(LoginActivity.this);
            //authentication.checkData(mail);

            authentication.serverResponse.setOnResponseReceiveListener(new OnResponseReceiveListener() {
                @Override
                public void onResponseReceive(JSONObject responseJSONObject) {

                    try {
                        String isAvailable = responseJSONObject.getString("isDataAvailable");

                        if (isAvailable.equals("true")) {
                            //code if user is signin in and required data is available
                        } else {
                            //code if user is signed in but data not available
                            startActivity(new Intent(LoginActivity.this, ProfileActivity.class));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    }

    private void checkSignInStatus() {

        isSignedIn = sharedPreferences.getBoolean("isSignedIn", false);


        //Deprecated
        /*
        googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (googleSignInAccount == null) {
            isSignedIn = false;
        } else {
            isSignedIn = true;
            mail = googleSignInAccount.getmail();
        }
        */
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.google_signIn_btn_id) {
            //Deprecated
            //SignInWithGoogle();
        }

        if (view.getId() == R.id.signIn_btn_id) {
            Log.e("edittext",mail_edittext.getText().toString() + " ... ... ");
            mail = mail_edittext.getText().toString();
            password = password_edittext.getText().toString();

            if (!TextUtils.isEmpty(mail) || !TextUtils.isEmpty(password)) {

                if (Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {

                    final Authentication authentication = new Authentication(LoginActivity.this);
                    authentication.signInWithEmail(mail, password);

                    authentication.serverResponse.setOnResponseReceiveListener(new OnResponseReceiveListener() {
                        @Override
                        public void onResponseReceive(JSONObject responseJSONObject) {

                            try {
                                String response_from = responseJSONObject.getString("response_from");
                                if (response_from.equals("check_in")) {

                                    boolean result = responseJSONObject.getBoolean("result");
                                    if (result) {
                                        //sign in success
                                        boolean isPasswordCorrect = responseJSONObject.getBoolean("isPasswordCorrect");
                                        if (isPasswordCorrect) {
                                            //set isSignedIn = true in sharedPref
                                            editor.putBoolean("isSignedIn", true);
                                            editor.putString("mail", mail);
                                            editor.putString("password", password);

                                            JSONArray jsonArray = responseJSONObject.getJSONArray("data");
                                            Log.e("jsonArray", jsonArray.toString());
                                            JSONObject jsonObject = (JSONObject) jsonArray.get(0);
                                            Log.e("jsonObject", jsonObject.toString());
                                            int retailerId = jsonObject.getInt("RetailerID");
                                            Log.e("retailerId", String.valueOf(retailerId));
                                            editor.putInt("retailerId", retailerId);
                                            editor.commit();

                                            //check if data is complete
                                            authentication.isProfileDataComplete(mail);

                                        } else {
                                            //show popup that password is wrong
                                            Toast.makeText(context, "Wrong Password !", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        //sign in failed
                                        boolean isFoundInTemp = responseJSONObject.getBoolean("temp_result");
                                        if (isFoundInTemp) {

                                            //not verified account send to verification activity

                                            editor.putBoolean("isSignedIn", true);
                                            editor.putString("mail", mail);
                                            editor.putString("password", password);

                                            JSONArray jsonArray = responseJSONObject.getJSONArray("data");
                                            Log.e("jsonArray", jsonArray.toString());
                                            JSONObject jsonObject = (JSONObject) jsonArray.get(0);
                                            Log.e("jsonObject", jsonObject.toString());
                                            int retailerId = jsonObject.getInt("RetailerID");
                                            Log.e("retailerId", String.valueOf(retailerId));

                                            editor.putInt("retailerId", retailerId);
                                            editor.commit();

                                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                            builder.setMessage("Please complete mail verification !");
                                            builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    Intent intent = new Intent(LoginActivity.this, VerificationActivity.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent);
                                                }
                                            });
                                            builder.setCancelable(false);
                                            AlertDialog dialog = builder.create();
                                            dialog.show();

                                            //Deprecated
                                        /*
                                        boolean isDataComplete = responseJSONObject.getBoolean("isDataComplete");
                                        if (isDataComplete) {
                                            //go to home
                                            //with full access
                                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        } else {
                                            //go to profile page
                                            Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        }
                                        */

                                        } else {
                                            //account not exist
                                            //go to sign up
                                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                            builder.setTitle("Account does not exist");
                                            builder.setMessage("Do you want to Sign Up ?");
                                            builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent);
                                                }
                                            });
                                            builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                }
                                            });
                                            builder.create();
                                            builder.show();
                                        }
                                    }
                                }else if (response_from.equals("is_data_filled")){

                                    boolean isDataFilled = responseJSONObject.getBoolean("isDataFilled");
                                    if (isDataFilled){
                                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }else {
                                        Toast.makeText(context, "Complete your profile !", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                } else {
                    Toast.makeText(context, "check yout mail", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Enter mail and password", Toast.LENGTH_SHORT).show();
            }
        }

        if (view.getId() == R.id.sign_up_link_btn_id) {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }


    //Deprecated
    /*
    private void SignInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {

                googleSignInAccount = task.getResult(ApiException.class);

                //now signed in successfully
                //code after successfull sign in
                mail = googleSignInAccount.getmail();
                checkData();


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    */

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Do you want to Exit ?");
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create();
        builder.show();
    }
}
