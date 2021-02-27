package com.vinicius.mototaxiapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.vinicius.mototaxiapp.R;
import com.vinicius.mototaxiapp.model.Usuario;

import config.ConfigFireBase;
import helper.UsuarioFirebase;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText campoEmail, campoSenha;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Inicializar componentes
        campoEmail = findViewById(R.id.editEmailLogin);
        campoSenha = findViewById(R.id.editSenhaLogin);

    }

    public void validarLoginUsuario(View view){

        //Recuperar textos dos campos
        String textoEmail = campoEmail.getText().toString();
        String textoSenha = campoSenha.getText().toString();

        if( !textoEmail.isEmpty() ) {//verifica e-mail
            if( !textoSenha.isEmpty() ) {//verifica senha
                Usuario usuario = new Usuario();
                usuario.setEmail( textoEmail );
                usuario.setSenha( textoSenha );

                logarUsuario( usuario );

            }else{
                Toast.makeText(LoginActivity.this,
                        "Preencha a senha!",
                        Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(LoginActivity.this,
                    "Preencha o email!",
                    Toast.LENGTH_SHORT).show();
        }

    }

    public void logarUsuario( Usuario usuario ){

        autenticacao = ConfigFireBase.getFirebaseAuth();
        autenticacao.signInWithEmailAndPassword(
                usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(task -> {
            if( task.isSuccessful() ){

                //Verificar o tipo de usuário logado
                // "Motorista" / "Passageiro"
                UsuarioFirebase.redirecionaUsuarioLogado(LoginActivity.this);

            }else {

                String excecao = "";
                try {
                    throw task.getException();
                }catch ( FirebaseAuthInvalidUserException e ) {
                    excecao = "Usuário não está cadastrado.";
                }catch ( FirebaseAuthInvalidCredentialsException e ){
                    excecao = "E-mail e senha não correspondem a um usuário cadastrado";
                }catch (Exception e){
                    excecao = "Erro ao cadastrar usuário: "  + e.getMessage();
                    e.printStackTrace();
                }
                Toast.makeText(LoginActivity.this,
                        excecao,
                        Toast.LENGTH_SHORT).show();

            }
        });

    }

}