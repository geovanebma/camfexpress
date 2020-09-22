package com.desenvolvimento.camfexpress.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ConfiguracaoFirebase {
    private static DatabaseReference database;
    private  static FirebaseAuth auth;

    //Instanciando o Firebase
    public static DatabaseReference getFirebaseDatabase(){
        if(database == null){
            database = FirebaseDatabase.getInstance().getReference();
        }
        return database;
    }

    //Retornando a instância do Firebase
    public static FirebaseAuth getFirebaseAuth(){
        if(auth == null){
            auth = FirebaseAuth.getInstance();
        }

        return auth;
    }
}
