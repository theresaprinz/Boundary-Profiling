package at.jku.ce.bp_v1.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import at.jku.ce.bp_v1.R;
import at.jku.ce.bp_v1.classes.Akzeptanz;
import at.jku.ce.bp_v1.classes.Durchdringung;
import at.jku.ce.bp_v1.classes.Eindringlichkeit;
import at.jku.ce.bp_v1.classes.Files;
import at.jku.ce.bp_v1.classes.KKanaele;
import at.jku.ce.bp_v1.classes.Kontext;
import at.jku.ce.bp_v1.classes.Profile;
import at.jku.ce.bp_v1.classes.RW;
import at.jku.ce.bp_v1.classes.Rolle;

import static at.jku.ce.bp_v1.classes.Files.KANAELE;
import static at.jku.ce.bp_v1.classes.Files.PROFILE;
import static java.lang.String.valueOf;

public class ExpandableDefinitionListViewAdapter extends BaseExpandableListAdapter {
    private Context thiscontext;
    private Activity activity;
    private EditText rolle;

    private List<Kontext> header;
    private Map<Kontext, List<Rolle>> child;

    private RW<Kontext, Rolle> kontextRW;
    private RW<Object, KKanaele> kanaeleRW;
    private RW<Profile, Durchdringung> profileRW;

    private TreeMap<Kontext, List<Rolle>> kontextMap;
    private TreeMap<Profile, List<Durchdringung>> profileMap;

    public ExpandableDefinitionListViewAdapter(Context context, Activity activity) {
        this.thiscontext = context;
        this.activity = activity;

        kontextRW = new RW<>();
        profileRW = new RW<>();
        kanaeleRW = new RW<>();

        kontextMap = kontextRW.readMap(thiscontext, String.valueOf(Files.KONTEXTE));

        this.header = new ArrayList<>(kontextMap.keySet());

        this.child = kontextMap;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this.child.get(this.header.get(groupPosition)).get(
                childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        LayoutInflater infalInflater = (LayoutInflater) this.thiscontext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //Here is the ListView of the ChildView
        if (childPosition < (getChildrenCount(groupPosition) - 1)) {
            // Getting child text
            final String childText = getChild(groupPosition, childPosition).toString();

            // Inflating child layout and setting textview
            convertView = infalInflater.inflate(R.layout.exp_lv_child, null);

            TextView child_text = convertView.findViewById(R.id.kontext_rollen);

            child_text.setText(childText);

            if (!childText.equals(thiscontext.getString(R.string.rolle_allgemein))) {
                ImageView deleteButton = convertView.findViewById(R.id.delete_rolle);
                deleteButton.setImageResource(R.drawable.ic_remove_rolle);

                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        kontextMap = kontextRW.readMap(thiscontext, String.valueOf(Files.KONTEXTE));
                        kontextMap.get(getGroup(groupPosition)).remove(childPosition);
                        child = kontextMap;
                        kontextRW.writeMap(thiscontext, kontextMap, String.valueOf(Files.KONTEXTE));
                        notifyDataSetChanged();
                    }
                });
            }


        } else {
            convertView = infalInflater.inflate(R.layout.exp_lv_child_footer, null);
            rolle = convertView.findViewById(R.id.newrolle);
            rolle.setOnEditorActionListener(
                    new EditText.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            if (actionId == EditorInfo.IME_ACTION_DONE || event.getAction() == KeyEvent.ACTION_DOWN
                                    && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                                if (v.getText().toString().length() >= 3 && v.getText().toString().length() <= 21) {
                                    //neue Rolle hinzufügen
                                    Kontext kontext_akt = (Kontext) getGroup(groupPosition);
                                    boolean exists = false;
                                    for (Rolle r : kontextMap.get(kontext_akt)) {
                                        if (r.getName().equals(v.getText().toString()))
                                            exists = true;
                                    }
                                    if (!exists) {
                                        v.setError(null);
                                        Rolle rolle_neu = new Rolle(v.getText().toString());
                                        kontextMap.get(kontext_akt).add(rolle_neu);
                                        child = kontextMap;
                                        kontextRW.writeMap(thiscontext, kontextMap, String.valueOf(Files.KONTEXTE));

                                        //neue Profile hinzufügen
                                        profileMap = profileRW.readMap(thiscontext, valueOf(PROFILE));
                                        Profile p;
                                        List<Durchdringung> durchdringungen;

                                        //neue Profile für bestehenden quellKontext mit neuer Rolle für bestehende zielKontexte
                                        for (Kontext zk : kontextMap.keySet()) {
                                            p = new Profile(zk, kontext_akt, rolle_neu);
                                            durchdringungen = setDefaultDurchringungen(p);
                                            profileMap.put(p, durchdringungen);
                                        }
                                        profileRW.writeMap(thiscontext, profileMap, String.valueOf(Files.PROFILE));

                                        //Tastatur ausblenden
                                        View view = activity.getCurrentFocus();
                                        if (view != null) {
                                            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                        }

                                        notifyDataSetChanged();
                                        return true;
                                    } else {
                                        v.setError(thiscontext.getString(R.string.error_existiert_bereits));
                                        return true;
                                    }
                                } else {
                                    v.setError(thiscontext.getString(R.string.error_BuchstabenAnz));
                                    return true;
                                }
                            } else {
                                return false;
                            }
                        };
                    });
        }
        return convertView;
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

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.child.get(this.header.get(groupPosition)).size() + 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.header.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.header.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        final Kontext kontext = (Kontext) getGroup(groupPosition);
        TextView header_text;
        ImageView deleteButton;

        // Inflating header layout and setting text
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.thiscontext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.exp_lv_header, null);

            header_text = convertView.findViewById(R.id.kontext);
            deleteButton = convertView.findViewById(R.id.delete_kontext);

            convertView.setTag(R.id.kontext, header_text);
            convertView.setTag(R.id.delete_kontext, deleteButton);
        } else {
            header_text = (TextView) convertView.getTag(R.id.kontext);
            deleteButton = (ImageView) convertView.getTag(R.id.delete_kontext);
        }

        deleteButton.setImageResource(R.drawable.ic_remove_kontext);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kontextMap = kontextRW.readMap(thiscontext, String.valueOf(Files.KONTEXTE));
                if (kontextMap.keySet().size() > 2){
                    header.remove(kontext);
                    child.remove(kontext);
                    kontextMap.remove(kontext);
                    kontextRW.writeMap(thiscontext, kontextMap, String.valueOf(Files.KONTEXTE));
                    notifyDataSetChanged();
                }else{
                    Toast.makeText(thiscontext, R.string.toast_mind2Kontexte, Toast.LENGTH_LONG).show();
                }

            }
        });

        header_text.setText(kontext.toString());

        if (isExpanded) {
            header_text.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                    R.drawable.ic_expand_less_black_24dp, 0);
        } else {
            header_text.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                    R.drawable.ic_expand_more_black_24dp, 0);
        }
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
