package com.radiomatalelaki;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import com.radiomatalelaki.util.StreamingService;
public class RadioActivity extends AppCompatActivity {
    private Spinner combo;
    private TextView txtDetails;
    private ImageView img_radio;

    private Toolbar toobar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radio);

        toobar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toobar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ml_logo);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //inisialisasi
        combo = (Spinner) findViewById(R.id.combo);
        //txtDetails = (TextView) findViewById(R.id.txtDetails);

        //set value to list
        final List<Radio> listRadio = new ArrayList<>();
        Radio radio1 = new Radio("MataLelaki 128kbps", "http://radio.matalelaki.com:8080/stream/1/;");
        Radio radio2 = new Radio("MataLelaki 192kbps", "http://radio.matalelaki.com:8080/stream/2/;");
        listRadio.add(radio1);
        listRadio.add(radio2);

        final String[] radioArr = new String[listRadio.size()];
        for (int i = 0; i < listRadio.size(); i++) {
            radioArr[i] = listRadio.get(i).getName();
        }

        //set value to autocomplete
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, radioArr);

        combo.setAdapter(adapter);
        combo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selection = (String) adapterView.getItemAtPosition(i);
                int pos = -1;

                for (int j = 0; j < radioArr.length; j++) {
                    if (radioArr[j].equals(selection)) {
                        pos = j;
                        break;
                    }
                }

                callRadio(listRadio.get(pos).getUrl(), listRadio.get(pos).getName());
               // txtDetails.setText(listRadio.get(pos).getName() + " is Now Playing");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void callRadio(String url, String name) {
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        bundle.putString("name", name);
        Intent serviceOn = new Intent(this, StreamingService.class);
        serviceOn.putExtras(bundle);
        startService(serviceOn);
    }

    class Radio {
        private String name, url;

        public Radio() {
        }

        public Radio(String name, String url) {
            this.name = name;
            this.url = url;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }


}
