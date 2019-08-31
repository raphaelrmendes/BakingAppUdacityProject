package br.com.rochamendes.bakingappudacityproject.masterDetailFlowPlus;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.media.session.MediaButtonReceiver;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import br.com.rochamendes.bakingappudacityproject.dataPersist.recipesViewModel;
import br.com.rochamendes.bakingappudacityproject.entities.Steps;
import br.com.rochamendes.bakingappudacityproject.Notifications;
import br.com.rochamendes.bakingappudacityproject.R;
import br.com.rochamendes.bakingappudacityproject.databinding.FragmentStepsDetailBinding;

import static br.com.rochamendes.bakingappudacityproject.Notifications.ID_Notification_Playing;

public class stepsDetailFragment extends Fragment implements SimpleExoPlayer.EventListener {

    private FragmentStepsDetailBinding fragmentStepsDetailBinding;
    private Steps[] stepsArray;
    private int position;
    private int size;
    private Bundle args;
    private SimpleExoPlayer simpleExoPlayer;
    private static MediaSessionCompat mediaSessionCompat;
    private DataSource.Factory dataSourceFactory;
    private PlaybackStateCompat.Builder playbackStateCompatBuilder;
    private NotificationManagerCompat notificationManagerCompat;
    private NotificationCompat.Builder notificationBuilder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        recipesViewModel recipesViewModel = ViewModelProviders.of(this).get(br.com.rochamendes.bakingappudacityproject.dataPersist.recipesViewModel.class);
        fragmentStepsDetailBinding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_steps_detail, container, false);
        // Get arguments about the step to be shown (passed from the Steps list)
        try {
            args = getArguments();
            size = args.getInt(stepsListFragment.string_step_size, 0);
            stepsArray = new Steps[size];
            position = args.getInt(stepsListFragment.string_step_position, -1);
            for (int i = 0; i < size; i++) {
                stepsArray[i] = new Steps(-1, "", "", "", "");
                stepsArray[i].setId(args.getInt(stepsListFragment.string_step_id + i, -1));
                stepsArray[i].setShortDescription(args.getString(stepsListFragment.string_step_short_description + i, ""));
                stepsArray[i].setDescription(args.getString(stepsListFragment.string_step_description + i, ""));
                stepsArray[i].setVideoURL(args.getString(stepsListFragment.string_step_videoURL + i, ""));
            }
            if (position != -1) {
                fragmentStepsDetailBinding.setStep(stepsArray[position]);
            }
            Log.i("Mensagem", "Entered on step #" + position + 1);
        } catch (Exception e) {
            Log.i("Mensagem", "Null Arguments");
        }

        // Reached last step (no next button)
        if (position == size - 1) {
            fragmentStepsDetailBinding.imgNextStep.setVisibility(View.GONE);
            fragmentStepsDetailBinding.txtNextStep.setVisibility(View.GONE);
        }
        // First step (no previous button)
        if (position == 0) {
            fragmentStepsDetailBinding.imgPreviousStep.setVisibility(View.GONE);
            fragmentStepsDetailBinding.txtPreviousStep.setVisibility(View.GONE);
        }
        // Set the View
        View view = fragmentStepsDetailBinding.getRoot();
        // Instantiate Notification Manager
        notificationManagerCompat = NotificationManagerCompat.from(getContext());
        // Instantiate Notification Builder
        notificationBuilder = new NotificationCompat.Builder(
                getContext().getApplicationContext(), Notifications.CHANNEL_1_ID);
        // Instantiate Media Session
        mediaSessionCompat = new MediaSessionCompat(getContext(), stepsDetailFragment.class.getSimpleName());
        // Instantiate DataSource Factory
        dataSourceFactory = new DefaultDataSourceFactory(getContext(),
                Util.getUserAgent(getContext(), getString(R.string.app_name)));
        // Instantiate Player
        TrackSelector trackSelector = new DefaultTrackSelector();
        LoadControl loadControl = new DefaultLoadControl();
        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
        fragmentStepsDetailBinding.video.setPlayer(simpleExoPlayer);
        simpleExoPlayer.addListener(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        int container;
        if (getActivity().findViewById(R.id.fragment_container_ingredients) != null) {
            container = R.id.fragment_container_detail;
        } else {
            container = R.id.fragment_container;
        }
        // listen to button next (for all the Steps that are not the last one)
        if (position < size - 1) {
            fragmentStepsDetailBinding.imgNextStep.setOnClickListener(view1 -> {
                Fragment selectedStep = new stepsDetailFragment();
                args.putInt("position", position + 1);
                selectedStep.setArguments(args);
                getActivity().getSupportFragmentManager().beginTransaction().replace(container, selectedStep).commit();
            });
            fragmentStepsDetailBinding.txtNextStep.setOnClickListener(view1 -> {
                Fragment selectedStep = new stepsDetailFragment();
                args.putInt("position", position + 1);
                selectedStep.setArguments(args);
                getActivity().getSupportFragmentManager().beginTransaction().replace(container, selectedStep).commit();
            });
        }
        // listen to button previous (for all the Steps that are not the first one)
        if (position > 0) {
            fragmentStepsDetailBinding.imgPreviousStep.setOnClickListener(view1 -> {
                Fragment selectedStep = new stepsDetailFragment();
                args.putInt("position", position - 1);
                selectedStep.setArguments(args);
                getActivity().getSupportFragmentManager().beginTransaction().replace(container, selectedStep).commit();
            });
            fragmentStepsDetailBinding.txtPreviousStep.setOnClickListener(view1 -> {
                Fragment selectedStep = new stepsDetailFragment();
                args.putInt("position", position - 1);
                selectedStep.setArguments(args);
                getActivity().getSupportFragmentManager().beginTransaction().replace(container, selectedStep).commit();
            });
        }
        // Initialize Media Session and Player if that Step contains it
        if (!stepsArray[position].getVideoURL().equals("") && stepsArray[position].getVideoURL() != null) {
            initializeMediaSession();
            initializePlayer(Uri.parse(stepsArray[position].getVideoURL()));
        } else {
            fragmentStepsDetailBinding.video.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Release Player and Media Session
        releasePlayer();
        mediaSessionCompat.release();
    }

    private void initializeMediaSession() {
        mediaSessionCompat.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSessionCompat.setMediaButtonReceiver(null);
        playbackStateCompatBuilder = new PlaybackStateCompat.Builder().setActions(
                PlaybackStateCompat.ACTION_PLAY |
                        PlaybackStateCompat.ACTION_PAUSE |
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                        PlaybackStateCompat.ACTION_PLAY_PAUSE);
        mediaSessionCompat.setPlaybackState(playbackStateCompatBuilder.build());
        mediaSessionCompat.setCallback(new sessionCallback());
        mediaSessionCompat.setActive(true);
    }

    private void initializePlayer(Uri mediaUri) {
        MediaSource videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(mediaUri);
        simpleExoPlayer.prepare(videoSource);
        simpleExoPlayer.setPlayWhenReady(true);
    }

    private void initializeNotification(PlaybackStateCompat state) {
        notificationBuilder = new NotificationCompat.Builder(
                getContext().getApplicationContext(), Notifications.CHANNEL_1_ID);
        int actionIcon, notificationIcon;
        String play_pause;
        if (state.getState() == PlaybackStateCompat.STATE_PLAYING) {
            actionIcon = R.drawable.exo_controls_pause;
            notificationIcon = R.drawable.exo_controls_play;
            play_pause = "Pause";
        } else {
            actionIcon = R.drawable.exo_controls_play;
            notificationIcon = R.drawable.exo_controls_pause;
            play_pause = "Play";
        }
        NotificationCompat.Action playPauseAction = new NotificationCompat.Action(actionIcon, play_pause,
                MediaButtonReceiver.buildMediaButtonPendingIntent(getContext(),
                        PlaybackStateCompat.ACTION_PLAY_PAUSE));
        NotificationCompat.Action restartAction = new NotificationCompat
                .Action(R.drawable.exo_controls_previous, "Restart",
                MediaButtonReceiver.buildMediaButtonPendingIntent
                        (getContext(), PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS));

        PendingIntent contentPendingIntent = PendingIntent.getActivity
                (getContext(), 0, new Intent(getContext(), RecipeActivity.class), 0);

        notificationBuilder.setContentTitle(getActivity().getTitle())
                .setContentText(stepsArray[position].getShortDescription())
                .setContentIntent(contentPendingIntent)
                .setSmallIcon(notificationIcon)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .addAction(restartAction)
                .addAction(playPauseAction)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle(notificationBuilder)
                        .setShowActionsInCompactView(0, 1));
        notificationManagerCompat.notify(ID_Notification_Playing, notificationBuilder.build());
    }

    private void releasePlayer() {
        notificationManagerCompat.cancel(ID_Notification_Playing);
        simpleExoPlayer.stop();
        simpleExoPlayer.release();
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if ((playbackState == SimpleExoPlayer.STATE_READY) && playWhenReady) {
            playbackStateCompatBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    simpleExoPlayer.getCurrentPosition(), 1f);
            initializeNotification(playbackStateCompatBuilder.build());
        } else if ((playbackState == SimpleExoPlayer.STATE_READY)) {
            playbackStateCompatBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    simpleExoPlayer.getCurrentPosition(), 1f);
            initializeNotification(playbackStateCompatBuilder.build());
        } else if ((playbackState == SimpleExoPlayer.STATE_ENDED)) {
            playbackStateCompatBuilder.setState(PlaybackStateCompat.STATE_STOPPED,
                    simpleExoPlayer.getCurrentPosition(), 1f);
            notificationManagerCompat.cancel(ID_Notification_Playing);
        }
        mediaSessionCompat.setPlaybackState(playbackStateCompatBuilder.build());
    }

    private class sessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            simpleExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            simpleExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            simpleExoPlayer.seekTo(0);
        }
    }

    public static class MediaReceiver extends BroadcastReceiver {
        public MediaReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            MediaButtonReceiver.handleIntent(mediaSessionCompat, intent);
        }
    }
}