package com.projeto.camfexpress.config;

import com.google.firebase.database.DatabaseReference;
import com.projeto.camfexpress.config.ConfiguracaoFirebase;

public class Veiculo {
    private String placa;
    private String marca;
    private String modelo;
    private String comprimento;
    private String largura;
    private String altura;
    private String peso;
    public String ativo;

    //Classe de objeto para buscar e alterar os dados do veículo
    public Veiculo() {
    }

    //Salva as informações do veículo no firebase
    public void salvar(String numero){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference usuarios = firebaseRef.child("usuarios").child(numero).child("veiculo");

        firebaseRef.child("usuarios").child(numero).child("ativo").setValue("true");
        usuarios.setValue(this);
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getComprimento() {
        return comprimento;
    }

    public void setComprimento(String comprimento) {
        this.comprimento = comprimento;
    }

    public String getLargura() {
        return largura;
    }

    public void setLargura(String largura) {
        this.largura = largura;
    }

    public String getAltura() {
        return altura;
    }

    public void setAltura(String altura) {
        this.altura = altura;
    }

    public String getPeso() {
        return peso;
    }

    public void setPeso(String peso) {
        this.peso = peso;
    }
}