package net.iessochoa.manuelmartinez.practica7.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import net.iessochoa.manuelmartinez.practica7.R;
import net.iessochoa.manuelmartinez.practica7.model.Empresa;
import net.iessochoa.manuelmartinez.practica7.model.FirebaseContract;


public class EmpresaActivity extends AppCompatActivity {
    //Componentes
    TextView tvNombre;
    TextView tvTelefono;
    TextView tvDireccion;
    //Empresa que se muestra actualmente
    private Empresa empresa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa);
        tvNombre = findViewById(R.id.tvNombre);
        tvTelefono = findViewById(R.id.tvTelefono);
        tvDireccion = findViewById(R.id.tvDireccion);

        //obtenemos los datos de la empresa
        obtenDatosEmpresa();
    }

    /**
     * Metodo para obtener los datos de la empresa desde firebase
     */
    void obtenDatosEmpresa() {
        //instanciamos firebase
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //buscamos la coleccion que vamos a mostrar y su ID, en este caso es la empresa ochoa
        DocumentReference docRef = db.collection(FirebaseContract.EmpresaEntry.COLLECTION_NAME).document(FirebaseContract.EmpresaEntry.ID);
        //creamos el listener que actuara cuando los datos sean correctos
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                //leemos los datos
                empresa = documentSnapshot.toObject(Empresa.class);
                //mostramos los datos y asignamos eventos
                asignaValoresEmpresa();
            }
        });
    }

    /**
     * Metodo para asignar los valores de la empresa a los componentes de la vista
     */
    void asignaValoresEmpresa() {
        tvNombre.setText(empresa.getNombre());
        tvDireccion.setText(empresa.getDireccion());
        tvTelefono.setText(empresa.getTelefono());
        //Establecemos los onClick para los textView
        tvTelefono.setOnClickListener(clickTelefono());
        tvDireccion.setOnClickListener(clickDireccion());
    }

    /**
     * Metodo que devuelve el evento onClick ejecutado cuando se pulse en el TextView de la direccion
     */
    public View.OnClickListener clickDireccion() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, empresa.getUriLocalizacion()));
            }
        };
    }

    /**
     * Metodo que devuelve el evento onclick cuando pulsas en el textview del telefono
     */
    public View.OnClickListener clickTelefono() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_DIAL, empresa.getUriTelefono()));
            }
        };
    }


}
