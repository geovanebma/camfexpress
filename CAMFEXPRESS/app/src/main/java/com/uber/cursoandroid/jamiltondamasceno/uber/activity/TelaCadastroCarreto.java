package com.uber.cursoandroid.jamiltondamasceno.uber.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.uber.cursoandroid.jamiltondamasceno.uber.R;
import com.uber.cursoandroid.jamiltondamasceno.uber.model.Validacoes;
import com.uber.cursoandroid.jamiltondamasceno.uber.model.Veiculo;

public class TelaCadastroCarreto extends AppCompatActivity {

    private TextInputEditText placa;
    private TextInputEditText marca;
    private TextInputEditText modelo;
    private TextInputEditText comprimento;
    private TextInputEditText largura;
    private TextInputEditText altura;
    private TextInputEditText peso;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_carreto);

        placa = findViewById(R.id.placa);
        marca = findViewById(R.id.marca);
        modelo = findViewById(R.id.modelo);
        comprimento = findViewById(R.id.comprimento);
        largura = findViewById(R.id.largura);
        altura = findViewById(R.id.altura);
        peso = findViewById(R.id.peso);

        SimpleMaskFormatter smf_medida = new SimpleMaskFormatter("NNNN,NN");

        MaskTextWatcher mtw_comprimento = new MaskTextWatcher(comprimento, smf_medida);
        comprimento.addTextChangedListener(mtw_comprimento);
        MaskTextWatcher mtw_largura = new MaskTextWatcher(largura, smf_medida);
        largura.addTextChangedListener(mtw_largura);
        MaskTextWatcher mtw_altura = new MaskTextWatcher(altura, smf_medida);
        altura.addTextChangedListener(mtw_altura);
        MaskTextWatcher mtw_peso = new MaskTextWatcher(peso, smf_medida);
        peso.addTextChangedListener(mtw_peso);
    }

    public void cadastrarCarretoVeiculo(View view){
        Validacoes validacao = new Validacoes();
        boolean placa_valid = validacao.valorENulo(placa.getText().toString());
        boolean marca_valid = validacao.valorENulo(marca.getText().toString());
        boolean modelo_valid = validacao.valorENulo(modelo.getText().toString());
        boolean comprimento_valid = validacao.valorENulo(comprimento.getText().toString());
        boolean largura_valid = validacao.valorENulo(largura.getText().toString());
        boolean altura_valid = validacao.valorENulo(altura.getText().toString());
        boolean peso_valid = validacao.valorENulo(peso.getText().toString());

        if(placa_valid && marca_valid && modelo_valid && comprimento_valid && largura_valid && altura_valid && peso_valid){
            Veiculo veiculo = new Veiculo();
            veiculo.setPlaca(placa.getText().toString());
            veiculo.setMarca(marca.getText().toString());
            veiculo.setModelo(modelo.getText().toString());
            veiculo.setComprimento(comprimento.getText().toString());
            veiculo.setLargura(largura.getText().toString());
            veiculo.setAltura(altura.getText().toString());
            veiculo.setPeso(peso.getText().toString());

            testeCadastro(veiculo);
        }else{
            String mensagem = "";
            if(!placa_valid){
                mensagem = "Preencha o campo 'Placa' corretamente.";
            }else if(!marca_valid){
                mensagem = "Preencha o campo 'Marca' corretamente.";
            }else if(!modelo_valid){
                mensagem = "Preencha o campo 'Modelo' corretamente.";
            }else if(!comprimento_valid){
                mensagem = "Preencha o campo 'Comprimento' corretamente.";
            }else if(!largura_valid){
                mensagem = "Preencha o campo 'Largura' corretamente.";
            }else if(!altura_valid){
                mensagem = "Preencha o campo 'Altura' corretamente.";
            }else if(!peso_valid){
                mensagem = "Preencha o campo 'Peso Máximo Suportado' corretamente.";
            }

            Toast.makeText(TelaCadastroCarreto.this, mensagem, Toast.LENGTH_SHORT).show();
        }
    }

    public void testeCadastro(Veiculo veiculo){
        String numero = getIntent().getStringExtra("numero");
        veiculo.salvar(numero);

        Intent intent_carreto = new Intent(TelaCadastroCarreto.this, RequisicoesActivity.class);
        intent_carreto.putExtra("numero", numero);
        startActivity(intent_carreto);
    }
}