package com.airbus.http2clienttest;

import static com.airbus.http2clienttest.Utils.getUnsafeOkHttpClient;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;

import com.airbus.http2clienttest.databinding.ActivityMainBinding;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ConnectionPool connectionPool = new ConnectionPool(5, 5, TimeUnit.MINUTES);
        OkHttpClient client = getUnsafeOkHttpClient()
                .followRedirects(true)
                .protocols(Arrays.asList(Protocol.HTTP_2, Protocol.HTTP_1_1))
                .retryOnConnectionFailure(true)
                .connectionPool(connectionPool)
                .build();


        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.newCall(new Request.Builder().url("https://10.0.0.16:8443/").build()).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        runOnUiThread(() -> {
                            try {
                                binding.textView.setText(response.body().string() + "\n" + response.protocol());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                    }
                });
            }
        });

    }

}
