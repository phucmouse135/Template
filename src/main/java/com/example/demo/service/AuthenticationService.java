package com.example.demo.service;

import com.example.demo.model.request.AuthenticationRequest;
import com.example.demo.model.request.IntrospectRequest;
import com.example.demo.model.request.LogoutRequest;
import com.example.demo.model.request.RefreshRequest;
import com.example.demo.model.response.AuthenticationResponse;
import com.example.demo.model.response.IntrospectResponse;
import com.nimbusds.jose.JOSEException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.text.ParseException;

@Service
public interface AuthenticationService {

//    AuthenticationResponse outboundAuthenticate(OAuth2User oAuth2User);

    IntrospectResponse introspect(IntrospectRequest request);

    AuthenticationResponse authenticate(AuthenticationRequest request);

    void logout(LogoutRequest request) throws ParseException, JOSEException;

    AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException;
}
