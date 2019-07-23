package com.example.fbuteamproject.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.fbuteamproject.R;

public class NoteContentFragment extends Fragment {

    private EditText etNoteContents;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.component_planet_contents, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        etNoteContents = view.findViewById(R.id.etNoteContents);
//
//        Note selectedNote = Parcels.unwrap( getArguments().getParcelable("note") );
//
//        etNoteContents.setText(selectedNote.getNoteContents() );


    }



}
