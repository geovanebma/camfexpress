package com.projeto.camfexpress.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.projeto.camfexpress.config.Veiculo;
import com.projeto.camfexpress.R;
import com.projeto.camfexpress.config.Usuario;

import java.util.concurrent.TimeUnit;

public class ValidarSMS extends AppCompatActivity {

    private TextInputEditText validaSMS;
    private String verificationId;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validar_senha);

        mAuth = FirebaseAuth.getInstance();
        validaSMS = findViewById(R.id.validaSMS);
        String numero = getIntent().getStringExtra("numero");

        enviarVerificacaoCodigo(numero);
    }

    //Botão para acionar a verificação do código sms
    public void validarSMS(View view){
        verificarCodigo(validaSMS.getText().toString());
    }

    //Pega o código sms e segue como credencial
    private void verificarCodigo(String codigo){
        PhoneAuthCredential credencial = PhoneAuthProvider.getCredential(verificationId, codigo);
        signInWithCredential(credencial, verificationId, codigo);
    }

    //Verifica se está correto
    private void signInWithCredential(final PhoneAuthCredential credencial, final String verificationId, final String codigo) {
        mAuth.signInWithCredential(credencial).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            public void onComplete(@NonNull Task<AuthResult> task) {
            if(task.isSuccessful()){
                String numero = getIntent().getStringExtra("numero");
                System.out.println(numero);

                DatabaseReference referencia = FirebaseDatabase.getInstance().getReference();
                DatabaseReference usuario_bd_celular = referencia.child("usuarios").child(numero).child("ativo");
                usuario_bd_celular.addValueEventListener(new ValueEventListener() {
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        System.out.println(dataSnapshot.getValue());
                        if(dataSnapshot.getValue() == null){
                            String tipo_usuario = getIntent().getStringExtra("modalidade");
                            String numero = getIntent().getStringExtra("numero");
                            salvarNumero(numero, tipo_usuario, "");
                        }else if(dataSnapshot.getValue().toString().equals("false")){
                            String tipo_usuario = getIntent().getStringExtra("modalidade");
                            String numero = getIntent().getStringExtra("numero");
                            salvarNumero(numero, tipo_usuario, "senha");
                        }else{
                            String tipo_usuario = getIntent().getStringExtra("modalidade");
                            String numero = getIntent().getStringExtra("numero");
                            salvarNumero(numero, tipo_usuario, "ok");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(ValidarSMS.this, "Conexão com Firebase em restrição, tente novamente mais tarde.", Toast.LENGTH_SHORT).show();
                    }
                });
            }else{
                Toast.makeText(ValidarSMS.this, "Código inválido.", Toast.LENGTH_SHORT).show();
            }
        }
        });
    }

    //Se estiver correto, dependendo do porte, encaminha para outra tela
    public void salvarNumero(String numero, String modalidade, String tela){
        Intent intent;

        if(tela.equals("senha")){
            intent = new Intent(ValidarSMS.this, SenhaUser.class);
            intent.putExtra("modalidade", modalidade);
            intent.putExtra("numero", numero);
            startActivity(intent);
        }else if (tela.equals("ok")){
            if(modalidade.equals("Carreto")){
                intent = new Intent(ValidarSMS.this, RequisicoesActivity.class);
            }else{
                intent = new Intent(ValidarSMS.this, EscolhaCarreto.class);
            }

            intent.putExtra("modalidade", modalidade);
            intent.putExtra("numero", numero);
            startActivity(intent);
        }else{
            Usuario usuario = new Usuario();
            usuario.setCelular(numero);
            usuario.salvar();

            if(modalidade.equals("Carreto")){
                intent = new Intent(ValidarSMS.this, TelaCadastroCarretoDados.class);
            }else{
                intent = new Intent(ValidarSMS.this, TelaCadastroCliente.class);
            }

            intent.putExtra("numero", numero);
            intent.putExtra("modalidade", modalidade);
            startActivity(intent);
        }
    }

    //Envia o código sms ao número informado
    private void enviarVerificacaoCodigo(String numero){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                numero,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBack
        );
    }

    //CallBack de erro ao enviar o código
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            verificationId = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if(code != null){
                verificarCodigo(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            System.out.println("AAA: "+e.getMessage());
            Toast.makeText(ValidarSMS.this, "Erro: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };
}