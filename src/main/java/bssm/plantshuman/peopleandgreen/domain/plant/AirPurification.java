package bssm.plantshuman.peopleandgreen.domain.plant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum AirPurification {
    NORMAL("보통"),
    HIGH("높음"),
    VERY_HIGH("매우 높음");

    private final String description;
}
