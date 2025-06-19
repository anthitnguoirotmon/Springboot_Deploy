package com.theanh1301.myapp.config;


import com.nimbusds.jose.JOSEException;
import com.theanh1301.myapp.dto.request.IntrospectRequest;
import com.theanh1301.myapp.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.text.ParseException;
import java.util.Objects;


//@Component được dùng khi muốn Spring tự tạo và quản lý vòng đời của một lớp.
//(kieu tu custom )

//Custom de logout
@Component
public class CustomJwtDecoder implements JwtDecoder {
    @Value("${jwt.signerKey}")
    private String signerKey;

    @Autowired
    private AuthenticationService authenticationService;

    private NimbusJwtDecoder nimbusJwtDecoder = null;

    //Giai ma cai token -> de minh so sanh co khop khong
    @Override
    public Jwt decode(String token) throws JwtException {
        try{
            //Check token nay con hieu luc khong (Hoac chua logout)
           var response =  authenticationService.introspect(IntrospectRequest.builder().token(token).build());
           if(!response.isValid()){ //response.isValid() tra ra true neu token hop le
               throw new JwtException("Token khong hop le ");

           }
        }catch (JOSEException | ParseException e){
            throw new JwtException(e.getMessage());
        }
        if(Objects.isNull(nimbusJwtDecoder)){
            SecretKeySpec secretKeySpec= new SecretKeySpec(signerKey.getBytes(), "HS512");
            nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(secretKeySpec).macAlgorithm(MacAlgorithm.HS512).build();
        }
        return  nimbusJwtDecoder.decode(token);
    }
}
