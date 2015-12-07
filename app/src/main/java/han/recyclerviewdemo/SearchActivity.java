package han.recyclerviewdemo;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.google.gson.Gson;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.LandingAnimator;
import jp.wasabeef.recyclerview.animators.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.adapters.ScaleInAnimationAdapter;

public class SearchActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SearchView searchView;
    private CustomAdapter Cusadapter;
    private ScaleInAnimationAdapter adapter;
    private AlphaInAnimationAdapter final_adapter;
    public ArrayList<InfoCollection> result_list = new ArrayList<>();
    public ArrayList<InfoCollection> list;
    public double[] location = new double[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_search_list);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        location = bundle.getDoubleArray("location");
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_search_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new LandingAnimator());
        Cusadapter = new CustomAdapter(result_list);
        Cusadapter.setOnItemClickListener(new CustomAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void OnItemClick(View view, InfoCollection data) {
                int[] startingLocation = new int[2];
                view.getLocationOnScreen(startingLocation);
                startingLocation[0] += view.getWidth() / 2;
                Gson gson = new Gson();
                String data_json = gson.toJson(data);
                Bundle bundle = new Bundle();
                bundle.putString("result", data_json);
                detail.startSecondFromLocation(startingLocation, SearchActivity.this, bundle);
                overridePendingTransition(0, 0);
            }
        });
        adapter = new ScaleInAnimationAdapter(Cusadapter);
        final_adapter = new AlphaInAnimationAdapter(adapter);
        recyclerView.setAdapter(final_adapter);
        searchView = (SearchView)findViewById(R.id.searchView);
        searchView.setIconifiedByDefault(false);
        searchView.setIconified(false);
        searchView.clearFocus();

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentFocus();
            }
        });
        searchView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                getCurrentFocus();
                return true;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                initData();
                return  true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                return true;
            }
        });
    }


    private void initData() {
        new AsyncTask<Void, Void, Void>() {
            String query;

            @Override
            protected void onPreExecute() {
                query = searchView.getQuery().toString();
            }

            @Override
            protected Void doInBackground(Void... params) {
                Yelp yelp = Yelp.getYelp(SearchActivity.this);
                try {
                    String food_json = yelp.search(query, location[0],location[1], "20");
                    list = ProcessJSON.processJson(food_json);
                    result_list.clear();
                    result_list.addAll(list);
                    Log.e("JSON", result_list.size() + "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                final_adapter.notifyDataSetChanged();
            }

        }.execute();
    }
}
