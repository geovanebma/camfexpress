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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.projeto.camfexpress.R;
import com.projeto.camfexpress.adpter.RequisicoesAdapter;
import com.projeto.camfexpress.config.ConfiguracaoFirebase;
import com.projeto.camfexpress.config.RecyclerItemClickListener;
import com.projeto.camfexpress.config.UsuarioFirebase;
import com.projeto.camfexpress.config.Requisicao;
import com.projeto.camfexpress.config.Usuario;

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
    private Usuario carreto;

    private LocationManager locationManager;
    private LocationListener locationListener;
    public int temCondicoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requisicoes);

        getSupportActionBar().setTitle("Requisições");

        //Configura componentes
        recyclerRequisicoes = findViewById(R.id.recyclerRequisicoes);
        textResultado = findViewById(R.id.textResultado);

        //Configurações iniciais
        carreto = UsuarioFirebase.getDadosUsuarioLogado();
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();

        //Configurar RecyclerView
        adapter = new RequisicoesAdapter(listaRequisicoes, getApplicationContext(), carreto );
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerRequisicoes.setLayoutManager( layoutManager );
        recyclerRequisicoes.setHasFixedSize(true);
        recyclerRequisicoes.setAdapter( adapter );

        recuperarRequisicoes();
        recuperarLocalizacaoUsuario();
    }

    @Override
    protected void onStart() {
        super.onStart();
        verificaStatusRequisicao();
    }

    //Verifica em qual status da requisição se encontra no momento
    private void verificaStatusRequisicao(){
        Usuario usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference requisicoes = firebaseRef.child("requisicoes");
        Query requisicoesPesquisa = requisicoes.orderByChild("carreto/id").equalTo( usuarioLogado.getId() );

        requisicoesPesquisa.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for( DataSnapshot ds: dataSnapshot.getChildren() ){

                    Requisicao requisicao = ds.getValue( Requisicao.class );

                    if( requisicao.getStatus().equals(Requisicao.STATUS_A_CAMINHO)
                            || requisicao.getStatus().equals(Requisicao.STATUS_VIAGEM)
                            || requisicao.getStatus().equals(Requisicao.STATUS_FINALIZADA)){
                        carreto = requisicao.getCarreto();
                        String tipo = requisicao.getDestino().getTipo();
                        abrirTelaCorrida(requisicao.getId(), carreto, true, tipo);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //Verifica e recupera a localização do usuário
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
                    carreto.setLatitude(latitude);
                    carreto.setLongitude(longitude);

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

    //Configura o botão de sair
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //Cria a ação do botão sair
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

    //Ao confirmar, é aberto a tela de corrida com o mapa
    private void abrirTelaCorrida(String idRequisicao, Usuario carreto, boolean requisicaoAtiva, String tipo){
        Intent i = new Intent(RequisicoesActivity.this, CorridaActivity.class );
        i.putExtra("idRequisicao", idRequisicao );
        i.putExtra("carreto", carreto );
        i.putExtra("requisicaoAtiva", requisicaoAtiva );
        i.putExtra("porte", tipo );
        startActivity( i );
    }

    //Evento ao clicar em alguma das requisições
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
                                String tipo = requisicao.getDestino().getTipo();
                                abrirTelaCorrida(requisicao.getId(), carreto, true, tipo);
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

    //Verifica se tem alguma requisição, se houver, coloca na tela e verifica qual foi o porte de veículo escolhido pelo cliente, se caber com as descrições do carreto atual, essas requisições aparecerão na tela dele
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
                                default:
                                    Toast.makeText(RequisicoesActivity.this, "No momento, não temos um veículo adequado para transportar sua carga.", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            //Toast.makeText(RequisicoesActivity.this, "Dados incorretos", Toast.LENGTH_SHORT).show();
                        }
                    }

                    adapter.notifyDataSetChanged();
                }else{
                    //Toast.makeText(RequisicoesActivity.this, "Dados incorretos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    //Lógica Paraconsistente para verificar o tamanho do veículo se é compatível
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
        } else {
            textResultado.setVisibility( View.VISIBLE );
            recyclerRequisicoes.setVisibility( View.GONE );
        }
    }
}