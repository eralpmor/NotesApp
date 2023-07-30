package app.ralpdevs.notesapp.listeners;

import app.ralpdevs.notesapp.entities.Note;

public interface NotesListener {
    void onNoteClicked(Note note, int positions);
}
