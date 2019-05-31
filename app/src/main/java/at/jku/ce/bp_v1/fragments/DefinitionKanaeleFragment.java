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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;
import java.util.TreeMap;

import at.jku.ce.bp_v1.R;
import at.jku.ce.bp_v1.classes.RW;
import at.jku.ce.bp_v1.adapter.DefinitionListViewAdapter;
import at.jku.ce.bp_v1.classes.Akzeptanz;
import at.jku.ce.bp_v1.classes.Durchdringung;
import at.jku.ce.bp_v1.classes.Eindringlichkeit;
import at.jku.ce.bp_v1.classes.Files;
import at.jku.ce.bp_v1.classes.KKanaele;
import at.jku.ce.bp_v1.classes.Profile;


/**
 * A simple {@link Fragment} subclass.
 */
public class DefinitionKanaeleFragment extends Fragment {
    private Context thiscontext;

    private ListView listView;
    private LinearLayout footerLayout;
    private EditText kanal;

    private DefinitionListViewAdapter adapter;

    private RW<Object, KKanaele> kanaeleRW;
    private RW<Profile, Durchdringung> profileRW;
    private List<KKanaele> kanaeleList;
    private TreeMap<Profile, List<Durchdringung>> profileMap;

    public DefinitionKanaeleFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_definition_kanaele, container, false);

        thiscontext = getContext();
        getActivity().setTitle(R.string.tab_kanäle);

        listView = view.findViewById(R.id.lv_definition_kanaele);

        View v = getLayoutInflater().inflate(R.layout.lv_footer, listView, false);
        footerLayout = v.findViewById(R.id.lv_footer_layout);

        listView.addFooterView(footerLayout);

        kanaeleRW = new RW<>();
        profileRW = new RW<>();
        kanaeleList = kanaeleRW.readList(thiscontext, String.valueOf(Files.KANAELE));
        adapter = new DefinitionListViewAdapter(thiscontext, kanaeleList);
        listView.setAdapter(adapter);

        setListeners();

        return view;
    }

    private void setListeners() {

        kanal = footerLayout.findViewById(R.id.newkanal);


        kanal.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE || event.getAction() == KeyEvent.ACTION_DOWN
                                && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (kanal.getText().toString().length() >= 3 && kanal.getText().toString().length() <= 21){
                        kanaeleList = kanaeleRW.readList(thiscontext, String.valueOf(Files.KANAELE));
                        boolean exists = false;
                        for (KKanaele k : kanaeleList) {
                            if (k.getName().equals(kanal.getText().toString()))
                                exists = true;
                        }
                        if (!exists) {
                            kanal.setError(null);
                            KKanaele kanal_neu = new KKanaele(kanal.getText().toString());
                            kanaeleList.add(kanal_neu);
                            kanaeleRW.writeList(thiscontext, kanaeleList, String.valueOf(Files.KANAELE));

                            profileMap = profileRW.readMap(thiscontext, String.valueOf(Files.PROFILE));
                            for (Profile p : profileMap.keySet()) {
                                List<Durchdringung> durchdringungen = profileMap.get(p);
                                Durchdringung durchdringung_neu = new Durchdringung(kanal_neu);
                                if (p.equalQuellUndZielkontext()) {
                                    durchdringung_neu.setAkzeptanz(Akzeptanz.UNERLAUBT);
                                    durchdringung_neu.setEindringlichkeit(Eindringlichkeit.UNERLAUBT);
                                } else {
                                    durchdringung_neu.setAkzeptanz(Akzeptanz.AUSSTEHEND);
                                    durchdringung_neu.setEindringlichkeit(Eindringlichkeit.AUSSTEHEND);
                                }

                                durchdringungen.add(durchdringung_neu);
                                profileMap.put(p, durchdringungen);
                            }
                            profileRW.writeMap(thiscontext, profileMap, String.valueOf(Files.PROFILE));

                            reload();
                            return true;
                        } else {
                            kanal.setError(thiscontext.getString(R.string.error_existiert_bereits));
                            return true;
                        }
                    }else{
                        kanal.setError(getString(R.string.error_BuchstabenAnz));
                        return true;
                    }
                } else {
                    return false;
                }
            }
        });

    }

    private void reload() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, new DefinitionKanaeleFragment());
        fragmentTransaction.commit();

        //Tastatur ausblenden
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
