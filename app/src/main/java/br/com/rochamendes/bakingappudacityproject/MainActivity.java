package br.com.rochamendes.bakingappudacityproject;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.com.rochamendes.bakingappudacityproject.adapters.MainActivityRecipesAdapter;
import br.com.rochamendes.bakingappudacityproject.dataPersist.recipesViewModel;
import br.com.rochamendes.bakingappudacityproject.databinding.ActivityMainBinding;
import br.com.rochamendes.bakingappudacityproject.entities.Recipes;
import br.com.rochamendes.bakingappudacityproject.masterDetailFlowPlus.RecipeActivity;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences preferences;
    private ActivityMainBinding activityMainBinding;
    private recipesViewModel RecipesViewModel;
    private UpdateService updateService;
    private RecyclerView RecipesRecycler;
    private RecyclerView.LayoutManager RecipesLayout;
    private MainActivityRecipesAdapter mainActivityRecipesAdapter;
    private boolean order;
    private int numColumns;
    private NotificationManagerCompat notificationManagerCompat;
    public static final int ID_Notification_Updating = 1;

    private BakingIdlingResource bakingIdlingResource;

    public BakingIdlingResource getBakingIdlingResource() {
        return bakingIdlingResource;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (bakingIdlingResource == null){
            bakingIdlingResource = new BakingIdlingResource();
            bakingIdlingResource.setIdleState(false);
        }

        Log.i("Mensagem", "onCreate");
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        Toolbar toolbar = activityMainBinding.toolbar;
        setSupportActionBar(toolbar);
        if (activityMainBinding.contentLargePanel != null) {
            numColumns = 4;
        } else {
            numColumns = 2;
        }
        Log.i("Mensagem", "numColumns = " + numColumns);
        preferences = getSharedPreferences("appPreferences", MODE_PRIVATE);
        preferences.registerOnSharedPreferenceChangeListener(this);
        order = preferences.getBoolean("order", false);
        RecipesViewModel = ViewModelProviders.of(this).get(recipesViewModel.class);
        RecipesRecycler = activityMainBinding.recipesRecyclerview;
        mainActivityRecipesAdapter = new MainActivityRecipesAdapter();
        RecipesRecycler.setAdapter(mainActivityRecipesAdapter);
        RecipesLayout = new GridLayoutManager(MainActivity.this, numColumns);
        RecipesRecycler.setLayoutManager(RecipesLayout);
        notificationManagerCompat = NotificationManagerCompat.from(getBaseContext());
        updateService();
        showRecipes();
        startService();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("Mensagem", "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("Mensagem", "onStop");
        if (RecipesViewModel.getUpdateServiceBinder() != null){
            unbindService(RecipesViewModel.getUpdateServiceConnection());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("Mensagem", "onRestart");
        startService();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("Mensagem", "onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("Mensagem", "onDestroy");
        preferences.unregisterOnSharedPreferenceChangeListener(this);
        // Cancel notification of Playing Video, if the user suddenly closes from the Recent Apps List
        notificationManagerCompat.cancel(Notifications.ID_Notification_Playing);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        Log.i("Mensagem", "onSharedPreferenceChanged (name): " + s);
        if (s.equals("favorites") || s.equals("wannaDeleteFavorites")) {
            showRecipes();
        }
    }

    private void startService(){
        Intent serviceIntent = new Intent(this, UpdateService.class);
        startService(serviceIntent);
        bindService();
    }

    private void bindService(){
        Intent serviceBindService = new Intent(this, UpdateService.class);
        bindService(serviceBindService, RecipesViewModel.getUpdateServiceConnection(), Context.BIND_AUTO_CREATE);
    }

    private void updateService() {
        RecipesViewModel.getUpdateServiceBinder().observe(this, new Observer<UpdateService.bindService>() {
            @Override
            public void onChanged(@Nullable UpdateService.bindService binder) {
                Log.i("Mensagem", "onChange binder....");
                updateService = binder.getService();

                if (updateService != null) {
                    Log.i("Mensagem", "updateService ok....");
                    RecipesViewModel.isUpdating().observe(MainActivity.this, new Observer<Boolean>() {
                        @Override
                        public void onChanged(Boolean isUpdating) {
                            Log.i("Mensagem", "onChange isUpdating....");
                            if (isUpdating) {
                                activityMainBinding.recipesRecyclerview.setVisibility(View.GONE);
                                activityMainBinding.progressBar.setVisibility(View.VISIBLE);
                                activityMainBinding.progressPercent.setVisibility(View.VISIBLE);
                                activityMainBinding.progressInfoTime.setVisibility(View.VISIBLE);
                                activityMainBinding.progressInfoItems.setVisibility(View.VISIBLE);
                                RecipesViewModel.getEstimatedRemainingTime().observe(MainActivity.this, new Observer<Long>() {
                                    @Override
                                    public void onChanged(Long estimatedRemainingTime) {
                                        Log.i("Mensagem", "onChange estimatedRemainingTime....");
                                        StringBuilder ETR = new StringBuilder();
                                        if (estimatedRemainingTime != 0) {
                                            ETR.append("Estimated time remaining: ")
                                                    .append(estimatedRemainingTime/1000)
                                                    .append(" seconds");
                                        } else {
                                            ETR.append("Estimating remaining time...");
                                        }
                                        activityMainBinding.progressInfoTime.setText(ETR);
                                    }
                                });
                                RecipesViewModel.getNumItems().observe(MainActivity.this, new Observer<Integer>() {
                                    @Override
                                    public void onChanged(Integer numItems) {
                                        RecipesViewModel.getPosition().observe(MainActivity.this, new Observer<Integer>() {
                                            @Override
                                            public void onChanged(Integer position) {
                                                if (numItems != 0){
                                                    float floatNumItems = numItems;
                                                    float floatPosition = position;
                                                    float concluded = (1-((floatNumItems-floatPosition)/floatNumItems))*100;
                                                    Log.i("Mensagem", "% Concluded: " + concluded);
                                                    StringBuilder percentConcluded = new StringBuilder();
                                                    percentConcluded.append(concluded).append(" %");
                                                    activityMainBinding.progressPercent.setText(percentConcluded);
                                                    activityMainBinding.progressBar.setProgress(Math.round(concluded));
                                                    StringBuilder infoItemsRemaining = new StringBuilder();
                                                    infoItemsRemaining.append("Retrieving ").append(position+1)
                                                            .append(" of ").append(numItems).append(" Recipes...");
                                                    activityMainBinding.progressInfoItems.setText(infoItemsRemaining);

                                                    NotificationCompat.Builder builder = new NotificationCompat.Builder(
                                                            getBaseContext().getApplicationContext(), Notifications.CHANNEL_2_ID);
                                                    PendingIntent contentPendingIntent = PendingIntent.getActivity
                                                            (getBaseContext(), 0, new Intent(getBaseContext(), MainActivity.class), 0);

                                                    builder.setSmallIcon(R.drawable.ic_refresh_white_24dp)
                                                            .setContentTitle(MainActivity.this.getTitle())
                                                            .setContentText("Acquiring Recipes from server")
                                                            .setContentIntent(contentPendingIntent)
                                                            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                                            .setProgress(numItems, position, false)
                                                            .build();
                                                    notificationManagerCompat.notify(Notifications.ID_Notification_Updating, builder.build());                                                }
                                            }
                                        });
                                    }
                                });
                            } else {
                                RecipesViewModel.getRecipesArray().observe(MainActivity.this, new Observer<Recipes[]>() {
                                    @Override
                                    public void onChanged(Recipes[] recipes) {
                                        Log.i("Mensagem", "onChange Recipes....");
                                        if (recipes != null) {
                                            Log.i("Mensagem", "Saving Recipes to the database...");
                                            RecipesViewModel.insertRecipes(recipes);
                                        }
                                    }
                                });
                                notificationManagerCompat.cancel(ID_Notification_Updating);
                                activityMainBinding.recipesRecyclerview.setVisibility(View.VISIBLE);
                                activityMainBinding.progressBar.setVisibility(View.GONE);
                                activityMainBinding.progressPercent.setVisibility(View.GONE);
                                activityMainBinding.progressInfoTime.setVisibility(View.GONE);
                                activityMainBinding.progressInfoItems.setVisibility(View.GONE);
                                mainActivityRecipesAdapter.notifyDataSetChanged();
                                bakingIdlingResource.setIdleState(true);
                            }
                        }
                    });
                }
            }
        });
    }

    private void showRecipes() {
        RecipesViewModel.getListRecipes().observe(this, new Observer<List<Recipes>>() {
            @Override
            public void onChanged(List<Recipes> recipes) {
                if (recipes != null) {
                    boolean favorites = preferences.getBoolean("favorites", false);
                    boolean wannaDeleteFavorites = preferences.getBoolean("wannaDeleteFavorites", false);
                    List<Recipes> listFavorites = new ArrayList<>();
                    for (int i = 0; i < recipes.size(); i++) {
                        if (wannaDeleteFavorites) {
                            preferences.edit().putBoolean("favorite" + recipes.get(i).getIdRecipes(), false).apply();
                            recipes.get(i).setFavorite(false);
                        } else if (favorites) {
                            if (recipes.get(i).isFavorite()) listFavorites.add(recipes.get(i));
                        }
                        if (i == recipes.size() - 1) {
                            if (wannaDeleteFavorites) {
                                preferences.edit().putBoolean("wannaDeleteFavorites", false).apply();
                                RecipesViewModel.insertRecipes(recipes.toArray(new Recipes[recipes.size()]));
                            }
                            if (favorites) {
                                mainActivityRecipesAdapter.submitList(listFavorites);
                            } else {
                                mainActivityRecipesAdapter.submitList(recipes);
                            }
                        }
                    }

                    mainActivityRecipesAdapter.notifyDataSetChanged();
                    mainActivityRecipesAdapter.setOnItemClickListener(new MainActivityRecipesAdapter.RecipesListener() {
                        @Override
                        public void recipesClick(int id) {
                            Intent intent = new Intent(MainActivity.this, RecipeActivity.class);
                            try {
                                intent.putExtra("id", id);
                                startActivity(intent);
                            } catch (Exception e) {
                                Toast.makeText(MainActivity.this, "Something got wrong...", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void setFavorite(Recipes Recipe) {
                            Recipe.setFavorite(true);
                            preferences.edit().putBoolean("favorite" + Recipe.getIdRecipes(), true).apply();
                            RecipesViewModel.updateFavorite(Recipe);
                            mainActivityRecipesAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void setUnfavorite(Recipes Recipe) {
                            Recipe.setFavorite(false);
                            preferences.edit().putBoolean("favorite" + Recipe.getIdRecipes(), false).apply();
                            RecipesViewModel.updateFavorite(Recipe);
                            mainActivityRecipesAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.order) {
            AlertDialog.Builder construtor = new AlertDialog.Builder(this);
            final SharedPreferences.Editor editor = preferences.edit();
            int choice;
            order = preferences.getBoolean("favorites", false);
            if (!order) choice = 0;
            else choice = 1;
            construtor.setTitle("Items to show");
            construtor.setSingleChoiceItems(new String[]{
                            "All items", "Favorites"}, choice,
                    (dialog, selecionado) -> {
                        if (selecionado == 0) editor.putBoolean("favorites", false);
                        if (selecionado == 1) editor.putBoolean("favorites", true);
                    });
            construtor.setPositiveButton("Save", (dialog, which) -> editor.apply());
            construtor.setNeutralButton("Cancel", (dialog, which) -> {
            });
            construtor.setOnDismissListener(dialog -> {
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                startActivity(intent);
            });
            AlertDialog dialog = construtor.create();
            dialog.show();
        }

        if (id == R.id.offlineData) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            String[] delete_options = new String[]{"Delete offline data", "Undo favorite all"};
            final boolean[] selected_delete_options = new boolean[]{false, false};
            builder.setMultiChoiceItems(delete_options, selected_delete_options, (dialog, which, isChecked) -> {
                selected_delete_options[which] = isChecked;
            });
            builder.setTitle("Offline data");
            builder.setPositiveButton("Ok", (dialog, which) -> {
                if (selected_delete_options[0]) {
                    RecipesViewModel.deleteRecipes();
                }
                if (selected_delete_options[1]) {
                    preferences.edit().putBoolean("wannaDeleteFavorites", true).apply();
                }
            });
            builder.setNeutralButton("Cancel", (dialog, which) -> {
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        if (id == R.id.updateInterval) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final SharedPreferences.Editor editor = preferences.edit();
            int choice;
            long interval = preferences.getLong("updateInterval", 86400);
            if (interval == 0) choice = 0;
            else if (interval == 3600) choice = 1;
            else if (interval == 86400) choice = 2;
            else if (interval == 604800) choice = 3;
            else choice = 0;
            builder.setTitle("Minimum update interval");
            builder.setSingleChoiceItems(new String[]{
                            "Always update (Not recommended)", "At least 1 hour", "At least 1 day", "At least 1 week"}, choice,
                    (dialog, selected) -> {
                        if (selected == 0) editor.putLong("updateInterval", 0);
                        if (selected == 1) editor.putLong("updateInterval", 3600);
                        if (selected == 2) editor.putLong("updateInterval", 86400);
                        if (selected == 3) editor.putLong("updateInterval", 604800);
                    });
            builder.setPositiveButton("Salve", (dialog, which) -> editor.apply());
            builder.setNeutralButton("Cancel", (dialog, which) -> {
            });
            builder.setOnDismissListener(dialog -> {
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                startActivity(intent);
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        if (id == R.id.refresh) {
            final SharedPreferences.Editor editor = preferences.edit();
            editor.putLong("lastUpdate", 0);
            editor.apply();
            startService();
            if (updateService != null) {
                updateService.Refresh();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}