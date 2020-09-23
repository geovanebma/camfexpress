package com.desenvolvimento.camfexpress.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.desenvolvimento.camfexpress.R;
import com.desenvolvimento.camfexpress.config.ConfiguracaoFirebase;
import com.desenvolvimento.camfexpress.modal.Usuario;
import com.desenvolvimento.camfexpress.modal.Validacoes;
import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class TelaCadastroCliente extends AppCompatActivity {

    private TextInputEditText nomeCompleto;
    private TextInputEditText dataNascimento;
    private TextInputEditText cpfCnpj;
    private TextInputEditText cep;
    private TextInputEditText endereco;
    private TextInputEditText numeroResidencial;
    private TextInputEditText bairro;
    private TextInputEditText cidade;
    private TextInputEditText estado;
    private TextInputEditText email;
    private TextInputEditText senha;
    private TextInputEditText confirmarSenha;

    private FirebaseAuth autenticar_susuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_cliente);

        nomeCompleto = findViewById(R.id.nomeCompleto);
        dataNascimento = findViewById(R.id.dataNascimento);
        cpfCnpj = findViewById(R.id.cpfCnpj);
        cep = findViewById(R.id.cep);
        endereco = findViewById(R.id.endereco);
        numeroResidencial = findViewById(R.id.numeroResidencial);
        bairro = findViewById(R.id.bairro);
        cidade = findViewById(R.id.cidade);
        estado = findViewById(R.id.estado);
        email = findViewById(R.id.email);
        senha = findViewById(R.id.senha);
        confirmarSenha = findViewById(R.id.confirmarSenha);

        SimpleMaskFormatter smf_data = new SimpleMaskFormatter("NN/NN/NNNN");
        MaskTextWatcher mtw_data = new MaskTextWatcher(dataNascimento, smf_data);
        dataNascimento.addTextChangedListener(mtw_data);

        String forma_cpfcnpj = "";
        if(cpfCnpj.getText().toString().length() > 14){
            forma_cpfcnpj = "NN.NNN.NNN/NNNN-NN";
        }else{
            forma_cpfcnpj = "NNN.NNN.NNN-NN";
        }

        SimpleMaskFormatter smf_cpfcnpj = new SimpleMaskFormatter(forma_cpfcnpj);
        MaskTextWatcher mtw_cpfcnpj = new MaskTextWatcher(cpfCnpj, smf_cpfcnpj);
        cpfCnpj.addTextChangedListener(mtw_cpfcnpj);

        SimpleMaskFormatter smf_cep = new SimpleMaskFormatter("NNNNN-NNN");
        MaskTextWatcher mtw_cep = new MaskTextWatcher(cep, smf_cep);
        cep.addTextChangedListener(mtw_cep);
    }

    public void buttonCadastrar(View view){
        Validacoes validacao = new Validacoes();

        boolean nome_completo = validacao.valorENulo(nomeCompleto.getText().toString());
        boolean data_nascimento = validacao.validarData(dataNascimento.getText().toString());
        boolean cpf_cnpj = validacao.validarCPFCNPJ(cpfCnpj.getText().toString());
        boolean cep_end = validacao.verificarCEP(cep.getText().toString());
        boolean endereco_end = validacao.valorENulo(endereco.getText().toString());
        boolean numero_residencial = validacao.valorENulo(numeroResidencial.getText().toString());
        boolean bairro_end = validacao.valorENulo(bairro.getText().toString());
        boolean cidade_end = validacao.valorENulo(cidade.getText().toString());
        boolean estado_end = validacao.valorENulo(estado.getText().toString());
        boolean email_end = validacao.verificarEmail(email.getText().toString());
        boolean senha_end = validacao.valorENulo(senha.getText().toString());
        boolean confirmarSenha_end = validacao.valorENulo(confirmarSenha.getText().toString());
        boolean verificar_senhas = senha.getText().toString().equals(confirmarSenha.getText().toString());

        if(nome_completo && data_nascimento && cpf_cnpj && cep_end && endereco_end && numero_residencial && bairro_end && cidade_end && estado_end && email_end && senha_end && confirmarSenha_end && verificar_senhas){
            Intent receber_tipo_usuario = getIntent();
            String numero = receber_tipo_usuario.getStringExtra("numero");
            String modalidade = receber_tipo_usuario.getStringExtra("modalidade");
            String codigo_sms = receber_tipo_usuario.getStringExtra("codigo_sms");

            if(modalidade.equals("Cliente")){
                Usuario cliente = new Usuario();
                cliente.setNome(nomeCompleto.getText().toString());
                cliente.setDataNascimento(dataNascimento.getText().toString());
                cliente.setCpfCnpj(cpfCnpj.getText().toString());
                cliente.setCep(cep.getText().toString());
                cliente.setEndereco(endereco.getText().toString());
                cliente.setNumeroResidencial(numeroResidencial.getText().toString());
                cliente.setBairro(bairro.getText().toString());
                cliente.setCidade(cidade.getText().toString());
                cliente.setEstado(estado.getText().toString());
                cliente.setCelular(numero);
                cliente.setEmail(email.getText().toString());
                cliente.setSenha(confirmarSenha.getText().toString());
                cliente.setTipo("Cliente");
                cliente.setSms(codigo_sms);

                cadastrarCliente(cliente);
                /*Intent cadastroCliente = new Intent(this, TelaCadastroCliente.class);
                cadastroCliente.putExtra("numero", numero);
                startActivity(cadastroCliente);*/
            }
        }else{
            String mensagem = "";
            if(!nome_completo){
                mensagem = "Preencha o campo 'Nome Completo' corretamente.";
            }else if(!data_nascimento){
                mensagem = "Preencha o campo 'Data de Nascimento' corretamente.";
            }else if(!cpf_cnpj){
                mensagem = "Preencha o campo 'CPF/CNPJ' corretamente.";
            }else if(!cep_end){
                mensagem = "Preencha o campo 'CEP' corretamente.";
            }else if(!endereco_end){
                mensagem = "Preencha o campo 'Endereço' corretamente.";
            }else if(!numero_residencial){
                mensagem = "Preencha o campo 'Número Residêncial' corretamente.";
            }else if(!bairro_end){
                mensagem = "Preencha o campo 'Bairro' corretamente.";
            }else if(!cidade_end){
                mensagem = "Preencha o campo 'Cidade' corretamente.";
            }else if(!estado_end){
                mensagem = "Preencha o campo 'Estado' corretamente.";
            }else if(!email_end){
                mensagem = "Preencha o campo 'E-mail' corretamente.";
            }else if(!senha_end){
                mensagem = "Preencha o campo 'Senha' corretamente.";
            }else if(!confirmarSenha_end){
                mensagem = "Preencha o campo 'Confirmar Senha' corretamente.";
            }else if(!verificar_senhas){
                mensagem = "Os campos Senha e Confirmar Senha não coincidem.";
            }

            Toast.makeText(TelaCadastroCliente.this, mensagem, Toast.LENGTH_SHORT).show();
        }
    }

    public void cadastrarCliente(final Usuario usuario){
        autenticar_susuario = ConfiguracaoFirebase.getFirebaseAuth();
        autenticar_susuario.createUserWithEmailAndPassword(
                usuario.getCelular(), usuario.getSms()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    String idCliente = task.getResult().getUser().getUid();
                    usuario.setId(idCliente);
                    usuario.salvar();
                }
            }
        });
    }
}
