package com.sunshine.okhttputillibrary;


import android.os.Handler;
import android.os.Message;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.sunshine.okhttputillibrary.callback.ResultCallback;
import com.sunshine.okhttputillibrary.request.OkHttpRequest;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OkHttpUtil {
	public void post(final String url, OKParams params, final ResultCallback callBack) {
		OkHttpRequest.Builder builder = new OkHttpRequest.Builder();
		builder.url(url);
		builder.tag("net");
		if (params != null && params.getMapParams() != null) {
			builder.params(params.getMapParams());
		}
		builder.post(callBack);
	}

	public void post(final String url, OKParams params, String tag, final ResultCallback callBack) {
		OkHttpRequest.Builder builder = new OkHttpRequest.Builder();
		builder.url(url);
		builder.tag(tag);
		if (params != null && params.getMapParams() != null) {
			builder.params(params.getMapParams());
		}
		builder.post(callBack);
	}

	public void cancel(String tag) throws IOException {
		OkHttpClientManager.getInstance().getOkHttpClient().cancel(tag);

	}

	public static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");
	private static final String IMGUR_CLIENT_ID = "9199fdef135c122";

	public static void postFile(final String url, final OKParams params, final String FileName, final File file, final ResultCallback callBack) {
		final Handler handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case 10001:
					String str = msg.obj.toString();
					callBack.onSuccess(str);
					break;
				case 10002:
					callBack.onError(null, (Exception)msg.obj);
					break;

				}
			}
		};
		Thread thread = new Thread() {

			@Override
			public void run() {
				super.run();
				OkHttpClient client = new OkHttpClient();
				client.setConnectTimeout(30, TimeUnit.SECONDS);
				MultipartBuilder build = new MultipartBuilder().type(MultipartBuilder.FORM).addFormDataPart(FileName, file.getName(),
						RequestBody.create(MEDIA_TYPE_MARKDOWN, file));
				if (params != null && params.getMapParams() != null) {

					Map<String, String> urlParams = params.getMapParams();
					if (urlParams != null)
						for (Map.Entry<String, String> entry : urlParams.entrySet()) {
							build.addFormDataPart(entry.getKey(), entry.getValue());
						}
				}
				RequestBody requestBody = build.build();
				Request request = new Request.Builder().header("Authorization", "Client-ID " + IMGUR_CLIENT_ID).url(url).post(requestBody).build();
				Message msg = new Message();
				Response response = null;
				try {
					response = client.newCall(request).execute();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					msg.obj = e.getMessage();
				}

				if (response != null && response.isSuccessful()) {

					msg.what = 10001;
					try {
						msg.obj = response.body().string();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						msg.obj=e;

					}
					handler.sendMessage(msg);
				} else {
					msg.what = 10002;
					handler.sendMessage(msg);
				}

			}
		};
		thread.start();

	}

	public void get(final String url, final ResultCallback callBack) {
		new OkHttpRequest.Builder().tag("net").url(url).get(callBack);
	}
}
