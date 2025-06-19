package com.theanh1301.myapp.service;


import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.theanh1301.myapp.dto.request.AuthenticationRequest;
import com.theanh1301.myapp.dto.request.IntrospectRequest;
import com.theanh1301.myapp.dto.request.LogoutRequest;
import com.theanh1301.myapp.dto.request.RefreshTokenRequest;
import com.theanh1301.myapp.dto.response.AuthenticationResponse;
import com.theanh1301.myapp.dto.response.IntrospectResponse;
import com.theanh1301.myapp.entity.InvalidatedToken;
import com.theanh1301.myapp.entity.User;
import com.theanh1301.myapp.exception.AppException;
import com.theanh1301.myapp.exception.ErrorCode;
import com.theanh1301.myapp.repository.InvalidatedTokenRepository;
import com.theanh1301.myapp.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j // tạo ra biến log
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class AuthenticationService {

    //UUID chuổi 32 kí tự ngẫu nhiên -> random global luôn
    UserRepository userRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;

    //Nữa đặt chổ khác -> da dat trong application.properties
    @NonFinal // de lombok khong them final vao constructor
    @Value("${jwt.signerKey}")
    protected String SINGER_KEY;

    @NonFinal
    @Value("${jwt.expired}")
    protected  long EXPIRED_TOKEN;

    @NonFinal
    @Value("${jwt.refreshabled}")
    protected  long REFRESHED_TOKEN;



    //Ktra trang thai su dung cua token
    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.getToken();
        try {
            verifyToken(token,false); // mình đã tách hàm

        }catch (AppException e){
            //Do dưới kia nếu logout rồi mà cố đăng nhập lại thì mình in ra AppException rồi
            //nên duyệt false luôn
            return IntrospectResponse.builder().valid(false).build();
        }
        return IntrospectResponse.builder().valid(true).build();

    }
    //dang nhap tạo token
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        //tìm user   ->
        var user = userRepository.findByUsername(request.getUsername()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTS));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());// so sánh password
        if (!authenticated) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        var token = generateToken(user);


        return AuthenticationResponse.builder().token(token).authenticated(true).build();
    }
    //Tao token cua jwt
    private String generateToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        //builder payload
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())//user đăng nhập
                .issuer("devtheanh.com") //token issuer từ ai
                .issueTime(new Date()) // tg đăng đăng ký
                .expirationTime(new Date(Instant.now().plus(EXPIRED_TOKEN, ChronoUnit.MINUTES).toEpochMilli())) // thời hạn token sau 1 giờ  -> trong kia 60 nên minnh chỉnh về phút
                .claim("scope", buildScope(user)) //thêm role vào -> SCOPE_ TÊN ROLE
                .jwtID(UUID.randomUUID().toString()) // xử lý logout token
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);
        //Ký token -> MACSigner() khóa ký và giải mã trùng nhau
        try {
            jwsObject.sign(new MACSigner(SINGER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Không thể tạo token lỗi:" + e);
            throw new RuntimeException(e);
        }
    }
    //build cho scope của token(jwt) chúa role và permission  -> muốn xem scope lên service xem luôn (khoongg xem ở current_user đc
    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" "); //ngăn cách nhau bởi dấu cách
        if (!CollectionUtils.isEmpty(user.getRoles())) // Không empty -> lấy role ra

            //Mình thêm tiền tố ROLE_ và permission mình để trống (để dễ phân biệt)
            user.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_"+role.getName());
                //add cả permission detail(về sau mình đã sửa)
                if(!CollectionUtils.isEmpty(role.getPermissions()))
                    role.getPermissions().forEach(permission -> {stringJoiner.add(permission.getName());});
            });
        return stringJoiner.toString();
    }

    public void logout(LogoutRequest request) throws JOSEException ,ParseException {
        try {
            var signToken = verifyToken(request.getToken(), true);
            //lấy jwtTokenID
            String jit = signToken.getJWTClaimsSet().getJWTID();
            Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();
            InvalidatedToken invalidatedToken =  InvalidatedToken.builder()
                    .id(jit).expiryTime(expiryTime).build();
            invalidatedTokenRepository.save(invalidatedToken);
        }catch (AppException e){
            log.info("Token trong logout có lỗi");
        }



    }
    //private mình viết mấy hàm dùng chung cho class này(để private vì chỉ để mấy thằng này dùng)

    //Co che ktra token duoc kha dung khong( neu het han , khong dung , co trong bang invalidatedToken(do logout hoac refresh )

    private SignedJWT verifyToken(String token,boolean isRefresh) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SINGER_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);

        //Token het han chua?  -> nếu dùng cho refresh thì dùng tg của refresh (tg từ luc tạo token + 120p mình quy định bên application.properties)
        Date expityTime = (isRefresh) ? new Date(signedJWT.getJWTClaimsSet().getIssueTime().toInstant()
                .plus(REFRESHED_TOKEN,ChronoUnit.MINUTES).toEpochMilli())
        : signedJWT.getJWTClaimsSet().getExpirationTime();
        var verifiedJWT = signedJWT.verify(verifier);
        //không đúng chữ ký  hoăc hết hạn
        if(!verifiedJWT && expityTime.after(new Date())){
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        //nếu logout rồi thì đã có id trong bảng invalid -> thì lúc đó token sẽ đc mình quy định
        //như xóa đi rồi (nên 401 không dùng token đó nữa)
        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        return signedJWT;
    }

    //do no cung tra ve token va boolean kia nen minh dung AuthenticationResponse luon
    public AuthenticationResponse refreshToken(RefreshTokenRequest request) throws JOSEException, ParseException {
        //Kiem tra token
        var singedJWT = verifyToken(request.getToken(), true); // Ktra kha dung cua token  ( neu het han , khong dung , co trong bang invalidatedToken(do logout hoac refresh ) xem chi tiet o tren
        String jit = singedJWT.getJWTClaimsSet().getJWTID();
        Date expiryTime = singedJWT.getJWTClaimsSet().getExpirationTime();

        //Luu vao invalid token -> de vo hieu luc token nay
        InvalidatedToken invalidatedToken =  InvalidatedToken.builder()
                .id(jit).expiryTime(expiryTime).build();
        invalidatedTokenRepository.save(invalidatedToken);


        //Tao token moi dua vua username ( khong can password )
        var username = singedJWT.getJWTClaimsSet().getSubject();
        var user = userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTS));
        var token = generateToken(user);


        return AuthenticationResponse.builder().token(token).authenticated(true).build();
    }
}
