package bssm.plantshuman.peopleandgreen.auth.adapter.in.web;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
@AutoConfigureMockMvc
class AuthHttpIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void returnsBadRequestWhenGoogleStateIsMalformed() throws Exception {
        mockMvc.perform(post("/auth/google/login")
                        .cookie(new Cookie(AuthController.OAUTH_STATE_COOKIE, "test-state-nonce"))
                        .contentType("application/json")
                        .content("""
                                {
                                  "authorizationCode": "fake-code",
                                  "state": "invalid-state",
                                  "redirectUri": "http://localhost:3000/auth/google/callback"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid google oauth state"));
    }

    @Test
    void returnsBadRequestWhenStateCookieIsMissing() throws Exception {
        mockMvc.perform(post("/auth/google/login")
                        .contentType("application/json")
                        .content("""
                                {
                                  "authorizationCode": "fake-code",
                                  "state": "still-fake",
                                  "redirectUri": "http://localhost:3000/auth/google/callback"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Missing oauth state cookie"));
    }
}
