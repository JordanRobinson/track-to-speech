package uk.co.jordanrobinson.tracktospeech.handlers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import uk.co.jordanrobinson.tracktospeech.MainActivity;

public class GooglePlayMusic extends PlayerHandler {

    public static final String[] IDENTIFIERS = {"com.android.music.metachanged",
            "com.android.music.playstatechanged",
            "com.android.music.metadatachanged",};

    public GooglePlayMusic(TextToSpeech tts, int initStatus, boolean playstate, String currentArtist, String currentTrack) {
        super(tts, initStatus, playstate, currentArtist, currentTrack);
    }

    @Override
    public void handle(Context context, Intent intent) {
        boolean handle = false;
        for (int i = 0; i < IDENTIFIERS.length; i++) {
            if (IDENTIFIERS[i].equals(intent.getAction())) {
                handle = true;
                break;
            }
        }

        if (handle) {
            super.handle(context, intent);

            String action = intent.getAction();

            Bundle bundle = intent.getExtras();
            //for our use, these are essentially the same
            playstate = (bundle.getBoolean("playstate") || bundle.getBoolean("playing"));
            Log.d("TrTS playstate", playstate + "");

            if (initStatus == TextToSpeech.SUCCESS && playstate) { //TTS is initialised, and we're actually playing
                String command = intent.getStringExtra("command"); //so log what's going on
                String artist = intent.getStringExtra("artist");
                String track = intent.getStringExtra("track");
                Log.d("TrTS track output", artist + " - " + track);
                Log.d("TrTS action output", action + " -  " + command);

                if (!artist.equals(currentArtist) || !track.equals(currentTrack)) { //if we haven't already
                    currentArtist = artist;
                    currentTrack = track;

                    //speak to the user
                    tts.speak(artist + ", " + track, TextToSpeech.QUEUE_FLUSH, null);
                    MainActivity.outputTextView.setText(artist + " \n " + track);
                    MainActivity.history.add(artist + " - " + track);
                    updateHistory(context);
                    Log.d("TrTS", "onRecieve success!");
                }
                else {
                    Log.d("TrTS", "onRecieve failed on artist comparison. Artist = "
                            + artist + " + " + currentArtist + " Track = " + track + " + " + currentTrack);
                }
            }
            else {
                Log.d("TrTS", "onRecieve failed on tts Success & playstate. Playstate = "
                        + playstate + " tts = " + (initStatus == TextToSpeech.SUCCESS));
            }

        }
    }
}
