package han.recyclerviewdemo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.LandingAnimator;
import jp.wasabeef.recyclerview.animators.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.adapters.ScaleInAnimationAdapter;


public class suggestFragment extends Fragment {
    private String search_data;
    private RecyclerView recyclerView_suggest;
    private CustomAdapter Cusadapter;
    private ScaleInAnimationAdapter adapter;
    private AlphaInAnimationAdapter final_adapter;
    public ArrayList<InfoCollection> food_list = new ArrayList<>();
    public ArrayList<InfoCollection> list;
    public double[] location;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_suggest, container, false);
        recyclerView_suggest = (RecyclerView) view.findViewById(R.id.recycler_view_suggest);
        recyclerView_suggest.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView_suggest.setItemAnimator(new LandingAnimator());
        Cusadapter = new CustomAdapter(food_list);
        Cusadapter.setOnItemClickListener(new CustomAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void OnItemClick(View view, InfoCollection data) {
                int[] startingLocation = new int[2];
                view.getLocationOnScreen(startingLocation);
                startingLocation[0] += view.getWidth() / 2;
                Gson gson = new Gson();
                Log.e("location",String.valueOf(data.getLatitude()));
                Log.e("location",String.valueOf(data.getLongtitude()));
                String data_json = gson.toJson(data);
                Bundle bundle = new Bundle();
                bundle.putString("result", data_json);
                detail.startSecondFromLocation(startingLocation, getActivity(), bundle);
                getActivity().overridePendingTransition(0, 0);
            }
        });
        adapter = new ScaleInAnimationAdapter(Cusadapter);
        final_adapter = new AlphaInAnimationAdapter(adapter);
        recyclerView_suggest.setAdapter(final_adapter);
        initData();
        return view;
    }
//    public suggestFragment(){
//        search_data = "";
//        location = new double[2];
//        location[0] = 0;
//        location[1] = 0;
//
//    }
    public suggestFragment(String data, double[] location) {
        super();
        this.search_data = data;
        this.location = location;
    }

    public static Fragment newInstance(String data, double[] location) {

        Bundle args = new Bundle();

        suggestFragment fragment = new suggestFragment(data, location);
        fragment.setArguments(args);
        return fragment;
    }

    private void initData() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                    Yelp yelp = Yelp.getYelp(getActivity());
                    try {
                        String food_json = yelp.search(search_data, location[0], location[1], "5");
                        list = ProcessJSON.processJson(food_json);
                        food_list.clear();
                        food_list.addAll(list);
                        Log.e("JSON", food_list.size() + "");
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
