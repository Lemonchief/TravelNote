package com.example.travelnote.adapter;

import com.example.travelnote.NoteActivity;
import com.example.travelnote.MainActivity;

import android.content.Intent;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.cardview.widget.CardView;

import com.example.travelnote.R;
import com.example.travelnote.model.Note;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {
    private Context context;
    private List<Note> noteList;
    private boolean deleteMode = false;

    // Интерфейс для удаления
    public interface OnNoteDeleteListener {
        void onNoteDelete(Note note, int position);
    }

    private OnNoteDeleteListener onNoteDeleteListener;

    public void setOnNoteDeleteListener(OnNoteDeleteListener listener) {
        this.onNoteDeleteListener = listener;
    }

    public NoteAdapter(Context context, List<Note> noteList) {
        this.context = context;
        this.noteList = noteList;
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        public TextView titleView;
        public TextView descView;
        public TextView dateView;
        public CardView cardView;

        public NoteViewHolder(View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.textViewTitle);
            descView = itemView.findViewById(R.id.textViewDescription);
            dateView = itemView.findViewById(R.id.textViewDate);
            cardView = itemView.findViewById(R.id.cardViewNote);
        }
    }

    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NoteViewHolder holder, int position) {
        Note note = noteList.get(position);
        holder.titleView.setText(note.getTitle());
        holder.descView.setText(note.getDescription());
        holder.dateView.setText(note.getDate());

        if (deleteMode) {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.delete_mode_background));
        } else {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.dark_yellow));
        }

        holder.itemView.setOnClickListener(v -> {
            if (deleteMode) {
                if (onNoteDeleteListener != null) {
                    onNoteDeleteListener.onNoteDelete(note, holder.getAdapterPosition());
                }
            } else {
                Intent intent = new Intent(context, NoteActivity.class);
                intent.putExtra("note", note);
                intent.putExtra("position", position);
                ((MainActivity) context).startActivityForResult(intent, MainActivity.REQUEST_CODE_VIEW_NOTE);
            }
        });
    }

    public void setDeleteMode(boolean mode) {
        this.deleteMode = mode;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }
}
