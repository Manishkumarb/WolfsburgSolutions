package com.example.abhishek.work;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.res.Configuration;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.abhishek.work.Model.ProductData;
import com.example.abhishek.work.ServerOperations.FetchData;
import com.example.abhishek.work.Model.CategoryData;
import com.example.abhishek.work.SupportClasses.CustomEventListeners.ServerResponseListener.OnResponseReceiveListener;
import com.example.abhishek.work.SupportClasses.CustomEventListeners.ServerResponseListener.ServerResponse;
import com.example.abhishek.work.ViewModels.CategoriesViewModel;
import com.example.abhishek.work.adapters.CategoryListAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NewCategoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CategoryListAdapter adapter;
    private CategoriesViewModel categoriesViewModel;

    private FetchData fetchData;
    private ArrayList<CategoryData> arrayList;
    private JSONArray jsonArray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_category);

        recyclerView = (RecyclerView) findViewById(R.id.new_category_activity_recycler_view_id);
        arrayList = new ArrayList<>();
        adapter = new CategoryListAdapter(NewCategoryActivity.this, arrayList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        fetchData = new FetchData(getApplication());

        categoriesViewModel = ViewModelProviders.of(this).get(CategoriesViewModel.class);
        categoriesViewModel.getCategories(fetchData).observe(this, new Observer<ArrayList<CategoryData>>() {
            @Override
            public void onChanged(@Nullable ArrayList<CategoryData> categoryData) {
                arrayList.addAll(categoryData);
                adapter.notifyDataSetChanged();
            }
        });


        ServerResponse serverResponse = fetchData.getServerResponseInstance();
        serverResponse.setOnResponseReceiveListener(new OnResponseReceiveListener() {
            @Override
            public void onResponseReceive(JSONObject responseJSONObject) {
                try {
                    ArrayList<CategoryData> list = new ArrayList<>();
                    JSONArray jsonArray = responseJSONObject.getJSONArray("items");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject j = (JSONObject) jsonArray.get(i);
                        CategoryData categoryData = new CategoryData();
                        categoryData.setName(j.getString("name"));
                        categoryData.setId(j.getInt("id"));
                        list.add(categoryData);
                    }
                    categoriesViewModel.setCategoriesList(list);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
