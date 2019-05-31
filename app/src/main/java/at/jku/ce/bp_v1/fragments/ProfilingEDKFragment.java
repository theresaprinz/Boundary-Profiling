package at.jku.ce.bp_v1.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import at.jku.ce.bp_v1.R;
import at.jku.ce.bp_v1.classes.Akzeptanz;
import at.jku.ce.bp_v1.classes.Durchdringung;
import at.jku.ce.bp_v1.classes.Eindringlichkeit;
import at.jku.ce.bp_v1.classes.Files;
import at.jku.ce.bp_v1.classes.Kontext;
import at.jku.ce.bp_v1.classes.Profile;
import at.jku.ce.bp_v1.classes.RW;
import at.jku.ce.bp_v1.classes.Rolle;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfilingEDKFragment extends Fragment {
    private Context thiscontext;

    private CardView hoch, mittel, gering, undefiniert;

    private RW<Profile, Durchdringung> profileRW;
    private RW<Kontext, Rolle> kontextRW;

    private TreeMap<Kontext, List<Rolle>> kontextMap;
    private TreeMap<Profile, List<Durchdringung>> profileMap;
    private List<Durchdringung> durchdringungen;
    private Kontext qKontext, zKontext;
    private Rolle qRolle;
    private Profile profile;
    private int aktPosition;
    private boolean next;
    private int allUndefiniert, definiert;

    private String qKontextName;
    private String qRolleName;
    private String zKontextName;

    public ProfilingEDKFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profiling_edk, container, false);

        setHasOptionsMenu(true);

        thiscontext = container.getContext();

        hoch = view.findViewById(R.id.hoch);
        mittel = view.findViewById(R.id.mittel);
        gering = view.findViewById(R.id.gering);
        undefiniert = view.findViewById(R.id.edk_undefined);

        aktPosition = -1;
        next = false;
        allUndefiniert = 0;

        setListeners();

        checkProfile();

        return view;
    }

    private void checkProfile() {
        //Reader & Writer initialisieren
        profileRW = new RW<>();
        kontextRW = new RW<>();

        kontextMap = kontextRW.readMap(thiscontext, String.valueOf(Files.KONTEXTE));
        profileMap = profileRW.readMap(thiscontext, String.valueOf(Files.PROFILE));

        if (getArguments() != null) {
            //Beim Klick im HomeFragment wird die ProfilID übergeben
            String profilID = getArguments().getString("ProfilID");
            if (profilID != null) {
                for (Profile p : profileMap.keySet()) {
                    if (p.get_id().toString().equals(profilID))
                        profile = p;
                }
                //Setzten der Kontext und Rolle Felder
                ProfilingFragment.setQuellkontextView(thiscontext, getActivity(), profile.getQuellkontext().toString(), false, true);
                ProfilingFragment.setQuellrolleView(thiscontext, getActivity(), profile.getQuellrolle().toString(), false, true);
                ProfilingZKFragment.setZielkontextView(thiscontext, getActivity(), profile.getZielkontext().toString(), false, true);
            } else {
                //Normaler Weg über Profiling
                qKontextName = getArguments().getString("qKontext");
                qRolleName = getArguments().getString("qRolle");
                zKontextName = getArguments().getString("zKontext");

                //Da nur die Namen der Kontexte und Rolle bekannt sind/ist müssen die jeweiligen Objekte gesucht werden
                for (Kontext k : kontextMap.keySet()) {
                    if (k.getName().equals(qKontextName)) {
                        qKontext = k;
                        for (Rolle r : kontextMap.get(k)) {
                            if (r.getName().equals(qRolleName)) {
                                qRolle = r;
                                break;
                            }
                        }
                    }
                    if (k.getName().equals(zKontextName))
                        zKontext = k;
                }
                profile = new Profile(zKontext, qKontext, qRolle);
                //Profil in bestehender Liste suchen
                for (Profile p : profileMap.keySet()) {
                    if (p.equalKontexte(profile)) {
                        profile = p;
                    }
                }
            }
            if (profile != null) {
                durchdringungen = profileMap.get(profile);
                reload();
            }
        }
    }

    private void setListeners() {
        hoch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (durchdringungen.get(aktPosition).getEindringlichkeit().equals(Eindringlichkeit.UNDEFINIERT)) {
                    durchdringungen.get(aktPosition).setAkzeptanz(Akzeptanz.AUSSTEHEND);
                }
                durchdringungen.get(aktPosition).setEindringlichkeit(Eindringlichkeit.HOCH);
                reload();
            }
        });
        mittel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (durchdringungen.get(aktPosition).getEindringlichkeit().equals(Eindringlichkeit.UNDEFINIERT)) {
                    durchdringungen.get(aktPosition).setAkzeptanz(Akzeptanz.AUSSTEHEND);
                }
                durchdringungen.get(aktPosition).setEindringlichkeit(Eindringlichkeit.MITTEL);
                reload();
            }
        });
        gering.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (durchdringungen.get(aktPosition).getEindringlichkeit().equals(Eindringlichkeit.UNDEFINIERT)) {
                    durchdringungen.get(aktPosition).setAkzeptanz(Akzeptanz.AUSSTEHEND);
                }
                durchdringungen.get(aktPosition).setEindringlichkeit(Eindringlichkeit.GERING);
                reload();
            }
        });
        undefiniert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                durchdringungen.get(aktPosition).setEindringlichkeit(Eindringlichkeit.UNDEFINIERT);
                durchdringungen.get(aktPosition).setAkzeptanz(Akzeptanz.UNDEFINIERT);
                reload();
            }
        });
    }

    private Bundle getBundle() {
        ArrayList<String> edk = new ArrayList<>();
        ArrayList<String> aas = new ArrayList<>();
        int nextAusstehend = 0;
        int definiert = 0;
        int undefiniert = 0;

        for (int i = durchdringungen.size() - 1; i >= 0; i--) {
            if (durchdringungen.get(i).getEindringlichkeit().equals(Eindringlichkeit.AUSSTEHEND)) {
                next = false;
                nextAusstehend = i;
            } else if (durchdringungen.get(i).getEindringlichkeit().equals(Eindringlichkeit.UNDEFINIERT)) {
                undefiniert++;
                definiert++;
            } else {
                definiert++;
            }
            //Listen müssen in richtiger Reihenfolge mitgegeben werden
            edk.add(durchdringungen.get(durchdringungen.size() - 1 - i).getEindringlichkeit().toValue());
            aas.add(durchdringungen.get(durchdringungen.size() - 1 - i).getAkzeptanz().toValue());
        }


        if (undefiniert == durchdringungen.size()) {
            // Prüft ob alle Eindringlichkeiten undefiniert sind
            Toast.makeText(thiscontext, R.string.toast_edk_allundefiniert, Toast.LENGTH_SHORT).show();
            next = false;

            // Sofern die Liste nicht vollständig durchlaufen wurde soll die aktuelle Position auf 0 gesetzt werden
            if (durchdringungen.size() - 1 > aktPosition) {
                aktPosition += 1;
            } else {
                aktPosition = 0;
            }
        } else if (definiert == durchdringungen.size()) {
            next = true;

            // Sofern die Liste nicht vollständig durchlaufen wurde soll die aktuelle Position auf 0 gesetzt werden
            if (durchdringungen.size() - 1 > aktPosition) {
                aktPosition += 1;
            } else {
                aktPosition = 0;
            }
        } else {
            // Setzt die aktuelle Position auf das nächste noch ausstehende Feld
            aktPosition = nextAusstehend;
        }
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
        fragmentTransaction.remove(getFragmentManager().findFragmentById(R.id.profiling_fragment_edk_kk));
        fragmentTransaction.add(R.id.profiling_fragment_edk_kk, kkFragment);
        fragmentTransaction.commit();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.next:
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                ProfilingAASFragment profilingAASFragment = new ProfilingAASFragment();
                ProfilingKKFragment kkFragment = new ProfilingKKFragment();
                if (profile != null) {
                    Bundle arguments = new Bundle();
                    arguments.putString("ProfilID", profile.get_id().toString());
                    profilingAASFragment.setArguments(arguments);
                    aktPosition = -1;
                    kkFragment.setArguments(getBundle());
                }
                fragmentTransaction.remove(getFragmentManager().findFragmentById(R.id.profiling_fragment));
                fragmentTransaction.add(R.id.profiling_fragment, profilingAASFragment, "AASFragment");
                fragmentTransaction.add(R.id.profiling_fragment_aas_kk, kkFragment, "AASKKFragment");
                fragmentTransaction.addToBackStack("EDKToAAS");
                fragmentTransaction.commit();

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.next).setVisible(next);
        super.onPrepareOptionsMenu(menu);
    }
}
