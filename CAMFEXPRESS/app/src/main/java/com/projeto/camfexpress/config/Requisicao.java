package com.projeto.camfexpress.config;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jamiltondamasceno
 */

public class Requisicao {

    private String id;
    private String status;
    private Usuario cliente;
    private Usuario motorista;
    private Destino destino;

    public static final String STATUS_AGUARDANDO = "aguardando";
    public static final String STATUS_A_CAMINHO = "acaminho";
    public static final String STATUS_VIAGEM = "viagem";
    public static final String STATUS_FINALIZADA = "finalizada";
    public static final String STATUS_ENCERRADA = "encerrada";
    public static final String STATUS_CANCELADA = "cancelada";

    //Classe de objeto para buscar e alterar os dados da requisição
    public Requisicao() {
    }

    //Salvar a requisição no firebase
    public void salvar(){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference requisicoes = firebaseRef.child("requisicoes");

        FirebaseUser idRequisicao = UsuarioFirebase.getUsuarioAtual();
        setId( idRequisicao.getPhoneNumber() );

        requisicoes.child( getId() ).setValue(this);
    }

    //Atualizar requisição no firebase
    public void atualizar(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference requisicoes = firebaseRef.child("requisicoes");

        DatabaseReference requisicao = requisicoes.child(getId());

        Map<String, Object> objeto = new HashMap<>();
        objeto.put("motorista", getMotorista() );
        objeto.put("status", getStatus());

        requisicao.updateChildren( objeto );
    }

    //Atualizar Status da corrida
    public void atualizarStatus(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference requisicoes = firebaseRef.child("requisicoes");
        DatabaseReference requisicao = requisicoes.child(getId());

        Map<String, Object> objeto = new HashMap<>();
        objeto.put("status", getStatus());

        requisicao.updateChildren(objeto);
    }

    //Atualiza a localização do Motorista
    public void atualizarLocalizacaoMotorista(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference requisicoes = firebaseRef.child("requisicoes");
        DatabaseReference requisicao = requisicoes.child(getId()).child("motorista");

        Map<String, Object> objeto = new HashMap<>();
        objeto.put("latitude", getMotorista().getLatitude() );
        objeto.put("longitude", getMotorista().getLongitude());

        requisicao.updateChildren( objeto );
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Usuario getCliente() {
        return cliente;
    }

    public void setCliente(Usuario cliente) {
        this.cliente = cliente;
    }

    public Usuario getMotorista() {
        return motorista;
    }

    public void setMotorista(Usuario motorista) {
        this.motorista = motorista;
    }

    public Destino getDestino() {
        return destino;
    }

    public void setDestino(Destino destino) {
        this.destino = destino;
    }
}
