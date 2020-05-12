package br.com.polenflorestal.siftrade;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static br.com.polenflorestal.siftrade.Constants.LOGOS_ROTATE_TIME;
import static br.com.polenflorestal.siftrade.Constants.empresasLogos;

public class TabelasSIFActivity extends AppCompatActivity {
    private static final int RC_FAZER_LOGIN = 9003;
    Map<String, String[]> uf_regions;
    private int empresaIndex;
    private boolean activeLogos;

    private Spinner spinnerUF;
    private Spinner spinnerRegiao;
    private Spinner spinnerProduto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabelas_s_i_f);

        uf_regions = new HashMap<>();
        uf_regions.put("MG", new String[] {getString(R.string.regiao_prompt), "Belo Horizonte", "Divinópolis", "Norte de Minas", "Sete Lagoas", "Vale do Aço", "Vale do Jequitinhonha e Mucuri", "Zona da Mata Mineira"});
        uf_regions.put("BA", new String[] {getString(R.string.regiao_prompt), "Estado da Bahia"});
        uf_regions.put("ES", new String[] {getString(R.string.regiao_prompt), "Estado do Espírito Santo"});

        spinnerUF = findViewById(R.id.spnUF);
        spinnerRegiao = findViewById(R.id.spnRegiao);
        spinnerProduto = findViewById(R.id.spnProduto);

        spinnerUF.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                //ToastUtil.show(TabelasSIFActivity.this, pos+" "+adapterView.getItemAtPosition(pos).toString(), Toast.LENGTH_LONG);

                ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(TabelasSIFActivity.this, android.R.layout.simple_spinner_item, new String[]{"Selecione uma Região"});

                if( pos > 0 ){
                    String uf_selected = adapterView.getItemAtPosition(pos).toString();

                    if( uf_regions.containsKey(uf_selected) ){
                        dataAdapter = new ArrayAdapter<>(TabelasSIFActivity.this, android.R.layout.simple_spinner_item, uf_regions.get(uf_selected));
                    }
                }

                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerRegiao.setAdapter(dataAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // primeira logo
        empresaIndex = new Random().nextInt(empresasLogos.length);
        if (empresaIndex >= empresasLogos.length)
            empresaIndex = 0;
        if( empresasLogos.length > 0 )
            ((ImageView) findViewById(R.id.empresas_logos)).setImageResource(empresasLogos[empresaIndex]);
        empresaIndex += 1;
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!UserUtil.isLogged()) {
            startActivityForResult(new Intent(this, LoginActivity.class), RC_FAZER_LOGIN);
            //finish();
        }

        // ativa a rotacao dos logos
        activeLogos = true;
        logosRotativas();
    }

    @Override
    protected void onStop() {
        super.onStop();

        activeLogos = false;
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
        if (activeLogos) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (empresaIndex >= empresasLogos.length)
                        empresaIndex = 0;

                    if (empresasLogos.length > 0)
                        ((ImageView) findViewById(R.id.empresas_logos)).setImageResource(empresasLogos[empresaIndex]);

                    empresaIndex += 1;

                    logosRotativas();
                }
            }, LOGOS_ROTATE_TIME);
        }
    }

    public void consultarPreco(View view) {
        if (!validaDados()) {
            return;
        }

        Intent intent = new Intent(this, ExibeTabelaSIF.class);
        intent.putExtra("UF", spinnerUF.getSelectedItem().toString());
        intent.putExtra("Região", spinnerRegiao.getSelectedItem().toString());
        intent.putExtra("Produto", spinnerProduto.getSelectedItem().toString());

        startActivity(intent);
    }

    private boolean validaDados() {
        boolean valido = true;

        if ( spinnerUF.getSelectedItemPosition() == 0 ) {
            ((TextView)spinnerUF.getSelectedView()).setError("Selecione um Estado");
            valido = false;
        }
        else {
            ((TextView)spinnerUF.getSelectedView()).setError(null);
        }

        if ( spinnerRegiao.getSelectedItemPosition() == 0 ) {
            ((TextView)spinnerRegiao.getSelectedView()).setError("Selecione uma Região");
            valido = false;
        }
        else {
            ((TextView)spinnerRegiao.getSelectedView()).setError(null);
        }

        if ( spinnerProduto.getSelectedItemPosition() == 0 ) {
            ((TextView)spinnerProduto.getSelectedView()).setError("Selecione um Produto");
            valido = false;
        }
        else {
            ((TextView)spinnerProduto.getSelectedView()).setError(null);
        }

        return valido;
    }
}
