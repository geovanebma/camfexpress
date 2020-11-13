package com.projeto.camfexpress.config;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;

/**
 * Created by jamiltondamasceno
 */

public class LocalidadeMetragem {
    //Calcula a distância em metros do local atual até destino
    public static float calcularDistancia(LatLng latLngInicial, LatLng latLngFinal){
        Location localInicial = new Location("LocalidadeMetragem inicial");
        localInicial.setLatitude( latLngInicial.latitude );
        localInicial.setLongitude( latLngInicial.longitude );

        Location localFinal = new Location("LocalidadeMetragem final");
        localFinal.setLatitude( latLngFinal.latitude );
        localFinal.setLongitude( latLngFinal.longitude );

        float distancia = localInicial.distanceTo(localFinal) / 1000;
        return distancia;
    }

    //Formata a distância em KM ou Metros
    public static String formatarDistancia(float distancia){
        String distanciaFormatada;
        if( distancia < 1 ){
            distancia = distancia * 1000;//em Metros
            distanciaFormatada = Math.round( distancia ) + " M ";
        }else {
            DecimalFormat decimal = new DecimalFormat("0.0");
            distanciaFormatada = decimal.format(distancia) + " KM ";
        }

        return distanciaFormatada;
    }
}
