package com.monkeybit.routability;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlacesViewHolder> {

    private List<Place> places;

    public PlaceAdapter(List<Place> places) {
        this.places = places;
    }

    @NonNull
    @Override
    public PlacesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        //View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_comments,parent,false);

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_place,null,false);

        return new PlacesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlacesViewHolder placesViewHolder, int posicion) {

        Place place = this.places.get(posicion);
        placesViewHolder.name.setText(place.getName());
        placesViewHolder.description.setText(place.getDescription());
        placesViewHolder.image.setText(place.getImage());
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    public class PlacesViewHolder extends RecyclerView.ViewHolder{
        private TextView name;
        private TextView description;
        private TextView image;

        public PlacesViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tittlePlace);
            description = itemView.findViewById(R.id.descriptionPlace);
            image = itemView.findViewById(R.id.imgPlacee);
        }
    }
}