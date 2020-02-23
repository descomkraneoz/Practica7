package net.iessochoa.manuelmartinez.practica7.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.common.ChangeEventType;
import com.firebase.ui.firestore.ChangeEventListener;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import net.iessochoa.manuelmartinez.practica7.R;
import net.iessochoa.manuelmartinez.practica7.model.ChatAdapter;
import net.iessochoa.manuelmartinez.practica7.model.Conferencia;
import net.iessochoa.manuelmartinez.practica7.model.FirebaseContract;
import net.iessochoa.manuelmartinez.practica7.model.Mensaje;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class InicioAppActivity extends AppCompatActivity {
    TextView tvNombreUsuario; //nombre del usuario
    //Constantes para comprobaciones en el LOG
    private static final String TAG_CONFERENCIA = "LECTURA_CONFERENCIAS";
    private static final String TAG_CONFERENCIA_INICIADA = "LECTURA_CONF_INICIADAS";
    Button btCerrarSesion; //boton de cerrar sesion
    Conferencia conferenciaActual; //Variable que guarda la conferencia actual
    ArrayList<Conferencia> listaConferencias;

    String usuarioActual; //Variable que guarda el Usuario actual
    TextView tvConferenciaIniciada;
    ImageButton btVerDatosConferencia;
    Spinner spConferencias; //spinner de conferencias
    EditText etMensaje; //cuadrito de texto para mandar mensajes al chat
    ImageButton btEnviar; //boton para enviar mensajes por chat, es una imagen
    RecyclerView rvChat;
    ChatAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_app);
        tvNombreUsuario = findViewById(R.id.tv_datosUsr);
        btCerrarSesion = findViewById(R.id.auth);

        //obtener el usuario que se ha logeado
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser usrFB= auth.getCurrentUser();

        spConferencias = findViewById(R.id.spConferencias);
        btVerDatosConferencia = findViewById(R.id.btVerConferencia);
        tvConferenciaIniciada = findViewById(R.id.tvConferenciaIniciada);
        etMensaje = findViewById(R.id.etTextoMensaje);
        btEnviar = findViewById(R.id.ibtEnviar);
        rvChat = findViewById(R.id.rvChat);
        //layout para el reciclerview
        rvChat.setLayoutManager(new LinearLayoutManager(this));
        //el siguiente textview mostrara el nombre y email del usuario logeado
        tvNombreUsuario.setText(usrFB.getDisplayName() + "\n" + usrFB.getEmail());
        //guardamos en una variable el usuario actual
        usuarioActual = usrFB.getEmail();
        //Metodo que iniciara y mostrara la conferencia iniciada
        ConferenciasIniciadas();
        //Llamamos al metodo que establece los valores al spinner
        leerConferencias();

        //Evento para cerrar la sesion actual
        btCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Cerramos la sesion
                auth.signOut();
                //Volvemos a la actividad del Main para pedir un nuevo usuario
                startActivity(new Intent(InicioAppActivity.this, MainActivity.class));
                finish();
            }
        });

        //Evento del boton que se ejecuta al pulsar sobre este para ver los datos de la conferencia
        btVerDatosConferencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Metodo que muestra un dialogo con los datos de la conferencia
                dialogoDatosConferencia((Conferencia) spConferencias.getSelectedItem()); //pide que haga casting del spinner
            }
        });

        //Evento del spinner para cambiar la conferencia cuando se cambia la conferencia en el spinner
        spConferencias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Actualizamos la variable de la conferencia actual
                conferenciaActual = (Conferencia) spConferencias.getItemAtPosition(position);
                //Metodo que define el adaptador cuando la conferencia cambia
                defineAdaptador();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //Evento del boton de enviar, sirve para mandar el mensaje del editText (etMensaje)
        btEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Metodo que envia el mensaje
                enviarMensaje();
            }
        });
    }

    /**
     * @param menu Método que inflará el menu de la aplicación main cuando esta se inicie
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflamos el menu de la aplicación
        getMenuInflater().inflate(R.menu.menu_inicio, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * @param item Metodo que se ejecuta cuando se hace click en un item del menu
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.itDatosEmpresa) {
            startActivity(new Intent(this, EmpresaActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Metodo que obtiene las conferencias de firebase
     */
    private void leerConferencias() {
        //Se obtiene la instancia de la base de datos
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //Variable para la lista de conferencias
        listaConferencias = new ArrayList<>();
        //Accedemos a firebase e intentamos obtener las conferencias
        db.collection(FirebaseContract.ConferenciaEntry.COLLECTION_NAME)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        //Si las hemos recuperado correctamente
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Informamos en el log
                                Log.d(TAG_CONFERENCIA, document.getId() + getResources().getString(R.string.flecha) + document.getData());
                                //Introducimos las conferencias una a una en la lista
                                listaConferencias.add(document.toObject(Conferencia.class));
                            }
                            //Cargamos el spinner con las conferencias
                            cargaSpinner();
                        } else {
                            //Informamos que no hemos recuperado las conferencias
                            Log.d(TAG_CONFERENCIA, getResources().getString(R.string.errorRecuperarConf), task.getException());
                        }
                    }
                });
    }

    /**
     * Metodo que carga el spinner de conferencias con los datos obtenidos en el metodo anterior
     */

    private void cargaSpinner() {
        //Creamos un adaptador y se lo pasamos al spinner para cargar los datos
        spConferencias.setAdapter(new ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, listaConferencias));
    }

    /**
     * Metodo que invoca un dialogo con los datos de la conferencia
     */

    private void dialogoDatosConferencia(Conferencia conferencia) {
        //Creamos un mensaje de alerta para informar al usuarioActual
        AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
        //Establecemos el título y el mensaje que queremos
        dialogo.setTitle(getResources().getString(R.string.tituloConferencia));
        //Mensaje que se muestra en el dialogo
        String mensaje = conferencia.getNombre() + "\n" + getResources().getString(R.string.ConferenciaFecha)
                + new SimpleDateFormat("dd/MM/yyyy").format(conferencia.getFecha()) + "\n" + getResources().getString(R.string.ConferenciaHorario)
                + conferencia.getHorario() + "\n" + getResources().getString(R.string.ConferenciaSala) + conferencia.getSala();
        //Establecemos el mensaje al dialogo
        dialogo.setMessage(mensaje);
        // agregamos botón de aceptar al dialogo
        dialogo.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Cuando hagan click en el boton saldremos automaticamente,de la misma forma que si pulsa fuera del cuadro de dialogo
            }
        });
        //Mostramos el dialogo
        dialogo.show();
    }

    /**
     * Metodo que indica la conferencia iniciada
     */

    private void ConferenciasIniciadas() {
        //Obtenemos la instancia de firebase
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //Accedemos al documento de las conferencias iniciadas
        final DocumentReference docRef = db.collection(FirebaseContract.ConferenciaIniciadaEntry.COLLECTION_NAME).document(FirebaseContract.ConferenciaIniciadaEntry.ID);
        //Creamos un evento para establecer una conexion con firebase en tiempo real
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                //En caso de que la escucha a la base de datos falle
                if (e != null) {
                    //Informamos en el log
                    Log.w(TAG_CONFERENCIA, getResources().getString(R.string.falloAlEscuchar), e);
                    return;
                }
                //Obtenemos el documento y si existe
                if (snapshot != null && snapshot.exists()) {
                    //Obtenemos la conferencia iniciada del documento
                    String conferenciaIniciada = snapshot.getString(FirebaseContract.ConferenciaIniciadaEntry.CONFERENCIA);
                    //Lo establecemos en su TextView
                    tvConferenciaIniciada.setText(getResources().getString(R.string.tvConferenciaIniciada) + conferenciaIniciada);
                    //Informamos en el log de la conferencia iniciada
                    Log.d(TAG_CONFERENCIA_INICIADA, getResources().getString(R.string.msgConferenciaIniciada) + snapshot.getData());
                } else {
                    //o bien informamos en el log de que no hemos obtenido la conferencia iniciada
                    Log.d(TAG_CONFERENCIA_INICIADA, getResources().getString(R.string.msgConferenciaNoIniciada));
                }
            }
        });
    }

    /**
     * Metodo para enviar mensajes al chat
     */
    private void enviarMensaje() {
        //Obtenemos el mensaje
        String body = etMensaje.getText().toString();
        if (!body.isEmpty()) {
            //usuarioActual y mensaje
            Mensaje mensaje = new Mensaje(usuarioActual, body);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection(FirebaseContract.ConferenciaEntry.COLLECTION_NAME)
                    //documento conferencia actual
                    .document(conferenciaActual.getId())
                    //subcolección de la conferencia
                    .collection(FirebaseContract.ChatEntry.COLLECTION_NAME)
                    //añadimos el mensaje nuevo
                    .add(mensaje);
            //Limpiamos el campo para escribir el mensaje
            etMensaje.setText("");
            //Ocultamos el teclado
            ocultarTeclado();
        }
    }


    /**
     * Permite ocultar el teclado
     */
    private void ocultarTeclado() {
        InputMethodManager i = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (i != null) {
            i.hideSoftInputFromWindow(etMensaje.getWindowToken(), 0);
        }
    }

    /**
     * Metodo que define el adaptador del ReciclerView
     */
    private void defineAdaptador() {
        //consulta en Firebase
        final Query query = FirebaseFirestore.getInstance()
                //coleccion conferencias
                .collection(FirebaseContract.ConferenciaEntry.COLLECTION_NAME)
                //documento: conferencia actual
                .document(conferenciaActual.getId())
                //colección chat de la conferencia
                .collection(FirebaseContract.ChatEntry.COLLECTION_NAME)
                //obtenemos la lista ordenada por fecha
                .orderBy(FirebaseContract.ChatEntry.FECHA_CREACION, Query.Direction.ASCENDING);

        //Creamos las opciones del FirebaseAdapter
        FirestoreRecyclerOptions<Mensaje> options = new FirestoreRecyclerOptions.Builder<Mensaje>()
                //consulta y clase en la que se guardan los datos
                .setQuery(query, Mensaje.class)
                .setLifecycleOwner(this)
                .build();
        //si el usuarioActual ya habia seleccionado otra conferencia, paramos las escucha
        if (adapter != null) {
            adapter.stopListening();
        }
        //Creamos el adaptador
        adapter = new ChatAdapter(options, usuarioActual);
        //asignamos el adaptador
        rvChat.setAdapter(adapter);
        //comenzamos a escuchar. Normalmente solo tenemos un adaptador, esto tenemos que hacerlo en el evento onStar, como indica la documentación
        adapter.startListening();
        //Podemos reaccionar ante cambios en la query( se añade un mesaje). Nosotros, lo que necesitamos es mover el scroll
        // del recyclerView al inicio para ver el mensaje nuevo
        adapter.getSnapshots().addChangeEventListener(new ChangeEventListener() {
            @Override
            public void onChildChanged(@NonNull ChangeEventType type, @NonNull DocumentSnapshot snapshot, int newIndex, int oldIndex) {
                //Llevamos el chat a la ultima posicion cuando envien un mensaje para mostrar el nuevo mensaje
                rvChat.smoothScrollToPosition(adapter.getItemCount());
            }

            @Override
            public void onDataChanged() {
            }

            @Override
            public void onError(@NonNull FirebaseFirestoreException e) {
            }
        });
    }

    //es necesario para la escucha
    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }


}
