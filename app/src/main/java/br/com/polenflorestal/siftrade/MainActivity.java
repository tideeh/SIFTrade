package br.com.polenflorestal.siftrade;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

import static br.com.polenflorestal.siftrade.Constants.LOGOS_ROTATE_TIME;
import static br.com.polenflorestal.siftrade.Constants.empresas_logos;

public class MainActivity extends AppCompatActivity {
    private int empresa_index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        empresa_index = new Random().nextInt(empresas_logos.length);
        if (empresa_index >= empresas_logos.length)
            empresa_index = 0;
        if( empresas_logos.length > 0 )
            ((ImageView) findViewById(R.id.empresas_logos)).setImageResource(empresas_logos[empresa_index]);
        empresa_index += 1;
        logosRotativas();
    }

    private void logosRotativas() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (empresa_index >= empresas_logos.length)
                    empresa_index = 0;

                if( empresas_logos.length > 0 )
                    ((ImageView) findViewById(R.id.empresas_logos)).setImageResource(empresas_logos[empresa_index]);

                empresa_index += 1;

                logosRotativas();
            }
        }, LOGOS_ROTATE_TIME);
    }

    public void btnTabelasSIF(View view) {
        Intent intent = new Intent(this, TabelasSIFActivity.class);
        startActivity(intent);
        //finish(); // nao precisa fechar essa activity. deixar voltar pra ela caso queiram
    }
}
