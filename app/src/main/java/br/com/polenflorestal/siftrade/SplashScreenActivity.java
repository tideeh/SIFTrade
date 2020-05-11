package br.com.polenflorestal.siftrade;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import static br.com.polenflorestal.siftrade.Constants.DEFAULT_INT_VALUE;
import static br.com.polenflorestal.siftrade.Constants.SP_KEY_VERSION_CODE;
import static br.com.polenflorestal.siftrade.Constants.SP_NOME;

public class SplashScreenActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private String versionName;
    private int versionCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        sharedPreferences = getSharedPreferences(SP_NOME, Context.MODE_PRIVATE);

        versionName = BuildConfig.VERSION_NAME;
        versionCode = BuildConfig.VERSION_CODE;

        Log.i("SPLASH_SCREEN", "CurrentVersionName: "+versionName);
        Log.i("SPLASH_SCREEN", "CurrentVersionCode: "+versionCode);

        //((TextView) findViewById(R.id.splash_version_name)).setText(versionName);

        checkFirstRun();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fechaSplash();
            }
        }, 1000 );
    }

    private void fechaSplash(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void checkFirstRun(){
        int savedVersionCode = sharedPreferences.getInt(SP_KEY_VERSION_CODE, DEFAULT_INT_VALUE);

        if( savedVersionCode == versionCode ){
            // this is just a normal run
            Log.i("SPLASH_SCREEN", "NORMAL RUN");
        }
        else if( savedVersionCode == DEFAULT_INT_VALUE ){
            // this is a new install (or the user cleared the preferences)
            Log.i("SPLASH_SCREEN", "NEW INSTALL");

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(SP_KEY_VERSION_CODE, versionCode);
            editor.apply();
        }
        else if ( savedVersionCode < versionCode ){
            // this is an upgrade
            Log.i("SPLASH_SCREEN", "UPDATE RUN");

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(SP_KEY_VERSION_CODE, versionCode);
            editor.apply();
        }
    }
}
