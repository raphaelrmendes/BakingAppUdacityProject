package br.com.rochamendes.bakingappudacityproject.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import br.com.rochamendes.bakingappudacityproject.entities.Recipes;
import br.com.rochamendes.bakingappudacityproject.R;
import br.com.rochamendes.bakingappudacityproject.databinding.ActivityMainRecipesRecyclerItemBinding;

public class MainActivityRecipesAdapter extends ListAdapter<Recipes, MainActivityRecipesAdapter.RecipesViewHolder> {
    private RecipesListener recipesListener;

    public MainActivityRecipesAdapter() {
        super(DIFF_CALLBACK);
    }

    public static final DiffUtil.ItemCallback<Recipes> DIFF_CALLBACK = new DiffUtil.ItemCallback<Recipes>() {
        @Override
        public boolean areItemsTheSame(@NonNull Recipes oldItem, @NonNull Recipes newItem) {
            return oldItem.getIdRecipes() == newItem.getIdRecipes();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Recipes oldItem, @NonNull Recipes newItem) {
            return oldItem.isFavorite() == newItem.isFavorite();
        }
    };

    class RecipesViewHolder extends RecyclerView.ViewHolder {
        ActivityMainRecipesRecyclerItemBinding recipesRecyclerItemLayoutBinding;

        RecipesViewHolder(@NonNull ActivityMainRecipesRecyclerItemBinding itemView) {
            super(itemView.getRoot());
            recipesRecyclerItemLayoutBinding = itemView;
        }
    }

    @NonNull
    @Override
    public RecipesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ActivityMainRecipesRecyclerItemBinding recipesRecyclerItemLayoutBinding = DataBindingUtil
                .inflate(layoutInflater, R.layout.activity_main_recipes_recycler_item, parent, false);

        return new RecipesViewHolder(recipesRecyclerItemLayoutBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecipesViewHolder holder, int position) {
        Recipes recipe = getItem(position);
        holder.recipesRecyclerItemLayoutBinding.setRecipe(recipe);

        if (recipe.getImage() != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(recipe.getImage(), 0, recipe.getImage().length);
            holder.recipesRecyclerItemLayoutBinding.imgLayoutRecipeImage.setImageBitmap(bitmap);
        }

        if (recipe.isFavorite()) {
            holder.recipesRecyclerItemLayoutBinding.imgLayoutFavoriteChecked.setVisibility(View.VISIBLE);
            holder.recipesRecyclerItemLayoutBinding.imgLayoutFavoriteButton.setVisibility(View.GONE);
        } else {
            holder.recipesRecyclerItemLayoutBinding.imgLayoutFavoriteButton.setVisibility(View.VISIBLE);
            holder.recipesRecyclerItemLayoutBinding.imgLayoutFavoriteChecked.setVisibility(View.GONE);
        }
        if (recipe.isImageAvailable()) {
            holder.recipesRecyclerItemLayoutBinding.imagePoweredByGoogle.setVisibility(View.GONE);
            holder.recipesRecyclerItemLayoutBinding.tagLayoutExplanation.setVisibility(View.GONE);
        } else {
            holder.recipesRecyclerItemLayoutBinding.imagePoweredByGoogle.setVisibility(View.VISIBLE);
            holder.recipesRecyclerItemLayoutBinding.tagLayoutExplanation.setVisibility(View.VISIBLE);
        }

        holder.recipesRecyclerItemLayoutBinding.imgLayoutFavoriteButton.setOnClickListener(view -> {
            if (recipesListener != null) {
                recipesListener.setFavorite(recipe);
            }
        });
        holder.recipesRecyclerItemLayoutBinding.imgLayoutFavoriteChecked.setOnClickListener(view -> {
            if (recipesListener != null) {
                recipesListener.setUnfavorite(recipe);
            }
        });
        holder.recipesRecyclerItemLayoutBinding.imgLayoutRecipeImage.setOnClickListener(view -> {
            if (recipesListener != null) {
                recipesListener.recipesClick(recipe.getIdRecipes());
            }
        });
    }

    public interface RecipesListener {
        void recipesClick(int ID);

        void setFavorite(Recipes Recipe);

        void setUnfavorite(Recipes Recipe);
    }

    public void setOnItemClickListener(MainActivityRecipesAdapter.RecipesListener recipesListener) {
        this.recipesListener = recipesListener;
    }
}
