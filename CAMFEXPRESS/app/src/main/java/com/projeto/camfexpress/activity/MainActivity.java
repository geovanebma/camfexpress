package com.projeto.camfexpress.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.uber.cursoandroid.jamiltondamasceno.uber.R;
import com.projeto.camfexpress.config.ConfiguracaoFirebase;
import com.projeto.camfexpress.helper.Permissoes;
import com.projeto.camfexpress.helper.UsuarioFirebase;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private String[] permissoes = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        //validar permissões
        Permissoes.validarPermissoes(permissoes, this, 1);


        mAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();
        /*autenticacao.signOut();*/

    }

    public void buttonCarreto(View view){
        if(mAuth.getCurrentUser() != null){
            Intent intent = new Intent(this, SenhaUser.class);
            intent.putExtra("modalidade", "Carreto");
            startActivity(intent);
        }else{
            Intent intent = new Intent(this, VerificarNumero.class);
            intent.putExtra("modalidade", "Carreto");
            startActivity(intent);
        }
    }

    public void buttonCliente(View view){
        if(mAuth.getCurrentUser() != null){
            Intent intent = new Intent(this, SenhaUser.class);
            intent.putExtra("modalidade", "Cliente");
            startActivity(intent);
        }else{
            Intent intent = new Intent(this, VerificarNumero.class);
            intent.putExtra("modalidade", "Cliente");
            startActivity(intent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        UsuarioFirebase.redirecionaUsuarioLogado(MainActivity.this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for(int permissaoResultado : grantResults){
            if( permissaoResultado == PackageManager.PERMISSION_DENIED){
                alertaValidacaoPermissao();
            }
        }
    }

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
