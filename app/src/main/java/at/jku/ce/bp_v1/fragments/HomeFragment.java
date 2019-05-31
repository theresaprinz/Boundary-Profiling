package at.jku.ce.bp_v1.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
//import android.support.v7.widget.ShareActionProvider;
import android.widget.Toast;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import at.jku.ce.bp_v1.BuildConfig;
import at.jku.ce.bp_v1.R;
import at.jku.ce.bp_v1.TableMainLayout;
import at.jku.ce.bp_v1.classes.Durchdringung;
import at.jku.ce.bp_v1.classes.KKanaele;
import at.jku.ce.bp_v1.classes.Kontext;
import at.jku.ce.bp_v1.classes.Profile;
import at.jku.ce.bp_v1.classes.RW;
import at.jku.ce.bp_v1.classes.Rolle;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    private RW<Kontext, Rolle> kontextRW;
    private RW<Object, KKanaele> kanaeleRW;
    private RW<Profile, Durchdringung> profileRW;

    private List<KKanaele> kanaeleList;
    private TreeMap<Kontext, List<Rolle>> kontextMap;
    private TreeMap<Profile, List<Durchdringung>> profileMap;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        kanaeleRW = new RW<>();
        kontextRW = new RW<>();
        profileRW = new RW<>();

        return new TableMainLayout(container.getContext(), getActivity());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        Toast.makeText(getActivity(), R.string.Zugriff_verweigert, Toast.LENGTH_LONG).show();
                    } else {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                    }
                } else {
                    export();
                    doShare();
                }
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0){
                    if( grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(getActivity(), R.string.Zugriff_verweigert, Toast.LENGTH_LONG).show();
                    }else{
                        export();
                        doShare();
                    }
                }
                return;
            }
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.next).setVisible(false);
        menu.findItem(R.id.share).setVisible(true);
        super.onPrepareOptionsMenu(menu);
    }

    private void doShare() {
        File zip = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Profiling" + ".zip");
        Uri uri = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".provider", zip);
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
        sendIntent.setType("application/zip");
        startActivity(Intent.createChooser(sendIntent, "Teilen"));
    }

    private void export() {

        String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Profiling";
        String fileName;
        String filePath;
        File f;
        CSVWriter writer;
        FileWriter mFileWriter;

        try {
            new File(baseDir).mkdirs();

            // Kanaele CSV
             fileName = "Kanaele.csv";
             filePath = baseDir + File.separator + fileName;
             f = new File(filePath );

            if(f.exists() && !f.isDirectory()){ // /storage/emulated/0/Kanaele.csv
                mFileWriter = new FileWriter(filePath , false);
                writer = new CSVWriter(mFileWriter);
            }
            else {
                writer = new CSVWriter(new FileWriter(filePath));
            }

            kanaeleList = kanaeleRW.readList(getContext(), String.valueOf(at.jku.ce.bp_v1.classes.Files.KANAELE));
            String[] kanaele = new String[kanaeleList.size()];
            int i = 0;
            for (KKanaele kk: kanaeleList                 ) {
                kanaele[i] = kk.toString();
                i++;
            }
            writer.writeNext(kanaele);
            writer.close();

            // Kontexte CSV
            fileName = "Kontexte.csv";
            filePath = baseDir + File.separator + fileName;
            f = new File(filePath );

            if(f.exists() && !f.isDirectory()){ // /storage/emulated/0/AnalysisData.csv
                mFileWriter = new FileWriter(filePath , false);
                writer = new CSVWriter(mFileWriter);
            }
            else {
                writer = new CSVWriter(new FileWriter(filePath));
            }

            kontextMap = kontextRW.readMap(getContext(), String.valueOf(at.jku.ce.bp_v1.classes.Files.KONTEXTE));
            List<String[]> kontexte = new ArrayList<>();
            for (Kontext k: kontextMap.keySet()                 ) {
                String[] rollen = new String[kontextMap.get(k).size()+1];
                rollen[0] = k.toString(); // 1. Spalte beinhaltet den Kontext
                for ( i = 0; i< kontextMap.get(k).size(); i++){
                    rollen[i+1] = kontextMap.get(k).get(i).toString(); // Weitere Spalten beinhalten Rollen
                }
                kontexte.add(rollen);
            }
            writer.writeAll(kontexte);
            writer.close();

            // Profile CSV
            fileName = "Profile.csv";
            filePath = baseDir + File.separator + fileName;
            f = new File(filePath );

            if(f.exists() && !f.isDirectory()){ // /storage/emulated/0/AnalysisData.csv
                mFileWriter = new FileWriter(filePath , false);
                writer = new CSVWriter(mFileWriter);
            }
            else {
                writer = new CSVWriter(new FileWriter(filePath));
            }

            profileMap = profileRW.readMap(getContext(), String.valueOf(at.jku.ce.bp_v1.classes.Files.PROFILE));
            List<String[]> profile = new ArrayList<>();
            for (Profile p: profileMap.keySet()                 ) {
                String[] d = new String[3];
                d[0] = p.getQuellkontext().toString(); // 1. Spalte beinhaltet den qKontext
                d[1] = p.getQuellrolle().toString(); // 2. Spalte beinhaltet den qRolle
                d[2] = p.getZielkontext().toString(); // 3. Spalte beinhaltet den zKontext
                profile.add(d);

                //neue Zeilen für Durchdringungen
                for ( i = 0; i< profileMap.get(p).size(); i++){
                    d = new String[3];
                    d[0] = profileMap.get(p).get(i).getKommunikationskanal().toString(); // 1. Spalte Kommunikationskanal
                    d[1] = profileMap.get(p).get(i).getEindringlichkeit().toString(); // 2. Spalte Eindringlichkeit
                    d[2] = profileMap.get(p).get(i).getAkzeptanz().toString(); // 3. Spalte Akzeptanz
                    profile.add(d);
                }

                // leere Zeile um Profile voneinander abzugrenzen
                d = new String[1];
                profile.add(d);
            }
            writer.writeAll(profile);
            writer.close();

            zipFolder(baseDir, baseDir + ".zip");

            Toast.makeText(getContext(), "File wurde erfolgreich exportiert!", Toast.LENGTH_SHORT).show();
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }

    static public void zipFolder(String srcFolder, String destZipFile) throws Exception {
        ZipOutputStream zip = null;
        FileOutputStream fileWriter = null;
        fileWriter = new FileOutputStream(destZipFile);
        zip = new ZipOutputStream(fileWriter);
        addFolderToZip("", srcFolder, zip);
        zip.flush();
        zip.close();
    }
    static private void addFileToZip(String path, String srcFile, ZipOutputStream zip)
            throws Exception {
        File folder = new File(srcFile);
        if (folder.isDirectory()) {
            addFolderToZip(path, srcFile, zip);
        } else {
            byte[] buf = new byte[1024];
            int len;
            FileInputStream in = new FileInputStream(srcFile);
            zip.putNextEntry(new ZipEntry(path + "/" + folder.getName()));
            while ((len = in.read(buf)) > 0) {
                zip.write(buf, 0, len);
            }
            in.close();
        }
    }

    static private void addFolderToZip(String path, String srcFolder, ZipOutputStream zip)
            throws Exception {
        File folder = new File(srcFolder);

        for (String fileName : folder.list()) {
            if (path.equals("")) {
                addFileToZip(folder.getName(), srcFolder + "/" + fileName, zip);
            } else {
                addFileToZip(path + "/" + folder.getName(), srcFolder + "/" +   fileName, zip);
            }
        }
    }

}
