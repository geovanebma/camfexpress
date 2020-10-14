package com.projeto.camfexpress.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uber.cursoandroid.jamiltondamasceno.uber.R;

public class SenhaUser extends AppCompatActivity {

    private TextInputEditText senha;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_senha_user);

        senha = findViewById(R.id.senha);
    }

    public int contador = 1;
    public void verificarSenha(View view){
        String numero = getIntent().getStringExtra("numero");

        //Retirar depois - fixo *****************************
        numero ="+5511945549457";

        DatabaseReference referencia = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usuario_bd_senha = referencia.child("usuarios").child(numero).child("senha");

        usuario_bd_senha.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println(dataSnapshot.getValue()+" "+senha.getText().toString());
                if(dataSnapshot.getValue().toString().equals(senha.getText().toString())){
                    String modalidade = getIntent().getStringExtra("modalidade");
                    String numero = getIntent().getStringExtra("numero");
                    System.out.println("mod: "+modalidade);
                    enviarIntent(numero, modalidade);
                }else{
                    if(contador == 3){
                        String tipo_usuario = getIntent().getStringExtra("modalidade");
                        Intent intent = new Intent(SenhaUser.this, VerificarNumero.class);
                        intent.putExtra("tipo_usuario", tipo_usuario);
                        //mAuth.signOut();
                        startActivity(intent);
                    }else{
                        senha.setError("Senha incorreta/vazia. Tentativa "+contador+" de 3.");
                        senha.requestFocus();
                    }

                    contador++;
                    System.out.println(contador);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void enviarIntent(String numero, String modalidade){
        System.out.println("modalidade: "+modalidade);
        if(modalidade.equals("Carreto") || modalidade == "Carreto"){
            Intent intent = new Intent(SenhaUser.this, RequisicoesActivity.class);
            intent.putExtra("modalidade", modalidade);
            intent.putExtra("numero", numero);
            startActivity(intent);
        }else{
            Intent intent = new Intent(SenhaUser.this, EscolhaCarreto.class);
            intent.putExtra("modalidade", modalidade);
            intent.putExtra("numero", numero);
            startActivity(intent);
        }
    }
}
