package com.github.birros.vlc.server;
    
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.json.JSONObject;
import org.json.JSONException;

import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.Media;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response.Status;

public class Server extends NanoHTTPD {
    private MediaPlayer mMediaPlayer = null;
    private String mPassword = null;

    public Server(MediaPlayer mediaPlayer, int port, String password) throws IOException {
        super(port);
        mMediaPlayer = mediaPlayer;
        mPassword = password;
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
    }

    public void destroy() {
        stop();
    }

    private Response basicAuth(IHTTPSession session, String username, String password) {
        String authorization = session.getHeaders().get("authorization");
        if (authorization != null && authorization.toLowerCase().startsWith("basic")) {
            String base64Credentials = authorization.substring("Basic".length()).trim();
            byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(credDecoded, StandardCharsets.UTF_8);
            final String[] values = credentials.split(":", 2);

            String u = values[0];
            String p = values[1];

            if (p.equals(password)) {
                return null;
            }
        }

        Response response = NanoHTTPD.newFixedLengthResponse(Status.UNAUTHORIZED, "", null);
        response.addHeader("WWW-Authenticate", "Basic realm=\"Authentication needed\"");
        return response;
    }

    @Override
    public Response serve(IHTTPSession session) {
        Response res = basicAuth(session, "", mPassword);
        if (res != null) {
            return res;
        }

        if (mMediaPlayer != null) {
            long position = mMediaPlayer.getTime();
            long duration = mMediaPlayer.getLength();
            Media media = mMediaPlayer.getMedia();
            String uri = media != null ? media.getUri().toString() : "";

            try {
                JSONObject sampleObject = new JSONObject();
                sampleObject.put("position", position);
                sampleObject.put("duration", duration);
                sampleObject.put("uri", uri);
                
                return newFixedLengthResponse(sampleObject.toString(2));
            } catch(JSONException e) {
                return newFixedLengthResponse(e.toString());
            }
        } else {
            return newFixedLengthResponse("No media");
        }
    }
}