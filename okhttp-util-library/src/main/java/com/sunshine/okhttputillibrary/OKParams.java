/**
 * Copyright (c) 2012-2013, Michael Yang 杨福海 (www.yangfuhai.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sunshine.okhttputillibrary;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * 使用方法:
 * <p>
 * 
 * <pre>
 * AjaxParams params = new AjaxParams();
 * params.put(&quot;username&quot;, &quot;michael&quot;);
 * params.put(&quot;password&quot;, &quot;123456&quot;);
 * params.put(&quot;email&quot;, &quot;test@tsz.net&quot;);
 * params.put(&quot;profile_picture&quot;, new File(&quot;/mnt/sdcard/pic.jpg&quot;)); // 上传文件
 * params.put(&quot;profile_picture2&quot;, inputStream); // 上传数据流
 * params.put(&quot;profile_picture3&quot;, new ByteArrayInputStream(bytes)); // 提交字节流
 * 
 * FinalHttp fh = new FinalHttp();
 * fh.post(&quot;http://www.yangfuhai.com&quot;, params, new AjaxCallBack&lt;String&gt;() {
 * 	&#064;Override
 * 	public void onLoading(long count, long current) {
 * 		textView.setText(current + &quot;/&quot; + count);
 * 	}
 * 
 * 	&#064;Override
 * 	public void onSuccess(String t) {
 * 		textView.setText(t == null ? &quot;null&quot; : t);
 * 	}
 * });
 * </pre>
 */
public class OKParams {
	private static String ENCODING = "UTF-8";

	protected ConcurrentHashMap<String, String> urlParams;
	protected ConcurrentHashMap<String, FileWrapper> fileParams;

	public OKParams() {
		init();
	}

	public OKParams(Map<String, String> source) {
		init();

		for (Map.Entry<String, String> entry : source.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	public OKParams(String key, String value) {
		init();
		put(key, value);
	}

	public OKParams(Object... keysAndValues) {
		init();
		int len = keysAndValues.length;
		if (len % 2 != 0)
			throw new IllegalArgumentException("Supplied arguments must be even");
		for (int i = 0; i < len; i += 2) {
			String key = String.valueOf(keysAndValues[i]);
			String val = String.valueOf(keysAndValues[i + 1]);
			put(key, val);
		}
	}

	public void put(String key, String value) {
		if (key != null && value != null) {
			urlParams.put(key, value);
		}
	}

	public void put(String key, File file) throws FileNotFoundException {
		put(key, new FileInputStream(file), file.getName());
	}

	public void put(String key, InputStream stream) {
		put(key, stream, null);
	}

	public void put(String key, InputStream stream, String fileName) {
		put(key, stream, fileName, null);
	}

	/**
	 * 添加 inputStream 到请求中.
	 * 
	 * @param key
	 *            the key name for the new param.
	 * @param stream
	 *            the input stream to add.
	 * @param fileName
	 *            the name of the file.
	 * @param contentType
	 *            the content type of the file, eg. application/json
	 */
	public void put(String key, InputStream stream, String fileName, String contentType) {
		if (key != null && stream != null) {
			fileParams.put(key, new FileWrapper(stream, fileName, contentType));
		}
	}

	public void remove(String key) {
		urlParams.remove(key);
		fileParams.remove(key);
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		for (ConcurrentHashMap.Entry<String, String> entry : urlParams.entrySet()) {
			if (result.length() > 0)
				result.append("&");

			result.append(entry.getKey());
			result.append("=");
			result.append(entry.getValue());
		}

		for (ConcurrentHashMap.Entry<String, FileWrapper> entry : fileParams.entrySet()) {
			if (result.length() > 0)
				result.append("&");

			result.append(entry.getKey());
			result.append("=");
			result.append("FILE");
		}

		return result.toString();
	}

	/**
	 * Returns an HttpEntity containing all request parameters
	 */
	public Map<String, String> getMapParams() {
		Map<String, String> map = null;
		// if (!urlParams.isEmpty()) {
		map = new HashMap<String, String>();

		// Add string params
		for (Map.Entry<String, String> entry : urlParams.entrySet()) {
			map.put(entry.getKey(), entry.getValue() + "");
		}

		return map;
	}

	private void init() {
		urlParams = new ConcurrentHashMap<String, String>();
		fileParams = new ConcurrentHashMap<String, FileWrapper>();
	}


	private static class FileWrapper {
		public InputStream inputStream;
		public String fileName;
		public String contentType;

		public FileWrapper(InputStream inputStream, String fileName, String contentType) {
			this.inputStream = inputStream;
			this.fileName = fileName;
			this.contentType = contentType;
		}

		public String getFileName() {
			if (fileName != null) {
				return fileName;
			} else {
				return "nofilename";
			}
		}
	}
}