package com.badlogic.gdx.backends.android;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;

import androidx.annotation.NonNull;

import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.audio.AudioDevice;
import com.badlogic.gdx.audio.AudioRecorder;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * An implementation of the {@link Audio} interface for Android.
 */
public class DefaultAndroidAudio implements AndroidAudio {
    private final SoundPool soundPool;
    private final AudioManager manager;
    private final List<AndroidMusic> musics = new ArrayList<AndroidMusic>();

    public DefaultAndroidAudio(Context context, AndroidApplicationConfiguration config) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttrib = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build();
            soundPool = new SoundPool.Builder().setAudioAttributes(audioAttrib).setMaxStreams(config.maxSimultaneousSounds).build();
        } else {
            soundPool = new SoundPool(config.maxSimultaneousSounds, AudioManager.STREAM_MUSIC, 0);// srcQuality: the sample-rate
            // converter quality. Currently
            // has no effect. Use 0 for the
            // default.
        }
        manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (context instanceof Activity) {
            ((Activity) context).setVolumeControlStream(AudioManager.STREAM_MUSIC);
        }
    }

    @Override
    public void pause() {
        synchronized (musics) {
            for (AndroidMusic music : musics) {
                if (music.isPlaying()) {
                    music.pause();
                    music.wasPlaying = true;
                } else
                    music.wasPlaying = false;
            }
        }
        this.soundPool.autoPause();
    }

    @Override
    public void resume() {
        synchronized (musics) {
            for (int i = 0; i < musics.size(); i++) {
                if (musics.get(i).wasPlaying) musics.get(i).play();
            }
        }
        this.soundPool.autoResume();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AudioDevice newAudioDevice(int samplingRate, boolean isMono) {
        return new AndroidAudioDevice(samplingRate, isMono);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Music newMusic(@NonNull FileHandle file) {
        AndroidFileHandle aHandle = (AndroidFileHandle) file;

        MediaPlayer mediaPlayer = createMediaPlayer();

        if (aHandle.type() == FileType.Internal) {
            try {
                AssetFileDescriptor descriptor = aHandle.getAssetFileDescriptor();
                mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
                descriptor.close();
                mediaPlayer.prepare();
                AndroidMusic music = new AndroidMusic(this, mediaPlayer);
                synchronized (musics) {
                    musics.add(music);
                }
                return music;
            } catch (Exception ex) {
                throw new GdxRuntimeException(
                        "Error loading audio file: " + file + "\nNote: Internal audio files must be placed in the assets directory.", ex);
            }
        } else {
            try {
                mediaPlayer.setDataSource(aHandle.file().getPath());
                mediaPlayer.prepare();
                AndroidMusic music = new AndroidMusic(this, mediaPlayer);
                synchronized (musics) {
                    musics.add(music);
                }
                return music;
            } catch (Exception ex) {
                throw new GdxRuntimeException("Error loading audio file: " + file, ex);
            }
        }
    }

    @Override
    public boolean switchOutputDevice(String deviceIdentifier) {
        return true;
    }

    @NonNull
    @Override
    public String[] getAvailableOutputDevices() {
        return new String[0];
    }

    /**
     * Creates a new Music instance from the provided FileDescriptor. It is the caller's responsibility to close the file
     * descriptor. It is safe to do so as soon as this call returns.
     *
     * @param fd the FileDescriptor from which to create the Music
     * @see Audio#newMusic(FileHandle)
     */
    public Music newMusic(FileDescriptor fd) {
        MediaPlayer mediaPlayer = createMediaPlayer();

        try {
            mediaPlayer.setDataSource(fd);
            mediaPlayer.prepare();

            AndroidMusic music = new AndroidMusic(this, mediaPlayer);
            synchronized (musics) {
                musics.add(music);
            }
            return music;
        } catch (Exception ex) {
            throw new GdxRuntimeException("Error loading audio from FileDescriptor", ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Sound newSound(@NonNull FileHandle file) {
        AndroidSound androidSound;
        AndroidFileHandle aHandle = (AndroidFileHandle) file;
        if (aHandle.type() == FileType.Internal) {
            try {
                AssetFileDescriptor descriptor = aHandle.getAssetFileDescriptor();
                androidSound = new AndroidSound(soundPool, manager, soundPool.load(descriptor, 1));
                descriptor.close();
            } catch (IOException ex) {
                throw new GdxRuntimeException(
                        "Error loading audio file: " + file + "\nNote: Internal audio files must be placed in the assets directory.", ex);
            }
        } else {
            try {
                androidSound = new AndroidSound(soundPool, manager, soundPool.load(aHandle.file().getPath(), 1));
            } catch (Exception ex) {
                throw new GdxRuntimeException("Error loading audio file: " + file, ex);
            }
        }
        return androidSound;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AudioRecorder newAudioRecorder(int samplingRate, boolean isMono) {
        return new AndroidAudioRecorder(samplingRate, isMono);
    }

    /**
     * Kills the soundpool and all other resources
     */
    @Override
    public void dispose() {
        synchronized (musics) {
            // gah i hate myself.... music.dispose() removes the music from the list...
            ArrayList<AndroidMusic> musicsCopy = new ArrayList<AndroidMusic>(musics);
            for (AndroidMusic music : musicsCopy) {
                music.dispose();
            }
        }
        soundPool.release();
    }

    @Override
    public void notifyMusicDisposed(AndroidMusic music) {
        synchronized (musics) {
            musics.remove(this);
        }
    }

    protected MediaPlayer createMediaPlayer() {
        MediaPlayer mediaPlayer = new MediaPlayer();
        if (Build.VERSION.SDK_INT <= 21) {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        } else {
            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_GAME).build());
        }
        return mediaPlayer;
    }
}
