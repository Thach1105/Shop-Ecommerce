package com.shopme.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.shopme.dto.request.IntrospectRequest;
import com.shopme.dto.request.LoginRequest;
import com.shopme.dto.request.LogoutRequest;
import com.shopme.dto.request.RefreshRequest;
import com.shopme.dto.response.AuthenticationResponse;
import com.shopme.dto.response.IntrospectResponse;
import com.shopme.entity.InvalidatedToken;
import com.shopme.entity.User;
import com.shopme.exception.AppException;
import com.shopme.exception.ErrorCode;
import com.shopme.repository.InvalidatedTokenRepository;
import com.shopme.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Service
public class AuthenticationService {

    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    @Value("${jwt.valid-duration}")
    protected long VALID_DURATION;

    @Value("${jwt.refreshable-duration}")
    protected long REFRESHABLE_DURATION;

    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    InvalidatedTokenRepository invalidatedTokenRepository;

    //login
    public AuthenticationResponse authenticate(LoginRequest request) throws Exception {
        var user = userRepository.findByUsername(request.getUsername());
        if(user != null) {
            var authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());

            if(!authenticated){
                throw new AppException(ErrorCode.LOGIN_FAIL);
            }

                String token = generateToken(user);
                return AuthenticationResponse.builder()
                        .authenticate(true)
                        .token(token)
                        .build();

        }
        throw new AppException(ErrorCode.USERNAME_NOT_FOUND);
    }

    //create token
    private String generateToken(User user){
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("Ecommerce Shop")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()
                ))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", user.buildRole())
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(jwsHeader, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException("Cannot create token: "+ e);
        }
    }

    public AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {

        // xác minh token
        var signJWT = verifyToken(request.getToken(), true);

        //vô hiệu token cũ
        var jit = signJWT.getJWTClaimsSet().getJWTID();
        var expiryTime = signJWT.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jit)
                .expiryTime(expiryTime)
                .build();
        invalidatedTokenRepository.save(invalidatedToken);

        //tạo token mới
        String username = signJWT.getJWTClaimsSet().getSubject();

        var user = userRepository.findByUsername(username);
        if(user == null) throw new AppException(ErrorCode.UNAUTHENTICATED);

        var token = generateToken(user);
        return AuthenticationResponse.builder()
                .token(token)
                .authenticate(true)
                .build();
    }


    //logout
    public void logout(LogoutRequest request) throws ParseException, JOSEException {

        try {
            //Xác thực token và lưu id và thời gian refresh của token vào db
            var signToken = verifyToken(request.getToken(), true);

            String jwtID = signToken.getJWTClaimsSet().getJWTID();
            Date expiryTime = new Date(signToken.getJWTClaimsSet().getExpirationTime()
                    .toInstant().plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS).toEpochMilli()
            );

            InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                    .expiryTime(expiryTime)
                    .id(jwtID)
                    .build();

            invalidatedTokenRepository.save(invalidatedToken);
        } catch (AppException e){
            log.info("Token already expired");
        }

    }

    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.getToken();
        boolean valid = true;
        try {
            verifyToken(token, false);
        } catch (AppException e) {
           valid = false;
        }


        return IntrospectResponse.builder()
                .valid(valid)
                .build();
    }

    private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {

        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = (isRefresh) ?
                new Date(signedJWT.getJWTClaimsSet().getIssueTime()
                        .toInstant().plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS).toEpochMilli()
                )
                : signedJWT.getJWTClaimsSet().getExpirationTime();

        //kiểm tra xem token có bị thay đổi không
        var verified = signedJWT.verify(verifier);

        // nếu token xác minh không thành công hoặc quá hạn thì báo lỗi không thể xác thực
        if (!(verified && expiryTime.after(new Date())))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        // nếu token nằm trong danh sách token đã logout thì báo lỗi không thể xác thực
        if (invalidatedTokenRepository
                .existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        return signedJWT;
    }
}
