package com.example.fbuteamproject.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.fbuteamproject.R;
import com.example.fbuteamproject.interfaces.NoteTitleSelectedInAdapterListener;
import com.example.fbuteamproject.models.Note;

import java.util.ArrayList;

public class NoteTitleAdapter extends RecyclerView.Adapter<NoteTitleAdapter.ViewHolder>{

    private ArrayList<Note> notesList;

    private Context context;

    private NoteTitleSelectedInAdapterListener noteTitleListener;



    public NoteTitleAdapter(ArrayList<Note> notesList){
        this.notesList = notesList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        context = viewGroup.getContext();

        LayoutInflater inflater = LayoutInflater.from(context);

        View noteView = inflater.inflate(R.layout.item_note, viewGroup, false);

        return new ViewHolder(noteView);

    }

    public void setNoteTitleSelectedInAdapterListener(NoteTitleSelectedInAdapterListener noteTitleListener){
        this.noteTitleListener = noteTitleListener;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        Note currNote = notesList.get(i);

        viewHolder.tvNoteTitle.setText(currNote.getNoteTitle() );

    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvNoteTitle;


        ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNoteTitle = itemView.findViewById(R.id.tvNoteTitle);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            //TODO - Open up Fragment that contains note contents when this happens
            int currPosition = getAdapterPosition();

            //TODO - Make sure this works
            noteTitleListener.getNoteTitlePosition(currPosition);

//            Toast.makeText(context, currNote.getNoteContents(), Toast.LENGTH_LONG).show();

        }

    }

}
