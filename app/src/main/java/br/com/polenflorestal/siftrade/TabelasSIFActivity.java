package br.com.polenflorestal.siftrade;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Random;

import static br.com.polenflorestal.siftrade.Constants.LOGOS_ROTATE_TIME;
import static br.com.polenflorestal.siftrade.Constants.empresas_logos;

public class TabelasSIFActivity extends AppCompatActivity {
    private static final int RC_FAZER_LOGIN = 9003;
    private int empresa_index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabelas_s_i_f);

        empresa_index = new Random().nextInt(empresas_logos.length);
        if (empresa_index >= empresas_logos.length)
            empresa_index = 0;
        if( empresas_logos.length > 0 )
            ((ImageView) findViewById(R.id.empresas_logos)).setImageResource(empresas_logos[empresa_index]);
        empresa_index += 1;
        logosRotativas();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!UserUtil.isLogged()) {
            startActivityForResult(new Intent(this, LoginActivity.class), RC_FAZER_LOGIN);
            //finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if( requestCode == RC_FAZER_LOGIN ){
            if( resultCode != RESULT_OK ){
                ToastUtil.show(this, "Falha ao realizar login!", Toast.LENGTH_LONG);
                finish();
            }
        }

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
}
