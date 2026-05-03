package com.vsignai.backend.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class GoogleService {

    private final String CLIENT_ID = "367800881851-0vas7cftbs3afhorluk2u9ipp4btttm9.apps.googleusercontent.com";

    public GoogleIdToken.Payload verifyToken(String idTokenString) {

        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    JacksonFactory.getDefaultInstance()
            )
                    .setAudience(Collections.singletonList(CLIENT_ID))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);

            if (idToken != null) {
                return idToken.getPayload();
            }

        } catch (Exception e) {
            throw new RuntimeException("Invalid Google token");
        }

        throw new RuntimeException("Invalid Google token");
    }
}
