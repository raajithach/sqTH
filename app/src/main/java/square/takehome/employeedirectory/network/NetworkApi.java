package square.takehome.employeedirectory.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Class for network API calls.
 */
public class NetworkApi {
    private Retrofit retrofit;
    private Api api;

    /**
     * API definition for network calls.
     */
    public interface Api
    {
        @GET("/sq-mobile-interview/employees.json")
        Call<ResponseBody> getEmployees();

        @GET
        @Streaming
        Call<ResponseBody> downloadFileWithUrl(@Url String fileUrl);
    }

    private Retrofit getRetrofit() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://s3.amazonaws.com")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public Api getApi() {
        if (api == null) {
            api = getRetrofit().create(Api.class);
        }
        return api;
    }
}
