package searchnative.com.topdoctors;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by root on 30/9/16.
 */

public class GoogleMapFragment extends Fragment implements OnMapReadyCallback {

    private double latitude, longitude;
    private GoogleMap googleMap;
    private SupportMapFragment supportMapFragment;
    private LatLng latLng;
    private String title;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.google_map, container, false);
        //supportMapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.google_map_fragment);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle args = getArguments();
        latitude = args.getDouble("latitude");
        longitude = args.getDouble("longitude");
        title = args.getString("title");

        latLng = new LatLng(latitude, longitude);

        try {
            initializeMap();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initializeMap() {
        if(googleMap == null) {
            MapFragment mapFragment = ((MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.google_map_fragment));
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.addMarker(new MarkerOptions().position(latLng).title("Ahmedabad"));
        map.animateCamera(CameraUpdateFactory.zoomTo(8.0f));
        map.setTrafficEnabled(true);
        map.setIndoorEnabled(true);
        map.setBuildingsEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
    }

}
