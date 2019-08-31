package br.com.rochamendes.bakingappudacityproject.dataPersist;

import android.app.Application;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import br.com.rochamendes.bakingappudacityproject.entities.Recipes;
import br.com.rochamendes.bakingappudacityproject.UpdateService;

public class recipesViewModel extends AndroidViewModel {

    private recipesRepository repository;
    private LiveData<List<Recipes>> listRecipes;

    private MutableLiveData<UpdateService.bindService> updateServiceBinder = new MutableLiveData<>();
    private LiveData<Long> estimatedRemainingTime;
    private LiveData<Integer> numItems;
    private LiveData<Integer> position;
    private LiveData<Boolean> isUpdating;
    private LiveData<Recipes[]> recipesArray;

    public recipesViewModel(@NonNull Application application) {
        super(application);
        repository = new recipesRepository(application);

        listRecipes = repository.getListRecipes();
    }

    public LiveData<List<Recipes>> getListRecipes() {
        return listRecipes;
    }

    public LiveData<List<Recipes>> getRecipe(int id) {
        return repository.getRecipe(id);
    }

    public void updateFavorite(Recipes Recipe) {
        repository.updateFavorite(Recipe);
    }

    public void insertRecipes(Recipes[] Recipes) {
        repository.insertRecipes(Recipes);
    }

    public void deleteRecipes() {
        repository.deleteRecipes();
    }

    public LiveData<Long> getEstimatedRemainingTime() {
        return estimatedRemainingTime;
    }

    public LiveData<Integer> getNumItems() {
        return numItems;
    }

    public LiveData<Integer> getPosition() {
        return position;
    }

    public LiveData<Boolean> isUpdating() {
        return isUpdating;
    }

    public LiveData<Recipes[]> getRecipesArray() {
        return recipesArray;
    }

    private ServiceConnection updateServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder iBinder) {
            Log.d("Mensagem", "ServiceConnection: connected to service.");
            UpdateService.bindService binder = (UpdateService.bindService) iBinder;
            updateServiceBinder.postValue(binder);

            UpdateService updateService = binder.getService();
            estimatedRemainingTime = updateService.getEstimatedRemainingTime();
            numItems = updateService.getNumItems();
            position = updateService.getPosition();
            isUpdating = updateService.isUpdating();
            recipesArray = updateService.getRecipesArray();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.d("Mensagem", "ServiceConnection: disconnected from service.");
            updateServiceBinder.postValue(null);
        }
    };

    public ServiceConnection getUpdateServiceConnection() {
        return updateServiceConnection;
    }

    public LiveData<UpdateService.bindService> getUpdateServiceBinder() {
        return updateServiceBinder;
    }
}