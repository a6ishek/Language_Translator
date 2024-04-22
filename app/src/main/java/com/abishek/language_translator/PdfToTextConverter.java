package com.abishek.language_translator;
import okhttp3.*;

public class PdfToTextConverter {
    private static final String API_KEY = "462637445";
    private static final String CONVERT_API_URL = "https://v2.convertapi.com/convert/pdf/to/txt?Secret=q6dQDViaJ8DMx8ue";

    public static void convertPdfToText(String inputFile, Callback callback) {
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/pdf");
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("ApiKey", API_KEY)
                .addFormDataPart("File", inputFile, RequestBody.create(mediaType, new java.io.File(inputFile)))
                .build();

        Request request = new Request.Builder()
                .url(CONVERT_API_URL)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(callback);
    }
}
