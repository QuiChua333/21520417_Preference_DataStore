package com.example.preferencedatastore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.rxjava2.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava2.RxDataStore;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import io.reactivex.Flowable;
import io.reactivex.Single;

public class MainActivity extends AppCompatActivity {

    private int counter = 0;

    RxDataStore<Preferences> dataStore; //Top Part
    Preferences.Key<Integer> COUNTER_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataStore = new RxPreferenceDataStoreBuilder(this,"my-counter-db").build(); //onCreate
        COUNTER_KEY = PreferencesKeys.intKey("counter");

        final TextView counterTextView = findViewById(R.id.counterTextView);
        Button decreaseButton = findViewById(R.id.decreaseButton);
        Button increaseButton = findViewById(R.id.increaseButton);

        // Đọc giá trị của counter từ DataStore khi Activity được tạo
        counter = getStringValue(COUNTER_KEY);
        counterTextView.setText("Counter: " + counter);


        // Xử lý sự kiện khi nhấn vào nút Decrease
        decreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counter--;
                counterTextView.setText("Counter: " + counter);
                saveCounterToDataStore(COUNTER_KEY, counter);
            }
        });

        // Xử lý sự kiện khi nhấn vào nút Increase
        increaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counter++;
                counterTextView.setText("Counter: " + counter);
                saveCounterToDataStore(COUNTER_KEY,counter);
            }
        });
    }

    public void saveCounterToDataStore(Preferences.Key<Integer> PREF_KEY, Integer value){
        Single<Preferences> updateResult =  dataStore.updateDataAsync(prefsIn -> {
            MutablePreferences mutablePreferences = prefsIn.toMutablePreferences();
            mutablePreferences.set(PREF_KEY, value);
            return Single.just(mutablePreferences);
        });
    }

    // Phương thức này được sử dụng để lưu giá trị của biến counter vào DataStore
    Integer getStringValue(Preferences.Key<Integer> Key) {
        Single<Integer> value = dataStore.data().firstOrError().map(prefs -> prefs.get(Key)).onErrorReturnItem(0);
        return value.blockingGet();
    }

}
