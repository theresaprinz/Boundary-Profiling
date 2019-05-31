package at.jku.ce.bp_v1.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import at.jku.ce.bp_v1.R;
import at.jku.ce.bp_v1.classes.Akzeptanz;
import at.jku.ce.bp_v1.classes.Eindringlichkeit;
import at.jku.ce.bp_v1.classes.KKanaele;

public class ProfilingListViewAdapter extends ArrayAdapter<KKanaele> {
    private Context thiscontext;
    private List<KKanaele> kanaele;
    private List<String> edk;
    private List<String> aas;

    private RelativeLayout active;
    private int aktposition;

    public ProfilingListViewAdapter(@NonNull Context context, @NonNull List<KKanaele> objects, Bundle arguments) {
        super(context, -1, objects);
        thiscontext = context;
        kanaele = objects;
        this.aas = arguments.getStringArrayList("aas");
        this.edk = arguments.getStringArrayList("edk");
        this.aktposition = arguments.getInt("position");
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        TextView header_text;

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.thiscontext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.lv_header_profile, null);

            header_text = convertView.findViewById(R.id.profil_kanal);
            active = convertView.findViewById(R.id.active);

            convertView = changeProfile(convertView, position);

            convertView.setTag(R.id.profil_kanal, header_text);
            convertView.setTag(R.id.active, active);
        } else {
            header_text = (TextView) convertView.getTag(R.id.profil_kanal);
            active = (RelativeLayout) convertView.getTag(R.id.active);
        }

        header_text.setText(kanaele.get(position).toString());

        return convertView;
    }

    private View changeProfile(View convertView, int position) {
        if (edk != null) {
            switch (Eindringlichkeit.fromValue(edk.get(position))) {
                case HOCH:
                    convertView.findViewById(R.id.profile_color).setBackgroundResource(R.color.hoch);
                    break;
                case MITTEL:
                    convertView.findViewById(R.id.profile_color).setBackgroundResource(R.color.mittel);
                    break;
                case GERING:
                    convertView.findViewById(R.id.profile_color).setBackgroundResource(R.color.gering);
                    break;
                case UNDEFINIERT:
                    convertView.findViewById(R.id.profile_color).setBackgroundResource(R.color.undefiniert);
                    break;
                case AUSSTEHEND:
                    convertView.findViewById(R.id.profile_color).setBackgroundResource(R.color.ausstehen);
                    break;
                default:
                    convertView.findViewById(R.id.profile_color).setBackgroundResource(R.color.ausstehen);
                    break;
            }

            if (aas != null) {
                ((ImageView) convertView.findViewById(R.id.profile_icon)).setImageTintList(ColorStateList.valueOf(thiscontext.getResources().getColor(R.color.black)));
                switch (Akzeptanz.fromValue(aas.get(position))) {
                    case AKZEPTANZ:
                        ((ImageView) convertView.findViewById(R.id.profile_icon)).setImageResource(R.drawable.ic_check_black_24dp);
                        break;
                    case ABLEHNUNG:
                        ((ImageView) convertView.findViewById(R.id.profile_icon)).setImageResource(R.drawable.ic_close_black_24dp);
                        break;
                    case SITUATIONSABHAENGIG:
                        ((ImageView) convertView.findViewById(R.id.profile_icon)).setImageResource(R.drawable.ic_situational);
                        break;
                    case UNDEFINIERT:
                        ((ImageView) convertView.findViewById(R.id.profile_icon)).setImageResource(R.drawable.ic_undefined);
                        break;
                    case AUSSTEHEND:
                        ((ImageView) convertView.findViewById(R.id.profile_icon)).setImageTintList(ColorStateList.valueOf(thiscontext.getResources().getColor(R.color.greyDark)));
                        ((ImageView) convertView.findViewById(R.id.profile_icon)).setImageResource(R.drawable.ic_undefined);
                        break;
                    default:
                        ((ImageView) convertView.findViewById(R.id.profile_icon)).setImageTintList(ColorStateList.valueOf(thiscontext.getResources().getColor(R.color.greyDark)));
                        ((ImageView) convertView.findViewById(R.id.profile_icon)).setImageResource(R.drawable.ic_undefined);
                        break;
                }
            }

            if (position == aktposition)
                active.setBackgroundResource(R.color.activated);
            else
                active.setBackgroundResource(R.color.transparent);
        } else {
            if (position == 0) {
                active.setBackgroundResource(R.color.activated);
            }
        }


        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).get_id().getMostSignificantBits();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
