package com.nsh.catchmusic;

import android.os.AsyncTask;

import com.google.api.gax.longrunning.OperationFuture;
import com.google.cloud.speech.v1.LongRunningRecognizeMetadata;
import com.google.cloud.speech.v1.LongRunningRecognizeResponse;
import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;

import java.io.IOException;
import java.security.PublicKey;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by ThisIsNSH on 5/19/2018.
 */

public class SimpleAsynsTask extends AsyncTask<Void, Void, String> {
    String AudioSavePathInDevice, query;

    SimpleAsynsTask(String abc) {
        AudioSavePathInDevice = abc;
    }

    @Override
    protected String doInBackground(Void... voids) {

        try (SpeechClient speech = SpeechClient.create()) {

            // Configure remote file request for Linear16
            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.FLAC)
                    .setLanguageCode("en-US")
                    .setSampleRateHertz(16000)
                    .build();
            RecognitionAudio audio = RecognitionAudio.newBuilder()
                    .setUri(AudioSavePathInDevice)
                    .build();

            // Use non-blocking call for getting file transcription
            OperationFuture<LongRunningRecognizeResponse, LongRunningRecognizeMetadata> response =
                    speech.longRunningRecognizeAsync(config, audio);
            while (!response.isDone()) {
                System.out.println("Waiting for response...");
                Thread.sleep(10000);
            }

            List<SpeechRecognitionResult> results = response.get().getResultsList();

            for (SpeechRecognitionResult result : results) {
                // There can be several alternative transcripts for a given chunk of speech. Just use the
                // first (most likely) one here.
                SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                query = alternative.getTranscript();

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return query;
    }

    @Override
    protected void onPostExecute(String query) {
        System.out.printf("Transcription: %s\n", query);
        super.onPostExecute(query);

    }


}
