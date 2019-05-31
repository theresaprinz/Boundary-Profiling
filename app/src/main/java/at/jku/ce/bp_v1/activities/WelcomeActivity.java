package at.jku.ce.bp_v1.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import at.jku.ce.bp_v1.R;
import at.jku.ce.bp_v1.fragments.Welcome1Fragment;
import at.jku.ce.bp_v1.fragments.Welcome2Fragment;
import at.jku.ce.bp_v1.fragments.Welcome3Fragment;
import at.jku.ce.bp_v1.fragments.Welcome4Fragment;
import at.jku.ce.bp_v1.fragments.Welcome5Fragment;

public class WelcomeActivity extends AppCompatActivity {
    private static final boolean DEVELOPER_MODE = false;
    private int position;
    private Button btn_skip, btn_weiter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (DEVELOPER_MODE) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectCustomSlowCalls()
                    .detectAll()   //  for all detectable problems
                    .penaltyLog()
                    .penaltyFlashScreen()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        }
        super.onCreate(savedInstanceState);

        if (getIntent().getExtras() == null && !getSharedPreferences("at.jku.ce.bp_v1", MODE_PRIVATE).getBoolean("FIRSTSTART", true)) {
            launchHomeScreen();
        } else {
            setContentView(R.layout.activity_welcome);

            btn_skip = findViewById(R.id.welcome_skip);
            btn_weiter = findViewById(R.id.welcome_weiter);

            position = 0;

            setListeners();
        }


    }

    private void setListeners() {
        btn_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchHomeScreen();
            }
        });
        btn_weiter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                position++;
                switch (position) {
                    case 1:
                        fragmentTransaction.replace(R.id.welcome_fragment, new Welcome1Fragment());
                        btn_skip.setTextColor(getResources().getColor(R.color.black));
                        btn_weiter.setTextColor(getResources().getColor(R.color.black));
                        break;
                    case 2:
                        fragmentTransaction.replace(R.id.welcome_fragment, new Welcome2Fragment());
                        break;
                    case 3:
                        fragmentTransaction.replace(R.id.welcome_fragment, new Welcome3Fragment());
                        break;
                    case 4:
                        fragmentTransaction.replace(R.id.welcome_fragment, new Welcome4Fragment());
                        break;
                    case 5:
                        fragmentTransaction.replace(R.id.welcome_fragment, new Welcome5Fragment());
                        break;
                    default:
                        launchHomeScreen();
                        break;
                }
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }

    private void launchHomeScreen() {
        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();

        if (fm.getBackStackEntryCount() > 0) {
            position--;
            fm.popBackStack();
            if (position == 0) {
                btn_skip.setTextColor(getResources().getColor(R.color.white));
                btn_weiter.setTextColor(getResources().getColor(R.color.white));
            }
        } else {
            position = 1;
            super.onBackPressed();
        }
    }
}
