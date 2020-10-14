package com.projeto.camfexpress.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.uber.cursoandroid.jamiltondamasceno.uber.R;
import com.projeto.camfexpress.adpter.RequisicoesAdapter;
import com.projeto.camfexpress.config.ConfiguracaoFirebase;
import com.projeto.camfexpress.helper.RecyclerItemClickListener;
import com.projeto.camfexpress.helper.UsuarioFirebase;
import com.projeto.camfexpress.model.Requisicao;
import com.projeto.camfexpress.model.Usuario;

import java.util.ArrayList;
import java.util.List;

public class RequisicoesActivity extends AppCompatActivity {

    //Componentes
    private RecyclerView recyclerRequisicoes;
    private TextView textResultado;

    private FirebaseAuth autenticacao;
    private DatabaseReference firebaseRef;
    private List<Requisicao> listaRequisicoes = new ArrayList<>();
    private RequisicoesAdapter adapter;
    private Usuario motorista;

    private LocationManager locationManager;
    private LocationListener locationListener;
    public int temCondicoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requisicoes);

        inicializarComponentes();

        //Recuperar localizacao do usuário
        recuperarLocalizacaoUsuario();
    }

    @Override
    protected void onStart() {
        super.onStart();
        verificaStatusRequisicao();
    }

    private void verificaStatusRequisicao(){

        Usuario usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();

        DatabaseReference requisicoes = firebaseRef.child("requisicoes");

        Query requisicoesPesquisa = requisicoes.orderByChild("motorista/id")
                .equalTo( usuarioLogado.getId() );

        requisicoesPesquisa.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for( DataSnapshot ds: dataSnapshot.getChildren() ){

                    Requisicao requisicao = ds.getValue( Requisicao.class );

                    if( requisicao.getStatus().equals(Requisicao.STATUS_A_CAMINHO)
                            || requisicao.getStatus().equals(Requisicao.STATUS_VIAGEM)
                            || requisicao.getStatus().equals(Requisicao.STATUS_FINALIZADA)){
                        motorista = requisicao.getMotorista();
                        abrirTelaCorrida(requisicao.getId(), motorista, true);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void recuperarLocalizacaoUsuario() {

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                //recuperar latitude e longitude
                String latitude = String.valueOf(location.getLatitude());
                String longitude = String.valueOf(location.getLongitude());

                //Atualizar GeoFire
                UsuarioFirebase.atualizarDadosLocalizacao(
                        location.getLatitude(),
                        location.getLongitude()
                );

                if( !latitude.isEmpty() && !longitude.isEmpty() ){
                    motorista.setLatitude(latitude);
                    motorista.setLongetude(longitude);

                    adicionaEventoCliqueRecyclerView();
                    locationManager.removeUpdates(locationListener);
                    //adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        //Solicitar atualizações de localização
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    0,
                    0,
                    locationListener
            );
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menuSair :
                autenticacao.signOut();
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void abrirTelaCorrida(String idRequisicao, Usuario motorista, boolean requisicaoAtiva){
        Intent i = new Intent(RequisicoesActivity.this, CorridaActivity.class );
        i.putExtra("idRequisicao", idRequisicao );
        i.putExtra("motorista", motorista );
        i.putExtra("requisicaoAtiva", requisicaoAtiva );
        startActivity( i );
    }

    private void inicializarComponentes(){

        getSupportActionBar().setTitle("Requisições");

        //Configura componentes
        recyclerRequisicoes = findViewById(R.id.recyclerRequisicoes);
        textResultado = findViewById(R.id.textResultado);

        //Configurações iniciais
        motorista = UsuarioFirebase.getDadosUsuarioLogado();
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();

        //Configurar RecyclerView
        adapter = new RequisicoesAdapter(listaRequisicoes, getApplicationContext(), motorista );
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerRequisicoes.setLayoutManager( layoutManager );
        recyclerRequisicoes.setHasFixedSize(true);
        recyclerRequisicoes.setAdapter( adapter );

        recuperarRequisicoes();

    }

    private void adicionaEventoCliqueRecyclerView(){

        //Adiciona evento de clique no recycler
        recyclerRequisicoes.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getApplicationContext(),
                        recyclerRequisicoes,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Requisicao requisicao = listaRequisicoes.get(position);
                                abrirTelaCorrida(requisicao.getId(), motorista, false);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )
        );

    }

    private void recuperarRequisicoes(){
        //DatabaseReference requisicoes = firebaseRef.child("requisicoes");
        DatabaseReference requisicoes = firebaseRef;

        //Query requisicaoPesquisa = requisicoes.orderByChild("status").equalTo(Requisicao.STATUS_AGUARDANDO);

        requisicoes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("requisicoes").exists()){
                    listaRequisicoes.clear();
                    for ( final DataSnapshot ds: dataSnapshot.child("requisicoes").getChildren() ){
                        final Requisicao requisicao = ds.getValue( Requisicao.class );

                        if(dataSnapshot.exists()){
                            DataSnapshot dados = dataSnapshot.child("requisicoes").child(requisicao.getId()).child("destino");
                            final int novo_comprimento = Integer.parseInt(dados.child("comprimento").getValue().toString());
                            final int novo_largura = Integer.parseInt(dados.child("largura").getValue().toString());
                            final int novo_altura = Integer.parseInt(dados.child("altura").getValue().toString());
                            final int novo_peso = Integer.parseInt(dados.child("peso").getValue().toString());
                            String tipo = dados.child("tipo").getValue().toString();

                            switch (tipo){
                                case "grande":
                                    if(novo_comprimento <= 630 && novo_largura <= 220 && novo_altura <= 350 && novo_peso <= 3000){
                                        listaRequisicoes.add( requisicao );
                                        textResultado.setVisibility( View.GONE );
                                        recyclerRequisicoes.setVisibility( View.VISIBLE );
                                    } else {
                                        textResultado.setVisibility( View.VISIBLE );
                                        recyclerRequisicoes.setVisibility( View.GONE );
                                    }
                                    break;
                                case "medio":
                                    if(novo_comprimento <= 260 && novo_largura <= 160 && novo_altura <= 200 && novo_peso <= 1600){
                                        listaRequisicoes.add( requisicao );
                                        textResultado.setVisibility( View.GONE );
                                        recyclerRequisicoes.setVisibility( View.VISIBLE );
                                    } else {
                                        textResultado.setVisibility( View.VISIBLE );
                                        recyclerRequisicoes.setVisibility( View.GONE );
                                    }
                                    break;
                                case "simples":
                                    if(novo_comprimento <= 120 && novo_largura <= 120 && novo_altura <= 180 && novo_peso <= 720){
                                        listaRequisicoes.add( requisicao );
                                        textResultado.setVisibility( View.GONE );
                                        recyclerRequisicoes.setVisibility( View.VISIBLE );
                                    } else {
                                        textResultado.setVisibility( View.VISIBLE );
                                        recyclerRequisicoes.setVisibility( View.GONE );
                                    }
                                    break;
                                case "veiculo ideal":
                                    UsuarioFirebase usuarioAtual = new UsuarioFirebase();
                                    DataSnapshot usuariosRef = dataSnapshot.child("usuarios").child(usuarioAtual.getCelularUsuario());
                                    verificarMedidas(usuariosRef, novo_comprimento, novo_largura, novo_altura, novo_peso, requisicao);
                                    break;
                            }
                        }else{
                            Toast.makeText(RequisicoesActivity.this, "Dados incorretos", Toast.LENGTH_SHORT).show();
                        }

                        System.out.println(listaRequisicoes);
                    }

                    adapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(RequisicoesActivity.this, "Dados incorretos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public static FirebaseUser getUsuarioAtual(){
        FirebaseAuth usuario = ConfiguracaoFirebase.getFirebaseAutenticacao();
        return usuario.getCurrentUser();
    }

    public void verificarMedidas(final DataSnapshot dataSnapshot, final int comprimento, final int largura, final int altura, final int peso, final Requisicao requisicao){
        DataSnapshot valores = dataSnapshot;
        int comprimento_int = Integer.parseInt(valores.child("veiculo").child("comprimento").getValue().toString());
        int largura_int = Integer.parseInt(valores.child("veiculo").child("largura").getValue().toString());
        int altura_int = Integer.parseInt(valores.child("veiculo").child("altura").getValue().toString());
        int peso_int = Integer.parseInt(valores.child("veiculo").child("peso").getValue().toString());

        if(comprimento <= comprimento_int && largura <= largura_int && altura <= altura_int && peso <= peso_int) {
            listaRequisicoes.add( requisicao );
            textResultado.setVisibility( View.GONE );
            recyclerRequisicoes.setVisibility( View.VISIBLE );
            System.out.println("Sinalizador 1");
            System.out.println(listaRequisicoes);
        } else {
            textResultado.setVisibility( View.VISIBLE );
            recyclerRequisicoes.setVisibility( View.GONE );
            System.out.println("Sinalizador 2");
        }
    }
}