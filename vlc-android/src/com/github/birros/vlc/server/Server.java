package com.github.birros.vlc.server;
    
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response.Status;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import org.videolan.libvlc.MediaPlayer;

public class Server extends NanoHTTPD {
    private MediaPlayer mMediaPlayer = null;
    private String mPassword = null;

    public Server(MediaPlayer mediaPlayer, int port, String password) throws IOException {
        super(port);
        mMediaPlayer = mediaPlayer;
        mPassword = password;
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
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
            float position = mMediaPlayer.getPosition(); // [0-1] percent
            long duration = mMediaPlayer.getLength(); // ms

            // length
            int length = 0;
            if (duration > 0) {
                length = Math.round(duration / 1000);
            }

            // state
            String state = "";
            int stateNum = mMediaPlayer.getPlayerState();
            switch (stateNum) {
                case 3:
                    state = "playing";
                    break;
                case 4:
                    state = "paused";
                    break;
                case 5:
                    state = "stopped";
                    break;
            }

            try {
                JSONObject sampleObject = new JSONObject();
                sampleObject.put("position", position); // [0-1] percent
                sampleObject.put("state", state);
                sampleObject.put("length", length); // seconds
                
                return newFixedLengthResponse(Status.OK, "application/json", sampleObject.toString(2));
            } catch(JSONException e) {
                return newFixedLengthResponse(e.toString());
            }
        } else {
            return newFixedLengthResponse("No media");
        }
    }
}