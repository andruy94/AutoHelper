package com.a1101studio.autohelper.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.a1101studio.autohelper.BeaconFragment;
import com.a1101studio.autohelper.MainActivity;
import com.a1101studio.autohelper.OpenFragment;
import com.a1101studio.autohelper.PolicePostFragment;
import com.a1101studio.autohelper.R;
import com.a1101studio.autohelper.WarnFragment;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private Context context;

    public SectionsPagerAdapter(FragmentManager fm,Context context) {
        super(fm);
        this.context=context;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
       // return PlaceholderFragment.newInstance(position + 1);
        Fragment fragment = null;
        switch (position+1) {
            case 1:
                fragment = new OpenFragment();
                break;
            case 2:
                fragment = new WarnFragment();
                break;
            case 3:
                fragment = new BeaconFragment();
                break;
            case 4:
                fragment = new PolicePostFragment();
        }
        return fragment;
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return context.getResources().getStringArray(R.array.titles)[position];
    }

    public void setContext(Context context) {
        this.context = context;
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {



        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            int k = getArguments().getInt(ARG_SECTION_NUMBER);
            View rootView = null;
            switch (k) {
                case 1:
                    rootView=inflater.inflate(R.layout.fragment_open, container, false);
                    break;
                case 2:
                    rootView = inflater.inflate(R.layout.fragment_warn, container, false);
                    break;
                case 3:
                    rootView=inflater.inflate(R.layout.fragment_beacon, container, false);
                    break;
                case 4:
                    rootView=inflater.inflate(R.layout.fragment_police_post, container, false);
                    break;
                default:
                    rootView = inflater.inflate(R.layout.fragment_main, container, false);
                    TextView textView = (TextView) rootView.findViewById(R.id.section_label);
                    textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));

            }
            rootView=newInstance(k).getView();
            return rootView;
        }
    }
}
