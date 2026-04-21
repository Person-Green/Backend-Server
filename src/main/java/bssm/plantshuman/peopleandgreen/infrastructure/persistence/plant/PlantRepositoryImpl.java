package bssm.plantshuman.peopleandgreen.infrastructure.persistence.plant;

import bssm.plantshuman.peopleandgreen.catalog.adapter.out.persistence.entity.QFavoritePlantEntity;
import bssm.plantshuman.peopleandgreen.catalog.domain.model.PlantCatalogFilter;
import bssm.plantshuman.peopleandgreen.catalog.domain.model.PlantCatalogItem;
import bssm.plantshuman.peopleandgreen.catalog.domain.model.PlantCatalogSortType;
import bssm.plantshuman.peopleandgreen.domain.plant.QPlant;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
public class PlantRepositoryImpl implements PlantRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public PlantRepositoryImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public List<PlantCatalogItem> findCatalogPage(
            String cursor,
            int sizePlusOne,
            PlantCatalogSortType sortType,
            PlantCatalogFilter filter
    ) {
        QPlant plant = QPlant.plant;
        QFavoritePlantEntity favorite = QFavoritePlantEntity.favoritePlantEntity;
        NumberExpression<Long> favoriteCount = favorite.id.count();

        BooleanBuilder whereClause = new BooleanBuilder();
        if (StringUtils.hasText(filter.keyword())) {
            whereClause.and(
                    plant.plantKoreanName.containsIgnoreCase(filter.keyword())
                            .or(plant.plantEnglishName.containsIgnoreCase(filter.keyword()))
            );
        }
        if (!filter.manageDifficulties().isEmpty()) {
            whereClause.and(plant.manageDifficulty.in(filter.manageDifficulties()));
        }
        if (!filter.airPurifications().isEmpty()) {
            whereClause.and(plant.airPurification.in(filter.airPurifications()));
        }
        if (!filter.sizes().isEmpty()) {
            whereClause.and(plant.size.in(filter.sizes()));
        }
        if (!filter.environmentTypeIds().isEmpty()) {
            whereClause.and(plant.primaryEnvironment.typeId.in(filter.environmentTypeIds()));
        }

        if (sortType == PlantCatalogSortType.ID_ASC && StringUtils.hasText(cursor)) {
            whereClause.and(plant.plantId.gt(cursor));
        }

        JPAQuery<PlantCatalogItem> query = queryFactory
                .select(Projections.constructor(
                        PlantCatalogItem.class,
                        plant.plantId,
                        plant.plantKoreanName,
                        plant.plantEnglishName,
                        plant.size,
                        plant.airPurification,
                        plant.manageDifficulty,
                        favoriteCount
                ))
                .from(plant)
                .leftJoin(favorite).on(favorite.plant.eq(plant))
                .where(whereClause)
                .groupBy(
                        plant.plantId,
                        plant.plantKoreanName,
                        plant.plantEnglishName,
                        plant.size,
                        plant.airPurification,
                        plant.manageDifficulty
                );

        if (sortType == PlantCatalogSortType.LIKE_DESC) {
            if (StringUtils.hasText(cursor)) {
                LikeCursorKey cursorKey = parseLikeCursor(cursor);
                BooleanExpression cursorCondition = favoriteCount.lt(cursorKey.favoriteCount())
                        .or(favoriteCount.eq(cursorKey.favoriteCount()).and(plant.plantId.gt(cursorKey.plantId())));
                query.having(cursorCondition);
            }
            query.orderBy(favoriteCount.desc(), plant.plantId.asc());
        } else {
            query.orderBy(plant.plantId.asc());
        }

        return query.limit(sizePlusOne).fetch();
    }

    private LikeCursorKey parseLikeCursor(String cursor) {
        String[] tokens = cursor.split("\\|", -1);
        if (tokens.length != 2 || !StringUtils.hasText(tokens[1])) {
            throw new IllegalArgumentException("Invalid cursor format for LIKE_DESC sort");
        }
        try {
            long favoriteCount = Long.parseLong(tokens[0]);
            if (favoriteCount < 0) {
                throw new IllegalArgumentException("Invalid cursor format for LIKE_DESC sort");
            }
            return new LikeCursorKey(favoriteCount, tokens[1]);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Invalid cursor format for LIKE_DESC sort");
        }
    }

    private record LikeCursorKey(long favoriteCount, String plantId) {
    }
}
