package at.jku.ce.bp_v1.fragments;


import android.content.Context;
import android.os.Bundle;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import at.jku.ce.bp_v1.R;
import at.jku.ce.bp_v1.adapter.ExpandableProfilingListViewAdapter;
import at.jku.ce.bp_v1.classes.Kontext;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfilingZKFragment extends Fragment {
    private static ExpandableListView expandableListView;
    private static ExpandableProfilingListViewAdapter adapter;

    private Context thiscontext;

    private Kontext zKontext;

    public ProfilingZKFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profiling_zk, container, false);

        setHasOptionsMenu(true);

        thiscontext = container.getContext();

        if (getArguments() != null){
            expandableListView = view.findViewById(R.id.expandable_lv_profiling_zk);
            expandableListView.setGroupIndicator(null);

            adapter = new ExpandableProfilingListViewAdapter(thiscontext, getArguments().getString("qKontext"));

            expandableListView.setAdapter(adapter);

            setListener();
        }

        return view;
    }

    private void setListener() {
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                Toast.makeText(thiscontext, R.string.toast_zielkontext, Toast.LENGTH_SHORT).show();
                TextView clickedkontext = view.findViewById(R.id.kontext);
                zKontext = new Kontext(clickedkontext.getText().toString());
                setZielkontextView(thiscontext, getActivity(), zKontext.toString(), true, false);
                getActivity().invalidateOptionsMenu();
                return true;
            }
        });
    }

    public static void setZielkontextView(Context context, FragmentActivity view, CharSequence textResource, boolean enabled, boolean choosen) {
        TextView zKontext = view.findViewById(R.id.zielkontext);
        LinearLayout zKontextCard = view.findViewById(R.id.zielkontextCard);
        if (!textResource.equals(""))
            zKontext.setText(textResource);

        if (choosen) {
            zKontext.setTextColor(ContextCompat.getColorStateList(context, R.color.black));
            zKontextCard.setActivated(false);
        } else if (!choosen && !enabled) {
            zKontext.setTextColor(ContextCompat.getColorStateList(context, R.color.greyDark));
            zKontextCard.setActivated(false);
        } else {
            zKontext.setTextColor(ContextCompat.getColorStateList(context, R.color.white));
            zKontextCard.setActivated(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.next:
                replaceZKtoEDKFragment();
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void replaceZKtoEDKFragment() {
        ProfilingFragment.setQuellkontextView(thiscontext, getActivity(), "", false, true);
        ProfilingFragment.setQuellrolleView(thiscontext, getActivity(), "", false, true);
        setZielkontextView(thiscontext, getActivity(), "", false, true);

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        ProfilingEDKFragment edkFragment = new ProfilingEDKFragment();
        if (getArguments() != null && zKontext != null) {
            Bundle arguments = new Bundle();
            arguments.putString("qKontext", getArguments().getString("qKontext"));
            arguments.putString("qRolle", getArguments().getString("qRolle"));
            arguments.putString("zKontext", zKontext.toString());
            edkFragment.setArguments(arguments);
        }
        fragmentTransaction.replace(R.id.profiling_fragment, edkFragment, "EDKFragment");
        fragmentTransaction.add(R.id.profiling_fragment_edk_kk, new ProfilingKKFragment(), "EDKKKFragment");
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.addToBackStack("ZKToEDK");
        fragmentTransaction.commit();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        // Wenn von Profiling auf Übersicht gewechselt wird
        if (getActivity().findViewById(R.id.quellrolle) == null) {
            menu.findItem(R.id.next).setVisible(false);
        } else {
            boolean defined = zKontext != null;
            menu.findItem(R.id.next).setVisible(defined);
        }

        super.onPrepareOptionsMenu(menu);
    }
}
