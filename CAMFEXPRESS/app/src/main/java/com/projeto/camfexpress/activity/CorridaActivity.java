package com.projeto.camfexpress.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.projeto.camfexpress.R;
import com.projeto.camfexpress.config.ConfiguracaoFirebase;
import com.projeto.camfexpress.config.LocalidadeMetragem;
import com.projeto.camfexpress.config.UsuarioFirebase;
import com.projeto.camfexpress.config.Destino;
import com.projeto.camfexpress.config.Requisicao;
import com.projeto.camfexpress.config.Usuario;

import java.text.DecimalFormat;

public class CorridaActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    //componente
    private Button buttonAceitarCorrida;
    private FloatingActionButton fabRota;

    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private LatLng localCarreto;
    private LatLng localCliente;
    private Usuario carreto;
    private Usuario cliente;
    private String idRequisicao;
    private Requisicao requisicao;
    private DatabaseReference firebaseRef;
    private Marker marcadorCarreto;
    private Marker marcadorCliente;
    private Marker marcadorDestino;
    private String statusRequisicao;
    private boolean requisicaoAtiva;
    private Destino destino;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_corrida);

        Toolbar toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Iniciar corrida");

        buttonAceitarCorrida = findViewById(R.id.buttonAceitarCorrida);

        //Configurações com o firebase
        firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();

        //Componente de inicialização do mapas
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Adiciona evento de clique no FabRota
        fabRota = findViewById(R.id.fabRota);
        fabRota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Verifica em que situação se encontra, se está a caminho, ou se está em viagem
                String status = statusRequisicao;
                if( status != null && !status.isEmpty() ){

                    String lat = "";
                    String lon = "";

                    switch ( status ){
                        case Requisicao.STATUS_A_CAMINHO :
                            lat = String.valueOf(localCliente.latitude);
                            lon = String.valueOf(localCliente.longitude);
                            break;
                        case Requisicao.STATUS_VIAGEM :
                            lat = destino.getLatitude();
                            lon = destino.getLongitude();
                            break;
                    }

                    //Abrir rota
                    String latLong = lat + "," + lon;
                    Uri uri = Uri.parse("google.navigation:q="+latLong+"&mode=d");
                    Intent i = new Intent(Intent.ACTION_VIEW, uri);
                    i.setPackage("com.google.android.apps.maps");
                    startActivity(i);
                }
            }
        });

        //Recupera dados do usuário
        if( getIntent().getExtras().containsKey("idRequisicao") && getIntent().getExtras().containsKey("carreto") ){
            Bundle extras = getIntent().getExtras();
            carreto = (Usuario) extras.getSerializable("carreto");
            localCarreto = new LatLng(
                    Double.parseDouble(carreto.getLatitude()),
                    Double.parseDouble(carreto.getLongitude())
            );

            idRequisicao = extras.getString("idRequisicao");
            requisicaoAtiva = extras.getBoolean("requisicaoAtiva");
            verificarRequisicao();
        }
    }

    //Busca e verifica a determinada requisição e coloca os dados do cliente
    private void verificarRequisicao(){
        DatabaseReference requisicoes = firebaseRef.child("requisicoes").child( idRequisicao );
        requisicoes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                requisicao = dataSnapshot.getValue(Requisicao.class);

                if(requisicao != null){
                    cliente = requisicao.getCliente();
                    localCliente = new LatLng(
                        Double.parseDouble(cliente.getLatitude()),
                        Double.parseDouble(cliente.getLongitude())
                    );

                    statusRequisicao = requisicao.getStatus();
                    destino = requisicao.getDestino();
                    alterarStatusDaRequisicao(statusRequisicao);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(CorridaActivity.this, "Erro de conexão, por favor, aguarde em instantes.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Altera em que ponto da viagem está, se está aguardando algum carreto confirmar, ou se o carreto já está a caminho do cliente, se ambos estão em viagem...
    private void alterarStatusDaRequisicao(String status){
        switch ( status ){
            case Requisicao.STATUS_AGUARDANDO :
                requisicaoAguardando();
                break;
            case Requisicao.STATUS_A_CAMINHO :
                requisicaoACaminho();
                break;
            case Requisicao.STATUS_VIAGEM :
                requisicaoViagem();
                break;
            case Requisicao.STATUS_FINALIZADA :
                requisicaoFinalizada();
                break;
            case Requisicao.STATUS_CANCELADA :
                requisicaoCancelada();
                break;
        }
    }

    //Caso o cliente cancele a requisição antes do carreto confirmar, o carreto é redirecionado de volta para a tela de requisições
    private void requisicaoCancelada(){
        Toast.makeText(this, "A requisição selecionada foi cancelada pelo cliente!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(CorridaActivity.this, RequisicoesActivity.class));
    }

    //Caso tenha finalizado a viagem, o GPS é fechado a requisição é finalizada e calcula o valor total da corrida a ser pago.
    private void requisicaoFinalizada(){
        fabRota.setVisibility(View.GONE);
        requisicaoAtiva = false;

        if( marcadorCarreto != null )
            marcadorCarreto.remove();

        if( marcadorDestino != null )
            marcadorDestino.remove();

        //Exibe marcador de destino
        LatLng localDestino = new LatLng(
            Double.parseDouble(destino.getLatitude()),
            Double.parseDouble(destino.getLongitude())
        );
        adicionaMarcadorDestino(localDestino, "Destino");
        centralizarMarcador(localDestino);

        //Calcular valor total
        String porte = getIntent().getStringExtra("porte");
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

        buttonAceitarCorrida.setText("Corrida finalizada - R$ " + resultado);
    }

    //Faz a tela de mapa sempre se ajustar no centro enquanto se locomover
    private void centralizarMarcador(LatLng local){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(local, 20));
    }

    //Caso a requisição esteja em aguardo, o botão na tela é configurado para aceitar a corrida
    private void requisicaoAguardando(){
        buttonAceitarCorrida.setText("Aceitar corrida");

        //Exibe marcador do carreto
        adicionaMarcadorCarreto(localCarreto, carreto.getNome());
        centralizarMarcador(localCarreto);
    }

    //Quando o carreto aceitar a corrida, o botão muda seu texto, e o posicionamento dos marcadores do cliente e carreto são ativados
    private void requisicaoACaminho(){
        buttonAceitarCorrida.setText("A caminho do cliente");
        fabRota.setVisibility(View.VISIBLE);

        //Exibe marcador do carreto
        adicionaMarcadorCarreto(localCarreto, carreto.getNome() );

        //Exibe marcador cliente
        adicionaMarcadorCliente(localCliente, cliente.getNome());

        //Centralizar dois marcadores
        centralizaOsMarcadores(marcadorCarreto, marcadorCliente);

        //Inicia monitoramento do carreto / cliente
        iniciarMonitoramento(carreto, localCliente, Requisicao.STATUS_VIAGEM );
    }

    //Quando o carreto chega ao local em que o cliente está, automáticamente já muda as rotas para onde eles devem ir, seu destino
    private void requisicaoViagem(){
        //Altera interface
        fabRota.setVisibility(View.VISIBLE);
        buttonAceitarCorrida.setText("A caminho do destino");

        //Exibe marcador do carreto
        adicionaMarcadorCarreto(localCarreto, carreto.getNome());

        //Exibe marcador de destino
        LatLng localDestino = new LatLng(
                Double.parseDouble(destino.getLatitude()),
                Double.parseDouble(destino.getLongitude())
        );
        adicionaMarcadorDestino(localDestino, "Destino");

        //Centraliza marcadores carreto / destino
        centralizaOsMarcadores(marcadorCarreto, marcadorDestino);

        //Inicia monitoramento do carreto / cliente
        iniciarMonitoramento(carreto, localDestino, Requisicao.STATUS_FINALIZADA );
    }

    //Esse método tem a funcionalidade de monitorar cada vez que concluir uma etapa e automatizar as rotas
    private void iniciarMonitoramento(final Usuario uOrigem, LatLng localDestino, final String status){

        //Inicializar GeoFire
        DatabaseReference localUsuario = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("local_usuario");
        GeoFire geoFire = new GeoFire(localUsuario);

        //Adiciona círculo no cliente que quando o carreto entrar dentro do círculo, já inicia a corrida de destino automáticamente
        final Circle circulo = mMap.addCircle(
                new CircleOptions()
                .center( localDestino )
                .radius(50)//em metros
                .fillColor(Color.argb(90,47, 255,0))
                .strokeColor(Color.argb(190,93,250,57))
        );

        //Posiciona o círculo
        final GeoQuery geoQuery = geoFire.queryAtLocation(
                new GeoLocation(localDestino.latitude, localDestino.longitude),
                0.05//em km (0.05 50 metros)
        );

        //Quando chega dentro do círculo, muda o destino e requisição
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {

                if( key.equals(uOrigem.getId()) ){

                    //Altera status da requisicao
                    requisicao.setStatus(status);
                    requisicao.atualizarStatus();

                    //Remove listener
                    geoQuery.removeAllListeners();
                    circulo.remove();
                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });

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

    //Coloca o marcador do carreto
    private void adicionaMarcadorCarreto(LatLng localizacao, String titulo){
        if( marcadorCarreto != null )
            marcadorCarreto.remove();

        marcadorCarreto = mMap.addMarker(new MarkerOptions().position(localizacao).title(titulo).icon(BitmapDescriptorFactory.fromResource(R.drawable.carro)));
    }

    //Coloca o marcador do cliente
    private void adicionaMarcadorCliente(LatLng localizacao, String titulo){
        if( marcadorCliente != null )
            marcadorCliente.remove();

        marcadorCliente = mMap.addMarker(new MarkerOptions().position(localizacao).title(titulo).icon(BitmapDescriptorFactory.fromResource(R.drawable.usuario)));
    }

    //Coloca o marcador do destino
    private void adicionaMarcadorDestino(LatLng localizacao, String titulo){
        if( marcadorCliente != null )
            marcadorCliente.remove();

        if( marcadorDestino != null )
            marcadorDestino.remove();

        marcadorDestino = mMap.addMarker(new MarkerOptions().position(localizacao).title(titulo).icon(BitmapDescriptorFactory.fromResource(R.drawable.destino)));
    }

    //Inicializar API do Mapas
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        recuperarLocalizacaoUsuario();
    }

    //Recupera a localização atual do usuário e coloca os dados no firebase
    private void recuperarLocalizacaoUsuario() {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                //recuperar latitude e longitude
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                localCarreto = new LatLng(latitude, longitude);

                //Atualizar GeoFire
                UsuarioFirebase.atualizarDadosLocalizacao(latitude, longitude);

                //Atualizar localização carreto no Firebase
                carreto.setLatitude(String.valueOf(latitude));
                carreto.setLongitude(String.valueOf(longitude));
                requisicao.setCarreto( carreto );
                requisicao.atualizarLocalizacaoCarreto();
                alterarStatusDaRequisicao(statusRequisicao);
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

    //Botão de aceitar a corrida
    public void aceitarCorrida(View view){
        //Configura requisicao
        requisicao = new Requisicao();
        requisicao.setId( idRequisicao );
        requisicao.setCarreto( carreto );
        requisicao.setStatus( Requisicao.STATUS_A_CAMINHO );

        requisicao.atualizar();
    }


    //Dependendo da situação, pode-se encerrar a requisição
    @Override
    public boolean onSupportNavigateUp() {
        if (requisicaoAtiva){
            Toast.makeText(CorridaActivity.this, "Necessário encerrar a requisição atual!", Toast.LENGTH_SHORT).show();
        }else {
            Intent i = new Intent(CorridaActivity.this, RequisicoesActivity.class);
            startActivity(i);
        }

        //Verificar o status da requisição para encerrar
        if( statusRequisicao != null && !statusRequisicao.isEmpty() ){
            requisicao.setStatus(Requisicao.STATUS_ENCERRADA);
            requisicao.atualizarStatus();
        }

        return false;
    }
}