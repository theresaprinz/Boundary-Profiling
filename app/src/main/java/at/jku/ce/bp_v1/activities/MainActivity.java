package at.jku.ce.bp_v1.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import at.jku.ce.bp_v1.R;
import at.jku.ce.bp_v1.classes.RW;
import at.jku.ce.bp_v1.fragments.DefinitionFragment;
import at.jku.ce.bp_v1.fragments.HomeFragment;
import at.jku.ce.bp_v1.fragments.ProfilingFragment;
import at.jku.ce.bp_v1.fragments.ProfilingZKFragment;
import at.jku.ce.bp_v1.classes.Akzeptanz;
import at.jku.ce.bp_v1.classes.Durchdringung;
import at.jku.ce.bp_v1.classes.Eindringlichkeit;
import at.jku.ce.bp_v1.classes.KKanaele;
import at.jku.ce.bp_v1.classes.Kontext;
import at.jku.ce.bp_v1.classes.Profile;
import at.jku.ce.bp_v1.classes.Rolle;

import static at.jku.ce.bp_v1.classes.Files.KANAELE;
import static at.jku.ce.bp_v1.classes.Files.KONTEXTE;
import static at.jku.ce.bp_v1.classes.Files.PROFILE;
import static java.lang.String.valueOf;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bn_bnv;

    private HomeFragment homeFragment;
    private DefinitionFragment definitionFragment;
    private ProfilingFragment profilingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bn_bnv = findViewById(R.id.bottom_navigation);

        homeFragment = new HomeFragment();
        definitionFragment = new DefinitionFragment();
        profilingFragment = new ProfilingFragment();

        setTitle(R.string.appbar_uebersicht);

        setFragement(homeFragment);

        bn_bnv.setSelectedItemId(R.id.nav_home);
        bn_bnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_frame);
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        setTitle(R.string.appbar_uebersicht);
                        setFragement(homeFragment);
                        return true;
                    case R.id.nav_definition:
                        setTitle(R.string.appbar_definition);
                        setFragement(definitionFragment);
                        return true;
                    case R.id.nav_profiling:
                        if (!(currentFragment instanceof ProfilingFragment && currentFragment.equals(profilingFragment))){
                            setFragement(profilingFragment);
                        }
                        setTitle(R.string.appbar_profiling);
                        return true;

                    default:
                        return false;
                }


            }
        });
        if (getSharedPreferences("at.jku.ce.bp_v1", MODE_PRIVATE).getBoolean("FIRSTSTART", true))
            setDefaultValues();

        clearBackStack();
    }

    private void setDefaultValues() {
        RW<Kontext, Rolle> kontextRW = new RW<>();
        TreeMap<Kontext, List<Rolle>> kontextListMap = new TreeMap<>();

        List<Rolle> rollenFamilie = new LinkedList<>();
        rollenFamilie.add(new Rolle(getString(R.string.rolle_allgemein)));
        rollenFamilie.add(new Rolle("Eltern"));
        rollenFamilie.add(new Rolle("Geschwister"));

        List<Rolle> rollenArbeit = new LinkedList<>();
        rollenArbeit.add(new Rolle(getString(R.string.rolle_allgemein)));
        rollenArbeit.add(new Rolle("Chef"));
        rollenArbeit.add(new Rolle("Mitarbeiter"));

        kontextListMap.put(new Kontext("Arbeit"), rollenArbeit);
        kontextListMap.put(new Kontext("Familie"), rollenFamilie);
        kontextRW.writeMap(this, kontextListMap, valueOf(KONTEXTE));

        RW<Object, KKanaele> kKanaeleRW = new RW<>();
        List<KKanaele> kKanaele = new LinkedList<>();
        kKanaele.add(new KKanaele("E-Mail"));
        kKanaele.add(new KKanaele("Nachricht"));
        kKanaele.add(new KKanaele("Telefon"));
        kKanaeleRW.writeList(this, kKanaele, valueOf(KANAELE));

        RW<Profile, Durchdringung> profileRW = new RW<>();
        TreeMap<Profile, List<Durchdringung>> profiles = new TreeMap<>();
        Profile p;
        List<Durchdringung> durchdringungen;
        Durchdringung durchdringung;
        for (Kontext k : kontextListMap.keySet()) {
            for (Rolle r : kontextListMap.get(k)) {
                for (Kontext zk : kontextListMap.keySet()) {
                    p = new Profile(zk, k, r);
                    durchdringungen = new LinkedList<>();
                    for (KKanaele kk : kKanaele) {
                        durchdringung = new Durchdringung(kk);
                        if (k.equals(zk)) {
                            durchdringung.setEindringlichkeit(Eindringlichkeit.UNERLAUBT);
                            durchdringung.setAkzeptanz(Akzeptanz.UNERLAUBT);
                        } else {
                            durchdringung.setEindringlichkeit(Eindringlichkeit.AUSSTEHEND);
                            durchdringung.setAkzeptanz(Akzeptanz.AUSSTEHEND);
                        }
                        durchdringungen.add(durchdringung);
                    }
                    profiles.put(p, durchdringungen);
                }
            }
        }
        profileRW.writeMap(this, profiles, valueOf(PROFILE));
        getSharedPreferences("at.jku.ce.bp_v1", MODE_PRIVATE).edit().putBoolean("FIRSTSTART", false).apply();
    }

    private void setFragement(Fragment fragment) {
        clearBackStack();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }

    private void clearBackStack() {
            for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {
                getSupportFragmentManager().popBackStackImmediate();
            }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profiling_action_bar, menu);
        menu.findItem(R.id.help).setVisible(true);
        menu.findItem(R.id.next).setVisible(false);
        menu.findItem(R.id.share).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.help:
                Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
                intent.putExtra("help", true);
                startActivity(intent);
                finish();
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();

        if (fm.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry entry = fm.getBackStackEntryAt(fm.getBackStackEntryCount() - 1);
            fm.popBackStack();
            FragmentManager.BackStackEntry entry2 = fm.getBackStackEntryAt(fm.getBackStackEntryCount() - 1);
            // Doppelte Einträge überspringen
            if (entry.getName().equals(entry2.getName())) {
                fm.popBackStack();
            }

            if (fm.getBackStackEntryAt(fm.getBackStackEntryCount() - 1).getName().equals("ZKToEDK")) {
                // Wird vom ZK Fragment zum QKQR Fragment gewechselt müssen die Cards auf grau geändert werden
                ProfilingFragment.setQuellkontextView(this, this, "", true, false);
                ProfilingFragment.setQuellrolleView(this, this, "", true, false);
                ProfilingZKFragment.setZielkontextView(this, this, "", true, false);
            } else if (fm.getBackStackEntryAt(fm.getBackStackEntryCount() - 1).getName().equals("QKundQRToZK")) {
                // Wird vom EDK Fragment zum ZK Fragment gewechselt muss die Card auf grau geändert werden
                ProfilingZKFragment.setZielkontextView(this, this, getString(R.string.zielKontext), false, false);
            }

            // Wurde vom Home Fragment zum EDK Fragment gesprungen soll bei Zurück vom EDK zum Home Fragment gesprungen werden
            if (fm.getBackStackEntryAt(fm.getBackStackEntryCount() - 1).getName().equals("HomeToEDK")) {
                ((BottomNavigationView) findViewById(R.id.bottom_navigation)).setSelectedItemId(R.id.nav_home);
            }
        } else {
            super.onBackPressed();
        }
    }

}
