package net.iessochoa.manuelmartinez.practica7.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import net.iessochoa.manuelmartinez.practica7.R;

public class InicioAppActivity extends AppCompatActivity {
    TextView tv_datosUsr;
    Button auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_app);
        tv_datosUsr=findViewById(R.id.tv_datosUsr);
        auth=findViewById(R.id.auth);

        //obtener el usuario que se ha logeado
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser usrFB= auth.getCurrentUser();


        tv_datosUsr.setText(usrFB.getDisplayName()+" - "+usrFB.getEmail());
        auth.signOut();
        startActivity(new Intent(InicioAppActivity.this, MainActivity.class));

        finish();
    }
}
