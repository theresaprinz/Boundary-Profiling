package at.jku.ce.bp_v1.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import at.jku.ce.bp_v1.R;
import at.jku.ce.bp_v1.classes.RW;
import at.jku.ce.bp_v1.adapter.ProfilingListViewAdapter;
import at.jku.ce.bp_v1.classes.Files;
import at.jku.ce.bp_v1.classes.KKanaele;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfilingKKFragment extends Fragment {
    private Context thiscontext;

    private ListView listView;
    private ProfilingListViewAdapter adapter;

    private RW<Object, KKanaele> kKanaeleRW;

    private List<KKanaele> kKanaele;

    public ProfilingKKFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profiling_kk, container, false);

        thiscontext = container.getContext();
        listView = view.findViewById(R.id.lv_profiling_kk);

        kKanaeleRW = new RW<>();
        kKanaele = kKanaeleRW.readList(thiscontext, String.valueOf(Files.KANAELE));

        if (getArguments() != null){
            adapter = new ProfilingListViewAdapter(thiscontext, kKanaele, getArguments());

            listView.setAdapter(adapter);
        }

        return view;

    }

}
