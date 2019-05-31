package at.jku.ce.bp_v1.fragments;


import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import at.jku.ce.bp_v1.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class Welcome3Fragment extends Fragment {
    private static final int MAX_KONTAKTE = 5;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private TextView tv_kgruppe;
    private Map<String, List<String>> gruppenKontakte;

    public Welcome3Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_welcome3, container, false);

        tv_kgruppe = view.findViewById(R.id.welcome3_KGruppetext);

        gruppenKontakte = new HashMap<>();

        analyseGroupcontacts();

        return view;
    }

    private void analyseGroupcontacts() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_CONTACTS)) {
                tv_kgruppe.setText(R.string.Zugriff_verweigert);
            } else {
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        } else {
            Cursor groups_cursor = getActivity().getContentResolver().query(
                    ContactsContract.Groups.CONTENT_URI,
                    new String[]{
                            ContactsContract.Groups._ID,
                            ContactsContract.Groups.TITLE
                    }, null, null, null
            );
            HashMap<String, String> groups = new HashMap<>();
            if (groups_cursor != null) {
                while (groups_cursor.moveToNext()) {
                    String group_title = groups_cursor.getString(1);
                    if (!group_title.equals("Starred in Android") && !group_title.equals("My Contacts") && group_title != null) {
                        String id = groups_cursor.getString(0);
                        groups.put(id, group_title);
                    }

                }
                groups_cursor.close();
            }
            Cursor dataCursor = getActivity().getContentResolver().query(
                    ContactsContract.Data.CONTENT_URI,
                    new String[]{
                            ContactsContract.Data.DISPLAY_NAME,
                            ContactsContract.Data.DATA1
                    },
                    ContactsContract.Data.MIMETYPE + "=?",
                    new String[]{ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE}, null
            );

            if (dataCursor != null) {
                while (dataCursor.moveToNext()) {
                    String contact_name = dataCursor.getString(0);
                    String group_id = dataCursor.getString(1);
                    String groupTitle = groups.get(group_id);
                    if (groupTitle != null) {
                        if (gruppenKontakte.containsKey(groupTitle)) {
                            List<String> kontakte = gruppenKontakte.get(groupTitle);
                            kontakte.add(contact_name);
                            gruppenKontakte.put(groupTitle, kontakte);
                        } else {
                            List<String> kontakte = new LinkedList<>();
                            kontakte.add(contact_name);
                            gruppenKontakte.put(groupTitle, kontakte);
                        }
                    }
                }
                dataCursor.close();
            }

        }

        int maxKontakte;
        for (String gruppe : gruppenKontakte.keySet()) {
            tv_kgruppe.setText(tv_kgruppe.getText() + "\nGruppe: " + gruppe + "\nKontakte:");
            maxKontakte = MAX_KONTAKTE;
            for (String kontakt : gruppenKontakte.get(gruppe)) {
                if (maxKontakte > 0) {
                    tv_kgruppe.setText(tv_kgruppe.getText() + " " + kontakt + ", ");
                    maxKontakte--;
                }
            }
            tv_kgruppe.setText(tv_kgruppe.getText() + "...\n");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0){
                    if(grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        tv_kgruppe.setText(R.string.Zugriff_verweigert);
                    }else{
                        analyseGroupcontacts();
                    }
                }
                return;
            }
        }
    }

}
