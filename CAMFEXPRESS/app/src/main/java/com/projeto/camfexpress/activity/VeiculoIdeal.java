package com.projeto.camfexpress.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.uber.cursoandroid.jamiltondamasceno.uber.R;


public class VeiculoIdeal extends AppCompatActivity {

    private TextInputEditText comprimento;
    private TextInputEditText largura;
    private TextInputEditText altura;
    private TextInputEditText peso;
    private CheckBox checkBoxConcordancia;
    private TextView descricaoDimensoes;
    private Button confirmarMedidas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_veiculo_ideal);

        comprimento = findViewById(R.id.comprimentoIdeal);
        largura = findViewById(R.id.larguraIdeal);
        altura = findViewById(R.id.alturaIdeal);
        peso = findViewById(R.id.pesoIdeal);
        checkBoxConcordancia = findViewById(R.id.checkBoxConcordancia);
        descricaoDimensoes = findViewById(R.id.descricaoDimensoes);
        confirmarMedidas = findViewById(R.id.confirmarMedidas);
    }

    public void concordarTexto(View view){
        if(((CheckBox) checkBoxConcordancia).isChecked()){
            comprimento.setEnabled(true);
            largura.setEnabled(true);
            altura.setEnabled(true);
            peso.setEnabled(true);
            descricaoDimensoes.setEnabled(true);
            confirmarMedidas.setEnabled(true);
        }else{
            comprimento.setEnabled(false);
            largura.setEnabled(false);
            altura.setEnabled(false);
            peso.setEnabled(false);
            descricaoDimensoes.setEnabled(false);
            confirmarMedidas.setEnabled(false);
        }
    }

    public void confirmarMedidas(View view){
        Intent passageiro = new Intent(VeiculoIdeal.this, PassageiroActivity.class);
        passageiro.putExtra("medidaComprimento", comprimento.getText().toString());
        passageiro.putExtra("medidaLargura", largura.getText().toString());
        passageiro.putExtra("medidaAltura", altura.getText().toString());
        passageiro.putExtra("medidaPeso", peso.getText().toString());
        passageiro.putExtra("porte", "veiculo ideal");
        startActivity(passageiro);
    }
}