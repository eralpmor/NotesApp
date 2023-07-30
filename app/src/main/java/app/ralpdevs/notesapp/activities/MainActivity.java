package app.ralpdevs.notesapp.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import app.ralpdevs.notesapp.R;
import app.ralpdevs.notesapp.adapter.NotesAdapter;
import app.ralpdevs.notesapp.db.NotesDb;
import app.ralpdevs.notesapp.entities.Note;
import app.ralpdevs.notesapp.listeners.NotesListener;

public class MainActivity extends AppCompatActivity implements NotesListener {

    public static final int REQUEST_CODE_ADD_NOTE = 1, REQUEST_CODE_UPDATE_NOTE = 2, REQUEST_CODE_SHOW_NOTES = 3;

    private RecyclerView notesRecyclerView;
    private ImageView imageAddNoteMain;
    private List<Note> noteList;
    private NotesAdapter notesAdapter;
    private int noteClickedPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findView();
    }


    private void findView() {
        imageAddNoteMain = findViewById(R.id.imageAddNoteMain);
        notesRecyclerView = findViewById(R.id.notesRecyclerView);

        imageAddNoteMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(
                        new Intent(getApplicationContext(), CreateNote.class), REQUEST_CODE_ADD_NOTE
                );
            }
        });

        notesRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        noteList = new ArrayList<>();
        notesAdapter = new NotesAdapter(noteList, this);
        notesRecyclerView.setAdapter(notesAdapter);

        getNotes(REQUEST_CODE_SHOW_NOTES);
    }

    @Override
    public void onNoteClicked(Note note, int positions) {
        noteClickedPosition = positions;
        Intent intentToDetails = new Intent(getApplicationContext(), CreateNote.class);
        intentToDetails.putExtra("isViewOrUpdate", true);
        intentToDetails.putExtra("note", note);
        startActivityForResult(intentToDetails, REQUEST_CODE_UPDATE_NOTE);
    }

    private void getNotes(final int requestCode) {

        class GetNotesTask extends AsyncTask<Void, Void, List<Note>> {
            @Override
            protected List<Note> doInBackground(Void... voids) {
                return NotesDb.getDatabase(getApplicationContext()).noteDao().getAllNotes();
            }


            @Override
            protected void onPostExecute(List<Note> notes) {
                super.onPostExecute(notes);
                if(requestCode == REQUEST_CODE_SHOW_NOTES) {
                    noteList.addAll(notes);
                    notesAdapter.notifyDataSetChanged();
                }else if(requestCode == REQUEST_CODE_ADD_NOTE){
                    noteList.add(0,notes.get(0));
                    notesAdapter.notifyItemInserted(0);
                    notesRecyclerView.smoothScrollToPosition(0);
                }else if(requestCode == REQUEST_CODE_UPDATE_NOTE){
                    noteList.remove(noteClickedPosition);
                    noteList.add(noteClickedPosition, notes.get(noteClickedPosition));
                    notesAdapter.notifyItemChanged(noteClickedPosition);
                }
            }
        }
        new GetNotesTask().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_NOTE && resultCode == RESULT_OK) {
            getNotes(REQUEST_CODE_ADD_NOTE);
        } else if(requestCode == REQUEST_CODE_UPDATE_NOTE && resultCode == RESULT_OK){
            if(data != null){
                getNotes(REQUEST_CODE_UPDATE_NOTE);
            }
        }
    }
}