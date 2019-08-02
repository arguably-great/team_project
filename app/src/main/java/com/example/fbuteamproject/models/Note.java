package com.example.fbuteamproject.models;

import org.parceler.Parcel;


/*
This Model creates Getters and Setters for Notes.
 */

@Parcel
public class Note {

    String noteTitle;
    String noteContents;

    public Note(){

    }

    public Note(String noteTitle, String noteContents) {
        this.noteTitle = noteTitle;
        this.noteContents = noteContents;
    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public String getNoteContents() {
        return noteContents;
    }

    public void setNoteContents(String noteContents) {
        this.noteContents = noteContents;
    }


}
