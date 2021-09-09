package in.co.eko.eko;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import in.co.eko.ekopay.EkoPayActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DemoActivity extends AppCompatActivity {

    private Button buttonSum, btnFetchParams;
    private ParamsResponse paramsData;
    private TextView tvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);

        buttonSum = findViewById(R.id.button_send);
        btnFetchParams = findViewById(R.id.btnFetchParams);
        tvStatus = findViewById(R.id.status);
        addListenerOnButton();

        btnFetchParams.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvStatus.setText("Fetching");
                fetchParams();
            }
        });
    }

    private void fetchParams() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()) // Here we are using the GsonConverterFactory to directly convert json data to object
                .build();

        Api api = retrofit.create(Api.class);

        Call<ParamsResponse> call = api.getParams();

        call.enqueue(new Callback<ParamsResponse>() {
            @Override
            public void onResponse(Call<ParamsResponse> call, Response<ParamsResponse> response) {
                tvStatus.setText("Done");
                paramsData = response.body();
            }

            @Override
            public void onFailure(Call<ParamsResponse> call, Throwable t) {
                tvStatus.setText("Failed");
            }
        });
    }

    public void addListenerOnButton() {
        System.out.println("===========================================");

        buttonSum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                System.out.println("========..............===== LLLLLLLLLLLL====== 1 ==...........======================");
                System.out.println("=======.............=====LLLLLLLLLLLLLLLLLLLL  2  LLLLL=========...........======================");
                System.out.println("=====================...........=============== 3 =======");
                System.out.println("======...................===============.......   4   ....======================");


                int AEPS_REQUEST_CODE = 10923;
// Set a value to AEPS_REQUEST_CODE

                //Intent intent = new Intent(MainActivity.this, EkoPayActivity.class);

                Intent intent = new Intent(DemoActivity.this, EkoPayActivity.class);
                Bundle bundle = new Bundle();

// set partner parameters
                String environment = "uat";
                String developer_key = "becbbce45f79c6f5109f848acd540567";
                String initiator_id = "7411111111";
                String user_code = "20310001";
                String initiator_logo_url = "https://files.eko.co.in:8080/docs/logos/payment/dummy-partner-logo.png";
                String partner_name = "PARTNER Name INC";
                String language = "en";
                String secret_key_timestamp = "1568274099712";

                String secret_key = "ZZWwfI1d0W3SC7XAiPh0g5xFXVZO/PJhRyXAB2mwR+w=";


                String callback_url = "https://beta.ekoconnect.in:20011/connect/callback";

                bundle.putString("environment", environment);
// Optional
// Default value "production"  If environment not passed then, default environment will be "production"
// Expected values "uat" for beta testing or"production" for production environment

                bundle.putString("product", "aeps");
// mandatory

                bundle.putString("secret_key_timestamp", paramsData.getSecretKeyTimestamp());
// mandatory

                bundle.putString("secret_key", paramsData.getSecretKey());
// mandatory

                bundle.putString("developer_key", paramsData.getDeveloperKey());
// mandatory

                bundle.putString("initiator_id", paramsData.getInitiatorId());
// mandatory


                bundle.putString("callback_url", paramsData.getCallbackUrl());
// mandatory

                bundle.putString("user_code", paramsData.getUserCode());
// mandatory


                bundle.putString("initiator_logo_url", paramsData.getInitiatorLogoUrl());
// mandatory

                bundle.putString("partner_name", paramsData.getPartnerName());
// mandatory

                bundle.putString("language", paramsData.getLanguage());

                System.out.println("BUTTON CLICKED...............");

                intent.putExtras(bundle);

                startActivity(intent);
            }

        });
    }
}
