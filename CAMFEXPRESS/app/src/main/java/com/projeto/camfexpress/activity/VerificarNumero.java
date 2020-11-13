package com.projeto.camfexpress.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.android.material.textfield.TextInputEditText;
import com.projeto.camfexpress.R;

public class VerificarNumero extends AppCompatActivity {

    private TextInputEditText numeroCelular;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verificar_numero);

        //Máscara celular
        numeroCelular = findViewById(R.id.numeroCelular);
        SimpleMaskFormatter formatarNumero = new SimpleMaskFormatter("+NN(NN)NNNNN-NNNN");
        MaskTextWatcher transformarNumero = new MaskTextWatcher(numeroCelular, formatarNumero);
        numeroCelular.addTextChangedListener(transformarNumero);
    }

    //Recebe o número e encaminha para a tela de validar o sms
    public void verificarNumero(View view){
        String numero = numeroCelular.getText().toString().replace("-", "").replace("(", "").replace(")", "").replace(" ", "");

        if(numero.isEmpty()){
            numeroCelular.setError("Informe o número.");
            numeroCelular.requestFocus();
            return;
        }

        Intent intent = new Intent(VerificarNumero.this, ValidarSMS.class);//ValidarSMS.class
        intent.putExtra("numero", numero);
        String modalidade = getIntent().getStringExtra("modalidade");
        intent.putExtra("modalidade", modalidade);
        startActivity(intent);
    }
}