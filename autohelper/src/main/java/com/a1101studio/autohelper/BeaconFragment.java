package com.a1101studio.autohelper;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.a1101studio.autohelper.adapters.ConnectionModel;
import com.a1101studio.autohelper.utils.ServerWorker;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.yandex.yandexmapkit.MapController;
import ru.yandex.yandexmapkit.MapView;
import ru.yandex.yandexmapkit.OverlayManager;
import ru.yandex.yandexmapkit.overlay.Overlay;
import ru.yandex.yandexmapkit.overlay.OverlayItem;
import ru.yandex.yandexmapkit.overlay.location.MyLocationItem;
import ru.yandex.yandexmapkit.overlay.location.OnMyLocationListener;
import ru.yandex.yandexmapkit.MapView;
import ru.yandex.yandexmapkit.utils.GeoPoint;

import static android.content.Context.LOCATION_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BeaconFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BeaconFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BeaconFragment extends Fragment {

    MapController mMapController;

    private OnFragmentInteractionListener mListener;
    private ServerWorker serverWorker;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    public BeaconFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static BeaconFragment newInstance(String param1, String param2) {
        BeaconFragment fragment = new BeaconFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_beacon, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();
        if (view != null) {
            final MapView mapView = (MapView) getView().findViewById(R.id.map);
            mapView.showBuiltInScreenButtons(true);
            mMapController = mapView.getMapController();
            // add listener
            mMapController.getOverlayManager().getMyLocation();
        }
    }

    @OnClick(R.id.fab)
    void fabClick() {
        fab.setVisibility(View.INVISIBLE);
        Snackbar.make(getView(), "Маяк утсановлен!", BaseTransientBottomBar.LENGTH_INDEFINITE).setAction("Отмена", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab.setVisibility(View.VISIBLE);
            }
        }).show();
    }


    private void connectToService(String autoNumber) {
        // if (serverWorker == null || !serverWorker.isConnected()) {
        final ConnectionModel connectionModel = createConnectionModel(getContext());
        connectionModel.setPublishTopic(connectionModel.getPublishTopic() + autoNumber);
        serverWorker = new ServerWorker(getContext(), connectionModel, new ServerWorker.CallBackMessage() {
            @Override
            public void onMessageArrive(String s1, final String s2) {
                //TODO тут будут приходить координаты тебе
            }
        });
    }

    public GeoPoint getLocation() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        String locationProvider = LocationManager.GPS_PROVIDER;
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return new GeoPoint(228, 228);
        }
        Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
        return new GeoPoint(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());

    }

    @NonNull
    private ConnectionModel createConnectionModel(Context context) {
        String serverUri = getStringFromPrefernce(context, context.getString(R.string.open_key_web_adress));
        String subTopic = getStringFromPrefernce(context, context.getString(R.string.open_key_sub_topic));
        String publishTopic = getStringFromPrefernce(context, context.getString(R.string.open_key_publish_topic));
        String userId = Build.SERIAL;
        return new ConnectionModel(
                serverUri,
                userId,
                subTopic,
                publishTopic


        );
    }

    @NonNull
    private String getStringFromPrefernce(Context context, String string) {
        return PreferenceManager
                .getDefaultSharedPreferences(context)
                .getString(string, "");
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
