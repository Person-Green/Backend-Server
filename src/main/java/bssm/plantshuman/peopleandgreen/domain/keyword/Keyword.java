package bssm.plantshuman.peopleandgreen.domain.keyword;

import bssm.plantshuman.peopleandgreen.domain.plantEnvironment.PlantEnvironment;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Keyword {

    @Id
    private String keywordId;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String keyword;

    @Column(nullable = false)
    private String environmentCondition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = true)
    private PlantEnvironment plantEnvironment;

}
