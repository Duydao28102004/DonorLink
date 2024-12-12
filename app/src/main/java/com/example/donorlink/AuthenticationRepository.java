package com.example.donorlink;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthenticationRepository {
    private FirebaseAuth mAuth;

    public AuthenticationRepository() {
        mAuth = FirebaseAuth.getInstance();
    }

    public void signUp(String email, String password, OnAuthCompleteListener listener) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listener.onSuccess(mAuth.getCurrentUser());
                    } else {
                        listener.onFailure(task.getException());
                    }
                });
    }

    public void signIn(String email, String password, OnAuthCompleteListener listener) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listener.onSuccess(mAuth.getCurrentUser());
                    } else {
                        listener.onFailure(task.getException());
                    }
                });
    }

    public void signOut() {
        mAuth.signOut();
    }

    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    public interface OnAuthCompleteListener {
        void onSuccess(FirebaseUser user);
        void onFailure(Exception exception);
    }
}
