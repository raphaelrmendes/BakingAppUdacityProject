package br.com.rochamendes.bakingappudacityproject.dataPersist;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import br.com.rochamendes.bakingappudacityproject.entities.Recipes;

@Database(entities = {Recipes.class}, version = 1, exportSchema = false)
@TypeConverters(DataConverter.class)
public abstract class recipesDatabase extends RoomDatabase{
    private static recipesDatabase instance;
    public abstract recipesDAO RecipesDAO();
    public static synchronized recipesDatabase getInstance(Context context){
        if (instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    recipesDatabase.class, "recipes_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }
    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db){
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }
    };
    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void>{
        private recipesDAO RecipesDAO;
        private PopulateDbAsyncTask(recipesDatabase db){
            RecipesDAO = db.RecipesDAO();
        }
        @Override
        protected Void doInBackground(Void... voids){
            return null;
        }
    }
}
