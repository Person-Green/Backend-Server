package bssm.plantshuman.peopleandgreen.shared.error;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

class PeopleAndGreenExceptionTest {

    @Test
    void preservesCauseWhenConstructedWithCause() {
        RuntimeException cause = new RuntimeException("boom");

        PeopleAndGreenException exception = new TestPeopleAndGreenException(cause);

        assertSame(cause, exception.getCause());
    }

    private static final class TestPeopleAndGreenException extends PeopleAndGreenException {

        private TestPeopleAndGreenException(Throwable cause) {
            super(GlobalErrorProperty.BAD_REQUEST, cause);
        }
    }
}
