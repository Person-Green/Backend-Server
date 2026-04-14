package bssm.plantshuman.peopleandgreen.auth.adapter.in.web.dto.request;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UpdateUsernameRequestTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void rejectsUsernameLongerThanThirtyCharacters() {
        UpdateUsernameRequest request = new UpdateUsernameRequest("1234567890123456789012345678901");

        assertEquals(1, validator.validate(request).size());
    }
}
