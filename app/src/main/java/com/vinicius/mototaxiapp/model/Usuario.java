package com.vinicius.mototaxiapp.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import config.ConfigFireBase;

public class Usuario {

    String id, nome, tipo, email, senha;

    public Usuario() {
    }

    public void salvar(){

        DatabaseReference firebaseRef = ConfigFireBase.getFirebaseDatabase();
        DatabaseReference usuarios = firebaseRef.child("usuarios").child(getId());

        usuarios.setValue(this);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getEmail() {
        return email;
    }

    @Exclude
    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
