package com.example.authenticatorapp.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.authenticatorapp.Adapters.UserAdapter;
import com.example.authenticatorapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.example.authenticatorapp.Model.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class UsersFragment extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userRef = db.collection("users");

    private UserAdapter userAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users, container, false);
        RecyclerView recyclerView1 = view.findViewById(R.id.recyclerview);
        recyclerView1.setHasFixedSize(true);
        recyclerView1.setLayoutManager(new LinearLayoutManager(getContext()));
        readUsers();
        recyclerView1.setAdapter(userAdapter);
        return  view;
    }
    private void readUsers(){

        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(userRef, User.class)
                .setLifecycleOwner(this)
                .build();
        userAdapter = new UserAdapter(options);
    }
}
