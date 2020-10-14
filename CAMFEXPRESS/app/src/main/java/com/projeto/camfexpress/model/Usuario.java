package com.projeto.camfexpress.model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.projeto.camfexpress.config.ConfiguracaoFirebase;

import java.io.Serializable;

/**
 * Created by jamiltondamasceno
 */

public class Usuario implements Serializable {

    private String id;
    private String email;
    private String senha;
    private String nome;
    private String dataNascimento;
    private String cpfCnpj;
    private String cep;
    private String endereco;
    private String numeroResidencial;
    private String bairro;
    private String cidade;
    private String estado;
    private String celular;
    private String tipo;
    private String sms;
    private String idChave;
    private String ativo;
    private String latitude;
    private String longetude;

    public Usuario() {
    }

    public void salvar(){
        FirebaseAuth usuario_atual = ConfiguracaoFirebase.getFirebaseAutenticacao();
        FirebaseUser usuario_ativo = usuario_atual.getCurrentUser();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(this.getNome()).build();

        usuario_ativo.updateProfile(profileUpdates);
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference usuarios = firebaseRef.child("usuarios").child(getCelular());

        usuarios.setValue(this);
    }

    public String getIdChave() {
        return idChave;
    }

    public void setIdChave(String idChave) {
        this.idChave = idChave;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongetude() {
        return longetude;
    }

    public void setLongetude(String longetude) {
        this.longetude = longetude;
    }

    public String getAtivo() {
        return ativo;
    }

    public void setAtivo(String ativo) {
        this.ativo = ativo;
    }

    public String getId() {
        return id;
    }

    @Exclude
    public void setId(String id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getEmail() {
        return email;
    }

    public String getSms() {
        return sms;
    }

    public void setSms(String sms) {
        this.sms = sms;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(String dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getNumeroResidencial() {
        return numeroResidencial;
    }

    public void setNumeroResidencial(String numeroResidencial) {
        this.numeroResidencial = numeroResidencial;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
