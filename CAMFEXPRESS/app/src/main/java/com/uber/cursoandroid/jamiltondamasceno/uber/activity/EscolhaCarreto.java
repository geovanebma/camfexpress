package com.uber.cursoandroid.jamiltondamasceno.uber.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.uber.cursoandroid.jamiltondamasceno.uber.R;

public class EscolhaCarreto extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escolha_carreto);
    }

    public void porteGrande(View view){
        Intent intent = new Intent(EscolhaCarreto.this, PassageiroActivity.class);
        startActivity(intent);
    }

    public void porteMedio(View view){
        Intent intent = new Intent(EscolhaCarreto.this, PassageiroActivity.class);
        startActivity(intent);
    }

    public void porteSimples(View view){
        Intent intent = new Intent(EscolhaCarreto.this, PassageiroActivity.class);
        startActivity(intent);
    }

    public void veiculoIdeal(View view){
        Intent intent = new Intent(EscolhaCarreto.this, PassageiroActivity.class);
        startActivity(intent);
    }
}
