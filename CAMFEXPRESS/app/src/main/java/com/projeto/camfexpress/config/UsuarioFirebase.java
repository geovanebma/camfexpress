package com.projeto.camfexpress.config;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.projeto.camfexpress.activity.ClienteActivity;
import com.projeto.camfexpress.activity.RequisicoesActivity;

/**
 * Created by jamiltondamasceno
 */

public class UsuarioFirebase {
    //Recupera o usuario atual
    public static FirebaseUser getUsuarioAtual(){
        FirebaseAuth usuario = ConfiguracaoFirebase.getFirebaseAutenticacao();
        return usuario.getCurrentUser();
    }

    //Pega os dados do usuário atual
    public static Usuario getDadosUsuarioLogado(){
        FirebaseUser firebaseUser = getUsuarioAtual();

        Usuario usuario = new Usuario();
        usuario.setId( firebaseUser.getUid() );
        usuario.setEmail( firebaseUser.getEmail() );
        usuario.setNome( firebaseUser.getDisplayName() );
        usuario.setCelular(firebaseUser.getPhoneNumber());

        return usuario;
    }

    //Redireciona o usuário logado
    public static void redirecionaUsuarioLogado(final Activity activity){
        FirebaseUser user = getUsuarioAtual();
        if(user != null ){
            DatabaseReference usuariosRef = ConfiguracaoFirebase.getFirebaseDatabase()
                    .child("usuarios")
                    .child( getUsuarioAtual().getPhoneNumber() );
            usuariosRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Usuario usuario = dataSnapshot.getValue( Usuario.class );

                    String tipoUsuario = usuario.getTipo();
                    if(tipoUsuario != null){
                        if( tipoUsuario.equals("Carreto") ){
                            Intent i = new Intent(activity, RequisicoesActivity.class);
                            activity.startActivity(i);
                        }else {
                            Intent i = new Intent(activity, ClienteActivity.class);
                            activity.startActivity(i);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }


    //Atualiza os dados de localização
    public static void atualizarDadosLocalizacao(double lat, double lon){
        //Define nó de local de usuário
        DatabaseReference localUsuario = ConfiguracaoFirebase.getFirebaseDatabase().child("local_usuario");
        GeoFire geoFire = new GeoFire(localUsuario);

        //Recupera dados usuário logado
        Usuario usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();

        //Configura localização do usuário
        geoFire.setLocation(
            usuarioLogado.getId(),
            new GeoLocation(lat, lon),
            new GeoFire.CompletionListener() {
                @Override
                public void onComplete(String key, DatabaseError error) {
                    if( error != null ){
                        Log.d("Erro", "Erro ao salvar local!");
                    }
                }
            }
        );
    }

    //Busca o id do usuário
    public static String getIdentificadorUsuario(){
        return getUsuarioAtual().getUid();
    }

    //Busca o número de telefone cadastrado pelo usuário
    public static String getCelularUsuario(){
        return getUsuarioAtual().getPhoneNumber();
    }
}