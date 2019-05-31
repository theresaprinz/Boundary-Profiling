package at.jku.ce.bp_v1.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import at.jku.ce.bp_v1.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class Welcome1Fragment extends Fragment {


    public Welcome1Fragment() {
        // Required empty public constructor
    }

    public static Welcome1Fragment newInstance() {
        Welcome1Fragment fragment = new Welcome1Fragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_welcome1, container, false);
    }

}
