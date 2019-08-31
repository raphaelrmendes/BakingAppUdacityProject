package br.com.rochamendes.bakingappudacityproject.dataPersist;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;

import br.com.rochamendes.bakingappudacityproject.entities.Ingredients;
import br.com.rochamendes.bakingappudacityproject.entities.Steps;

public class DataConverter implements Serializable {

    @TypeConverter
    public String fromIngredientsArray(Ingredients[] ingredients) {
        if (ingredients == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<Ingredients[]>() {
        }.getType();
        return gson.toJson(ingredients, type);
    }

    @TypeConverter
    public Ingredients[] toIngredientsArray(String ingredientsString) {
        if (ingredientsString == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<Ingredients[]>() {
        }.getType();
        return gson.fromJson(ingredientsString, type);
    }

    @TypeConverter
    public String fromStepsArray(Steps[] steps) {
        if (steps == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<Steps[]>() {
        }.getType();
        return gson.toJson(steps, type);
    }

    @TypeConverter
    public Steps[] toStepsArray(String stepsString) {
        if (stepsString == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<Steps[]>() {
        }.getType();
        return gson.fromJson(stepsString, type);
    }
}