package in.co.eko.eko;

import retrofit2.Call;
import retrofit2.http.GET;


public interface Api {
    String BASE_URL = "https://beta.ekoconnect.in:20011/";

    @GET("aeps/ekogateway/params")
    Call<ParamsResponse> getParams();
}

