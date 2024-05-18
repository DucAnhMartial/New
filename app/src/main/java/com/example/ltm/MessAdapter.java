package com.example.ltm;

import android.graphics.ColorSpace;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MessAdapter extends RecyclerView.Adapter<MessAdapter.viewHolder>{

    List<MessModel>modelList;

    public MessAdapter(List<MessModel> modelList){
        this.modelList = modelList;
    }
    @NonNull
    @Override
    public MessAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mess,null);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessAdapter.viewHolder holder, int position) {

        MessModel model = modelList.get(position);
        if (model.getSentBy().equals(MessModel.SENT_BY_ME)){
            holder.left_chat.setVisibility(View.VISIBLE);
            holder.right_chat.setVisibility(View.GONE);
            holder.right_text.setText(model.getMess());
        }
        else{
            holder.right_chat.setVisibility(View.VISIBLE);
            holder.left_chat.setVisibility(View.GONE);
            holder.left_text.setText(model.getMess());
        }

    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout right_chat,left_chat;
        TextView left_text,right_text;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            left_chat = itemView.findViewById(R.id.left_chat);
            right_chat = itemView.findViewById(R.id.right_chat);
            left_text = itemView.findViewById(R.id.left_text);
            right_text = itemView.findViewById(R.id.right_text);

        }
    }

}
