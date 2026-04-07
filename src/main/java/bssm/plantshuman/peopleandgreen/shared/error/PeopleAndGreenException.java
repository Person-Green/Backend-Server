package bssm.plantshuman.peopleandgreen.shared.error;

import lombok.Getter;

@Getter
public abstract class PeopleAndGreenException extends RuntimeException {

    private final ErrorProperty errorProperty;

    public PeopleAndGreenException(ErrorProperty errorProperty, Object... args) {
        super(String.format(errorProperty.getMessage(), args));
        this.errorProperty = errorProperty;
    }

    public PeopleAndGreenException(ErrorProperty errorProperty) {
        super(errorProperty.getMessage());
        this.errorProperty = errorProperty;
    }
}
