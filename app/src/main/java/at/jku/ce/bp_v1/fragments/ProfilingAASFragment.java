package at.jku.ce.bp_v1.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import at.jku.ce.bp_v1.R;
import at.jku.ce.bp_v1.classes.RW;
import at.jku.ce.bp_v1.classes.Akzeptanz;
import at.jku.ce.bp_v1.classes.Durchdringung;
import at.jku.ce.bp_v1.classes.Eindringlichkeit;
import at.jku.ce.bp_v1.classes.Files;
import at.jku.ce.bp_v1.classes.Profile;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfilingAASFragment extends Fragment {
    private Context thiscontext;

    private CardView akzeptanz, ablehnung, situationsabhängig;

    private RW<Profile, Durchdringung> profileRW;
    private TreeMap<Profile, List<Durchdringung>> profileMap;
    private List<Durchdringung> durchdringungen;
    private Profile profile;
    private int aktPosition;

    public ProfilingAASFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profiling_aas, container, false);

        setHasOptionsMenu(true);

        thiscontext = container.getContext();

        akzeptanz = view.findViewById(R.id.akzeptanz);
        ablehnung = view.findViewById(R.id.ablehnung);
        situationsabhängig = view.findViewById(R.id.situationsabhängig);

        aktPosition = -1;

        setListeners();

        checkProfile();

        return view;
    }

    private void checkProfile() {
        //Reader & Writer initialisieren
        profileRW = new RW<>();
        profileMap = profileRW.readMap(thiscontext, String.valueOf(Files.PROFILE));

        if (getArguments() != null){
            String profilID = getArguments().getString("ProfilID");
            for (Profile p : profileMap.keySet()) {
                if (p.get_id().toString().equals(profilID))
                    profile = p;
            }

            durchdringungen = profileMap.get(profile);

            reload();
        }
    }

    private void setListeners() {
        akzeptanz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                durchdringungen.get(aktPosition).setAkzeptanz(Akzeptanz.AKZEPTANZ);
                reload();
            }
        });
        ablehnung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                durchdringungen.get(aktPosition).setAkzeptanz(Akzeptanz.ABLEHNUNG);
                reload();
            }
        });
        situationsabhängig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                durchdringungen.get(aktPosition).setAkzeptanz(Akzeptanz.SITUATIONSABHAENGIG);
                reload();
            }
        });
    }

    private Bundle getBundle(){
        ArrayList<String> edk = new ArrayList<>();
        ArrayList<String> aas = new ArrayList<>();
        int nextAusstehend = 0;
        int definiert = 0;

        for (int i = durchdringungen.size()-1; i >= 0; i--){
            if (durchdringungen.get(i).getAkzeptanz().equals(Akzeptanz.AUSSTEHEND)) {
                nextAusstehend = i;
            }else
            {
                definiert++;
            }
            //Listen müssen in richtiger Reihenfolge mitgegeben werden
            edk.add(durchdringungen.get(durchdringungen.size()-1 - i).getEindringlichkeit().toValue());
            aas.add(durchdringungen.get(durchdringungen.size()-1 - i).getAkzeptanz().toValue());
        }

        //Prüft ob alle Akzeptanzen ausgefüllt wurden
        if (definiert == durchdringungen.size()){
            Toast.makeText(thiscontext, R.string.toast_profil_vollstaendig, Toast.LENGTH_LONG).show();

            // Sofern die Liste nicht vollständig durchlaufen wurde soll die aktuelle Position auf 0 gesetzt werden
            if (durchdringungen.size()-1 > aktPosition) {
                aktPosition += 1;
            } else {
                aktPosition = 0;
            }
        } else{
            // Setzt die aktuelle Position auf das nächste noch ausstehende Feld
            aktPosition = nextAusstehend;
        }

        if (durchdringungen.get(aktPosition).getAkzeptanz().equals(Akzeptanz.UNDEFINIERT))
            return getBundle();

        getActivity().invalidateOptionsMenu();

        profileMap.put(profile, durchdringungen);
        profileRW.writeMap(thiscontext, profileMap, String.valueOf(Files.PROFILE));

        Bundle arguments = new Bundle();
        arguments.putStringArrayList("edk", edk);
        arguments.putStringArrayList("aas", aas);
        arguments.putInt("position", aktPosition);
        return arguments;
    }

    private void reload() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        ProfilingKKFragment kkFragment = new ProfilingKKFragment();
        if (profile != null){
            kkFragment.setArguments(getBundle());
        }
        fragmentTransaction.remove(getFragmentManager().findFragmentById(R.id.profiling_fragment_aas_kk));
        fragmentTransaction.add(R.id.profiling_fragment_aas_kk, kkFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.next).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

}
