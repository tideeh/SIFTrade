package br.com.polenflorestal.siftrade;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static br.com.polenflorestal.siftrade.Constants.LOGOS_ROTATE_TIME;
import static br.com.polenflorestal.siftrade.Constants.empresas_logos;

public class TabelasSIFActivity extends AppCompatActivity {
    private static final int RC_FAZER_LOGIN = 9003;
    private int empresa_index;
    Map<String, String[]> uf_regions;
    private Spinner spinnerUF;
    private Spinner spinnerRegiao;
    private Spinner spinnerProduto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabelas_s_i_f);

        uf_regions = new HashMap<String, String[]>();
        uf_regions.put("MG", new String[] {"Belo Horizonte", "Divinópolis", "Norte de Minas", "Sete Lagoas", "Vale do Aço", "Vale do Jequitinhonha e Mucuri", "Zona da Mata Mineira"});
        uf_regions.put("BA", new String[] {"Estado da Bahia"});
        uf_regions.put("ES", new String[] {"Estado do Espírito Santo"});

        spinnerUF = findViewById(R.id.spnUF);
        spinnerRegiao = findViewById(R.id.spnRegiao);
        spinnerProduto = findViewById(R.id.spnProduto);

        spinnerUF.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                //ToastUtil.show(TabelasSIFActivity.this, pos+" "+adapterView.getItemAtPosition(pos).toString(), Toast.LENGTH_LONG);

                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(TabelasSIFActivity.this, android.R.layout.simple_spinner_item, new String[] {"Selecione uma Região"});

                if( pos > 0 ){
                    String uf_selected = adapterView.getItemAtPosition(pos).toString();

                    if( uf_regions.containsKey(uf_selected) ){
                        dataAdapter = new ArrayAdapter<String>(TabelasSIFActivity.this, android.R.layout.simple_spinner_item, uf_regions.get(uf_selected));
                    }
                }

                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerRegiao.setAdapter(dataAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

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
