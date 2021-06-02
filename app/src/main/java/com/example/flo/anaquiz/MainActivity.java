package com.example.flo.anaquiz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.preference.PreferenceManager;

import com.example.flo.anaquiz.R;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton startButton;
    BottomAppBar bottomAppBar;

    private long millis;
    private boolean timer_running = false;
    private long startTime = 0;

    //Handler und Runnable für Timer
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {

            //Timer
            millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;

            //Ausgabe der aktuellen Zeit in der BottomAppBar
            if (bottomAppBar.getMenu().size() > 0) {
                bottomAppBar.getMenu().getItem(1).setTitle(String.format("%d:%02d", minutes, seconds));
            }
            timerHandler.postDelayed(this, 500);
        }
    };

    //Update FloatingAppButton Icon
    private void setPlay() {startButton.setImageResource(R.drawable.ic_start);};

    //Update FloatingAppButton Icon
    private void setBack() {startButton.setImageResource(R.drawable.ic_back);};

    //Update FloatingAppButton Icon
    private void setPause() {startButton.setImageResource(R.drawable.ic_pause);}

    //Update FloatingAppButton Icon
    private void setReplay() {startButton.setImageResource(R.drawable.ic_replay);};

    //Entfernen der Zeitanzeige in der BottomAppBar
    private void clearBottomAppBar() {bottomAppBar.replaceMenu(R.menu.bottom_app_bar_clear);}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomAppBar = findViewById(R.id.bottom_app_bar);
        NavigationView navigationView = findViewById(R.id.nav_view);
        final DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        startButton = findViewById(R.id.startButton);

        //Listener für Ergebnispaket von QuestionFragment
        //Weitergabe des Ergebnispaketes mit Zeit an ResultFragment
        getSupportFragmentManager().setFragmentResultListener("resultBundle1", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {

                //Timer anhalten
                timerHandler.removeCallbacks(timerRunnable);
                timer_running = false;
                setReplay();
                result.putLong("time", millis);
                getSupportFragmentManager().setFragmentResult("resultBundle2", result);
            }
        });

        //Beim erzeugen MainFragment anzeigen
        getSupportFragmentManager().beginTransaction()
            .setCustomAnimations(R.anim.fade_in_left_to_right, R.anim.slide_out_left_to_right)
            .replace(R.id.frag_view, new MainFragment(), "MAIN_FRAGMENT")
            .setReorderingAllowed(true)
            .commit();

        //Clicks in der BottomAppBar
        bottomAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        //Clicks in der NavigationBar
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    //MainFragment ausgewählt
                    case R.id.mainMenu:
                        getSupportFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.fade_in_left_to_right, R.anim.slide_out_left_to_right)
                                .replace(R.id.frag_view, new MainFragment(), "MAIN_FRAGMENT")
                                .setReorderingAllowed(true)
                                .commit();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        setPlay();
                        clearBottomAppBar();
                        return true;

                    //AboutFragment ausgewählt
                    case R.id.aboutMenu:
                        getSupportFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(R.anim.slide_in_right_to_left, R.anim.fade_out_right_to_left)
                                .replace(R.id.frag_view, new AboutFragment(), "OTHER_FRAGMENT")
                                .setReorderingAllowed(true)
                                .commit();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        setBack();
                        clearBottomAppBar();
                        return true;

                    //RulesFragment ausgewählt
                    case R.id.rulesMenu:
                        getSupportFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.slide_in_right_to_left, R.anim.fade_out_right_to_left)
                                .replace(R.id.frag_view, new RulesFragment(), "OTHER_FRAGMENT")
                                .setReorderingAllowed(true)
                                .commit();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        setBack();
                        clearBottomAppBar();
                        return true;

                    //SettingsFragment ausgewählt
                    case R.id.settingsMenu:
                        getSupportFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.slide_in_right_to_left, R.anim.fade_out_right_to_left)
                                .replace(R.id.frag_view, new SettingsFragment(), "OTHER_FRAGMENT")
                                .setReorderingAllowed(true)
                                .commit();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        setBack();
                        clearBottomAppBar();
                        return true;

                    case R.id.gitLink:
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(getString(R.string.gitURL)));
                        startActivity(intent);
                }
                return false;
            }
        });

        //Clicks auf FloatingActionButton
        assert startButton != null;
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Aktuell im MainFragment
                Fragment myFragment = getSupportFragmentManager().findFragmentByTag("MAIN_FRAGMENT");
                if (myFragment != null) {

                    //MainFragment ist sichtbar
                    if (myFragment.isVisible()) {

                        //Erzeugen eines Result und QuestionFragment
                        getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_right_to_left, R.anim.fade_out_right_to_left)
                            .replace(R.id.frag_view, new ResultFragment(), "RESULT_FRAGMENT")
                            .setReorderingAllowed(true)
                            .setCustomAnimations(R.anim.slide_in_right_to_left, R.anim.fade_out_right_to_left)
                            .add(R.id.frag_view, new QuestionFragment(), "QUESTION_FRAGMENT")
                            .setReorderingAllowed(true)
                            .commit();

                        //Update FloatingActionButton Icon
                        setPause();

                        //Update BottomAppBar Menü
                        bottomAppBar.replaceMenu(R.menu.bottom_app_bar_menu);

                        //Timer starten
                        startTime = System.currentTimeMillis();
                        timerHandler.postDelayed(timerRunnable, 0);
                        timer_running = true;
                    }
                }

                //Aktuell im About- oder RulesFragment
                myFragment = getSupportFragmentManager().findFragmentByTag("OTHER_FRAGMENT");
                if (myFragment != null) {

                    //Zurück zum MainFragment
                    getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.fade_in_left_to_right, R.anim.slide_out_left_to_right)
                        .replace(R.id.frag_view, new MainFragment(), "MAIN_FRAGMENT")
                        .setReorderingAllowed(true)
                        .commit();

                    //Update FloatingActionButton Icon
                    setPlay();
                }

                //Aktuell im QuestionFragment
                myFragment = getSupportFragmentManager().findFragmentByTag("QUESTION_FRAGMENT");
                if (myFragment != null) {

                    //Timer läuft
                    if(timer_running) {

                        //Timer stoppen
                        timer_running = false;
                        timerHandler.removeCallbacks(timerRunnable);

                        //PauseFragment anzeigen
                        getSupportFragmentManager().beginTransaction()
                            .add(R.id.frag_view, new PauseFragment(), "PAUSE_FRAGMENT")
                            .setReorderingAllowed(true)
                            .setCustomAnimations(R.anim.slide_in_right_to_left, R.anim.slide_out_left_to_right)
                            .hide(getSupportFragmentManager().findFragmentByTag("QUESTION_FRAGMENT"))
                            .commit();

                        //Update FloatingActionButton Icon
                        setPlay();
                    }

                    //Timer läuft nicht
                    else {

                        //Timer starten
                        timer_running = true;
                        startTime = System.currentTimeMillis()- millis;
                        timerHandler.postDelayed(timerRunnable, 0);

                        //PauseFragment entfernen
                        if (getSupportFragmentManager().findFragmentByTag("PAUSE_FRAGMENT") != null) {
                            getSupportFragmentManager().beginTransaction()
                                .remove(getSupportFragmentManager().findFragmentByTag("PAUSE_FRAGMENT"))
                                .setCustomAnimations(R.anim.slide_in_right_to_left, R.anim.slide_out_left_to_right)
                                .show(getSupportFragmentManager().findFragmentByTag("QUESTION_FRAGMENT"))
                                .commit();
                        }

                        //Update FloatingActionButton Icon
                        setPause();
                    }
                }

                //Aktuell im ResultFragment
                myFragment = getSupportFragmentManager().findFragmentByTag("RESULT_FRAGMENT");
                if (myFragment != null && myFragment.isVisible()) {

                    //Neustart
                    getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frag_view, new MainFragment(), "MAIN_FRAGMENT")
                        .setCustomAnimations(R.anim.fade_in_left_to_right, R.anim.slide_out_left_to_right)
                        .setReorderingAllowed(true)
                        .commit();

                    //Update FloatingActionButton Icon
                    setPlay();

                    //Update BottomAppBar Menü
                    clearBottomAppBar();
                }
             }
        });
    }

    @Override
    public void onPause() {

        //Aktuell im QuestioNFragment
        Fragment myFragment = getSupportFragmentManager().findFragmentByTag("QUESTION_FRAGMENT");
        if (myFragment != null) {
            myFragment = getSupportFragmentManager().findFragmentByTag("PAUSE_FRAGMENT");

            //Kein PauseFragemtn angezeigt
            if (myFragment == null) {

                //PauseFragment anzeigen
                getSupportFragmentManager().beginTransaction()
                    .add(R.id.frag_view, new PauseFragment(), "PAUSE_FRAGMENT")
                    .setCustomAnimations(R.anim.slide_in_right_to_left, R.anim.fade_out_right_to_left)
                    .setReorderingAllowed(true)
                    .hide(getSupportFragmentManager().findFragmentByTag("QUESTION_FRAGMENT"))
                    .commit();

                //Update FloatingActionButton Icon
                setPlay();
            }
        }

        //Timer stoppen
        timerHandler.removeCallbacks(timerRunnable);
        timer_running = false;
        super.onPause();
    }
}
