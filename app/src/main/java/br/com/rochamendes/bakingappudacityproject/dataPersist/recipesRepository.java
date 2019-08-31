package br.com.rochamendes.bakingappudacityproject.dataPersist;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.List;

import br.com.rochamendes.bakingappudacityproject.entities.Recipes;

public class recipesRepository {
    private recipesDAO RecipesDAO;
    private LiveData<List<Recipes>> listRecipes;

    public recipesRepository(Application application){
        recipesDatabase database = recipesDatabase.getInstance(application);
        RecipesDAO = database.RecipesDAO();

        listRecipes = RecipesDAO.listRecipes();
    }

    public LiveData<List<Recipes>> getListRecipes(){
        return listRecipes;
    }

    public LiveData<List<Recipes>> getRecipe(int id) { return RecipesDAO.Recipe(id); }

    public void updateFavorite(Recipes Recipe){
        new UpdateFavoriteAsyncTask(RecipesDAO).execute(Recipe);
    }

    public void insertRecipes(Recipes[] Recipes){
        Log.i("Mensagem", "Saving " + Recipes.length + " Recipes");
        new InsertRecipesAsyncTask(RecipesDAO).execute(Recipes);
    }

    public void deleteRecipes(){
        new DeleteRecipesAsyncTask(RecipesDAO).execute();
    }

    private static class UpdateFavoriteAsyncTask extends AsyncTask<Recipes, Void, Void>{
        private recipesDAO RecipesDAO;
        private UpdateFavoriteAsyncTask(recipesDAO RecipesDAO){
            this.RecipesDAO = RecipesDAO;
        }
        @Override
        protected Void doInBackground(Recipes... recipes) {
            RecipesDAO.updateFavorite(recipes[0]);
            return null;
        }
    }

    private static class InsertRecipesAsyncTask extends AsyncTask<Recipes, Void, Void>{
        private recipesDAO RecipesDAO;
        private InsertRecipesAsyncTask(recipesDAO RecipesDAO){
            this.RecipesDAO = RecipesDAO;
        }
        @Override
        protected Void doInBackground(Recipes... recipes) {
            RecipesDAO.insertRecipes(recipes);
            return null;
        }
    }

    private static class DeleteRecipesAsyncTask extends AsyncTask<Void, Void, Void>{
        private recipesDAO RecipesDAO;
        private DeleteRecipesAsyncTask(recipesDAO RecipesDAO){
            this.RecipesDAO = RecipesDAO;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            RecipesDAO.deleteRecipes();
            return null;
        }
    }
}
