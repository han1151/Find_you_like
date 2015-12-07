package han.recyclerviewdemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.software.shell.fab.ActionButton;

public class detail extends AppCompatActivity implements RippleBackgroundView.OnStateChangeListener {
    public static final String ARG_RIPPLE_START_LOCATION = "arg_ripple_start_location";
    private RippleBackgroundView rippleBackgroundView;
    private ActionButton fab, fab_share;
    private boolean marked;
    private CardView card1, card2, card3;
    private TextView detailName, detailReviews, detailDistance;
    private TextView detailDescription;
    private RatingBar detailRating;
    private TextView detaiAdress, detailPhone, detailWebsite;
    private String web_url;
    private String phone;
    private String mapaddress;
    private String id;
    private double latitude_detail;
    private double longgitude_detail;
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    private GoogleMap googleMap;
    private LocationManager locationManager;
    private LocationListener ls;
    private DataBaseHelper dataBaseHelper;

    /**
     * DBHelper GoogleMap
     **/
    public static void startSecondFromLocation(int[] startingLocation, Activity startingActivity, Bundle bundle) {
        Intent intent = new Intent(startingActivity, detail.class);
        intent.putExtra(ARG_RIPPLE_START_LOCATION, startingLocation);
        intent.putExtras(bundle);
        startingActivity.startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        rippleBackgroundView = (RippleBackgroundView) findViewById(R.id.ripple_background);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        dataBaseHelper= new DataBaseHelper(this);
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String json_data = "";
        if (extras != null) {
            json_data = extras.getString("result");
        }
        final InfoCollection infoCollectiondetail = new Gson().fromJson(json_data, InfoCollection.class);
        ImageView imageView_detail = (ImageView) findViewById(R.id.image_detail);
        imageLoader.displayImage(infoCollectiondetail.getImage_url(), imageView_detail);
        id = infoCollectiondetail.getId();
        card1 = (CardView) findViewById(R.id.detail_cardview_1);
        card2 = (CardView) findViewById(R.id.detail_cardview_2);
        card3 = (CardView) findViewById(R.id.detail_cardview_3);
        fab_share = (ActionButton) findViewById(R.id.action_button_share);
        fab = (ActionButton) findViewById(R.id.action_button);
        if(!dataBaseHelper.getDetail(id).moveToNext()){
            fab.setImageResource(android.R.drawable.star_big_off);
            marked = false;
        }else{
            fab.setImageResource(android.R.drawable.star_big_on);
            marked = true;
        }

        collapsingToolbar.setTitle(infoCollectiondetail.getName());
        detailName = (TextView) findViewById(R.id.detail_name);
        detailRating = (RatingBar) findViewById(R.id.ratingBar);
        detailReviews = (TextView) findViewById(R.id.detail_comment);
        detailDistance = (TextView) findViewById(R.id.detail_distance);
        detailDescription = (TextView) findViewById(R.id.detail_description);
        detaiAdress = (TextView) findViewById(R.id.textViewAddress);
        detaiAdress.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        detailPhone = (TextView) findViewById(R.id.textViewPhone);
        detailPhone.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        detailWebsite = (TextView) findViewById(R.id.textViewWebsite);
        detailWebsite.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        detailName.setText(infoCollectiondetail.getName());
        detailRating.setRating(infoCollectiondetail.getRating());
        detailReviews.setText(infoCollectiondetail.getNumber_comment() + "reviews");
        detailDistance.setText(infoCollectiondetail.getDistance() + "miles");
        detailDescription.setText(infoCollectiondetail.getCategory());
        detaiAdress.setText(infoCollectiondetail.getAddress());
        detailPhone.setText(infoCollectiondetail.getPhone_number());
        detailWebsite.setText(infoCollectiondetail.getMobile_url());
        mapaddress = infoCollectiondetail.getAddress();
        web_url = infoCollectiondetail.getMobile_url();
        phone = infoCollectiondetail.getPhone_number();
        latitude_detail = infoCollectiondetail.getLatitude();
        longgitude_detail = infoCollectiondetail.getLongtitude();

        fab_share.setShowAnimation(ActionButton.Animations.ROLL_FROM_RIGHT);
        fab.setShowAnimation(ActionButton.Animations.SCALE_UP);
        fab.setRippleEffectEnabled(true);
        fab_share.setRippleEffectEnabled(true);
        fab_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fab_share.playShowAnimation();
                String share_data = infoCollectiondetail.getName()+" is a really good place to go.\n It provides "+infoCollectiondetail.getCategory()+" \nThe address is "+infoCollectiondetail.getAddress();
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,share_data);
                sendIntent.setType("text/plain");
                startActivity(sendIntent.createChooser(sendIntent, "Share this via"));
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fab.playShowAnimation();
                if (!marked) {
                    marked = true;
                    fab.setImageResource(android.R.drawable.star_big_on);
                    Gson gson = new Gson();
                    String data_json = gson.toJson(infoCollectiondetail);
                    dataBaseHelper.insertDetail(id,data_json);
                } else if (marked) {
                    marked = false;
                    fab.setImageResource(android.R.drawable.star_big_off);
                    dataBaseHelper.deleteDetail(id);
                }

            }
        });
        detaiAdress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri geoLocation = Uri.parse("geo:0,0?").buildUpon().appendQueryParameter("q", mapaddress).build();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(geoLocation);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        detailWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(web_url);
                Intent it = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(it);
            }
        });

        detailPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("tel:" + phone);
                Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                startActivity(intent);
            }
        });

        SupportMapFragment supportMapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.detail_map);

        if (supportMapFragment.getMap() == null)
            Log.e("getMap: ", "null");
        else {
            googleMap = supportMapFragment.getMap();
            latitude_detail = infoCollectiondetail.getLatitude();
            longgitude_detail = infoCollectiondetail.getLongtitude();
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            LatLng latLng = new LatLng(latitude_detail, longgitude_detail);
            googleMap.addMarker(new MarkerOptions().position(latLng));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(14));
        }
        setupRippleBackground(savedInstanceState);
    }

    private void setupRippleBackground(Bundle savedInstanceState) {
        rippleBackgroundView.setOnStateChangeListener(this);
        if (savedInstanceState == null) {
            final int[] startingLocation = getIntent().getIntArrayExtra(ARG_RIPPLE_START_LOCATION);
            rippleBackgroundView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    rippleBackgroundView.getViewTreeObserver().removeOnPreDrawListener(this);
                    rippleBackgroundView.startFromLocation(startingLocation);
                    return true;
                }
            });
        } else {
            rippleBackgroundView.setToFinishedFrame();
        }
    }

    @Override
    public void onStateChange(int state) {
        if (RippleBackgroundView.STATE_FINISHED == state) {
            card1.setVisibility(View.VISIBLE);
            card2.setVisibility(View.VISIBLE);
            card3.setVisibility(View.VISIBLE);
            fab.show();
            fab_share.show();

        } else {
            card1.setVisibility(View.INVISIBLE);
            card2.setVisibility(View.INVISIBLE);
            card3.setVisibility(View.INVISIBLE);
            fab.hide();
            fab_share.hide();
        }
    }


}
