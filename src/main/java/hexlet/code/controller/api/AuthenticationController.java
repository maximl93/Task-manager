package hexlet.code.controller.api;

import hexlet.code.dto.AuthDTO;
import hexlet.code.util.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthenticationController {
    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public String login(@RequestBody AuthDTO authData) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(authData.getUsername(), authData.getPassword());
        authenticationManager.authenticate(authentication);
        return jwtUtils.generateToken(authData.getUsername());
    }
}
