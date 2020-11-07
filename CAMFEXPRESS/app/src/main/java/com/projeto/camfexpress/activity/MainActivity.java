package com.projeto.camfexpress.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.projeto.camfexpress.R;
import com.projeto.camfexpress.config.ConfiguracaoFirebase;
import com.projeto.camfexpress.config.Permissoes;
import com.projeto.camfexpress.config.UsuarioFirebase;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private String[] permissoes = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private CheckBox checkBoxConcordancia;
    private Button buttonCarreto;
    private Button buttonCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        //validar permissões
        Permissoes.validarPermissoes(permissoes, this, 1);
        checkBoxConcordancia = findViewById(R.id.checkBoxConcordancia);
        buttonCarreto = findViewById(R.id.buttonCarreto);
        buttonCliente = findViewById(R.id.buttonCliente);

        mAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();
    }

    //Botão de escolha para carreto
    public void buttonCarreto(View view){
        if(((CheckBox) checkBoxConcordancia).isChecked()){
            if(mAuth.getCurrentUser() != null){
                Intent intent = new Intent(this, SenhaUser.class);
                intent.putExtra("modalidade", "Carreto");
                startActivity(intent);
            }else{
                Intent intent = new Intent(this, VerificarNumero.class);
                intent.putExtra("modalidade", "Carreto");
                startActivity(intent);
            }
        }else{
            Toast.makeText(MainActivity.this, "É necessário aceitar os termos e condições.", Toast.LENGTH_SHORT).show();
        }
    }

    //Botão de escolha para cliente
    public void buttonCliente(View view){
        if(((CheckBox) checkBoxConcordancia).isChecked()){
            if(mAuth.getCurrentUser() != null){
                Intent intent = new Intent(this, SenhaUser.class);
                intent.putExtra("modalidade", "Cliente");
                startActivity(intent);
            }else{
                Intent intent = new Intent(this, VerificarNumero.class);
                intent.putExtra("modalidade", "Cliente");
                startActivity(intent);
            }
        }else{
            Toast.makeText(MainActivity.this, "É necessário aceitar os termos e condições.", Toast.LENGTH_SHORT).show();
        }
    }

    //Botão checkbox para aceitar os termos e condições
    public void concordarTexto(View view){
        if(((CheckBox) checkBoxConcordancia).isChecked()){
            buttonCarreto.setEnabled(true);
            buttonCliente.setEnabled(true);
        }else{
            buttonCarreto.setEnabled(false);
            buttonCliente.setEnabled(false);
        }
    }

    //Botão para visualizar os termos e condições do APP
    public void termosCondicoes(View view){
        Intent intent = new Intent(this, TermosCondicoes.class);
        startActivity(intent);
    }

    //Startar a aplicação
    @Override
    protected void onStart() {
        super.onStart();
        UsuarioFirebase.redirecionaUsuarioLogado(MainActivity.this);
    }

    //Aceitar a permissão de ver a localização
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for(int permissaoResultado : grantResults){
            if( permissaoResultado == PackageManager.PERMISSION_DENIED){
                alertaValidacaoPermissao();
            }
        }
    }

    //Componentes de aceitação de localização
    private void alertaValidacaoPermissao(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões Negadas");
        builder.setMessage("Para utilizar o app é necessário aceitar as permissões");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
