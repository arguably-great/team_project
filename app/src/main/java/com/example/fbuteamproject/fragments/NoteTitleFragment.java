package com.example.fbuteamproject.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fbuteamproject.R;
import com.example.fbuteamproject.adapters.NoteTitleAdapter;
import com.example.fbuteamproject.interfaces.NoteTitleSelectedInAdapterListener;
import com.example.fbuteamproject.interfaces.PassNoteToActivityListener;
import com.example.fbuteamproject.models.Note;

import java.util.ArrayList;

public class NoteTitleFragment extends Fragment implements NoteTitleSelectedInAdapterListener {

    private RecyclerView rvNoteTitles;
    private ArrayList<Note> notesList;
    private NoteTitleAdapter noteTitleAdapter;
    private LinearLayoutManager layoutManager;

    private PassNoteToActivityListener passNoteListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.component_planet_title, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //TODO - Bring this back if needed
//        setupRecyclerView(view);
//
//        noteTitleAdapter.setNoteTitleSelectedInAdapterListener(this);

    }


    public void setPassNoteListener(PassNoteToActivityListener passNoteListener){
        this.passNoteListener = passNoteListener;
    }


    private void setupRecyclerView(@NonNull View view) {
//        rvNoteTitles = view.findViewById(R.id.rvNoteTitles);

        notesList = new ArrayList<>();




        noteTitleAdapter = new NoteTitleAdapter(notesList);

        rvNoteTitles.setAdapter(noteTitleAdapter);

        layoutManager = new LinearLayoutManager(getActivity() );

        rvNoteTitles.setLayoutManager(layoutManager);

        //TODO - Populate with information at first before allowing for user input
        generateNotes();

        setupScrollListener();

    }


    private void setupScrollListener() {

        rvNoteTitles.setOnScrollListener(new RecyclerView.OnScrollListener() {
            int mLastFirstVisibleItem = 0;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                final int currentFirstVisibleItem = layoutManager.findFirstVisibleItemPosition();

                if (currentFirstVisibleItem > this.mLastFirstVisibleItem) {
                    ( (AppCompatActivity) getActivity() ).getSupportActionBar().hide();

                } else if (currentFirstVisibleItem < this.mLastFirstVisibleItem) {
                    ( (AppCompatActivity) getActivity() ).getSupportActionBar().show();
                }

                this.mLastFirstVisibleItem = currentFirstVisibleItem;
            }
        });

    }


    private void generateNotes() {

        for(int i = 0; i < 20; i++){
            String noteTitle = "NOTE " + (i+1);

            String noteContent = "INSIDE OF NOTE " + (i+1);

            Note note = new Note(noteTitle, noteContent);

            notesList.add(note);
            noteTitleAdapter.notifyItemInserted(notesList.size() - 1);
        }

    }


    @Override
    public void getNoteTitlePosition(int position) {
        //TODO - Using this position, get the Note and pass it up to Activity using another Interface
        Note selectedNote = notesList.get(position);

        passNoteListener.returnSelectedNote(selectedNote);
    }
}
