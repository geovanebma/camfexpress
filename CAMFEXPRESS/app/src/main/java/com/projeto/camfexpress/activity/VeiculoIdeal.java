package com.projeto.camfexpress.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.projeto.camfexpress.config.ConfiguracaoFirebase;
import com.projeto.camfexpress.config.MedidasCarreto;
import com.projeto.camfexpress.R;


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

    //Botão de aceitar os termos e condições de carga
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

    //Ao confirmar as medidas da carga, é encaminhado a tela de Cliente para escolher o endereço
    public void confirmarMedidas(View view){
        MedidasCarreto medidas = new MedidasCarreto();
        medidas.setTipo("veiculo ideal");
        medidas.setComprimento(comprimento.getText().toString());
        medidas.setLargura(largura.getText().toString());
        medidas.setAltura(altura.getText().toString());
        medidas.setPeso(peso.getText().toString());

        FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        FirebaseUser usuario_atual = autenticacao.getCurrentUser();
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference usuarios = firebaseRef.child("usuarios").child(usuario_atual.getPhoneNumber());
        usuarios.child("carga").setValue(medidas);

        Intent cliente = new Intent(VeiculoIdeal.this, ClienteActivity.class);
        cliente.putExtra("medidaComprimento", comprimento.getText().toString());
        cliente.putExtra("medidaLargura", largura.getText().toString());
        cliente.putExtra("medidaAltura", altura.getText().toString());
        cliente.putExtra("medidaPeso", peso.getText().toString());
        cliente.putExtra("porte", "veiculo ideal");
        cliente.putExtra("ajudante", getIntent().getStringExtra("ajudante"));
        cliente.putExtra("seguro", getIntent().getStringExtra("seguro"));
        startActivity(cliente);
    }
}