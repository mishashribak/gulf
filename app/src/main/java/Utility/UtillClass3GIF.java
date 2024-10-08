/*
 * Copyright 2010 Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package Utility;

import android.annotation.TargetApi;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Set;

/**
 * Utility class supporting the Facebook Object.
 *
 * @author ssoneff@facebook.com
 */
public final class UtillClass3GIF {

    /**
     * Generate the multi-part post body providing the parameters and boundary
     * string
     *
     * @param parameters the parameters need to be posted
     * @param boundary   the random string as boundary
     * @return a string of the post body
     */
    public static String encodePostBody(Bundle parameters, String boundary) {
        if (parameters == null)
            return "";
        StringBuilder sb = new StringBuilder();

        for (String key : parameters.keySet()) {
            if (parameters.getByteArray(key) != null) {
                continue;
            }

            sb.append("Content-Disposition: form-data; name=\"" + key
                    + "\"\r\n\r\n" + parameters.getString(key));
            sb.append("\r\n" + "--" + boundary + "\r\n");
        }

        return sb.toString();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String encodeUrl(Bundle parameters) {
        if (parameters == null) {
            return "";
        }

        JSONObject json = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        Set<String> keys = parameters.keySet();
        for (String key : keys) {
            try {
                // json.put(key, bundle.get(key)); see edit below
                json.put(key, JSONObject.wrap(parameters.get(key)));
                jsonArray.put(json);
            } catch (JSONException e) {
                //Handle exception here
            }
        }
        return jsonArray.toString();

//        StringBuilder sb = new StringBuilder();
//        boolean first = true;
//        for (String key : parameters.keySet()) {
//            if (first)
//                first = false;
//            else
//                sb.append("&");
//            sb.append(URLEncoder.encode(key) + "="
//                    + URLEncoder.encode(parameters.getString(key)));
//        }
//
//        System.out.print("sb value" + sb.toString());
//        return sb.toString();
    }

    public static Bundle decodeUrl(String s) {
        Bundle params = new Bundle();
        if (s != null) {
            String array[] = s.split("&");
            for (String parameter : array) {
                String v[] = parameter.split("=");
                params.putString(URLDecoder.decode(v[0]),
                        URLDecoder.decode(v[1]));
            }
        }
        return params;
    }

    /**
     * Parse a URL query and fragment parameters into a key-value bundle.
     *
     * @param url the URL to parse
     * @return a dictionary bundle of keys and values
     */
    public static Bundle parseUrl(String url) {
        // hack to prevent MalformedURLException
        url = url.replace("fbconnect", "http");
        try {
            URL u = new URL(url);
            Bundle b = decodeUrl(u.getQuery());
            b.putAll(decodeUrl(u.getRef()));
            return b;
        } catch (MalformedURLException e) {
            return new Bundle();
        }
    }

    /**
     * Connect to an HTTP URL and return the response as a string.
     * <p>
     * Note that the HTTP method override is used on non-GET requests. (i.e.
     * requests are made as "POST" with method specified in the body).
     *
     * @param url    - the resource to open: must be a welformed URL
     * @param method - the HTTP method to use ("GET", "POST", etc.)
     * @param params - the query parameter for the URL (e.g. access_token=foo)
     * @return the URL contents as a String
     * @throws MalformedURLException - if the URL format is invalid
     * @throws IOException           - if a network problem occurs
     */

    public static String openUrl(String url, String method, Bundle params,
                                 Boolean type) throws MalformedURLException, IOException {
        System.out.println("step 11");
        // random string as boundary for multi-part http post
        String strBoundary = "3i2ndDfv2rTHiSisAbouNdArYfORhtTPEefj3q2f";
        String endLine = "\r\n";
        boolean methodStatus = true;
        OutputStream os = null;

        if (method.equals("GET")) {
            url = url + "?" + encodeUrl(params);
        }
        Log.d("Facebook-Util", method + " URL: " + url);

        // Open a HTTP connection to the URL
        HttpURLConnection conn = (HttpURLConnection) new URL(url)
                .openConnection();

		/* conn.setChunkedStreamingMode(1024*4); */

        conn.setRequestProperty("User-Agent", System.getProperties()
                .getProperty("http.agent") + " FacebookAndroidSDK");
        try {
            if (!method.equals("GET")) {
                Bundle dataparams = new Bundle();
                for (String key : params.keySet()) {
                    if (params.getByteArray(key) != null) {
                        dataparams.putByteArray(key, params.getByteArray(key));
                    } else {
                        System.out.println("byte array else:"

                                + params.getByteArray(key));
                        System.out.println("byte array else:"
                                + params.getString(key));
                    }
                }

                // use method override
                if (!params.containsKey("method")) {
                    params.putString("method", method);
                }

                if (params.containsKey("access_token")) {
                    String decoded_token = URLDecoder.decode(params
                            .getString("access_token"));
                    params.putString("access_token", decoded_token);
                }

                // Use a post method.
                conn.setRequestMethod("POST");

                if (type) {
                    conn.setRequestProperty("Content-Type",
                            "multipart/form-data;boundary=" + strBoundary);
                }

                // Allow Outputs
                conn.setDoOutput(true);

                // Allow Inputs
                conn.setDoInput(true);
                System.out.println("step 12");
                conn.setRequestProperty("Connection", "Keep-Alive");
                System.out.println("step 13");
                conn.connect();
                System.out.println("step 14");
                // System.out.println(params.getString("input"));
                os = new BufferedOutputStream(conn.getOutputStream());

                System.out.println("step 15");

                if (type) {
                    os.write(("--" + strBoundary + endLine).getBytes());
                    os.write((encodePostBody(params, strBoundary)).getBytes());
                    os.write((endLine + "--" + strBoundary + endLine)
                            .getBytes());
                } else {
                    os.write((params.getString("input")).getBytes());
                }

                if (!dataparams.isEmpty()) {
                    System.out.println("step 16");
                    for (String key : dataparams.keySet()) {
                        os.write(("Content-Disposition: form-data; filename=\""
                                + key + "\"" + endLine).getBytes());
                        os.write(("Content-Type: content/unknown" + endLine + endLine)
                                .getBytes());
                        os.write(dataparams.getByteArray(key));
                        os.write((endLine + "--" + strBoundary + endLine)
                                .getBytes());
                        // os.flush();
                    }
                }
                os.flush();
                System.out.println("step 17");
                dataparams.clear();

            }
            System.out.println("step 18");
            String response = "";
            try {
                System.out.println("step 19");
                response = read(conn.getInputStream());
            } catch (FileNotFoundException e) {
                System.out.println("step 20");
                // Error Stream contains JSON that we can parse to a FB error
                response = read(conn.getErrorStream());
            }

            System.out.println("step response is : " + response);
            conn.disconnect();

            return response;
        } catch (OutOfMemoryError e) {
            if (os != null)
                os.flush();
            conn.disconnect();
            e.printStackTrace();
            return null;
        }
    }

    public static String openUrll(String url, String method, Bundle params)
            throws MalformedURLException, IOException {
        System.out.println("step 11");
        // random string as boundary for multi-part http post
        String strBoundary = "3i2ndDfv2rTHiSisAbouNdArYfORhtTPEefj3q2f";
        String endLine = "\r\n";

        OutputStream os = null;

        if (method.equals("GET")) {
            url = url + "&data=" + encodeUrl(params);
        }
        Log.d("Facebook-Util", method + " URL: " + url);

        // Open a HTTP connection to the URL
        HttpURLConnection conn = (HttpURLConnection) new URL(url)
                .openConnection();

		/* conn.setChunkedStreamingMode(1024*4); */

        conn.setRequestProperty("User-Agent", System.getProperties()
                .getProperty("http.agent") + " FacebookAndroidSDK");
        try {
            if (!method.equals("GET")) {
                Bundle dataparams = new Bundle();

                for (String key : params.keySet()) {
                    System.out.println("params" + params + "key" + key);
                    if (params.getByteArray(key) != null) {
                        dataparams.putByteArray(key, params.getByteArray(key));
                        System.out.println("byte array:"
                                + params.getByteArray(key));
                    } else {
                        dataparams.putString(key, params.getString(key));
                    }
                }

                // use method override
//				if (!params.containsKey("method")) {
//					params.putString("method", method);
//				}

                if (params.containsKey("access_token")) {
                    String decoded_token = URLDecoder.decode(params
                            .getString("access_token"));
                    params.putString("access_token", decoded_token);
                }

                // Use a post method.
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type",
                        "multipart/form-data;boundary=" + strBoundary);

                // Allow Outputs
                conn.setDoOutput(true);

                // Allow Inputs
                conn.setDoInput(true);
                System.out.println("step 12");
                conn.setRequestProperty("Connection", "Keep-Alive");
                System.out.println("step 13");
                conn.connect();
                System.out.println("step 14");
                // System.out.println(params.getString("input"));
                os = new BufferedOutputStream(conn.getOutputStream());

                // os.write((params.getString("input")).getBytes());

                System.out.println("step 15");
                os.write(("--" + strBoundary + endLine).getBytes());
                os.write((encodePostBody(params, strBoundary)).getBytes());
                os.write((endLine + "--" + strBoundary + endLine).getBytes());

                if (!dataparams.isEmpty()) {
                    System.out.println("step 16");
                    for (String key : dataparams.keySet()) {
                        System.out.println("key is:" + key);

                        if (key.equals("thumb") )
                        {

                            os.write(("Content-Disposition: form-data; name=\""
                                    + key + "\"; filename=\"" + key + ".gif"
                                    + "\"" + endLine).getBytes());
                            os.write(("Content-Type: application/octet-stream"
                                    + endLine + endLine).getBytes());
                            os.write(dataparams.getByteArray(key));
                        }
                        else if( key.equals("media"))
                        {
                            os.write(("Content-Disposition: form-data; name=\""
                                    + key + "\"; filename=\"" + key + ".gif"
                                    + "\"" + endLine).getBytes());
                            os.write(("Content-Type: application/octet-stream"
                                    + endLine + endLine).getBytes());
                            os.write(dataparams.getByteArray(key));
                        }
                        else {
                            os.write(("Content-Disposition: form-data; name=\""
                                    + key + "\"" + endLine).getBytes());
                            os.write(("Content-Type: content/unknown" + endLine + endLine)
                                    .getBytes());
                            os.write(dataparams.getString(key).getBytes());
                        }
                        // os.write();
                        os.write((endLine + "--" + strBoundary + endLine)
                                .getBytes());
                        // os.flush();
                    }
                }
                os.flush();
                System.out.println("step 17");
                dataparams.clear();

            }
            System.out.println("step 18");
            String response = "";
            try {
                System.out.println("step 19");
                response = read(conn.getInputStream());

                System.out.println("step 19A");

            } catch (FileNotFoundException e) {
                System.out.println("step 20");
                // Error Stream contains JSON that we can parse to a FB error
                response = read(conn.getErrorStream());
            }

            System.out.println("response is : " + response);
            conn.disconnect();

            return response;
        } catch (OutOfMemoryError e) {
            if (os != null)
                os.flush();
            conn.disconnect();
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("deprecation")
//	public static String postData(String url, String method, Bundle params) {
//
//		String response = "";
//
//		if (!params.containsKey("method")) {
//			params.putString("method", method);
//		}
//
//		HttpClient httpclient = new DefaultHttpClient();
//		HttpPost httppost = new HttpPost(url);
//
//		try {
//			MultipartEntity entity = new MultipartEntity(
//					HttpMultipartMode.BROWSER_COMPATIBLE);
//
//			for (String key : params.keySet()) {
//				if (params.getByteArray(key) != null) {
//					ByteArrayBody bab = new ByteArrayBody(
//							params.getByteArray(key),
//							params.getString("filename"));
//					entity.addPart(key, bab);
//				} else {
//					entity.addPart(key, new StringBody(params.getString(key)));
//				}
//			}
//			httppost.setEntity(entity);
//
//			// Execute HTTP Post Request
//			ResponseHandler<String> responseHandler = new BasicResponseHandler();
//			response = httpclient.execute(httppost, responseHandler);
//
//			// response = httpclient.execute(httppost);
//		} catch (ClientProtocolException e) {
//		} catch (IOException e) {
//		}
//		return response;
//	}

    private static String read(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader r = new BufferedReader(new InputStreamReader(in), 1000);
        for (String line = r.readLine(); line != null; line = r.readLine()) {
            sb.append(line);
        }
        in.close();
        return sb.toString();
    }

    public static void clearCookies(Context context) {
        // Edge case: an illegal state exception is thrown if an instance of
        // CookieSyncManager has not be created. CookieSyncManager is normally
        // created by a WebKit view, but this might happen if you start the
        // app, restore saved state, and click logout before running a UI
        // dialog in a WebView -- in which case the app crashes
        @SuppressWarnings("unused")
        CookieSyncManager cookieSyncMngr = CookieSyncManager
                .createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
    }

    /**
     * Parse a server response into a JSON Object. This is a basic
     * implementation using org.json.JSONObject representation. More
     * sophisticated applications may wish to do their own parsing.
     * <p>
     * The parsed JSON is checked for a variety of error fields and a
     * FacebookException is thrown if an error condition is set, populated with
     * the error message and error type or code if available.
     *
     * @param response - string representation of the response
     * @return the response as a JSON Object
     * @throws JSONException - if the response is not valid JSON
     *                       - if an error condition is set
     */
    public static JSONObject parseJson(String response) throws JSONException {
        // Edge case: when sending a POST request to /[post_id]/likes
        // the return value is 'true' or 'false'. Unfortunately
        // these values cause the JSONObject constructor to throw
        // an exception.

        if (response.equalsIgnoreCase("true")) {
            response = "{value : true}";
        }
        JSONObject json = new JSONObject(response);

        // errors set by the server are not consistent
        // they depend on the method and endpoint
        if (json.has("error")) {
            JSONObject error = json.getJSONObject("error");
            Log.i("error", error.getString("message"));
        }

        return json;
    }

    /**
     * Display a simple alert dialog with the given text and title.
     *
     * @param context Android context in which the dialog should be displayed
     * @param title   Alert dialog title
     * @param text    Alert dialog message
     */
    public static void showAlert(Context context, String title, String text) {
        Builder alertBuilder = new Builder(context);
        alertBuilder.setTitle(title);
        alertBuilder.setMessage(text);
        alertBuilder.create().show();
    }

}
