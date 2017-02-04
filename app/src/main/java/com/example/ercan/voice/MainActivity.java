package com.example.ercan.voice;

        import java.util.ArrayList;
        import java.util.Locale;

        import android.content.Context;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.app.Activity;
        import android.content.Intent;
        import android.os.Vibrator;
        import android.speech.tts.TextToSpeech;
        import android.speech.tts.TextToSpeech.OnInitListener;
        import android.util.Log;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.view.View.OnClickListener;
        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.ListView;
        import android.speech.RecognizerIntent;
        import android.support.v4.app.NavUtils;
        import android.widget.TextView;
        import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener, OnInitListener{

    public ListView mList;
    public Button speakButton;
    public boolean menu;
    public boolean orden;
    Vibrator v;
    TextToSpeech tts;
    public TextView text;

    public static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        speakButton = (Button) findViewById(R.id.button);
        speakButton.setOnClickListener(this);
        mList = (ListView) findViewById(R.id.lista);

        text = (TextView) findViewById(R.id.textView);

        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        tts = new TextToSpeech(this, this);

        menu=false;
        orden=false;
    }

    public void startVoiceRecognitionActivity() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Habla");
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }

    public void onClick(View v) {
        // TODO Auto-generated method stub
        startVoiceRecognitionActivity();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
            // Fill the list view with the strings the recognizer thought it
            // could have heard
            ArrayList matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            mList.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, matches));
            // matches is the result of voice input. It is a list of what the
            // user possibly said.
            // Using an if statement for the keyword you want to use allows the
            // use of any activity if keywords match
            // it is possible to set up multiple keywords to use the same
            // activity so more than one word will allow the user
            // to use the activity (makes it so the user doesn't have to
            // memorize words from a list)
            // to use an activity from the voice input information simply use
            // the following format;
            // if (matches.contains("keyword here") { startActivity(new
            // Intent("name.of.manifest.ACTIVITY")
            if(menu){
                habla("Has dicho " + matches.get(0).toString());
                menu=false;
            }else{
                mList.setVisibility(View.INVISIBLE);
                if (matches.contains("repetir")) {
                    habla("Di la frase que quieres repetir");
                    menu=true;
                    new LongOperation().doInBackground();
                    startVoiceRecognitionActivity();
                } else if (matches.contains("comprobar")) {
                    habla("Di una frase para comprobarla");
                    menu=true;
                    new LongOperation().doInBackground();
                    mList.setVisibility(View.VISIBLE);
                    startVoiceRecognitionActivity();
                } else if (matches.contains("ordenar") || orden) {
                    if(!orden) {
                        habla("Di Vibrar o Cerrar");
                        new LongOperation().doInBackground();
                        orden=true;
                        startVoiceRecognitionActivity();
                    }
                    if (matches.contains("vibrar")) {
                        habla("vibrando");
                        v.vibrate(500L);
                        orden=false;
                    }
                    if (matches.contains("cerrar")) {
                        habla("Cerrando la aplicación. Hasta luego.");
                        new LongOperation().doInBackground();
                        finish();
                    }
                } else {
                    habla("No le he entendido");
                }
            }
        }
    }

    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {
            //coloca lenguaje por defecto en el celular, en nuestro caso el lenguaje es aspañol ;)
            int result = tts.setLanguage(Locale.getDefault());
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            }
        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }

    /* metodo para convertir texto a voz
     * @param String texto
     * */
    private void habla(String texto) {
        tts.speak(texto, TextToSpeech.QUEUE_FLUSH, null);
    }

    private class LongOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            for (int i = 0; i < 3; i++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.interrupted();
                }
            }
            return "Executed";
        }
    }

    }