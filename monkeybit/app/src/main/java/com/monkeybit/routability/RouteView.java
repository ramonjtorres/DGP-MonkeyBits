package com.monkeybit.routability;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.VolleyError;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class RouteView extends Fragment implements DBConnectInterface{
    View view;
    String choosen = null;
    private List<Comments> comments = new ArrayList<>();
    private RecyclerView listComments;
    private ArrayList<Place> places;
    private CommentsAdapter adapter;
    private int result = 0;
    private boolean isFavorite = false;
    private String email;
    private ImageButton favButton;
    private DBConnectInterface dbInter;
    private TextInputEditText commentText;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.view_rute, container, false);
        //Receive the id
        //choosen = getArguments().getString("routeId");
        choosen = "2";
        email = ((MainActivity) getActivity()).getUserEmail();
        dbInter = this;
        places = null;
        if(email != null) {
            DBConnect.getFavoriteRoutes(getContext(), this,email);
            favButton = view.findViewById(R.id.posRtFav);
            favButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (email != null) {
                        if (isFavorite) {
                            removeFavoritePlace();
                        } else {
                            addFavoritePlace();
                        }
                    } else {
                        Toast.makeText(getContext(), R.string.should_login, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(getContext(), R.string.should_login, Toast.LENGTH_SHORT);
        }


        final Button button = view.findViewById(R.id.postFollowBtRt);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                //@Todo enviar a map
                String aux = "Map";
                Toast info = Toast.makeText(getContext(),aux,Toast.LENGTH_SHORT);
                info.show();
            }
        });

        commentText = view.findViewById(R.id.myComment);

        Button button2 = view.findViewById(R.id.sendComment);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email != null && !commentText.getText().toString().equals("")){
                    Toast.makeText(getContext(), "Enviando comentario", Toast.LENGTH_SHORT).show();
                    addComment();
                }
                else if(email == null){
                    Toast.makeText(getContext(), R.string.should_login, Toast.LENGTH_SHORT).show();
                }
                else if(commentText.getText().toString().equals("")){
                    Toast.makeText(getContext(), R.string.empty_fields, Toast.LENGTH_SHORT).show();
                }
            }
        });

        listComments = view.findViewById(R.id.list_comments);
        LinearLayoutManager lim = new LinearLayoutManager(getContext());
        lim.setOrientation(LinearLayoutManager.VERTICAL);
        listComments.setLayoutManager(lim);

        initializedAdapter();

        DBConnect.getRoute(getContext(),this,choosen);


        return view;
        // return super.onCreateView(inflater, container, savedInstanceState);
    }
    public void addComment(){
        DBConnect.addPlaceComment(getContext(),this, choosen, email, commentText.getText().toString());
    }

    public void addFavoritePlace(){
        DBConnect.addAsFavoritePlace(getContext(), this, email, choosen);
    }

    public void removeFavoritePlace(){
        DBConnect.removeFavoritePlace(getContext(), this, email, choosen);
    }

    protected void Conf_List_Route(ArrayList<Place> dataList) {
        if(dataList != null){
            ListView list = view.findViewById(R.id.postListLug);
            //adapt to the list
            list.setAdapter(new AdapterList(getContext(), R.layout.post_rute, dataList) {
                @Override
                public void onPost(Object post, View view) {
                    if (post != null) {
                        TextView pt_tittle = view.findViewById(R.id.post_tittle);
                        if (pt_tittle != null)
                            pt_tittle.setText(((Place) post).getName());

                        TextView pt_desc = view.findViewById(R.id.post_desc);
                        if (pt_desc != null)
                            pt_desc.setText(((Place) post).getDescription());

                        ImageView image = view.findViewById(R.id.imageRoute);
                        if (image != null && ((Place) post).getImage() != null){
                                Picasso.get().load(((Place) post).getImage()).into(image);
                        }
                    }

                }
            });

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> post, View view, int pos, long id) {
                    //Toast toast = Toast.makeText(getContext()," Pulsado", Toast.LENGTH_SHORT);
                    //toast.show();

                    Place choosen = (Place) post.getItemAtPosition(pos);
                    PlaceView place = new PlaceView();
                    Bundle bundle = new Bundle();
                    bundle.putString("placeId", choosen.getIdPlace());
                    place.setArguments(bundle);

                    if (place != null) {
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_rp_view, place).commit(); //go to the fragment
                    }


                }
            });
        }




    }

    public void initializedAdapter () {
        if(comments.size() >=4){
            listComments.getLayoutParams().height = 1300;
        }
        else{
            listComments.getLayoutParams().height = RecyclerView.LayoutParams.WRAP_CONTENT;
        }
        CommentsAdapter adapter = new CommentsAdapter(comments);
        listComments.setAdapter(adapter);
    }

    public void setComments(JSONArray query1) throws JSONException {

        for(int i=0; i<query1.length(); i++) {

            JSONObject query = query1.getJSONObject(i);

            //Comments
            String author = "";
            if (query.has("Name")) {
                author = query.optString("Name");
            }

            String comment = "";
            if (query.has("Content")) {
                comment = query.optString("Content");
            }

            String date = "";
            if (query.has("Date")) {
                date = query.optString("Date");
            }

            String time = "";
            if (query.has("Time")) {
                time = query.optString("Time");
                comments.add(new Comments(author, comment, date, time));
                //Toast.makeText(getContext(), time, Toast.LENGTH_SHORT).show();
            }
        }
        initializedAdapter();
    }


    public void SetView(JSONObject bdelements ) throws JSONException {

            Route ruta = new Route(bdelements);
            TextView tittle = view.findViewById(R.id.postTittleRt);
            if (tittle != null){
               // Log.d("Debug", ruta.getName());
                if(ruta.getName()!=null)
                    tittle.setText(ruta.getName());
            }


            ImageView image = view.findViewById(R.id.imageRoute);
            if (image != null && bdelements.has("Image")){
                if(bdelements.optString("Image") != "")
                    Picasso.get().load(bdelements.optString("Image")).into(image);
            }


            TextView description = view.findViewById(R.id.postDescRt);
            if (description != null){
               // Log.d("Debug", ruta.getDescription());
                if(ruta.getDescription() != null)
                    description.setText(ruta.getDescription());
            }


    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getContext(), "Error BD: " + error, Toast.LENGTH_SHORT).show();
        result++;
    }

    @Override
    public void onResponse(JSONObject response) {
        try {
            if (response.has("OPERATIONS")) {

                for (int i = 0; i < response.getJSONArray("OPERATIONS").length(); i++) {
                    String operation = response.getJSONArray("OPERATIONS").getString(i);
                    if (response.has(operation)) { // Si no lo cumple, significa que no ha devuelto tuplas

                        if (operation.equals("GET_ROUTE")) {
                            JSONObject operationResult = response.getJSONObject(operation); // Este elemento tendrá la/s tupla/s
                            //Toast.makeText(getContext(), "Lugar\n" + operationResult.toString(), Toast.LENGTH_LONG).show();
                            SetView(operationResult);
                        }
                        if (operation.equals("GET_ROUTE_COMMENTS")) {
                            JSONArray operationResult = response.getJSONArray(operation); // Este elemento tendrá la/s tupla/s
                            this.setComments(operationResult);
                        }
                        if (operation.equals("GET_AVERAGE_SCORE_ROUTE")) {
                            double aux = -1;
                           try{
                               Double operationResult = response.getDouble(operation);

                                if( !operationResult.isNaN() || operationResult != null ){
                                    aux = operationResult;

                                }
                               SetViewRating(aux);
                           }
                           catch (JSONException e){
                               aux = -1;

                               SetViewRating(aux);
                           }

                        }
                        if (operation.equals("GET_FAVORITE_ROUTES")) {
                            JSONArray operationResult = response.getJSONArray(operation);
                            for (int j = 0; j < operationResult.length(); j++) {
                                String favRoute = operationResult.getJSONObject(j).getString("IdRoute");
                                if (choosen.equals(favRoute)) {
                                    setFavButtonState(true);
                                }
                            }
                        }
                        if(operation.equals("GET_PLACES_FROM_ROUTE")){
                            places = new ArrayList<Place>();
                            JSONArray operationResult = response.getJSONArray(operation);
                            Place aux;
                            Log.d("Debug", "size: "+ operationResult.length());
                            for (int j = 0; j < operationResult.length(); j++) {
                                Log.d("Debug", "obj: "+ operationResult.getJSONObject(i));
                                Log.d("Debug","otra linea");
                                aux = new Place(operationResult.getJSONObject(i));
                                places.add(aux);
                            }
                            this.Conf_List_Route(places);
                        }
                    }
                    // Estas operaciones, no necesitan datos de vuelta, por eso no están dentro del if anterior
                    if (operation.equals("ADD_FAVORITE_ROUTE")) {
                        Toast.makeText(getContext(), R.string.added_favorites, Toast.LENGTH_SHORT).show();
                        setFavButtonState(true);
                    }
                    if (operation.equals("REMOVE_FAVORITE_ROUTE")) {
                        Toast.makeText(getContext(), R.string.remove_favorites, Toast.LENGTH_SHORT).show();
                        setFavButtonState(false);
                    }
                }
                SetView(response);
            } else {
                Toast.makeText(getContext(),"ERROR", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        result ++;

        //@TODO se deberia mejorar esta comprobacion
        if (result >= 0){
            initializedAdapter();
            result = 0;
        }
    }


    private void SetViewRating(double rat){

        TextView rating = view.findViewById(R.id.postRtRating);

        if (rating != null) {

            if(rat == -1){

                rating.setText(getString(R.string.notrating));
            }
            else{

                rating.setText(" "+rat);
            }

        }


    }
    private void setFavButtonState(boolean activate) {
        isFavorite = activate;
        favButton.setSelected(activate);
    }
}

