package at.jku.ce.bp_v1.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.TreeMap;

import at.jku.ce.bp_v1.R;
import at.jku.ce.bp_v1.classes.RW;
import at.jku.ce.bp_v1.classes.Durchdringung;
import at.jku.ce.bp_v1.classes.Files;
import at.jku.ce.bp_v1.classes.KKanaele;
import at.jku.ce.bp_v1.classes.Profile;

public class DefinitionListViewAdapter extends ArrayAdapter<KKanaele> {
    private RW<Object, KKanaele> kanaeleRW;
    private List<KKanaele> kanaele;
    private RW<Profile, Durchdringung> profileRW;
    private TreeMap<Profile, List<Durchdringung>> profileMap;

    private Context thiscontext;

    public DefinitionListViewAdapter(Context thiscontext, List<KKanaele> kanaeleList) {
        super(thiscontext, -1, kanaeleList);
        this.thiscontext = thiscontext;
        kanaeleRW = new RW<>();
        profileRW = new RW<>();
        kanaele = kanaeleList;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView header_text;

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.thiscontext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.lv_header_kanaele, null);
        }


        header_text = convertView.findViewById(R.id.kanal);
        ImageView delete_kanal = convertView.findViewById(R.id.kanal_remove);

        delete_kanal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kanaele = kanaeleRW.readList(thiscontext, String.valueOf(Files.KANAELE));
                if (kanaele.size() > 1){
                    KKanaele kanal_remove = kanaele.get(position);
                    kanaele.remove(kanal_remove);
                    kanaeleRW.writeList(thiscontext, kanaele, String.valueOf(Files.KANAELE));

                    profileMap = profileRW.readMap(thiscontext, String.valueOf(Files.PROFILE));
                    Durchdringung durchdringung_remove = null;
                    for (Profile p : profileMap.keySet()) {
                        List<Durchdringung> durchdringungen = profileMap.get(p);
                        for (Durchdringung d : durchdringungen) {
                            if (d.getKommunikationskanal().getName().equals(kanal_remove.getName())) {
                                durchdringung_remove = d;
                                break;
                            }
                        }
                        durchdringungen.remove(durchdringung_remove);
                        profileMap.put(p, durchdringungen);
                    }
                    profileRW.writeMap(thiscontext, profileMap, String.valueOf(Files.PROFILE));
                    notifyDataSetChanged();
                }else{
                    Toast.makeText(thiscontext, R.string.toast_mind1Kanal, Toast.LENGTH_LONG).show();
                }

            }
        });

        header_text.setText(kanaele.get(position).toString());

        return convertView;
    }

    @Nullable
    @Override
    public KKanaele getItem(int position) {
        return kanaele.get(position);
    }

    @Override
    public long getItemId(int position) {
        return kanaele.get(position).get_id().getMostSignificantBits();
    }

    @Override
    public int getCount() {
        return kanaele.size();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
