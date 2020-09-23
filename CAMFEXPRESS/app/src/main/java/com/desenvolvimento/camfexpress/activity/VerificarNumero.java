package com.desenvolvimento.camfexpress.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.desenvolvimento.camfexpress.R;
import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.android.material.textfield.TextInputEditText;
import com.desenvolvimento.camfexpress.modal.Validacoes;

import java.util.Random;

public class VerificarNumero extends AppCompatActivity {

    private TextInputEditText numeroCelular;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verificar_numero);

        //Máscara celular
        numeroCelular = findViewById(R.id.numeroCelular);
        SimpleMaskFormatter formatarNumero = new SimpleMaskFormatter("(NN)NNNNN-NNNN");
        MaskTextWatcher transformarNumero = new MaskTextWatcher(numeroCelular, formatarNumero);
        numeroCelular.addTextChangedListener(transformarNumero);
    }

    public void verificarNumero(View view){

        String numero = numeroCelular.getText().toString();
        Validacoes validar_numero = new Validacoes();
        boolean resposta_validacao =  validar_numero.numero(numero);

        Intent receber_tipo_usuario = getIntent();
        String tipo_usuario = receber_tipo_usuario.getStringExtra("tipo_usuario");


        if(resposta_validacao == false){
            Toast.makeText(VerificarNumero.this, "Preencha o campo Número corretamente.", Toast.LENGTH_SHORT).show();
        }else{
            Random random = new Random();
            int numero_sorteio = random.nextInt(9999);
            String numero_sorteio_string = Integer.toString(numero_sorteio);

            if(numero_sorteio < 1000 && numero_sorteio > 99){
                numero_sorteio_string = "0"+numero_sorteio_string;
            }else if(numero_sorteio < 100 && numero_sorteio > 9){
                numero_sorteio_string = "00"+numero_sorteio_string;
            }else if(numero_sorteio < 10 && numero_sorteio >= 0){
                numero_sorteio_string = "000"+numero_sorteio_string;
            }
            System.out.println(numero_sorteio_string+" numero_aqui");

            Intent validarSenha = new Intent(this, ValidarSenha.class);
            validarSenha.putExtra("numero_sorteio", numero_sorteio_string);
            validarSenha.putExtra("numero", numero);

            if(tipo_usuario.equals("Carreto")){
                validarSenha.putExtra("modalidade", "Carreto");
            }else{
                validarSenha.putExtra("modalidade", "Cliente");
            }

            startActivity(validarSenha);

        }
    }
}