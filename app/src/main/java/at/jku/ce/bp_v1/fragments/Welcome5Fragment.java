package at.jku.ce.bp_v1.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import at.jku.ce.bp_v1.R;

public class Welcome5Fragment extends Fragment {
    private static final int MAX_KONTAKTE = 10;
    private static final int MY_PERMISSIONS_REQUEST_READ_CALL_LOG = 1;

    private Map<String, Integer> anruferHäufig;
    private TextView tv_kontakte;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_welcome5, container, false);

        tv_kontakte = view.findViewById(R.id.welcome5_letzte_Dtext);

        anruferHäufig = new LinkedHashMap<>();

        analyseContacts();

        return view;
    }

    private void analyseContacts() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CALL_LOG)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_CALL_LOG)) {
                tv_kontakte.setText(R.string.Zugriff_verweigert);
            } else {
                requestPermissions(new String[]{Manifest.permission.READ_CALL_LOG}, MY_PERMISSIONS_REQUEST_READ_CALL_LOG);
            }
        } else {
            Cursor cursor = getActivity().getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DATE + " DESC");
            List<String> listAll = new LinkedList<>();
            if (cursor.moveToFirst()) {
                while (cursor.moveToNext()) {
                    listAll.add(cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME)));
                }

                if (listAll != null && !listAll.isEmpty()) {
                    Map<String, Integer> counterMap = new HashMap<>();

                    for (String valueAsKey : listAll) {
                        if (valueAsKey != null) {
                            Integer counter = counterMap.get(valueAsKey);
                            counterMap.put(valueAsKey, counter == null ? 1 : counter + 1);
                        }
                    }

                    List<Map.Entry<String, Integer>> list = new LinkedList<>(counterMap.entrySet());

                    // Sorting the list based on values
                    Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
                        public int compare(Map.Entry<String, Integer> o1,
                                           Map.Entry<String, Integer> o2) {
                            return o2.getValue().compareTo(o1.getValue());
                        }
                    });

                    // Maintaining insertion order with the help of LinkedList

                    for (Map.Entry<String, Integer> entry : list) {
                        anruferHäufig.put(entry.getKey(), entry.getValue());
                    }
                }
            }
            cursor.close();

            int maxKontakte = MAX_KONTAKTE;

            for (String kontakt : anruferHäufig.keySet()) {
                if (maxKontakte > 0) {
                    tv_kontakte.setText(tv_kontakte.getText() + kontakt + ", ");
                    maxKontakte--;
                }
            }
            tv_kontakte.setText(tv_kontakte.getText() + "...\n");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CALL_LOG: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    tv_kontakte.setText(R.string.Zugriff_verweigert);
                }else{
                    analyseContacts();
                }
                return;
            }
        }
    }
}
