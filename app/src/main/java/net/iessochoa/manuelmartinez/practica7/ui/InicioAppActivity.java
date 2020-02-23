package net.iessochoa.manuelmartinez.practica7.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import net.iessochoa.manuelmartinez.practica7.R;

public class InicioAppActivity extends AppCompatActivity {
    TextView tvNombreUsuario; //nombre del usuario
    Button btCerrarSesion; //boton de cerrar session


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_app);
        tvNombreUsuario = findViewById(R.id.tv_datosUsr);
        btCerrarSesion = findViewById(R.id.auth);

        //obtener el usuario que se ha logeado
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser usrFB= auth.getCurrentUser();


        tvNombreUsuario.setText(usrFB.getDisplayName() + " - " + usrFB.getEmail());

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
        //Si pulsan el boton de los datos de la empresa
        if (item.getItemId() == R.id.itDatosEmpresa) {
            startActivity(new Intent(this, EmpresaActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }


}
