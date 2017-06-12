package com.a1101studio.autohelper;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

import static android.R.string.no;
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

    public static String msg = "";
    // TODO: Rename and change types of parameters


    private OnFragmentInteractionListener mListener;
    private ServerWorker serverWorker;

    @BindView(R.id.buttonOpen)
    Button buttonOpen;
    @BindView(R.id.etCarNumber)
    EditText editText;
    private int delayMillis;

    public OpenFragment() {
        // Required empty public constructor
    }


    public static OpenFragment newInstance(String param1, String param2) {
        OpenFragment fragment = new OpenFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void connectToService() {
        connectToService("");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_open, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        connectToService();
    }

    @Override
    public void onStop() {
        super.onStop();
        serverWorker.close();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @OnClick(R.id.buttonOpen)
    public void openMe() {
        final String msgText = editText.getText().toString();
        serverWorker.close();
        connectToService(msgText);
        delayMillis = 500;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (serverWorker.isConnected()) {
                    editText.setHint(editText.getText().toString());
                    serverWorker.publishMessage(getString(R.string.get_number));
                    editText.setText("");
                } else {
                    Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                }
            }
        }, delayMillis);
    }

    private void connectToService(String autoNumber) {
       // if (serverWorker == null || !serverWorker.isConnected()) {
            final ConnectionModel connectionModel = createConnectionModel(getContext());
            connectionModel.setPublishTopic(connectionModel.getPublishTopic()+autoNumber);
            serverWorker = new ServerWorker(getContext(), connectionModel, new ServerWorker.CallBackMessage() {
                @Override
                public void onMessageArrive(String s1, final String s2) {
                    new AlertDialog.Builder(getContext()).setTitle("Позвонить?").setMessage("Номер телефона: "+s2).setPositiveButton(ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:+7"+s2));
                            startActivity(intent);
                        }
                    }).setNegativeButton(no,null).show();

                }
            });
       // }
    }

    @OnLongClick(R.id.buttonOpen)
    public boolean onLong() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setMessage(msg).setPositiveButton(ok, null);
        builder.setNegativeButton(R.string.clean, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                msg = "";
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
