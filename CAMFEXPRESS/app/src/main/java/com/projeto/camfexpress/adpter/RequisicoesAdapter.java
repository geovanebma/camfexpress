package com.projeto.camfexpress.adpter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.projeto.camfexpress.R;
import com.projeto.camfexpress.config.LocalidadeMetragem;
import com.projeto.camfexpress.config.Requisicao;
import com.projeto.camfexpress.config.Usuario;

import java.util.List;

/**
 * Created by jamiltondamasceno
 */

public class RequisicoesAdapter extends RecyclerView.Adapter<RequisicoesAdapter.MyViewHolder> {

    private List<Requisicao> requisicoes;
    private Context context;
    private Usuario motorista;

    //Construtor
    public RequisicoesAdapter(List<Requisicao> requisicoes, Context context, Usuario motorista) {
        this.requisicoes = requisicoes;
        this.context = context;
        this.motorista = motorista;
    }

    //Cria o ViewHolder
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_requisicoes, parent, false);
        return new MyViewHolder( item ) ;
    }

    //Organiza o ViewHolder
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Requisicao requisicao = requisicoes.get( position );
        Usuario cliente = requisicao.getCliente();

        if(cliente.getNome().isEmpty()){
            holder.nome.setText( cliente.getNome() );
        }else{
            holder.nome.setText( cliente.getNome() );
        }

        if(motorista!= null){
            LatLng localCliente = new LatLng(
                    Double.parseDouble(cliente.getLatitude()),
                    Double.parseDouble(cliente.getLongitude())
            );

            LatLng localMotorista = new LatLng(
                    Double.parseDouble(motorista.getLatitude()),
                    Double.parseDouble(motorista.getLongitude())
            );

            float distancia = LocalidadeMetragem.calcularDistancia(localCliente, localMotorista);
            String distanciaFormatada = LocalidadeMetragem.formatarDistancia(distancia);
            holder.distancia.setText(distanciaFormatada + "- aproximadamente");

            String ajudante = requisicao.getDestino().getAjudante();
            if(ajudante.equals("true")){
                holder.ajudante.setText("Precisa de ajudante: sim.");
            }else{
                holder.ajudante.setText("Precisa de ajudante: não.");
            }
        }
    }

    //Conta quantas requisições tem
    @Override
    public int getItemCount() {
        return requisicoes.size();
    }

    //Recupera os valores do TextView
    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView nome, distancia, ajudante;

        public MyViewHolder(View itemView) {
            super(itemView);

            nome = itemView.findViewById(R.id.textRequisicaoNome);
            distancia = itemView.findViewById(R.id.textRequisicaoDistancia);
            ajudante = itemView.findViewById(R.id.textAjudante);
        }
    }
}
