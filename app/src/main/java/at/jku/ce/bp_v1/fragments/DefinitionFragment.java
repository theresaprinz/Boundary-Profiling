package at.jku.ce.bp_v1.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import at.jku.ce.bp_v1.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class DefinitionFragment extends Fragment {
    private CardView kontextCard;
    private CardView kanaeleCard;

    public DefinitionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_definition, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        kontextCard = view.findViewById(R.id.kontextCard);
        kanaeleCard = view.findViewById(R.id.kanaeleCard);

        kontextCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_frame, new DefinitionKontextFragment());
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                fragmentTransaction.addToBackStack("KontextDefiniton");
                fragmentTransaction.commit();
            }
        });

        kanaeleCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_frame, new DefinitionKanaeleFragment());
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                fragmentTransaction.addToBackStack("KanaeleDefiniton");
                fragmentTransaction.commit();
            }
        });
    }
}
