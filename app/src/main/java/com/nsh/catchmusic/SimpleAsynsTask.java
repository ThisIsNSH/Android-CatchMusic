package com.nsh.catchmusic;

import android.net.wifi.hotspot2.pps.Credential;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.TextView;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.protobuf.ByteString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by ThisIsNSH on 5/19/2018.
 */

public class SimpleAsynsTask extends AsyncTask<Void, Void, String> {

    String AudioSavePathInDevice, query;
    public TextView mTextView;

    SimpleAsynsTask(TextView t) {
        mTextView = t;
    }

    @Override
    protected String doInBackground(Void... voids) {

        try (SpeechClient speech = SpeechClient.create()) {

            Path path = Paths.get(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "AudioRecording.mp3");
            byte[] data = Files.readAllBytes(path);
            ByteString audioBytes = ByteString.copyFrom(data);

            // Configure request with local raw PCM audio
            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                    .setLanguageCode("en-US")
                    .setSampleRateHertz(16000)
                    .build();
            RecognitionAudio audio = RecognitionAudio.newBuilder()
                    .setContent(audioBytes)
                    .build();

            // Use blocking call to get audio transcript
            RecognizeResponse response = speech.recognize(config, audio);
            List<SpeechRecognitionResult> results = response.getResultsList();

            for (SpeechRecognitionResult result : results) {
                // There can be several alternative transcripts for a given chunk of speech. Just use the
                // first (most likely) one here.
                SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                query=alternative.getTranscript();
                System.out.printf("Transcription: %s%n", alternative.getTranscript());
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return query;
    }

    @Override
    protected void onPostExecute(String query) {
        mTextView.setText(query);
        System.out.printf("Transcription: %s\n", query);
        super.onPostExecute(query);

    }


}
