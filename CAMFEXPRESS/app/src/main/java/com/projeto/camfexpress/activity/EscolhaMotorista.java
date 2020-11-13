package com.projeto.camfexpress.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.projeto.camfexpress.R;

public class EscolhaMotorista extends AppCompatActivity {

    private CheckBox ajudantes;
    private CheckBox seguro;
    private String a = "";
    private String s = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escolha_motorista);

        ajudantes = findViewById(R.id.necessarioAjudante);
        seguro = findViewById(R.id.Seguro);
    }

    public void onclickSeguro(View view){
        if(((CheckBox) seguro).isChecked()){
            s = "true";
        }else{
            s = "false";
        }
    }

    public void onclickAjudante(View view){
        if(((CheckBox) ajudantes).isChecked()){
            a = "true";
        }else{
            a = "false";
        }
    }

    //Botão para escolher veículos de porte de carga grande
    public void porteGrande(View view){
        Intent intent = new Intent(EscolhaMotorista.this, ClienteActivity.class);
        intent.putExtra("porte", "grande");
        intent.putExtra("ajudante", a);
        intent.putExtra("seguro", s);
        startActivity(intent);
    }

    //Botão para escolher veículos de porte de carga médio
    public void porteMedio(View view){
        Intent intent = new Intent(EscolhaMotorista.this, ClienteActivity.class);
        intent.putExtra("porte", "medio");
        intent.putExtra("ajudante", a);
        intent.putExtra("seguro", s);
        startActivity(intent);
    }

    //Botão para escolher veículos de porte de carga simples
    public void porteSimples(View view){
        Intent intent = new Intent(EscolhaMotorista.this, ClienteActivity.class);
        intent.putExtra("porte", "simples");
        intent.putExtra("ajudante", a);
        intent.putExtra("seguro", s);
        startActivity(intent);
    }

    //Botão para escolher veículos de porte ideal para a carga, ou simples, ou média ou grande
    public void veiculoIdeal(View view){
        Intent intent = new Intent(EscolhaMotorista.this, VeiculoIdeal.class);
        intent.putExtra("porte", "ideal");
        intent.putExtra("ajudante", a);
        intent.putExtra("seguro", s);
        startActivity(intent);
    }
}