package com.example.abhishek.work;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abhishek.work.Model.ItemData;
import com.example.abhishek.work.ServerOperations.Authentication;
import com.example.abhishek.work.SupportClasses.BlurBuilder;
import com.example.abhishek.work.SupportClasses.CustomEventListeners.ServerResponseListener.OnResponseReceiveListener;
import com.example.abhishek.work.SupportClasses.CustomEventListeners.ServerResponseListener.ServerResponse;
import com.example.abhishek.work.SupportClasses.LocalDatabaseHelper;
import com.example.abhishek.work.adapters.ItemsListAdapter;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.ProcessingInstruction;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private ImageView img;
    private Context context;
    private AppBarLayout appBarLayout;
    private TextView shopNametxt, openCloseTxt, tempTextView;
    private Switch openCloseSwitch;
    private ItemData itemData;
    private FloatingActionButton fab;
    private NavigationView navigationView;

    //Recycler View
    private RecyclerView recyclerView;
    private ItemsListAdapter myListAdapter;
    private ArrayList<ItemData> arrayList;
    private RecyclerView.LayoutManager layoutManager;

    //server
    private Authentication authentication;

    //local database
    private LocalDatabaseHelper databaseHelper;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        context = HomeActivity.this;
        sharedPreferences = getApplicationContext().getSharedPreferences("userdata", MODE_PRIVATE);
        authentication = new Authentication(context);


        //TODO check if database is present in phone
        //TODO if not already present then fetch retailer's products database
        //TODO if present put all in recyclerview


        //TODO    IMPORTANT
        //every time homeActivity starts, check if profile is complete and is verified
        //if both is done then give access to home
        //else make user complete profile and verification both


        //initialize ui components
        appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayoutId);
        shopNametxt = (TextView) findViewById(R.id.shopNameTextviewId);
        openCloseTxt = (TextView) findViewById(R.id.openCloseTextviewId);
        openCloseSwitch = (Switch) findViewById(R.id.openCloseBtnId);
        recyclerView = (RecyclerView) findViewById(R.id.itemslist_recyclerview_id);
        fab = (FloatingActionButton) findViewById(R.id.new_item_fab_id);
        navigationView = (NavigationView) findViewById(R.id.home_activity_navigation_view_id);

        // Make image blur and set as collapsing toolbar Background
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.temp_toolbar_background);
        BlurBuilder blurBuilder = new BlurBuilder();
        Bitmap newImg = blurBuilder.blur(this, bitmap);
        Drawable image = new BitmapDrawable(getResources(), newImg);
        img = (ImageView) findViewById(R.id.collapsingToolbarImageViewId);
        img.setImageDrawable(image);

        //Fade in/out effect for ShopNameText,SwitchBtn,Open/CloseText
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int percent = (appBarLayout.getTotalScrollRange() / 2) + verticalOffset;
                float alpha = (float) ((float) percent / 144);
                shopNametxt.setAlpha(alpha);
                openCloseTxt.setAlpha(alpha);
                openCloseSwitch.setAlpha(alpha);
            }
        });

        //Shop Open/Close switch click listner
        openCloseSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    openCloseTxt.setText("Open");
                } else {
                    openCloseTxt.setText("Closed");
                }
            }
        });

        //fab temporary implementation
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, NewCategoryActivity.class));
            }
        });

        arrayList = new ArrayList<ItemData>();
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        myListAdapter = new ItemsListAdapter(context, arrayList);
        recyclerView.setAdapter(myListAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        //show database data to recycler view


        //navigation draver implementation
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int itemId = item.getItemId();

                if (itemId == R.id.home_nav_menu_home_id) {

                } else if (itemId == R.id.home_nav_menu_manageSS_id) {

                } else if (itemId == R.id.home_nav_menu_profile_id) {

                    startActivity(new Intent(HomeActivity.this, ProfileActivity.class));

                } else if (itemId == R.id.home_nav_menu_orders_id) {

                    startActivity(new Intent(HomeActivity.this, OrdersActivity.class));

                } else if (itemId == R.id.home_nav_menu_membership_id) {

                } else if (itemId == R.id.home_nav_menu_advertise_id) {

                } else if (itemId == R.id.home_nav_menu_contact_id) {

                    startActivity(new Intent(HomeActivity.this, ContactUsActivity.class));

                }

                return false;
            }
        });

        /*
        //server response listener
        authentication.serverResponse.setOnResponseReceiveListener(new OnResponseReceiveListener() {
            @Override
            public void onResponseReceive(JSONObject responseJSONObject) {
                try {
                    String response_from = "";
                    response_from = responseJSONObject.getString("response_from");
                    if (response_from.equals("check_in")) {
                        boolean result = responseJSONObject.getBoolean("result");
                        if (result) {
                            //account in permanent
                            //account is verified

                            //now check id profile is complete or not
                            String email = sharedPreferences.getString("mail", "");
                            if (!email.isEmpty()) {
                                authentication.isProfileDataComplete(email);
                            } else {
                                Toast.makeText(context, "Please Sign In !", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }

                        } else {
                            boolean temp_result = responseJSONObject.getBoolean("temp_result");
                            if (temp_result) {
                                //account not verified
                                //send user to verification activity
                                Toast.makeText(context, "Please verify your email !", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(HomeActivity.this, VerificationActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }
                    } else if (response_from.equals("is_data_filled")) {

                        boolean isDataFilled = responseJSONObject.getBoolean("isDataFilled");
                        if (isDataFilled) {
                            //profile is complete
                        } else {
                            //profile is not complete
                            //send user to profile activity
                            Toast.makeText(context, "Complete your profile !", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        */

        //local database
        databaseHelper = new LocalDatabaseHelper(context);
    }

    @Override
    protected void onStart() {
        super.onStart();

        /*
        //check if email is verified
        String mail = sharedPreferences.getString("mail", "");
        String password = sharedPreferences.getString("password", "");
        if (!mail.isEmpty() && !password.isEmpty()) {
            authentication.checkInPermanent(mail, password);
        } else {
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        */

        arrayList.clear();
        int productsCount = databaseHelper.getProductesCount();
        if (productsCount > 0) {
            Log.e("all products ...|", databaseHelper.getAllProducts().get(0).getName() + "| ... ");
            arrayList.addAll(databaseHelper.getAllProducts());
            myListAdapter.notifyDataSetChanged();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkStateReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkStateReceiver);
    }

    private void updateUI(boolean isNetworkAbailable){
        if (!isNetworkAbailable){
            Toast.makeText(context, "no internet connection", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context, "connected to internet", Toast.LENGTH_SHORT).show();
        }
    }

    private BroadcastReceiver networkStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED){
                    //connected
                    updateUI(true);
                }else {
                    //not connected
                    updateUI(false);
                }
            }
        }
    };
}















