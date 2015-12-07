package han.recyclerviewdemo;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.adapters.ScaleInAnimationAdapter;

public class markList extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MarkListAdapter Cusadapter;
    private ScaleInAnimationAdapter adapter;
    private AlphaInAnimationAdapter final_adapter;
    private List<InfoCollection> mark_list;
    DataBaseHelper dataBaseHelper = new DataBaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_list);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_mark_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mark_list=new ArrayList<>();

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_mark_list);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        initData();
        if (mark_list.size() == 0) {
            Toast.makeText(this,"You don't have marked place yet.",Toast.LENGTH_SHORT).show();
        } else {
            Cusadapter = new MarkListAdapter(mark_list,this);
            Cusadapter.setOnItemClickListener(new MarkListAdapter.OnRecyclerViewItemClickListener() {
                @Override
                public void OnItemClick(View view, InfoCollection data) {
                    int[] startingLocation = new int[2];
                    view.getLocationOnScreen(startingLocation);
                    startingLocation[0] += view.getWidth() / 2;
                    Gson gson = new Gson();
                    String data_json = gson.toJson(data);
                    Bundle bundle = new Bundle();
                    bundle.putString("result", data_json);
                    detail.startSecondFromLocation(startingLocation, markList.this, bundle);
                    overridePendingTransition(0, 0);
                }
            });
            recyclerView.setAdapter(Cusadapter);
            ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(Cusadapter);
            ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
            touchHelper.attachToRecyclerView(recyclerView);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        initData();
        if(mark_list.size()>0) {
            Cusadapter.notifyDataSetChanged();
        }
    }

    private void initData() {
        mark_list.clear();
        Cursor cursor = dataBaseHelper.getAllDetails();
        while (cursor.moveToNext()) {
            String data = cursor.getString(1);
            InfoCollection info = new Gson().fromJson(data,InfoCollection.class);
            mark_list.add(info);
        }
    }

}
