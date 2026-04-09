package bssm.plantshuman.peopleandgreen.catalog.domain.exception;

import bssm.plantshuman.peopleandgreen.shared.error.GlobalErrorProperty;
import bssm.plantshuman.peopleandgreen.shared.error.PeopleAndGreenException;

public class PlantNotFoundException extends PeopleAndGreenException {

    public PlantNotFoundException() {
        super(GlobalErrorProperty.NOT_FOUND);
    }
}
