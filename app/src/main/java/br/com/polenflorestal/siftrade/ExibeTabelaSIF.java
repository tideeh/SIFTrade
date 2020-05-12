package br.com.polenflorestal.siftrade;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Random;

import static br.com.polenflorestal.siftrade.Constants.LOGOS_ROTATE_TIME;
import static br.com.polenflorestal.siftrade.Constants.empresasLogos;
import static br.com.polenflorestal.siftrade.Constants.prodUnidades;

public class ExibeTabelaSIF extends AppCompatActivity {
    private static final int RC_FAZER_LOGIN = 9003;
    private int empresaIndex;
    private boolean activeLogos;
    private TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exibe_tabela_s_i_f);

        TextView txtUF = findViewById(R.id.exibe_tabela_uf);
        TextView txtRegiao = findViewById(R.id.exibe_tabela_regiao);
        TextView txtUnidade = findViewById(R.id.exibe_tabela_unidade);
        tableLayout = findViewById(R.id.exibe_tabela_tableLayout);

        Intent intent = getIntent();
        String uf = intent.getStringExtra("UF");
        String regiao = intent.getStringExtra("Região");
        String produto = intent.getStringExtra("Produto");

        String sUF = getString(R.string.UF_dois_pontos) + uf;
        String sRegiao = getString(R.string.Regiao_dois_pontos) + regiao;
        txtUF.setText(sUF);
        txtRegiao.setText(sRegiao);
        String sUnidade = getString(R.string.Unidade_dois_pontos);
        if( prodUnidades.containsKey(produto) )
            sUnidade += prodUnidades.get(produto);
        txtUnidade.setText(sUnidade);

        DataBaseUtil.getInstance().getCollection("/UF/" + uf + "/Regioes/" + regiao + "/Produtos/" + produto + "/Preços", "data", Query.Direction.DESCENDING)
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot documents = task.getResult();
                            if (documents != null) {
                                int i = 0;
                                for (QueryDocumentSnapshot document : documents) {
                                    //Log.d("MY_DATABASE", document.getId() + " => " + document.getData());
                                    Timestamp data = (Timestamp) document.get("data");
                                    double preco = (double) document.get("preço");

                                    TableLayout.LayoutParams tableParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);

                                    TableRow tableRow = new TableRow(ExibeTabelaSIF.this);
                                    tableRow.setLayoutParams(tableParams);// TableLayout is the parent view
                                    if (i % 2 == 0)
                                        tableRow.setBackgroundColor(Color.parseColor("#cfcfcf"));
                                    else
                                        tableRow.setBackgroundColor(Color.parseColor("#ededed"));

                                    TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1);

                                    TextView txtData = new TextView(ExibeTabelaSIF.this);
                                    txtData.setLayoutParams(rowParams);// TableRow is the parent view
                                    String sData = new SimpleDateFormat("MM/yyyy").format(data != null ? data.toDate() : "-");
                                    txtData.setText(sData);
                                    txtData.setGravity(Gravity.CENTER_HORIZONTAL);

                                    TextView txtPreco = new TextView(ExibeTabelaSIF.this);
                                    txtPreco.setLayoutParams(rowParams);// TableRow is the parent view
                                    txtPreco.setText(preco + "");
                                    txtPreco.setGravity(Gravity.CENTER_HORIZONTAL);

                                    tableLayout.addView(tableRow);
                                    tableRow.addView(txtData);
                                    tableRow.addView(txtPreco);

                                    i += 1;
                                }
                            }
                        } else {
                            Log.w("MY_DATABASE", "Error getting documents.", task.getException());
                        }
                    }
                });

        // primeira logo
        empresaIndex = new Random().nextInt(empresasLogos.length);
        if (empresaIndex >= empresasLogos.length)
            empresaIndex = 0;
        if (empresasLogos.length > 0)
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

        if (requestCode == RC_FAZER_LOGIN) {
            if (resultCode != RESULT_OK) {
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
}
