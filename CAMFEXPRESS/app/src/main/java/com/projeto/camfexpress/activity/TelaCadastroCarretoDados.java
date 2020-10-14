package com.projeto.camfexpress.activity;

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
import com.projeto.camfexpress.model.Usuario;
import com.projeto.camfexpress.model.Validacoes;

public class TelaCadastroCarretoDados extends AppCompatActivity {

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

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_cadastro_carreto_dados);

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

        mAuth = FirebaseAuth.getInstance();
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
            System.out.println("AAA");
            Intent receber_tipo_usuario = getIntent();
            String numero = receber_tipo_usuario.getStringExtra("numero");
            String modalidade = receber_tipo_usuario.getStringExtra("modalidade");

            if(modalidade.equals("Carreto")){
                Usuario carreto = new Usuario();
                carreto.setNome(nomeCompleto.getText().toString());
                carreto.setDataNascimento(dataNascimento.getText().toString());
                carreto.setCpfCnpj(cpfCnpj.getText().toString());
                carreto.setCep(cep.getText().toString());
                carreto.setEndereco(endereco.getText().toString());
                carreto.setNumeroResidencial(numeroResidencial.getText().toString());
                carreto.setBairro(bairro.getText().toString());
                carreto.setCidade(cidade.getText().toString());
                carreto.setEstado(estado.getText().toString());
                carreto.setCelular(numero);
                carreto.setEmail(email.getText().toString());
                carreto.setSenha(confirmarSenha.getText().toString());
                carreto.setTipo("Carreto");
                carreto.setAtivo("True");

                cadastrarCarreto(carreto);
            }
        }else{
            String mensagem = "";
            if(!nome_completo){
                mensagem = "Preencha o campo 'Nome Completo' corretamente.";
                nomeCompleto.setError(mensagem);
                nomeCompleto.requestFocus();
            }else if(!data_nascimento){
                mensagem = "Preencha o campo 'Data de Nascimento' corretamente.";
                dataNascimento.setError(mensagem);
                dataNascimento.requestFocus();
            }else if(!cpf_cnpj){
                mensagem = "Preencha o campo 'CPF/CNPJ' corretamente.";
                cpfCnpj.setError(mensagem);
                cpfCnpj.requestFocus();
            }else if(!cep_end){
                mensagem = "Preencha o campo 'CEP' corretamente.";
                cep.setError(mensagem);
                cep.requestFocus();
            }else if(!endereco_end){
                mensagem = "Preencha o campo 'Endereço' corretamente.";
                endereco.setError(mensagem);
                endereco.requestFocus();
            }else if(!numero_residencial){
                mensagem = "Preencha o campo 'Número Residêncial' corretamente.";
                numeroResidencial.setError(mensagem);
                numeroResidencial.requestFocus();
            }else if(!bairro_end){
                mensagem = "Preencha o campo 'Bairro' corretamente.";
                bairro.setError(mensagem);
                bairro.requestFocus();
            }else if(!cidade_end){
                mensagem = "Preencha o campo 'Cidade' corretamente.";
                cidade.setError(mensagem);
                cidade.requestFocus();
            }else if(!estado_end){
                mensagem = "Preencha o campo 'Estado' corretamente.";
                estado.setError(mensagem);
                estado.requestFocus();
            }else if(!email_end){
                mensagem = "Preencha o campo 'E-mail' corretamente.";
                email.setError(mensagem);
                email.requestFocus();
            }else if(!senha_end){
                mensagem = "Preencha o campo 'Senha' corretamente.";
                senha.setError(mensagem);
                senha.requestFocus();
            }else if(!confirmarSenha_end){
                mensagem = "Preencha o campo 'Confirmar Senha' corretamente.";
                confirmarSenha.setError(mensagem);
                confirmarSenha.requestFocus();
            }else if(!verificar_senhas){
                mensagem = "Os campos Senha e Confirmar Senha não coincidem.";
                confirmarSenha.setError(mensagem);
                confirmarSenha.requestFocus();
            }

            Toast.makeText(TelaCadastroCarretoDados.this, mensagem, Toast.LENGTH_SHORT).show();
        }
    }

    public void cadastrarCarreto(final Usuario usuario){
        usuario.setId(usuario.getCelular());
        usuario.salvar();

        Intent intent_carreto = new Intent(TelaCadastroCarretoDados.this, TelaCadastroCarreto.class);
        String numero = usuario.getCelular();
        intent_carreto.putExtra("numero", numero);
        startActivity(intent_carreto);
    }
}