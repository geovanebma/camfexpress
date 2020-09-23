package com.desenvolvimento.camfexpress.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.desenvolvimento.camfexpress.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();
    }

    public void buttonCarreto(View view){
        Intent verificarNumero = new Intent(this, VerificarNumero.class);
        verificarNumero.putExtra("tipo_usuario", "Carreto");
        startActivity(verificarNumero);
    }

    public void buttonCliente(View view){
        Intent verificarNumero = new Intent(this, VerificarNumero.class);
        verificarNumero.putExtra("tipo_usuario", "Cliente");
        startActivity(verificarNumero);
    }
}