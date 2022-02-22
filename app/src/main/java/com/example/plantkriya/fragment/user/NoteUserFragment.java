package com.example.plantkriya.fragment.user;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.plantkriya.R;
import com.example.plantkriya.activity.CreateNoteActivity;
import com.example.plantkriya.adapter.NotesAdapter;
import com.example.plantkriya.database.NoteDatabase;
import com.example.plantkriya.entities.Note;
import com.example.plantkriya.listener.NotesListener;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;


public class NoteUserFragment extends Fragment implements NotesListener {

    public static final int REQUEST_CODE_ADD_NOTE = 1;
    public static final int REQUEST_CODE_UPDATE_NOTE = 2;
    public static final int REQUEST_CODE_SHOW_NOTE = 3;
    public static final int REQUEST_CODE_SELECT_IMAGE = 4;
    public static final int REQUEST_CODE_STORAGE_PERMISSION = 5;
    private EditText editText;
    private LinearLayout layoutSearch;
    private TextView img_searc;
    private RecyclerView recyclerViewNote;
    private List<Note> noteList;
    private NotesAdapter notesAdapter;
    private AlertDialog dialogAddUrl;
    private int noteClickedPosition = -1;

    public NoteUserFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_note_user, container, false);
        layoutSearch = view.findViewById(R.id.layout_search);
        img_searc = view.findViewById(R.id.searchbtn);
        recyclerViewNote = view.findViewById(R.id.noteRecycler);
        recyclerViewNote.setLayoutManager(
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        );

        ImageView imageAddNote = view.findViewById(R.id.addNoteBtn);
        imageAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getContext(), CreateNoteActivity.class),
                        REQUEST_CODE_ADD_NOTE);
            }
        });

        view.findViewById(R.id.image_add_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(
                        getContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);
                } else {
                    selectImage();
                }
            }
        });
        view.findViewById(R.id.image_add_note).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getContext(), CreateNoteActivity.class),
                        REQUEST_CODE_ADD_NOTE);
            }
        });
        view.findViewById(R.id.image_add_web).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddUrlDialog();
            }
        });

        img_searc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutSearch.setVisibility(View.VISIBLE);
            }
        });

        noteList = new ArrayList<>();
        notesAdapter = new NotesAdapter(noteList, this);
        recyclerViewNote.setAdapter(notesAdapter);

        getNotes(REQUEST_CODE_SHOW_NOTE, false);

        EditText inputSearch = view.findViewById(R.id.input_search);
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                notesAdapter.cancelTime();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (noteList.size() != 0) {
                    notesAdapter.searchNote(s.toString());
                }
            }
        });

        return view;
    }

    private void showAddUrlDialog() {
        if (dialogAddUrl == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            View view = LayoutInflater.from(getContext()).inflate(
                    R.layout.layout_add_url,
                    (ViewGroup) getView().findViewById(R.id.layoutAddUrlContainer)
            );

            builder.setView(view);

            dialogAddUrl = builder.create();
            if (dialogAddUrl.getWindow() != null) {
                dialogAddUrl.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            final EditText inputUrl = view.findViewById(R.id.inputUrl);
            inputUrl.requestFocus();

            view.findViewById(R.id.textAdd).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (inputUrl.getText().toString().trim().isEmpty()) {
                        StyleableToast.makeText(getContext(), "Masukkan URL", R.style.backToast).show();
                    } else if (!Patterns.WEB_URL.matcher(inputUrl.getText().toString()).matches()) {
                        StyleableToast.makeText(getContext(), "URL tidak valid", R.style.exampleToast).show();
                    } else {
                        dialogAddUrl.dismiss();
                        Intent intent = new Intent(getContext(), CreateNoteActivity.class);
                        intent.putExtra("isFromQuickActions", true);
                        intent.putExtra("quickActionType", "URL");
                        intent.putExtra("URL", inputUrl.getText().toString());
                        startActivityForResult(intent, REQUEST_CODE_ADD_NOTE);
                    }
                }
            });

            view.findViewById(R.id.textCancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogAddUrl.dismiss();
                }
            });
        }
        dialogAddUrl.show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImage();
            } else {
                StyleableToast.makeText(getContext(), "Permission Denied!", R.style.exampleToast).show();
            }
        }
    }

    private String getPathFromUri(Uri contentUri) {
        String filepath;
        Cursor cursor = getActivity().getContentResolver().query(
                contentUri,
                null,
                null,
                null,
                null);
        if (cursor == null) {
            filepath = contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex("_data");
            filepath = cursor.getString(index);
            cursor.close();
        }
        return filepath;
    }


    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_NOTE && resultCode == RESULT_OK) {
            getNotes(REQUEST_CODE_ADD_NOTE, false);
        } else if (requestCode == REQUEST_CODE_UPDATE_NOTE && resultCode == RESULT_OK) {
            if (data != null) {
                getNotes(REQUEST_CODE_UPDATE_NOTE, data.getBooleanExtra("isNoteDeleted", false));
            }
        } else if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    try {
                        String selectedImagePath = getPathFromUri(selectedImageUri);
                        Intent intent = new Intent(getContext(), CreateNoteActivity.class);
                        intent.putExtra("isFromQuickActions", true);
                        intent.putExtra("quickActionType", "image");
                        intent.putExtra("imagePath", selectedImagePath);
                        startActivityForResult(intent, REQUEST_CODE_ADD_NOTE);
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private void getNotes(final int requestCode, final boolean isNoteDeleted) {

        @SuppressLint("StaticFieldLeak")
        class getNotesTask extends AsyncTask<Void, Void, List<Note>> {

            @Override
            protected List<Note> doInBackground(Void... voids) {
                return NoteDatabase.getNoteDatabase(getContext()).noteDao().getAllNotes();
            }

            @Override
            protected void onPostExecute(List<Note> notes) {
                super.onPostExecute(notes);
                if (requestCode == REQUEST_CODE_SHOW_NOTE) {
                    noteList.addAll(notes);
                    notesAdapter.notifyDataSetChanged();
                } else if (requestCode == REQUEST_CODE_ADD_NOTE) {
                    noteList.add(0, notes.get(0));
                    notesAdapter.notifyItemInserted(0);
                    recyclerViewNote.smoothScrollToPosition(0);
                } else if (requestCode == REQUEST_CODE_UPDATE_NOTE) {
                    noteList.remove(noteClickedPosition);
                    if (isNoteDeleted) {
                        notesAdapter.notifyItemRemoved(noteClickedPosition);
                    } else {
                        noteList.add(noteClickedPosition, notes.get(noteClickedPosition));
                        notesAdapter.notifyItemChanged(noteClickedPosition);
                    }
                }
            }
        }

        new getNotesTask().execute();
    }

    @Override
    public void onNoteClicked(Note note, int position) {
        noteClickedPosition = position;
        Intent intent = new Intent(getContext(), CreateNoteActivity.class);
        intent.putExtra("isViewOrUpdate", true);
        intent.putExtra("note", note);
        startActivityForResult(intent, REQUEST_CODE_UPDATE_NOTE);
    }
}