package br.com.polenflorestal.siftrade.activitys;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import br.com.polenflorestal.siftrade.R;
import br.com.polenflorestal.siftrade.utils.DataBaseUtil;
import br.com.polenflorestal.siftrade.utils.ToastUtil;
import br.com.polenflorestal.siftrade.utils.UserUtil;

import static br.com.polenflorestal.siftrade.utils.Constants.DB_COLLECTION_PRECOS;
import static br.com.polenflorestal.siftrade.utils.Constants.DB_COLLECTION_PRODUTOS;
import static br.com.polenflorestal.siftrade.utils.Constants.DB_COLLECTION_REGIOES;
import static br.com.polenflorestal.siftrade.utils.Constants.DB_COLLECTION_UF;
import static br.com.polenflorestal.siftrade.utils.Constants.DB_COLLECTION_USUARIOS;
import static br.com.polenflorestal.siftrade.utils.Constants.DB_DOCUMENT_PRECO_FIELD_DATA;
import static br.com.polenflorestal.siftrade.utils.Constants.DB_DOCUMENT_PRECO_FIELD_PRECO;
import static br.com.polenflorestal.siftrade.utils.Constants.DB_DOCUMENT_USUARIO_FIELD_ADMIN;
import static br.com.polenflorestal.siftrade.utils.Constants.LOGOS_ROTATE_TIME;
import static br.com.polenflorestal.siftrade.utils.Constants.empresasLogos;
import static br.com.polenflorestal.siftrade.utils.Constants.prodUnidades;

public class ExibeTabelaSIF extends AppCompatActivity {
    private static final int RC_FAZER_LOGIN = 9003;
    ListenerRegistration precosListener;
    private int empresaIndex;
    private boolean activeLogos;
    private TableLayout tableLayout;
    private LinearLayout adminView;
    private boolean isAdmin = false;
    private EditText inputData;
    private EditText inputPreco;
    private String uf;
    private String regiao;
    private String produto;
    private int rowIndex;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exibe_tabela_s_i_f);

        progressBar = findViewById(R.id.progress_bar);

        TextView txtUF = findViewById(R.id.exibe_tabela_uf);
        TextView txtRegiao = findViewById(R.id.exibe_tabela_regiao);
        TextView txtUnidade = findViewById(R.id.exibe_tabela_unidade);
        tableLayout = findViewById(R.id.exibe_tabela_tableLayout);
        adminView = findViewById(R.id.admin_add_preco);
        inputData = findViewById(R.id.exibe_tabela_input_data);
        inputPreco = findViewById(R.id.exibe_tabela_input_preco);

        Intent intent = getIntent();
        uf = intent.getStringExtra("UF");
        regiao = intent.getStringExtra("Região");
        produto = intent.getStringExtra("Produto");

        String sUF = getString(R.string.UF_dois_pontos) + uf;
        String sRegiao = getString(R.string.Regiao_dois_pontos) + regiao;
        txtUF.setText(sUF);
        txtRegiao.setText(sRegiao);
        String sUnidade = getString(R.string.Unidade_dois_pontos);
        if( prodUnidades.containsKey(produto) )
            sUnidade += prodUnidades.get(produto);
        txtUnidade.setText(sUnidade);

        progressBar.setVisibility(View.VISIBLE);
        precosListener = DataBaseUtil.getInstance().getCollectionReference("/" + DB_COLLECTION_UF + "/" + uf + "/" + DB_COLLECTION_REGIOES + "/" + regiao + "/" + DB_COLLECTION_PRODUTOS + "/" + produto + "/" + DB_COLLECTION_PRECOS, DB_DOCUMENT_PRECO_FIELD_DATA, Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        progressBar.setVisibility(View.VISIBLE);
                        if (e != null) {
                            Log.w("MY_DATABASE", "Listen failed.", e);
                            progressBar.setVisibility(View.GONE);
                            return;
                        }
                        if( queryDocumentSnapshots != null ){
                            limpaTabelaPrecos();
                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                if (doc.get(DB_DOCUMENT_PRECO_FIELD_DATA) != null && doc.get(DB_DOCUMENT_PRECO_FIELD_PRECO) != null) {
                                    addTableRow((Timestamp) doc.get(DB_DOCUMENT_PRECO_FIELD_DATA), (double) doc.get(DB_DOCUMENT_PRECO_FIELD_PRECO));
                                }
                            }
                        }
                        progressBar.setVisibility(View.GONE);
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
            startActivityForResult(new Intent(this, Login.class), RC_FAZER_LOGIN);
            //finish();
        }

        // exibe ou esconde a caixa de cadastrar preco
        adminView.setVisibility(View.GONE);
        DataBaseUtil.getInstance().getDocument(DB_COLLECTION_USUARIOS, UserUtil.getCurrentUser().getEmail())
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null) {
                                if (document.exists()) {
                                    //Log.d("MY_DATABASE", "DocumentSnapshot data: " + document.getData());
                                    if (document.contains(DB_DOCUMENT_USUARIO_FIELD_ADMIN)) {
                                        isAdmin = (boolean) document.get(DB_DOCUMENT_USUARIO_FIELD_ADMIN);
                                        if (isAdmin) {
                                            adminView.setVisibility(View.VISIBLE);
                                        }
                                    }
                                } else {
                                    Log.d("MY_DATABASE", "No such document");
                                }
                            }
                        }
                    }
                });

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

    public void btnCadastrarPreco(View view) {
        if (!validaDados()) {
            return;
        }

        String sData = inputData.getText().toString();
        double preco = Double.parseDouble(inputPreco.getText().toString());

        Map<String, Object> precoData = new HashMap<>();
        precoData.put(DB_DOCUMENT_PRECO_FIELD_PRECO, preco);

        Date date = null;
        try {
            date = new SimpleDateFormat("MMyyyy").parse(sData);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if( date != null )
            precoData.put(DB_DOCUMENT_PRECO_FIELD_DATA, new Timestamp(date));

        DataBaseUtil.getInstance().insertDocument("/" + DB_COLLECTION_UF + "/" + uf + "/" + DB_COLLECTION_REGIOES + "/" + regiao + "/" + DB_COLLECTION_PRODUTOS + "/" + produto + "/" + DB_COLLECTION_PRECOS, precoData);

        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            inputManager.hideSoftInputFromWindow((null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }

        inputData.setText("");
        inputPreco.setText("");
    }

    private boolean validaDados() {
        boolean valido = true;

        String sData = inputData.getText().toString();
        if (TextUtils.isEmpty(sData)) {
            inputData.setError("Campo necessário.");
            valido = false;
        } else if (sData.length() != 6) {
            inputData.setError("Utilize o formato: MMAAAA (ex: 012009)");
            valido = false;
        } else {
            inputData.setError(null);
        }

        String sPreco = inputPreco.getText().toString();
        if (TextUtils.isEmpty(sPreco) || sPreco.equals(".") || sPreco.equals(",") || sPreco.equals("-")) {
            inputPreco.setError("Campo necessário.");
            valido = false;
        }
        else {
            double preco = Double.parseDouble(sPreco);
            if (preco < 0) {
                inputPreco.setError("Digite um valor positivo.");
                valido = false;
            }
            else {
                inputPreco.setError(null);
            }
        }

        return valido;
    }

    private void limpaTabelaPrecos(){
        ((TableLayout)findViewById(R.id.exibe_tabela_tableLayout)).removeAllViews();
        rowIndex = 0;
    }

    private void addTableRow(Timestamp data, double preco){
        TableLayout.LayoutParams tableParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);

        TableRow tableRow = new TableRow(ExibeTabelaSIF.this);
        tableRow.setLayoutParams(tableParams);// TableLayout is the parent view
        if (rowIndex % 2 == 0)
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

        rowIndex += 1;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        precosListener.remove();
    }
}
