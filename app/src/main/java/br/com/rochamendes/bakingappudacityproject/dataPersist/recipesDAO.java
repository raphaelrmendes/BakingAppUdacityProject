package br.com.rochamendes.bakingappudacityproject.dataPersist;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import br.com.rochamendes.bakingappudacityproject.entities.Recipes;

@Dao
public interface recipesDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRecipes(Recipes[] Recipes);

    @Update
    void updateFavorite(Recipes Recipe);

    @Query("DELETE FROM table_recipes")
    void deleteRecipes();

    @Query("SELECT * FROM table_recipes WHERE idRecipes = :idRecipe")
    LiveData<List<Recipes>> Recipe(int idRecipe);

    @Query("SELECT * FROM table_recipes ORDER BY idRecipes")
    LiveData<List<Recipes>> listRecipes();
}
