package br.com.polenflorestal.siftrade;

import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    int[] empresas_logos = {R.drawable.aperam, R.drawable.arcelor, R.drawable.bracell, R.drawable.def, R.drawable.duratex, R.drawable.gerdau, R.drawable.suzano, R.drawable.ufv, R.drawable.veracel, R.drawable.westrock};
    int empresas_size = empresas_logos.length;
    int empresa_index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logosRotativas();
    }

    private void logosRotativas() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (empresa_index >= empresas_size)
                    empresa_index = 0;

                ((ImageView) findViewById(R.id.empresas_logos)).setImageResource(empresas_logos[empresa_index]);

                empresa_index += 1;

                logosRotativas();
            }
        }, 3000);
    }
}
