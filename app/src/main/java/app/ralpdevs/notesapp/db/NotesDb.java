package app.ralpdevs.notesapp.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import app.ralpdevs.notesapp.dao.NoteDao;
import app.ralpdevs.notesapp.entities.Note;

@Database(entities = Note.class, version = 1, exportSchema = false)
public abstract class NotesDb extends RoomDatabase {

    private static NotesDb notesDb;

    public static synchronized NotesDb getDatabase(Context context){
        if(notesDb == null){
            notesDb = Room.databaseBuilder(context, NotesDb.class,"notes_db").build();
        }
        return notesDb;
    }

    public abstract NoteDao noteDao();
}
