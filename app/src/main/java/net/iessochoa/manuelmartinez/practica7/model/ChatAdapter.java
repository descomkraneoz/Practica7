package net.iessochoa.manuelmartinez.practica7.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import net.iessochoa.manuelmartinez.practica7.R;


//Adaptador para el chat
public class ChatAdapter extends FirestoreRecyclerAdapter<Mensaje, ChatAdapter.ChatHolder> {
    //Variable para guardar el usuario logeado en la app
    String usuarioActual;


    public ChatAdapter(@NonNull FirestoreRecyclerOptions<Mensaje> options, String usuarioActual) {
        super(options);
        //Guardamos el usuario
        this.usuarioActual = usuarioActual;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatAdapter.ChatHolder holder, int position, @NonNull Mensaje model) {
        //Miramos si es el usario logeado en nuestro telefono es el que envia el mensaje
        //Si lo es cambiamos la alineacion del texto y el color de fondo del mensaje
        if (model.getUsuario().equals(usuarioActual)) {
            holder.tvMensaje.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
            holder.tvUsuario.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        } else {
            //Si no lo es lo cambiamos al otro lado,ya que si la vista ha sido reciclada por el RecycledView puede ser que tenga el formato anterior
            holder.tvMensaje.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            holder.tvUsuario.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        }
        //Establecemos el mesaje en los componentes de la vista guardados en el holder
        holder.tvUsuario.setText(model.getUsuario());
        holder.tvMensaje.setText(model.getBody());
        //Guardamos el mensaje que esta mostrando actualmente el holder
        holder.mensaje = model;


    }

    @NonNull
    @Override
    //Metodo que inflará el layout del item
    public ChatAdapter.ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.mensaje_item, parent, false));
    }

    //Clase holder para guardar las referencias a los componentes
    public class ChatHolder extends RecyclerView.ViewHolder {
        //Variables para la relacion con los componentes de cada item
        private TextView tvUsuario;
        private TextView tvMensaje;
        //Guardamos el mensaje que se esta mostrando en el item actualmente
        private Mensaje mensaje;


        public ChatHolder(@NonNull View itemView) {
            super(itemView);
            //Relacion de los componentes del layout con los items
            tvMensaje = itemView.findViewById(R.id.tvMensaje);
            tvUsuario = itemView.findViewById(R.id.tvUsuario);
        }

        //Metodo para recuperar el mensaje que se esta mostrando
        public Mensaje getMensaje() {
            return mensaje;
        }
    }
}
