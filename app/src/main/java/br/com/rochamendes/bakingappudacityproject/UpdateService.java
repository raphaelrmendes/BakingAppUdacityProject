package br.com.rochamendes.bakingappudacityproject;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import br.com.rochamendes.bakingappudacityproject.entities.Ingredients;
import br.com.rochamendes.bakingappudacityproject.entities.Recipes;
import br.com.rochamendes.bakingappudacityproject.entities.Steps;

public class UpdateService extends Service {

    private static final String searchURL = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";
    private static final String cxParameterFromGoogle = "004608261399183694890:ulagds49fas";
    private static final String str_id = "id";
    private static final String str_name = "name";
    private static final String str_ingredients = "ingredients";
    private static final String str_steps = "steps";
    private static final String str_servings = "servings";
    private static final String str_image = "image";
    private static final String str_quantity = "quantity";
    private static final String str_measure = "measure";
    private static final String str_ingredient = "ingredient";
    private static final String str_shortDescription = "shortDescription";
    private static final String str_description = "description";
    private static final String str_videoURL = "videoURL";
    private static final String str_thumbnailURL = "thumbnailURL";
    private static final String VideoExtension = ".mp4";
    private static final String PhotoExtension = ".jpg";
    private SharedPreferences preferences;
    private final IBinder binder = new bindService();
    private MutableLiveData<Boolean> updating = new MutableLiveData<>();
    private MutableLiveData<Integer> position = new MutableLiveData<>();
    private MutableLiveData<Integer> numItems = new MutableLiveData<>();
    private MutableLiveData<Long> estimatedRemainingTime = new MutableLiveData<>();
    private MutableLiveData<Recipes[]> recipesArray = new MutableLiveData<>();

    @Override
    public void onCreate() {
        super.onCreate();
        preferences = getSharedPreferences("appPreferences", MODE_PRIVATE);
        new updateRecipes().execute();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class bindService extends Binder {
        public UpdateService getService(){
            return UpdateService.this;
        }
    }

    public LiveData<Boolean> isUpdating(){
        return updating;
    }

    public LiveData<Integer> getPosition(){
        return position;
    }

    public LiveData<Integer> getNumItems(){
        return numItems;
    }

    public LiveData<Long> getEstimatedRemainingTime(){
        return estimatedRemainingTime;
    }

    public LiveData<Recipes[]> getRecipesArray(){
        return recipesArray;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void Refresh(){
        new updateRecipes().execute();
    }

    public class updateRecipes extends AsyncTask<Void, Void, Recipes[]> {
        protected void onPreExecute(){
            super.onPreExecute();
            // on MainActivity, sets up the layout to show progress and hide recycler
            updating.postValue(true);
        }

        protected Recipes[] doInBackground(Void... voids) {
            // Updates only if already passed time specified on Update Interval since Last Update
            // Refresh button sets Last Refresh to Zero, so it will pass through this point
            long updateInterval = preferences.getLong("updateInterval", 86400);
            long lastRefresh = preferences.getLong("lastUpdate", 0);
            if (lastRefresh + updateInterval <= System.currentTimeMillis() / 1000) {
                // Gets data provided from the URL for saving in a Room Database.
                // Images are also saved in the database as byte array, then even if users open if without internet
                // connection, it look nice and saves data. If no images are available, then it searches one from
                // Google and places with a watermark logo "powered by Google" over it.
                // This process takes a while, so decided to let users know exactly whats happening on progress
                // though graphic progressbar, text of item, number of items, estimated remaining time and a notification
                try {
                    URL url = new URL(searchURL);
                    URLConnection request = url.openConnection();
                    request.connect();

                    JsonParser JsonData = new JsonParser();
                    JsonElement JsonRoot = JsonData.parse(new InputStreamReader((InputStream) request.getContent()));
                    JsonArray JsonSearch = JsonRoot.getAsJsonArray();

                    int numOfItems = JsonSearch.size();
                    numItems.postValue(numOfItems);
                    Log.i("Mensagem", "JsonSearch.size() = " + numOfItems);
                    Recipes[] recipesArray = new Recipes[JsonSearch.size()];
                    long[] t = new long[numOfItems+1];
                    for (int pos = 0; pos < numOfItems; pos++) {
                        position.postValue(pos);
                        Log.i("Mensagem", "Position # " + pos);
                        t[pos] = System.currentTimeMillis();

                        recipesArray[pos] = new Recipes(0, null, null, null, 0, null,false, false);

                        JsonObject RecipeJson = JsonSearch.get(pos).getAsJsonObject();

                        JsonPrimitive idRecipe = RecipeJson.getAsJsonPrimitive(str_id);
                        JsonPrimitive name = RecipeJson.getAsJsonPrimitive(str_name);
                        JsonArray ingredients = RecipeJson.getAsJsonArray(str_ingredients);
                        JsonArray steps = RecipeJson.getAsJsonArray(str_steps);
                        JsonPrimitive servings = RecipeJson.getAsJsonPrimitive(str_servings);
                        JsonPrimitive image = RecipeJson.getAsJsonPrimitive(str_image);

                        recipesArray[pos].setIdRecipes(idRecipe.getAsInt());
                        recipesArray[pos].setName(name.getAsString());
                        recipesArray[pos].setServings(servings.getAsInt());

                        Ingredients[] ingredientsArray = new Ingredients[ingredients.size()];
                        for (int ii = 0; ii < ingredients.size(); ii++) {
                            ingredientsArray[ii] = new Ingredients(0, null, null);

                            JsonObject Ingredient = ingredients.get(ii).getAsJsonObject();
                            JsonPrimitive quantity = Ingredient.getAsJsonPrimitive(str_quantity);
                            JsonPrimitive measure = Ingredient.getAsJsonPrimitive(str_measure);
                            JsonPrimitive ingredient = Ingredient.getAsJsonPrimitive(str_ingredient);
                            ingredientsArray[ii].setQuantity(quantity.getAsFloat());
                            ingredientsArray[ii].setMeasure(measure.getAsString());
                            ingredientsArray[ii].setIngredient(ingredient.getAsString());
                        }
                        recipesArray[pos].setIngredientsList(ingredientsArray);

                        Steps[] stepsArray = new Steps[steps.size()];
                        for (int is = 0; is < steps.size(); is++) {
                            stepsArray[is] = new Steps(0, null, null, null, null);

                            JsonObject Steps = steps.get(is).getAsJsonObject();
                            JsonPrimitive id = Steps.getAsJsonPrimitive(str_id);
                            JsonPrimitive shortDescription = Steps.getAsJsonPrimitive(str_shortDescription);
                            JsonPrimitive description = Steps.getAsJsonPrimitive(str_description);
                            JsonPrimitive videoURL = Steps.getAsJsonPrimitive(str_videoURL);
                            JsonPrimitive thumbnailURL = Steps.getAsJsonPrimitive(str_thumbnailURL);
                            stepsArray[is].setId(id.getAsInt());
                            stepsArray[is].setShortDescription(shortDescription.getAsString());
                            stepsArray[is].setDescription(description.getAsString());
                            String notSureVideoURL = videoURL.getAsString();
                            String notSureThumbnailURL = thumbnailURL.getAsString();
                            String videoURLfileExtension = "";
                            String thumbnailURLfileExtension = "";
                            String VideoURL = "";
                            String ThumbnailURL = "";
                            if (notSureVideoURL.lastIndexOf(".") != -1) {
                                videoURLfileExtension = notSureVideoURL
                                        .substring(notSureVideoURL.lastIndexOf("."));
                            }
                            if (notSureThumbnailURL.lastIndexOf(".") != -1) {
                                thumbnailURLfileExtension = notSureThumbnailURL
                                        .substring(notSureThumbnailURL.lastIndexOf("."));
                            }
                            if (videoURLfileExtension.equals(VideoExtension)) {
                                VideoURL = notSureVideoURL;
                            } else if (videoURLfileExtension.equals(PhotoExtension) && !thumbnailURLfileExtension.equals(PhotoExtension)) {
                                ThumbnailURL = notSureVideoURL;
                            }
                            if (thumbnailURLfileExtension.equals(PhotoExtension)) {
                                ThumbnailURL = notSureThumbnailURL;
                            } else if (thumbnailURLfileExtension.equals(VideoExtension) && !videoURLfileExtension.equals(VideoExtension)) {
                                VideoURL = notSureThumbnailURL;
                            }
                            stepsArray[is].setVideoURL(VideoURL);
                            stepsArray[is].setThumbnailURL(ThumbnailURL);
                        }
                        recipesArray[pos].setStepsList(stepsArray);

                        String Image = image.getAsString();
                        byte[] finalImage;
                        if (!Image.equals("")) {
                            Log.i("Mensagem", "Image = " + Image);
                            URL urlImage = new URL(Image);
                            Bitmap bitmapImage = BitmapFactory.decodeStream(urlImage.openConnection().getInputStream());
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                            bitmapImage.recycle();
                            finalImage = stream.toByteArray();
                            recipesArray[pos].setImageAvailable(true);
                        } else {
                            Uri.Builder GoogleImageSearchUri = new Uri.Builder();
                            GoogleImageSearchUri.scheme("https")
                                    .authority("www.googleapis.com")
                                    .appendPath("customsearch")
                                    .appendPath("v1")
                                    .appendQueryParameter("key", BuildConfig.Google_Search_API_key)
                                    .appendQueryParameter("cx", cxParameterFromGoogle)
                                    .appendQueryParameter("q", name.getAsString())
                                    .appendQueryParameter("fileType", "jpg")
                                    .appendQueryParameter("imgSize", "medium")
                                    .appendQueryParameter("imgType", "photo")
                                    .appendQueryParameter("num", "1")
                                    .appendQueryParameter("safe", "active")
                                    .appendQueryParameter("searchType", "image");
                            String GoogleImageSearchUrl = GoogleImageSearchUri.build().toString();
                            URL GoogleSearchURL = new URL(GoogleImageSearchUrl);
                            URLConnection requestGoogleSearch = GoogleSearchURL.openConnection();
                            requestGoogleSearch.connect();

                            JsonParser JsonGoogleData = new JsonParser();
                            JsonElement JsonGoogleRoot = JsonGoogleData
                                    .parse(new InputStreamReader((InputStream) requestGoogleSearch.getContent()));
                            JsonObject JsonGoogleSearch = JsonGoogleRoot.getAsJsonObject();
                            JsonArray JsonGoogleArray = JsonGoogleSearch.getAsJsonArray("items");
                            JsonObject item = JsonGoogleArray.get(0).getAsJsonObject();
                            JsonPrimitive link = item.getAsJsonPrimitive("link");
                            Log.i("Mensagem", "link = " + link);

                            URL urlGooglePhotoLink = new URL(link.getAsString());
                            Bitmap googleImageBitmap = BitmapFactory
                                    .decodeStream(urlGooglePhotoLink.openConnection().getInputStream());
                            ByteArrayOutputStream streamGoogle = new ByteArrayOutputStream();
                            googleImageBitmap.compress(Bitmap.CompressFormat.JPEG, 80, streamGoogle);
                            googleImageBitmap.recycle();
                            finalImage = streamGoogle.toByteArray();
                            recipesArray[pos].setImageAvailable(false);
                        }
                        recipesArray[pos].setImage(finalImage);
                        recipesArray[pos].setFavorite(preferences.getBoolean("favorite" + idRecipe.getAsString(), false));
                        t[pos+1] = System.currentTimeMillis();
                        long ERT = ((t[pos+1] - t[pos])) * (numOfItems - pos-1);
                        estimatedRemainingTime.postValue(ERT);
                        Log.i("Mensagem", "Estimated Remaining Time: " + ERT + " milliseconds...");
                    }
                    preferences.edit().putLong("lastUpdate", System.currentTimeMillis() / 1000).apply();
                    return recipesArray;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
            return null;
        }

        protected void onPostExecute(Recipes[] Recipes) {
            updating.postValue(false);
            if (Recipes != null) {
                for (int i = 0; i < Recipes.length; i++) {
                    Log.i("Mensagem", "New recipe #" + i + " (Name) = " + Recipes[i].getName());
                }
                recipesArray.postValue(Recipes);
            }
            stopSelf();
        }
    }
}