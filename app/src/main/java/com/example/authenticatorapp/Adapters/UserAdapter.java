package com.example.authenticatorapp.Adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.authenticatorapp.MessageActivity;
import com.example.authenticatorapp.R;
import com.example.authenticatorapp.Model.User;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;


public class UserAdapter extends FirestoreRecyclerAdapter<User, UserAdapter.ViewHolder> {
    public UserAdapter(@NonNull FirestoreRecyclerOptions<User> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull final User model) {
        holder.fName.setText(model.getfName());
        holder.profile_image.setImageResource(R.drawable.ic_default);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MessageActivity.class);
                intent.putExtra("recipientId", model.getUserId());
                v.getContext().startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new UserAdapter.ViewHolder(view);
    }



    class ViewHolder extends RecyclerView.ViewHolder{
        public TextView fName;
        public ImageView profile_image;

        public ViewHolder(View itemView){
            super(itemView);
            fName = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
        }
    }
}
