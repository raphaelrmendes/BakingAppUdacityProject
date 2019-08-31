package br.com.rochamendes.bakingappudacityproject.adapters;
import br.com.rochamendes.bakingappudacityproject.entities.Steps;
import br.com.rochamendes.bakingappudacityproject.R;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import br.com.rochamendes.bakingappudacityproject.databinding.FragmentStepsRecyclerItemBinding;

public class StepsListAdapter extends RecyclerView.Adapter<StepsListAdapter.StepsViewHolder> {

    private Steps[] stepsArray;
    private stepsListener stepsListener;
    private Context context;

    public StepsListAdapter(Steps[] steps, Context context) {
        stepsArray = steps;
        this.context = context;
    }

    class StepsViewHolder extends RecyclerView.ViewHolder {
        FragmentStepsRecyclerItemBinding fragmentStepsRecyclerItemBinding;

        StepsViewHolder(@NonNull FragmentStepsRecyclerItemBinding itemView) {
            super(itemView.getRoot());
            fragmentStepsRecyclerItemBinding = itemView;

            itemView.getRoot().setOnClickListener(view -> {
                int pos = getAdapterPosition();
                if (stepsListener != null){
                    stepsListener.stepsClick(pos);
                }
            });
        }
    }

    @NonNull
    @Override
    public StepsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        FragmentStepsRecyclerItemBinding fragmentStepsRecyclerItemBinding = DataBindingUtil
                .inflate(layoutInflater, R.layout.fragment_steps_recycler_item, parent, false);
        return new StepsViewHolder(fragmentStepsRecyclerItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull final StepsViewHolder holder, int position) {
        Steps step = stepsArray[position];
        holder.fragmentStepsRecyclerItemBinding.setStep(step);

        if (!step.getThumbnailURL().equals("") && step.getThumbnailURL() != null){
            Glide.with(context).load(Uri.parse(step.getThumbnailURL())).into(holder.fragmentStepsRecyclerItemBinding.videoThumbnail);
        } else if (!step.getVideoURL().equals("") && step.getVideoURL() != null) {
            Glide.with(context).load(Uri.parse(step.getVideoURL())).into(holder.fragmentStepsRecyclerItemBinding.videoThumbnail);
        }

        if (position == 0){
            holder.fragmentStepsRecyclerItemBinding.tagStepNumber.setVisibility(View.GONE);
            holder.fragmentStepsRecyclerItemBinding.strStepId.setVisibility(View.GONE);
            holder.fragmentStepsRecyclerItemBinding.strStepDescription.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        if (stepsArray == null) return 0;
        return stepsArray.length;
    }

    public interface stepsListener{
        void stepsClick(int position);
    }

    public void setOnItemClickListener(StepsListAdapter.stepsListener StepsListener){
        stepsListener = StepsListener;
    }
}