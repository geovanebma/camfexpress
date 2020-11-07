package com.projeto.camfexpress.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.projeto.camfexpress.config.LocalidadeMetragem;
import com.projeto.camfexpress.config.MedidasCarreto;
import com.projeto.camfexpress.R;
import com.projeto.camfexpress.config.ConfiguracaoFirebase;
import com.projeto.camfexpress.config.UsuarioFirebase;
import com.projeto.camfexpress.config.Destino;
import com.projeto.camfexpress.config.Requisicao;
import com.projeto.camfexpress.config.Usuario;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ClienteActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    /*
    * Lat/lon destino:-23.556407, -46.662365 (Av. Paulista, 2439)
    * Lat/lon cliente: -23.562791, -46.654668
    * Lat/lon Motorista (a caminho):
    *   longe: -23.571139, -46.660936
    *   inicial: -23.563196, -46.650607
    *   intermediaria: -23.564801, -46.652196
    *   final: -23.562801, -46.654660
    * Encerramento intermediário: -23.557499, -46.661084
    * Encerramento da corrida: -23.556439, -46.662313
    * */

    //Componentes
    private EditText editDestino;
    private LinearLayout linearLayoutDestino;
    private Button buttonChamarCamf;

    private GoogleMap mMap;
    private FirebaseAuth autenticacao;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private LatLng localCliente;
    private LatLng localDestino;
    private boolean cancelarCamf = false;
    private DatabaseReference firebaseRef;
    private Requisicao requisicao;
    private Usuario cliente;
    private String statusRequisicao;
    private Destino destino;
    private Marker marcadorCarreto;
    private Marker marcadorCliente;
    private Marker marcadorDestino;
    private Usuario carreto;
    private LatLng localCarreto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente);

        Toolbar toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Iniciar uma viagem");
        setSupportActionBar(toolbar);

        //Inicializar componentes
        editDestino = findViewById(R.id.editDestino);
        linearLayoutDestino = findViewById(R.id.linearLayoutDestino);
        buttonChamarCamf = findViewById(R.id.buttonChamarCamf);

        //Configurações iniciais
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        verificarRequisicao();
    }

    //Verifica se o usuário atual está com alguma requisição em andamento
    private void verificarRequisicao(){
        Usuario usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        DatabaseReference requisicoes = firebaseRef.child("requisicoes");
        System.out.println(usuarioLogado.getCelular());
        Query requisicaoPesquisa = requisicoes.equalTo( usuarioLogado.getCelular() );

        requisicoes.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Requisicao> lista = new ArrayList<>();
                for( DataSnapshot ds: dataSnapshot.getChildren() ){
                    lista.add( ds.getValue( Requisicao.class ) );
                }

                Collections.reverse(lista);
                if( lista!= null && lista.size()>0 ){
                    requisicao = lista.get(0);

                    if(requisicao != null){
                        if( !requisicao.getStatus().equals(Requisicao.STATUS_ENCERRADA) ) {
                            cliente = requisicao.getCliente();
                            localCliente = new LatLng(
                                    Double.parseDouble(cliente.getLatitude()),
                                    Double.parseDouble(cliente.getLongitude())
                            );

                            statusRequisicao = requisicao.getStatus();
                            destino = requisicao.getDestino();

                            if (requisicao.getCarreto() != null) {
                                carreto = requisicao.getCarreto();
                                localCarreto = new LatLng(
                                    Double.parseDouble(carreto.getLatitude()),
                                    Double.parseDouble(carreto.getLongitude())
                                );
                            }
                            alterarStatusDaRequisicao(statusRequisicao);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //Altera em que ponto da viagem está, se está aguardando algum carreto confirmar, ou se o carreto já está a caminho do cliente, se ambos estão em viagem...
    private void alterarStatusDaRequisicao(String status){
        if(status != null && !status.isEmpty()) {
            cancelarCamf = false;
            switch (status) {
                case Requisicao.STATUS_AGUARDANDO:
                    requisicaoAguardando();
                    break;
                case Requisicao.STATUS_A_CAMINHO:
                    requisicaoACaminho();
                    break;
                case Requisicao.STATUS_VIAGEM:
                    requisicaoViagem();
                    break;
                case Requisicao.STATUS_FINALIZADA:
                    requisicaoFinalizada();
                    break;
                case Requisicao.STATUS_CANCELADA:
                    requisicaoCancelada();
                    break;
            }
        }else {
            //Adiciona marcador cliente
            adicionaMarcadorCliente(localCliente, "Seu local");
            centralizarMarcador(localCliente);
        }
    }

    //Faz cancelar a viagem
    private void requisicaoCancelada(){
        linearLayoutDestino.setVisibility( View.VISIBLE );
        buttonChamarCamf.setText("Chamar Camf");
        cancelarCamf = false;
    }

    //Aguardando enquanto algum carreto aceita
    private void requisicaoAguardando(){
        linearLayoutDestino.setVisibility( View.GONE );
        buttonChamarCamf.setText("Cancelar Camf");
        cancelarCamf = true;

        //Adiciona marcador cliente
        adicionaMarcadorCliente(localCliente, cliente.getNome());
        centralizarMarcador(localCliente);
    }

    //Muda e mostra quando um carreto está a caminho do cliente
    private void requisicaoACaminho(){
        linearLayoutDestino.setVisibility( View.GONE );
        buttonChamarCamf.setText("Motorista a caminho");
        buttonChamarCamf.setEnabled(false);

        //Adiciona marcador cliente
        adicionaMarcadorCliente(localCliente, cliente.getNome());

        //Adiciona marcador carreto
        adicionaMarcadorCarreto(localCarreto, carreto.getNome());

        //Centralizar cliente / carreto
        centralizaOsMarcadores(marcadorCarreto, marcadorCliente);

    }

    //Muda a requisição para o modo de viagem ao destino
    private void requisicaoViagem(){
        linearLayoutDestino.setVisibility( View.GONE );
        buttonChamarCamf.setText("A caminho do destino");
        buttonChamarCamf.setEnabled(false);

        //Adiciona marcador carreto
        adicionaMarcadorCarreto(localCarreto, carreto.getNome());

        //Adiciona marcador de destino
        LatLng localDestino = new LatLng(
                Double.parseDouble(destino.getLatitude()),
                Double.parseDouble(destino.getLongitude())
        );
        adicionaMarcadorDestino(localDestino, "Destino");

        //Centraliza marcadores carreto / destino
        centralizaOsMarcadores(marcadorCarreto, marcadorDestino);
    }

    private void requisicaoFinalizada(){
        linearLayoutDestino.setVisibility( View.GONE );
        buttonChamarCamf.setEnabled(false);

        //Adiciona marcador de destino
        LatLng localDestino = new LatLng(
            Double.parseDouble(destino.getLatitude()),
            Double.parseDouble(destino.getLongitude())
        );

        adicionaMarcadorDestino(localDestino, "Destino");
        centralizarMarcador(localDestino);

        //Calcular distancia e valor
        String porte = getIntent().getStringExtra("porte");
        porte = (porte.equals(""))?"veiculo ideal":porte;
        double distancia = LocalidadeMetragem.calcularDistancia(localCliente, localDestino);
        double tarifa_base = 0;
        double custo_por_km = 1.5;

        if(porte.equals("grande")){
            tarifa_base = distancia*10;
        } else if(porte.equals("medio")){
            tarifa_base = distancia*8;
        } else if(porte.equals("simples")){
            tarifa_base = distancia*6;
        } else if(porte.equals("veiculo ideal")){
            tarifa_base = distancia*7;
        }

        double valor = tarifa_base * custo_por_km;
        DecimalFormat decimal = new DecimalFormat("0.00");
        String resultado = decimal.format(valor);

        buttonChamarCamf.setText("Corrida finalizada - R$ " + resultado);

        AlertDialog.Builder builder = new AlertDialog.Builder(ClienteActivity.this)
                .setTitle("Total da viagem")
                .setMessage("Sua viagem ficou: R$ " + resultado)
                .setCancelable(false)
                .setNegativeButton("Encerrar viagem", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    requisicao.setStatus(Requisicao.STATUS_ENCERRADA);
                    requisicao.atualizarStatus();

                    finish();
                    startActivity(new Intent(getIntent()));
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
        Button nbutton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setTextColor(Color.BLUE);
        Button pbutton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(Color.BLUE);

    }

    //Adiciona a marcação do cliente
    private void adicionaMarcadorCliente(LatLng localizacao, String titulo){
        if( marcadorCliente != null )
            marcadorCliente.remove();

        marcadorCliente = mMap.addMarker(
            new MarkerOptions()
                .position(localizacao)
                .title(titulo)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.usuario))
        );
    }

    //Adiciona a marcação do carreto
    private void adicionaMarcadorCarreto(LatLng localizacao, String titulo){
        if( marcadorCarreto != null )
            marcadorCarreto.remove();

        marcadorCarreto = mMap.addMarker(
                new MarkerOptions()
                        .position(localizacao)
                        .title(titulo)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.carro))
        );
    }

    //Adiciona a marcação do destino que devem ir
    private void adicionaMarcadorDestino(LatLng localizacao, String titulo){
        if( marcadorCliente != null )
            marcadorCliente.remove();

        if( marcadorDestino != null )
            marcadorDestino.remove();

        marcadorDestino = mMap.addMarker(
                new MarkerOptions()
                        .position(localizacao)
                        .title(titulo)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.destino))
        );
    }

    //Centraliza a tela de mapa corretamente
    private void centralizarMarcador(LatLng local){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(local, 20));
    }

    //Centraliza os marcadores de posicionamento em suas devidas posições
    private void centralizaOsMarcadores(Marker marcador1, Marker marcador2){
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        builder.include( marcador1.getPosition() );
        builder.include( marcador2.getPosition() );

        LatLngBounds bounds = builder.build();

        int largura = getResources().getDisplayMetrics().widthPixels;
        int altura = getResources().getDisplayMetrics().heightPixels;
        int espacoInterno = (int) (largura * 0.20);

        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds,largura,altura,espacoInterno));
    }

    //Inicializar API do Mapas
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        recuperarLocalizacaoUsuario();
    }

    //Botão para chamar o carreto
    public void chamarCamf(View view){
        if( cancelarCamf ){
            //Cancelar a requisição
            requisicao.setStatus(Requisicao.STATUS_CANCELADA);
            requisicao.atualizarStatus();
        } else {
            String enderecoDestino = editDestino.getText().toString();
            if( !enderecoDestino.equals("") || enderecoDestino != null ){
                Address addressDestino = recuperarEndereco( enderecoDestino );
                if( addressDestino != null ){
                    verificarCarreto(addressDestino);
                }
            } else {
                Toast.makeText(this,"Informe o endereço de destino!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Método para saber as dimensões do veículo para ver se ele bate corretamente com a descrição informada - Lógica Paraconsistente
    public void verificarCarreto(final Address addressDestino){
        String porte = getIntent().getStringExtra("porte");
        if(porte == null){
            FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
            FirebaseUser usuario_atual = autenticacao.getCurrentUser();
            DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
            DatabaseReference usuarios = firebaseRef.child("usuarios").child(usuario_atual.getPhoneNumber());

            DatabaseReference valor_porte = usuarios.child("carga");
            valor_porte.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String porte_novo = dataSnapshot.child("tipo").getValue().toString();

                    String comprimentoMaximo = "";
                    String larguraMaximo = "";
                    String alturaMaximo = "";
                    String pesoMaximo = "";

                    switch (porte_novo){
                        case "grande":
                            comprimentoMaximo = "630";
                            larguraMaximo = "220";
                            alturaMaximo = "350";
                            pesoMaximo = "3000";
                            break;

                        case "medio":
                            comprimentoMaximo = "260";
                            larguraMaximo = "160";
                            alturaMaximo = "200";
                            pesoMaximo = "1600";
                            break;

                        case "simples":
                            comprimentoMaximo = "120";
                            larguraMaximo = "120";
                            alturaMaximo = "180";
                            pesoMaximo = "720";
                            break;

                        case "veiculo ideal":
                            comprimentoMaximo = dataSnapshot.child("comprimento").getValue().toString();
                            larguraMaximo = dataSnapshot.child("largura").getValue().toString();
                            alturaMaximo = dataSnapshot.child("altura").getValue().toString();
                            pesoMaximo = dataSnapshot.child("peso").getValue().toString();
                            break;

                        default:
                            comprimentoMaximo = "0";
                            larguraMaximo = "0";
                            alturaMaximo = "0";
                            pesoMaximo = "0";
                    }

                    MedidasCarreto medidas = new MedidasCarreto();
                    medidas.setComprimento(comprimentoMaximo);
                    medidas.setLargura(larguraMaximo);
                    medidas.setAltura(alturaMaximo);
                    medidas.setPeso(pesoMaximo);
                    medidas.setTipo(porte_novo);

                    if(comprimentoMaximo.equals("0") && larguraMaximo.equals("0") && alturaMaximo.equals("0") && pesoMaximo.equals("0")){
                        Toast.makeText(ClienteActivity.this, "No momento, não foram encontrados carretos adequados para essa carga.", Toast.LENGTH_SHORT).show();
                    }else{
                        final Destino destino = new Destino();

                        destino.setCidade( addressDestino.getAdminArea() );
                        destino.setCep( addressDestino.getPostalCode() );
                        destino.setBairro( addressDestino.getSubLocality() );
                        destino.setRua( addressDestino.getThoroughfare() );
                        destino.setNumero( addressDestino.getFeatureName() );
                        destino.setLatitude( String.valueOf(addressDestino.getLatitude()) );
                        destino.setLongitude( String.valueOf(addressDestino.getLongitude()) );

                        destino.setComprimento(medidas.getComprimento());
                        destino.setLargura(medidas.getLargura());
                        destino.setAltura(medidas.getAltura());
                        destino.setPeso(medidas.getPeso());
                        destino.setTipo(medidas.getTipo());

                        destino.setSeguro(getIntent().getStringExtra("seguro"));
                        destino.setAjudante(getIntent().getStringExtra("ajudante"));
                        System.out.println("Olha só: "+getIntent().getStringExtra("ajudante")+" "+getIntent().getStringExtra("seguro"));

                        LatLng localDestino = new LatLng(
                                Double.parseDouble(destino.getLatitude()),
                                Double.parseDouble(destino.getLongitude())
                        );

                        //Calculo do valor
                        double distancia = LocalidadeMetragem.calcularDistancia(localCliente, localDestino);
                        double tarifa_base = 0;
                        double custo_por_km = 1.5;

                        if(porte_novo.equals("grande")){
                            tarifa_base = distancia*10;
                        } else if(porte_novo.equals("medio")){
                            tarifa_base = distancia*8;
                        } else if(porte_novo.equals("simples")){
                            tarifa_base = distancia*6;
                        } else if(porte_novo.equals("veiculo ideal")){
                            tarifa_base = distancia*7;
                        }

                        double valor = tarifa_base * custo_por_km;
                        DecimalFormat decimal = new DecimalFormat("0.00");
                        String resultado = decimal.format(valor);

                        destino.setValor(resultado);

                        StringBuilder mensagem = new StringBuilder();
                        mensagem.append( "Cidade: " + destino.getCidade() );
                        mensagem.append( "\nRua: " + destino.getRua() );
                        mensagem.append( "\nBairro: " + destino.getBairro() );
                        mensagem.append( "\nNúmero: " + destino.getNumero() );
                        mensagem.append( "\nCep: " + destino.getCep() );
                        mensagem.append( "\nValor Total: " + destino.getValor());

                        AlertDialog.Builder builder = new AlertDialog.Builder(ClienteActivity.this)
                                .setTitle("Confirme seu endereco!")
                                .setMessage(mensagem)
                                .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        //salvar requisição
                                        salvarRequisicao( destino );

                                    }
                                }).setNegativeButton("cancelar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        Button nbutton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                        nbutton.setTextColor(Color.BLUE);
                        Button pbutton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                        pbutton.setTextColor(Color.BLUE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else{
            String comprimentoMaximo = "";
            String larguraMaximo = "";
            String alturaMaximo = "";
            String pesoMaximo = "";

            switch (porte){
                case "grande":
                    comprimentoMaximo = "630";
                    larguraMaximo = "220";
                    alturaMaximo = "350";
                    pesoMaximo = "3000";
                    break;

                case "medio":
                    comprimentoMaximo = "260";
                    larguraMaximo = "160";
                    alturaMaximo = "200";
                    pesoMaximo = "1600";
                    break;

                case "simples":
                    comprimentoMaximo = "120";
                    larguraMaximo = "120";
                    alturaMaximo = "180";
                    pesoMaximo = "720";
                    break;

                case "veiculo ideal":
                    comprimentoMaximo = getIntent().getStringExtra("medidaComprimento");
                    larguraMaximo = getIntent().getStringExtra("medidaLargura");
                    alturaMaximo = getIntent().getStringExtra("medidaAltura");
                    pesoMaximo = getIntent().getStringExtra("medidaPeso");
                    break;

                default:
                    comprimentoMaximo = "0";
                    larguraMaximo = "0";
                    alturaMaximo = "0";
                    pesoMaximo = "0";
            }

            MedidasCarreto medidas = new MedidasCarreto();
            medidas.setComprimento(comprimentoMaximo);
            medidas.setLargura(larguraMaximo);
            medidas.setAltura(alturaMaximo);
            medidas.setPeso(pesoMaximo);
            medidas.setTipo(porte);

            if(medidas.getComprimento().equals("0") && medidas.getLargura().equals("0") && medidas.getAltura().equals("0") && medidas.getPeso().equals("0")){
                Toast.makeText(ClienteActivity.this, "No momento, não foram encontrados carretos adequados para essa carga.", Toast.LENGTH_SHORT).show();
            }else{
                final Destino destino = new Destino();

                destino.setCidade( addressDestino.getAdminArea() );
                destino.setCep( addressDestino.getPostalCode() );
                destino.setBairro( addressDestino.getSubLocality() );
                destino.setRua( addressDestino.getThoroughfare() );
                destino.setNumero( addressDestino.getFeatureName() );
                destino.setLatitude( String.valueOf(addressDestino.getLatitude()) );
                destino.setLongitude( String.valueOf(addressDestino.getLongitude()) );

                destino.setComprimento(medidas.getComprimento());
                destino.setLargura(medidas.getLargura());
                destino.setAltura(medidas.getAltura());
                destino.setPeso(medidas.getPeso());
                destino.setTipo(medidas.getTipo());

                destino.setSeguro(getIntent().getStringExtra("seguro"));
                destino.setAjudante(getIntent().getStringExtra("ajudante"));

                LatLng localDestino = new LatLng(
                        Double.parseDouble(destino.getLatitude()),
                        Double.parseDouble(destino.getLongitude())
                );

                //Calculo do valor

                double distancia = LocalidadeMetragem.calcularDistancia(localCliente, localDestino);
                double tarifa_base = 0;
                double custo_por_km = 1.5;

                if(porte.equals("grande")){
                    tarifa_base = distancia*10;
                } else if(porte.equals("medio")){
                    tarifa_base = distancia*8;
                } else if(porte.equals("simples")){
                    tarifa_base = distancia*6;
                } else if(porte.equals("veiculo ideal")){
                    tarifa_base = distancia*7;
                }

                double valor = tarifa_base * custo_por_km;
                DecimalFormat decimal = new DecimalFormat("0.00");
                String resultado = decimal.format(valor);

                destino.setValor(resultado);

                StringBuilder mensagem = new StringBuilder();
                mensagem.append( "Cidade: " + destino.getCidade() );
                mensagem.append( "\nRua: " + destino.getRua() );
                mensagem.append( "\nBairro: " + destino.getBairro() );
                mensagem.append( "\nNúmero: " + destino.getNumero() );
                mensagem.append( "\nCep: " + destino.getCep() );
                mensagem.append( "\nValor Total: " + destino.getValor());

                AlertDialog.Builder builder = new AlertDialog.Builder(ClienteActivity.this)
                        .setTitle("Confirme seu endereco!")
                        .setMessage(mensagem)
                        .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //salvar requisição
                                salvarRequisicao( destino );

                            }
                        }).setNegativeButton("cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
                Button nbutton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                nbutton.setTextColor(Color.BLUE);
                Button pbutton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                pbutton.setTextColor(Color.BLUE);
            }
        }
    }

    //Salva os dados da requisição no firebase
    private void salvarRequisicao(Destino destino){
        Requisicao requisicao = new Requisicao();
        requisicao.setDestino( destino );

        Usuario usuarioCliente = UsuarioFirebase.getDadosUsuarioLogado();
        usuarioCliente.setLatitude( String.valueOf( localCliente.latitude ) );
        usuarioCliente.setLongitude( String.valueOf( localCliente.longitude ) );

        requisicao.setCliente( usuarioCliente );
        requisicao.setStatus( Requisicao.STATUS_AGUARDANDO );
        requisicao.salvar();

        linearLayoutDestino.setVisibility( View.GONE );
        buttonChamarCamf.setText("Cancelar CAMF");
    }

    //Recupera o endereço anunciado
    private Address recuperarEndereco(String endereco){
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> listaEnderecos = geocoder.getFromLocationName(endereco, 1);
            if( listaEnderecos != null && listaEnderecos.size() > 0 ){
                Address address = listaEnderecos.get(0);

                return address;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    //Recupera a localização do usuário
    private void recuperarLocalizacaoUsuario() {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //recuperar latitude e longitude
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                localCliente = new LatLng(latitude, longitude);

                //Atualizar GeoFire
                UsuarioFirebase.atualizarDadosLocalizacao(latitude, longitude);

                //Altera interface de acordo com o status
                alterarStatusDaRequisicao( statusRequisicao );

                if(statusRequisicao != null && !statusRequisicao.isEmpty()) {
                    if (statusRequisicao.equals(Requisicao.STATUS_VIAGEM)
                            || statusRequisicao.equals(Requisicao.STATUS_FINALIZADA)) {
                        locationManager.removeUpdates(locationListener);
                    }else {
                        //Solicitar atualizações de localização
                        if (ActivityCompat.checkSelfPermission(ClienteActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
                            locationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    10000,
                                    10,
                                    locationListener
                            );
                        }
                    }
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
                    10000,
                    10,
                    locationListener
            );
        }
    }

    //Configura o menu de opções
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //Caso o menu sair seja pressionado, sai para a tela inicial
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
}
