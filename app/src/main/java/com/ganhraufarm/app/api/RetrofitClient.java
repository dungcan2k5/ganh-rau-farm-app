package com.ganhraufarm.app.api;

import com.ganhraufarm.app.SupabaseConfig;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit = null;
    private static SupabaseApi supabaseApi = null;
    private static String authToken = null;

    public static void setAuthToken(String token) {
        authToken = token;
        // Reset API instance to apply new token
        supabaseApi = null;
    }

    public static SupabaseApi getApi() {
        if (supabaseApi == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        Request original = chain.request();
                        Request.Builder builder = original.newBuilder()
                                .header("apikey", SupabaseConfig.SUPABASE_ANON_KEY);
                        
                        if (authToken != null) {
                            builder.header("Authorization", "Bearer " + authToken);
                        }
                        
                        return chain.proceed(builder.build());
                    })
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(SupabaseConfig.SUPABASE_URL + "/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
            
            supabaseApi = retrofit.create(SupabaseApi.class);
        }
        return supabaseApi;
    }
}
