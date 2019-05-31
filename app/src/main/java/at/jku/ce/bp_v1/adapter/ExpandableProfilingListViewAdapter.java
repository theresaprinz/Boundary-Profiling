package at.jku.ce.bp_v1.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.jku.ce.bp_v1.R;
import at.jku.ce.bp_v1.classes.RW;
import at.jku.ce.bp_v1.classes.Files;
import at.jku.ce.bp_v1.classes.Kontext;
import at.jku.ce.bp_v1.classes.Rolle;

public class ExpandableProfilingListViewAdapter extends BaseExpandableListAdapter {
    private Context thiscontext;
    private List<Kontext> header;
    private Map<Kontext, List<Rolle>> child;
    private boolean expand;

    private RW<Kontext, Rolle> kontextRW;
    private Map<Kontext, List<Rolle>> kontextListHashMap;

    private Kontext removeKontext;

    public ExpandableProfilingListViewAdapter(Context context) {
        this.thiscontext = context;

        kontextRW = new RW<>();

        kontextListHashMap = kontextRW.readMap(thiscontext, String.valueOf(Files.KONTEXTE));

        this.header = new ArrayList<>(kontextListHashMap.keySet());

        expand = true;

        this.child = kontextListHashMap;

    }

    public ExpandableProfilingListViewAdapter(Context context, String qKontext) {
        this.thiscontext = context;

        kontextRW = new RW<>();

        kontextListHashMap = kontextRW.readMap(thiscontext, String.valueOf(Files.KONTEXTE));

        this.header = new ArrayList<>(kontextListHashMap.keySet());

        for (Kontext k : header) {
            if (k.getName().equals(qKontext))
                removeKontext = k;
        }

        header.remove(removeKontext);
        kontextListHashMap.remove(removeKontext);

        expand = false;

        this.child = new HashMap<>();
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

        // Getting child text
        final String childText = getChild(groupPosition, childPosition).toString();

        // Inflating child layout and setting textview
        convertView = infalInflater.inflate(R.layout.exp_lv_child, null);

        TextView child_text = convertView.findViewById(R.id.kontext_rollen);

        child_text.setText(childText);

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (expand)
            return this.child.get(this.header.get(groupPosition)).size();
        return 0;
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

        // Inflating header layout and setting text
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.thiscontext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.exp_lv_header, null);

            header_text = convertView.findViewById(R.id.kontext);

            convertView.setTag(R.id.kontext, header_text);
        } else {
            header_text = (TextView) convertView.getTag(R.id.kontext);
        }

        header_text.setText(kontext.toString());

        if (expand) {
            if (isExpanded) {
                header_text.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                        R.drawable.ic_expand_less_black_24dp, 0);
            } else {
                header_text.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                        R.drawable.ic_expand_more_black_24dp, 0);
            }
        }

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        if (expand)
            return true;
        return false;
    }
}
