package at.jku.ce.bp_v1.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import at.jku.ce.bp_v1.R;
import at.jku.ce.bp_v1.adapter.ExpandableProfilingListViewAdapter;
import at.jku.ce.bp_v1.classes.Kontext;
import at.jku.ce.bp_v1.classes.Rolle;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfilingFragment extends Fragment {
    private static ExpandableListView expandableListView;
    private static ExpandableProfilingListViewAdapter adapter;

    private Kontext qKontext;
    private Rolle qRolle;

    private Context thiscontext;

    public ProfilingFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profiling, container, false);

        setHasOptionsMenu(true);

        thiscontext = container.getContext();
        expandableListView = view.findViewById(R.id.expandable_lv_profiling);
        expandableListView.setGroupIndicator(null);

        adapter = new ExpandableProfilingListViewAdapter(thiscontext);

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
                    setQuellrolleView(thiscontext, getActivity(), thiscontext.getResources().getString(R.string.quellRolle), false, false);
                    expandableListView.collapseGroup(previousGroup);
                    getActivity().invalidateOptionsMenu();
                }
                previousGroup = groupPosition;
                thiscontext.getSharedPreferences("at.jku.ce.bp_v1", Context.MODE_PRIVATE).edit().putInt("ProfExpandPrev", groupPosition).apply();
            }
        });

        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                if (expandableListView.isGroupExpanded(i)) {
                    setQuellkontextView(thiscontext, getActivity(), thiscontext.getResources().getString(R.string.quellKontext), false, false);
                    setQuellrolleView(thiscontext, getActivity(), thiscontext.getResources().getString(R.string.quellRolle), false, false);
                    expandableListView.collapseGroup(i);
                    getActivity().invalidateOptionsMenu();
                    return true;
                } else {
                    Toast.makeText(thiscontext, R.string.toast_quellkontext, Toast.LENGTH_SHORT).show();
                    TextView clickedkontext = view.findViewById(R.id.kontext);
                    qKontext = new Kontext(clickedkontext.getText().toString());

                    setQuellkontextView(thiscontext, getActivity(), qKontext.toString(), true, false);
                    expandableListView.expandGroup(i);
                    return true;
                }

            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childposition, long l) {
                Toast.makeText(thiscontext, R.string.toast_quellrolle, Toast.LENGTH_SHORT).show();
                TextView clickedRolle = view.findViewById(R.id.kontext_rollen);
                qRolle = new Rolle(clickedRolle.getText().toString());
                setQuellrolleView(thiscontext, getActivity(), qRolle.toString(), true, false);
                getActivity().invalidateOptionsMenu();
                return true;
            }
        });
    }

    public static void setQuellkontextView(Context context, FragmentActivity view, CharSequence textResource, boolean enabled, boolean choosen) {
        LinearLayout qKontextCard = view.findViewById(R.id.quellkontextCard);
        TextView qKontext = view.findViewById(R.id.quellkontext);

        if (!textResource.equals(""))
            qKontext.setText(textResource);

        if (enabled) {
            qKontext.setTextColor(ContextCompat.getColorStateList(context, R.color.white));
            qKontextCard.setActivated(true);
        } else if (!enabled && !choosen) {
            qKontext.setTextColor(ContextCompat.getColorStateList(context, R.color.greyDark));
            qKontextCard.setActivated(false);
        } else {
            qKontext.setTextColor(ContextCompat.getColorStateList(context, R.color.black));
            qKontextCard.setActivated(false);
        }

    }
    public static void setQuellrolleView(Context context, FragmentActivity view, CharSequence textResource, boolean enabled, boolean choosen) {
        ImageView qRolleImage = view.findViewById(R.id.quellrolleImage);
        TextView qRolle = view.findViewById(R.id.quellrolle);

        if (!textResource.equals(""))
            qRolle.setText(textResource);

        if (enabled) {
            qRolle.setTextColor(ContextCompat.getColorStateList(context, R.color.white));
            qRolleImage.setActivated(true);
        } else if (!enabled && !choosen) {
            qRolle.setTextColor(ContextCompat.getColorStateList(context, R.color.greyDark));
            qRolleImage.setActivated(false);
        } else {
            qRolle.setTextColor(ContextCompat.getColorStateList(context, R.color.black));
            qRolleImage.setActivated(false);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.next:
                replaceQKundQRToZKragment();
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void replaceQKundQRToZKragment() {
        ProfilingZKFragment profilingZKFragment = new ProfilingZKFragment();
        if (qKontext != null && qRolle != null) {
            Bundle arguments = new Bundle();
            arguments.putString("qKontext", qKontext.toString());
            arguments.putString("qRolle", qRolle.toString());
            profilingZKFragment.setArguments(arguments);
        }
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.profiling_fragment, profilingZKFragment, "ZKFragment");
        fragmentTransaction.addToBackStack("QKundQRToZK");
        fragmentTransaction.commit();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        boolean defined = qRolle != null;
        menu.findItem(R.id.next).setVisible(defined);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        int expandPosition = thiscontext.getSharedPreferences("at.jku.ce.bp_v1", Context.MODE_PRIVATE).getInt("ProfExpandPrev", -1);
        if (expandPosition != -1) {
            expandableListView.collapseGroup(expandPosition);
            thiscontext.getSharedPreferences("at.jku.ce.bp_v1", Context.MODE_PRIVATE).edit().putInt("ProfExpandPrev", -1).apply();
        }
    }
}
