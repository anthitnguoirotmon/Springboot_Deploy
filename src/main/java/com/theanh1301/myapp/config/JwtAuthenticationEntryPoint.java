package com.theanh1301.myapp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theanh1301.myapp.dto.request.NormalizeApiRequest;
import com.theanh1301.myapp.exception.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;


//Nhằm muốn nếu 401 thì nó in theo ErrorCode có message của mình(ban đầu nó chỉ ra 401 k có message)
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        ErrorCode errorCode = ErrorCode.UNAUTHENTICATED;
        response.setStatus(errorCode.getStatusCode().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        NormalizeApiRequest normalizeApiResponse = NormalizeApiRequest.builder().code(errorCode.getCode()).message(errorCode.getMessage()).build();

        ObjectMapper mapper = new ObjectMapper();
        //viết nội dung vòa response
        response.getWriter().write(mapper.writeValueAsString(normalizeApiResponse));
        response.flushBuffer();//commit xuống
    }
}
