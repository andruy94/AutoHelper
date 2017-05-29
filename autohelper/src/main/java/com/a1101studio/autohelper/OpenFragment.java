package com.a1101studio.autohelper;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.a1101studio.autohelper.adapters.ConnectionModel;
import com.a1101studio.autohelper.utils.ServerWorker;

import org.eclipse.paho.client.mqttv3.MqttException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

import static android.R.string.ok;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OpenFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OpenFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OpenFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static  String msg="";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private ServerWorker serverWorker;

    @BindView(R.id.buttonOpen)
    Button buttonOpen;
    @BindView(R.id.editText)
    EditText editText;

    public OpenFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OpenFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OpenFragment newInstance(String param1, String param2) {
        OpenFragment fragment = new OpenFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_open, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        super.onViewCreated(view, savedInstanceState);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @OnClick(R.id.buttonOpen)
    public void openMe() {
            connectToService();
        if (serverWorker.getMqttAndroidClient().isConnected()) {
            editText.setHint(editText.getText().toString());

            final String msgText = editText.getText().toString();
            serverWorker.publishMessage(msgText);
            editText.setText("");
        } else {
            try {
                serverWorker.getMqttAndroidClient().disconnect();
            } catch (MqttException|NullPointerException e) {
                e.printStackTrace();
            }
            connectToService();
            Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
        }
    }

    private void connectToService() {
        if (serverWorker == null || !serverWorker.getMqttAndroidClient().isConnected()) {
            ConnectionModel connectionModel = createConnectionModel(getContext());

            serverWorker = new ServerWorker(getContext(),connectionModel , new ServerWorker.CallBackMessage() {
                @Override
                public void onMessageArrive(String s1, String s2) {
                    msg += "{topic= " + s1 + "msg=" + s2 + '}' + '\n';
                }
            });
        }
    }

    @OnLongClick(R.id.buttonOpen)
    public boolean onLong(){
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity()).setMessage(msg).setPositiveButton(ok,null);
        builder.setNegativeButton(R.string.clean, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                msg="";
            }
        });
        builder.show();
        return false;
    }


    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }


    }

    @NonNull
    private ConnectionModel createConnectionModel(Context context) {
        String serverUri = getStringFromPrefernce(context, context.getString(R.string.open_key_web_adress));
        String subTopic = getStringFromPrefernce(context, context.getString(R.string.open_key_sub_topic));
        String publishTopic = getStringFromPrefernce(context, context.getString(R.string.open_key_publish_topic));
        String userId = "228";
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
