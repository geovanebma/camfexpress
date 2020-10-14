package com.projeto.camfexpress.adpter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.uber.cursoandroid.jamiltondamasceno.uber.R;
import com.projeto.camfexpress.helper.Local;
import com.projeto.camfexpress.model.Requisicao;
import com.projeto.camfexpress.model.Usuario;

import java.util.List;

/**
 * Created by jamiltondamasceno
 */

public class RequisicoesAdapter extends RecyclerView.Adapter<RequisicoesAdapter.MyViewHolder> {

    private List<Requisicao> requisicoes;
    private Context context;
    private Usuario motorista;

    public RequisicoesAdapter(List<Requisicao> requisicoes, Context context, Usuario motorista) {
        this.requisicoes = requisicoes;
        this.context = context;
        this.motorista = motorista;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_requisicoes, parent, false);
        return new MyViewHolder( item ) ;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        System.out.println("abs:"+position);

        Requisicao requisicao = requisicoes.get( position );
        Usuario passageiro = requisicao.getPassageiro();

        System.out.println("Nome dele: "+passageiro.getNome()+" "+requisicao.getPassageiro().getNome());

        if(passageiro.getNome().isEmpty()){
            holder.nome.setText( passageiro.getNome() );
        }else{
            holder.nome.setText( passageiro.getNome() );
        }

        if(motorista!= null){

            LatLng localPassageiro = new LatLng(
                    Double.parseDouble(passageiro.getLatitude()),
                    Double.parseDouble(passageiro.getLongetude())
            );

            LatLng localMotorista = new LatLng(
                    Double.parseDouble(motorista.getLatitude()),
                    Double.parseDouble(motorista.getLongetude())
            );
            float distancia = Local.calcularDistancia(localPassageiro, localMotorista);
            String distanciaFormatada = Local.formatarDistancia(distancia);
            holder.distancia.setText(distanciaFormatada + "- aproximadamente");

        }

    }

    @Override
    public int getItemCount() {
        return requisicoes.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView nome, distancia;

        public MyViewHolder(View itemView) {
            super(itemView);

            nome = itemView.findViewById(R.id.textRequisicaoNome);
            distancia = itemView.findViewById(R.id.textRequisicaoDistancia);

        }
    }

}
