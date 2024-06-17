package com.example.attendify_student;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

public class MultipartRequest extends Request<NetworkResponse> {

    private final String boundary;
    private final String mimeType = "multipart/form-data;boundary=";
    private final Response.Listener<NetworkResponse> listener;
    private final Response.ErrorListener errorListener;
    private final Map<String, String> headers;
    private final Map<String, String> params;
    private final byte[] imageData;
    private final String imageFileName;

    public MultipartRequest(int method, String url, Map<String, String> params, byte[] imageData, String imageFileName,
                            Response.Listener<NetworkResponse> listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.boundary = "apiclient-" + System.currentTimeMillis();
        this.params = params;
        this.imageData = imageData;
        this.imageFileName = imageFileName;
        this.listener = listener;
        this.errorListener = errorListener;
        this.headers = null;
    }

    @Override
    public String getBodyContentType() {
        return mimeType + boundary;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        try {
            // Add text params
            if (params != null && params.size() > 0) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    buildTextPart(dos, entry.getKey(), entry.getValue());
                }
            }

            // Add image data
            if (imageData != null) {
                buildFilePart(dos, "profilePic", imageFileName, imageData);
            }

            // End of multipart/form-data.
            dos.writeBytes("--" + boundary + "--" + "\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bos.toByteArray();
    }

    private void buildTextPart(DataOutputStream dos, String parameterName, String parameterValue) throws IOException {
        dos.writeBytes("--" + boundary + "\r\n");
        dos.writeBytes("Content-Disposition: form-data; name=\"" + parameterName + "\"" + "\r\n");
        dos.writeBytes("\r\n");
        dos.writeBytes(parameterValue + "\r\n");
    }

    private void buildFilePart(DataOutputStream dos, String parameterName, String fileName, byte[] fileData) throws IOException {
        dos.writeBytes("--" + boundary + "\r\n");
        dos.writeBytes("Content-Disposition: form-data; name=\"" + parameterName + "\"; filename=\"" + fileName + "\"" + "\r\n");
        dos.writeBytes("Content-Type: application/octet-stream" + "\r\n");
        dos.writeBytes("\r\n");
        dos.write(fileData);
        dos.writeBytes("\r\n");
    }

    @Override
    protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {
        try {
            return Response.success(response, HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(NetworkResponse response) {
        listener.onResponse(response);
    }

    @Override
    public void deliverError(VolleyError error) {
        errorListener.onErrorResponse(error);
    }
}
