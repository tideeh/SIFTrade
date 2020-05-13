package br.com.polenflorestal.siftrade.activitys;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import br.com.polenflorestal.siftrade.R;

import static br.com.polenflorestal.siftrade.utils.Constants.LOGOS_ROTATE_TIME;
import static br.com.polenflorestal.siftrade.utils.Constants.empresasLogos;

public class FaleConosco extends AppCompatActivity {
    private int empresaIndex;
    private boolean activeLogos;
    private RadioGroup radioGroupAssociado;
    private Spinner spnAssunto;
    private Spinner spnSetor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fale_conosco);

        radioGroupAssociado = findViewById(R.id.fale_conosco_radioGroup_associado);
        spnAssunto = findViewById(R.id.fale_conosco_spn_assunto);
        spnSetor = findViewById(R.id.fale_conosco_spn_setor);

        // exibe o Primeiro logo
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

        // Seta a variavel de controle, para ativar o slide dos logos
        activeLogos = true;
        logosRotativas();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // desativa o slide dos logos
        activeLogos = false;
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

    public void btnViaChat(View view) {
        if (!validaDados()) {
            return;
        }

        String phoneNumberWilton = "03182692655"; // colocar esse numero e outros no xml values ou algo melhor
        String phoneNumberGustavo = "03188108460"; // colocar esse numero e outros no xml values ou algo melhor

        String message = "Olá SIF Trade! Preciso de ajuda no setor de "+spnSetor.getSelectedItem().toString();

        String messageEncode = "-";
        try {
            messageEncode = URLEncoder.encode(message, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String phoneNumber = phoneNumberGustavo;
        if( spnSetor.getSelectedItem().toString().equals("Comunicação") )
            phoneNumber = phoneNumberWilton;

        String url = "https://api.whatsapp.com/send?phone=55"+phoneNumber+"&text="+messageEncode;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    private boolean validaDados() {
        boolean valido = true;

        if ( spnAssunto.getSelectedItemPosition() == 0 ) {
            ((TextView)spnAssunto.getSelectedView()).setError("Selecione um Assunto");
            valido = false;
        }
        else {
            ((TextView)spnAssunto.getSelectedView()).setError(null);
        }

        if ( spnSetor.getSelectedItemPosition() == 0 ) {
            ((TextView)spnSetor.getSelectedView()).setError("Selecione um Setor");
            valido = false;
        }
        else {
            ((TextView)spnSetor.getSelectedView()).setError(null);
        }

        if( radioGroupAssociado.getCheckedRadioButtonId() != R.id.fale_conosco_radio_associado_sim
                && radioGroupAssociado.getCheckedRadioButtonId() != R.id.fale_conosco_radio_associado_nao ){
            ((RadioButton) findViewById(R.id.fale_conosco_radio_associado_nao)).setError("Selecione uma opção");
            valido = false;
        }
        else {
            ((RadioButton) findViewById(R.id.fale_conosco_radio_associado_nao)).setError(null);
        }

        return valido;
    }
}
