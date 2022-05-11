package com.yuchieh02.omdbapi;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

public class RecycleView_Movie extends RecyclerView.Adapter<RecycleView_Movie.ViewHolder> {

    private ArrayList<HashMap<String, String>> receiveDATA = new ArrayList<>();
    private Activity activity;

    public RecycleView_Movie(ArrayList<HashMap<String, String>> receiveDATA, Activity activity) {
        this.receiveDATA = receiveDATA;
        this.activity = activity;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public ArrayList<HashMap<String, String>> getReceiveDATA() {
        return receiveDATA;
    }

    public void setReceiveDATA(ArrayList<HashMap<String, String>> receiveDATA) {
        this.receiveDATA = receiveDATA;
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView cardview_item_title, cardview_item_year, cardview_item_rated, cardview_item_released,
                cardview_item_runtime, cardview_item_genre, cardview_item_director, cardview_item_writer, cardview_item_actors,
                cardview_item_plot;

        private View mView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardview_item_title = itemView.findViewById(R.id.cardview_item_title);
            cardview_item_year = itemView.findViewById(R.id.cardview_item_year);
            cardview_item_rated = itemView.findViewById(R.id.cardview_item_rated);
            cardview_item_released = itemView.findViewById(R.id.cardview_item_released);
            cardview_item_runtime = itemView.findViewById(R.id.cardview_item_runtime);
            cardview_item_genre = itemView.findViewById(R.id.cardview_item_genre);
            cardview_item_director = itemView.findViewById(R.id.cardview_item_director);
            cardview_item_writer = itemView.findViewById(R.id.cardview_item_writer);
            cardview_item_actors = itemView.findViewById(R.id.cardview_item_actors);
            cardview_item_plot = itemView.findViewById(R.id.cardview_item_plot);
            mView = itemView;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.cardview_item_title.setText("Title：" + receiveDATA.get(holder.getAdapterPosition()).get("title"));
        holder.cardview_item_year.setText("Year：" + receiveDATA.get(holder.getAdapterPosition()).get("year"));
        holder.cardview_item_rated.setText("Rated：" + receiveDATA.get(holder.getAdapterPosition()).get("rated"));
        holder.cardview_item_released.setText("Released：" + receiveDATA.get(holder.getAdapterPosition()).get("released"));
        holder.cardview_item_runtime.setText("Runtime：" + receiveDATA.get(holder.getAdapterPosition()).get("runtime"));
        holder.cardview_item_genre.setText("Genre：" + receiveDATA.get(holder.getAdapterPosition()).get("genre"));
        holder.cardview_item_director.setText("Director：" + receiveDATA.get(holder.getAdapterPosition()).get("director"));
        holder.cardview_item_writer.setText("Writer：" + receiveDATA.get(holder.getAdapterPosition()).get("writer"));
        holder.cardview_item_actors.setText("Actors：" + receiveDATA.get(holder.getAdapterPosition()).get("actors"));
        holder.cardview_item_plot.setText("Plot：" + receiveDATA.get(holder.getAdapterPosition()).get("plot"));
    }

    @Override
    public int getItemCount() {
        return receiveDATA.size();
    }

    public void closeKeyBoard() {
        View view = activity.getCurrentFocus();
        if(view!=null){
            InputMethodManager inputMethodManager=(InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }


}
