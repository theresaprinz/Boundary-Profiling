package at.jku.ce.bp_v1.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import at.jku.ce.bp_v1.R;

public class Welcome2Fragment extends Fragment {
    private static final int MAX = 5;
    private static final int MY_PERMISSIONS_REQUEST = 1;

    private boolean kalender_done = false;
    private boolean kgruppen_done = false;

    private TextView tv_kalender, tv_kgruppen;

    private ArrayList<String> kalender;
    private ArrayList<String> kontaktgruppen;

    String[] permissions = {Manifest.permission.READ_CALENDAR, Manifest.permission.READ_CONTACTS};

    public Welcome2Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_welcome2, container, false);

        tv_kalender = view.findViewById(R.id.welcome2_kalendertext);
        tv_kgruppen = view.findViewById(R.id.welcome2_Kontaktgruppentext);

        kalender = new ArrayList<>();
        kontaktgruppen = new ArrayList<>();

        analyseCalendar();
        analyseGroups();

        return view;
    }

    private void analyseCalendar() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CALENDAR)
                == PackageManager.PERMISSION_DENIED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_CALENDAR)) {
                tv_kalender.setText(R.string.Zugriff_verweigert);
            } else {
                requestPermissions(permissions, MY_PERMISSIONS_REQUEST);
            }
        } else {
            Cursor cursor = getActivity().getContentResolver().query(CalendarContract.Calendars.CONTENT_URI, null, null, null, CalendarContract.Calendars.CALENDAR_DISPLAY_NAME);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String name = cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.NAME));
                    if (name != null)
                        kalender.add(name);
                }
                cursor.close();
            }
        }

        if (kalender.size() > 0){
            int maxkalender = MAX;
            for (String s:kalender) {
                if (maxkalender > 0){
                    tv_kalender.setText(tv_kalender.getText() + s + ", ");
                    maxkalender--;
                }
            }
            tv_kalender.setText(tv_kalender.getText() +"...");
            kalender_done = true;
        }
    }

    private void analyseGroups(){
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_DENIED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_CONTACTS)) {
                tv_kgruppen.setText(R.string.Zugriff_verweigert);
            } else {
                requestPermissions(permissions, MY_PERMISSIONS_REQUEST);
            }
        }else{
            Cursor groups_cursor= getActivity().getContentResolver().query(
                    ContactsContract.Groups.CONTENT_URI,
                    new String[]{
                            ContactsContract.Groups._ID,
                            ContactsContract.Groups.TITLE
                    }, null, null, null
            );

            if(groups_cursor!=null){
                while(groups_cursor.moveToNext()){
                    String group_title = groups_cursor.getString(1);
                    if (!group_title.equals("Starred in Android") && !group_title.equals("My Contacts")&& group_title != null ){
                        kontaktgruppen.add(group_title);
                    }

                }
                groups_cursor.close();
            }

        }

        if (kontaktgruppen.size() > 0){
            int maxgruppen = MAX;
            for (String s:kontaktgruppen) {
                if (maxgruppen > 0){
                    tv_kgruppen.setText(tv_kgruppen.getText() + s + ", ");
                    maxgruppen--;
                }
            }
            tv_kgruppen.setText(tv_kgruppen.getText() +"...");
            kgruppen_done = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                if (grantResults.length > 1)  {
                    if(grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        tv_kalender.setText(R.string.Zugriff_verweigert);
                    }else{
                        if (!kalender_done)
                            analyseCalendar();
                    }

                    if(grantResults[1] == PackageManager.PERMISSION_DENIED) {
                        tv_kgruppen.setText(R.string.Zugriff_verweigert);
                    }else {
                        if (!kgruppen_done)
                            analyseGroups();
                    }
                }
                return;
            }
        }
    }
}
