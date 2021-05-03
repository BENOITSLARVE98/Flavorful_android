package com.example.flavorful.net;

import android.os.AsyncTask;

import com.example.flavorful.object.Recipe;
import com.example.flavorful.ui.discover.DiscoverViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetRecipesTask extends AsyncTask<String, Void, ArrayList<Recipe>> {

    final ArrayList<Recipe> recipes= new ArrayList<>();
    JSONArray instructions = new JSONArray();
    String video;
    JSONArray sections;

    @Override
    protected ArrayList<Recipe> doInBackground(String... strings) {

        OkHttpClient client = new OkHttpClient();
        Request request;

        String tag = strings[0];
        String searchWord = strings[1];

        request = new Request.Builder()
                .url(String.format("https://tasty.p.rapidapi.com/recipes/list?from=0&size=100&tags=%s&q=%s"
                        , tag, searchWord))
                .get()
                .addHeader("x-rapidapi-key", "59d338597cmsh4f789cced9df6a2p1b0e7fjsna39bda851ccd")
                .addHeader("x-rapidapi-host", "tasty.p.rapidapi.com")
                .build();

//        if (word != null) {
//            request = new Request.Builder()
//                    .url(String.format("https://tasty.p.rapidapi.com/recipes/list?from=0&size=20&tags=under_30_minutes=%a"
//                            , word))
//                    .get()
//                    .addHeader("x-rapidapi-key", "59d338597cmsh4f789cced9df6a2p1b0e7fjsna39bda851ccd")
//                    .addHeader("x-rapidapi-host", "tasty.p.rapidapi.com")
//                    .build();
//        } else {
//            request = new Request.Builder()
//                    .url("https://tasty.p.rapidapi.com/recipes/list?from=0&size=20&tags=under_30_minutes")
//                    .get()
//                    .addHeader("x-rapidapi-key", "59d338597cmsh4f789cced9df6a2p1b0e7fjsna39bda851ccd")
//                    .addHeader("x-rapidapi-host", "tasty.p.rapidapi.com")
//                    .build();
//        }

        try {
            Response response = client.newCall(request).execute();

            assert response.body() != null;
            String data = Objects.requireNonNull(response.body()).string();
            JSONObject jsonObject = new JSONObject(data);
            JSONArray results = jsonObject.getJSONArray("results");

            for(int i = 0; i < results.length(); i++) {
                JSONObject obj = results.getJSONObject(i);

                String name = obj.getString("name");
                String image = obj.getString("thumbnail_url");
                if (obj.has("original_video_url")) {
                    video = obj.getString("original_video_url");
                }
                if (obj.has("instructions")) {
                    instructions = obj.getJSONArray("instructions");
                }
                if (obj.has("sections")) {
                    sections = obj.getJSONArray("sections");
                }


                //Instructions Array
                ArrayList<String> instructionArray = new ArrayList<>();
                for(int e = 0; e < instructions.length(); e++) {
                    JSONObject otherObj = instructions.getJSONObject(e);

                    String instructionText = otherObj.getString("display_text");

                    instructionArray.add(instructionText);
                }

                //Sections Array
                ArrayList<String> ingredientArray = new ArrayList<>();
                for(int a = 0; a < sections.length(); a++) {
                    JSONObject sectionObj = sections.getJSONObject(a);

                    JSONArray components = sectionObj.getJSONArray("components");

                    for(int l = 0; l < components.length(); l++) {
                        JSONObject ingredientObj = components.getJSONObject(l);

                        String ingredient = ingredientObj.getString("raw_text");
                        ingredientArray.add(ingredient);
                    }
                }

                recipes.add(new Recipe(name, image, video, instructionArray, ingredientArray));
            }

            return recipes;

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<Recipe> recipes) {
        super.onPostExecute(recipes);
        DiscoverViewModel.loadRecipes(recipes);
    }
}
