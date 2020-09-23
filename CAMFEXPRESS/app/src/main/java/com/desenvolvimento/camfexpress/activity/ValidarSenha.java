package com.desenvolvimento.camfexpress.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.desenvolvimento.camfexpress.R;
import com.google.android.material.textfield.TextInputEditText;

public class ValidarSenha extends AppCompatActivity {

    private TextInputEditText validaSMS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validar_senha);

        validaSMS = findViewById(R.id.validaSMS);
    }

    public void validarSMS(View view){
        String valida_sms = validaSMS.getText().toString();
        Intent receber_informacoes = getIntent();
        String tipo_usuario = receber_informacoes.getStringExtra("modalidade");
        String numero = receber_informacoes.getStringExtra("numero");
        String numero_sorteio = receber_informacoes.getStringExtra("numero_sorteio");

        if(numero_sorteio.equals(valida_sms)){
            if(tipo_usuario.equals("Carreto")){
                Intent cadastroCarreto = new Intent(this, TelaCadastroCarretoDados.class);
                cadastroCarreto.putExtra("numero", numero);
                cadastroCarreto.putExtra("modalidade", numero_sorteio);
                cadastroCarreto.putExtra("modalidade", "Carreto");
                startActivity(cadastroCarreto);
            }else{
                Intent cadastroCliente = new Intent(this, TelaCadastroCliente.class);
                cadastroCliente.putExtra("numero", numero);
                cadastroCliente.putExtra("codigo_sms", numero_sorteio);
                cadastroCliente.putExtra("modalidade", "Cliente");
                startActivity(cadastroCliente);
            }
        }else{
            Toast.makeText(ValidarSenha.this, "Código SMS inválido.", Toast.LENGTH_SHORT).show();
        }
    }
}
