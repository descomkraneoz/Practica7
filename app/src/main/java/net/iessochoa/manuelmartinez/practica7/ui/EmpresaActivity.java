package net.iessochoa.manuelmartinez.practica7.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import net.iessochoa.manuelmartinez.practica7.R;
import net.iessochoa.manuelmartinez.practica7.model.Empresa;
import net.iessochoa.manuelmartinez.practica7.model.FirebaseContract;


public class EmpresaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa);
    }

    void obtenDatosEmpresa() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection(FirebaseContract.EmpresaEntry.COLLECTION_NAME).document(FirebaseContract.EmpresaEntry.ID);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                //leemos los datos
                Empresa empresa = documentSnapshot.toObject(Empresa.class);
                //mostramos los datos y asignamos eventos
                asignaValoresEmpresa();
            }
        });
    }

}
