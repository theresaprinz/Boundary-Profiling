package at.jku.ce.bp_v1.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import at.jku.ce.bp_v1.R;
import at.jku.ce.bp_v1.classes.RW;
import at.jku.ce.bp_v1.adapter.ExpandableDefinitionListViewAdapter;
import at.jku.ce.bp_v1.classes.Akzeptanz;
import at.jku.ce.bp_v1.classes.Durchdringung;
import at.jku.ce.bp_v1.classes.Eindringlichkeit;
import at.jku.ce.bp_v1.classes.Files;
import at.jku.ce.bp_v1.classes.KKanaele;
import at.jku.ce.bp_v1.classes.Kontext;
import at.jku.ce.bp_v1.classes.Profile;
import at.jku.ce.bp_v1.classes.Rolle;

import static at.jku.ce.bp_v1.classes.Files.KANAELE;
import static at.jku.ce.bp_v1.classes.Files.PROFILE;
import static java.lang.String.valueOf;


/**
 * A simple {@link Fragment} subclass.
 */
public class DefinitionKontextFragment extends Fragment {
    private static ExpandableListView expandableListView;
    private static ExpandableDefinitionListViewAdapter adapter;
    private EditText kontext;
    private LinearLayout footerLayout;
    private RW<Kontext, Rolle> kontextRW;
    private RW<Profile, Durchdringung> profileRW;
    private RW<Object, KKanaele> kanaeleRW;
    private TreeMap<Kontext, List<Rolle>> kontextMap;
    private TreeMap<Profile, List<Durchdringung>> profileMap;

    private Context thiscontext;


    public DefinitionKontextFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_definition_kontext, container, false);

        thiscontext = getContext();

        kontextRW = new RW<>();
        profileRW = new RW<>();
        kanaeleRW = new RW<>();

        expandableListView = view.findViewById(R.id.exp_lv_definition_kontext);
        expandableListView.setGroupIndicator(null);

        adapter = new ExpandableDefinitionListViewAdapter(thiscontext, getActivity());

        getActivity().setTitle(R.string.tab_kontext);

        View v = getLayoutInflater().inflate(R.layout.exp_lv_footer, expandableListView, false);
        footerLayout = v.findViewById(R.id.footer_layout);

        expandableListView.addFooterView(footerLayout);
        //expandableListView.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        expandableListView.setAdapter(adapter);

        setListener();

        return view;
    }

    private void setListener() {
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousGroup = -1;

            @Override
            public void onGroupExpand(int groupPosition) {
                if (groupPosition != previousGroup) {
                    expandableListView.collapseGroup(previousGroup);
                    getActivity().invalidateOptionsMenu();
                }
                previousGroup = groupPosition;
            }
        });

        kontext = footerLayout.findViewById(R.id.newkontext);

        kontext.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (kontext.getText().toString().length() >= 3 && kontext.getText().toString().length() <= 21 ){
                        //neuen Kontext und neue Rolle Allgemein hinzufügen
                        kontextMap = kontextRW.readMap(thiscontext, String.valueOf(Files.KONTEXTE));

                        boolean exists = false;
                        for (Kontext k:kontextMap.keySet()) {
                            if (k.getName().equals(kontext.getText().toString()))
                                exists = true;
                        }
                        if (!exists){
                            kontext.setError(null);
                            Kontext kontext_neu = new Kontext(DefinitionKontextFragment.this.kontext.getText().toString());
                            LinkedList<Rolle> rollen = new LinkedList<>();
                            Rolle allgemein = new Rolle(getString(R.string.rolle_allgemein));
                            rollen.add(allgemein);
                            kontextMap.put(kontext_neu, rollen);
                            kontextRW.writeMap(thiscontext, kontextMap, String.valueOf(Files.KONTEXTE));

                            //neue Profile hinzufügen
                            profileMap = profileRW.readMap(thiscontext, valueOf(PROFILE));
                            Profile p;
                            List<Durchdringung> durchdringungen;

                            for (Kontext k : kontextMap.keySet()) {
                                //neue Profile für bestehende Quellkontexte/Quellrollen und neuen Zielkontext
                                for (Rolle qr : kontextMap.get(k)) {
                                    p = new Profile(kontext_neu, k, qr);
                                    durchdringungen = setDefaultDurchringungen(p);
                                    profileMap.put(p, durchdringungen);
                                }

                                //neue Profile für neuen Quellkontext
                                p = new Profile(k, kontext_neu, allgemein);
                                durchdringungen = setDefaultDurchringungen(p);
                                profileMap.put(p, durchdringungen);

                            }
                            profileRW.writeMap(thiscontext, profileMap, String.valueOf(Files.PROFILE));
                            reload();
                            return true;
                        }else{
                            kontext.setError(getString(R.string.error_existiert_bereits));
                            return true;
                        }
                    }else{
                        kontext.setError(getString(R.string.error_BuchstabenAnz));
                        return true;
                    }
                } else {
                    return false;
                }
            }
        });
    }

    private List<Durchdringung> setDefaultDurchringungen(Profile p) {
        List<Durchdringung> durchdringungen = new LinkedList<>();
        Durchdringung durchdringung;
        for (KKanaele kk : kanaeleRW.readList(thiscontext, valueOf(KANAELE))) {
            durchdringung = new Durchdringung(kk);
            if (p.equalQuellUndZielkontext()) {
                durchdringung.setEindringlichkeit(Eindringlichkeit.UNERLAUBT);
                durchdringung.setAkzeptanz(Akzeptanz.UNERLAUBT);
            } else {
                durchdringung.setEindringlichkeit(Eindringlichkeit.AUSSTEHEND);
                durchdringung.setAkzeptanz(Akzeptanz.AUSSTEHEND);
            }
            durchdringungen.add(durchdringung);
        }
        return durchdringungen;
    }

    private void reload() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, new DefinitionKontextFragment());
        fragmentTransaction.commit();

        //Tastatur ausblenden
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
