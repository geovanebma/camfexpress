package com.projeto.camfexpress.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.projeto.camfexpress.R;

public class TermosCondicoes extends AppCompatActivity {

    private TextView termos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_termos_condicoes);

        termos = findViewById(R.id.termos);
    }

    //Classe para ver os termos e condições
}