package config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ConfigFireBase {

    private static DatabaseReference dataBase;
    private static FirebaseAuth auth;

    public static DatabaseReference getFirebaseDatabase(){
        if (dataBase == null){
            dataBase = FirebaseDatabase.getInstance().getReference();
        }

        return dataBase;
    }

    //retorna a instancia do FirebaseAuth
    public static FirebaseAuth getFirebaseAuth(){
        if (auth == null){
            auth = FirebaseAuth.getInstance();
        }
        return auth;
    }
}
