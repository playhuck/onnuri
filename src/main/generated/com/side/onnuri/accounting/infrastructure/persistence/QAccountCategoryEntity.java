package com.side.onnuri.accounting.infrastructure.persistence;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAccountCategoryEntity is a Querydsl query type for AccountCategoryEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAccountCategoryEntity extends EntityPathBase<AccountCategoryEntity> {

    private static final long serialVersionUID = -928494778L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAccountCategoryEntity accountCategoryEntity = new QAccountCategoryEntity("accountCategoryEntity");

    public final StringPath categoryId = createString("categoryId");

    public final StringPath categoryName = createString("categoryName");

    public final QCompanyEntity company;

    public final ListPath<String, StringPath> excludeKeywords = this.<String, StringPath>createList("excludeKeywords", String.class, StringPath.class, PathInits.DIRECT2);

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final ListPath<CategoryKeywordEntity, QCategoryKeywordEntity> keywords = this.<CategoryKeywordEntity, QCategoryKeywordEntity>createList("keywords", CategoryKeywordEntity.class, QCategoryKeywordEntity.class, PathInits.DIRECT2);

    public final NumberPath<java.math.BigDecimal> maxAmount = createNumber("maxAmount", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> minAmount = createNumber("minAmount", java.math.BigDecimal.class);

    public final NumberPath<Integer> priority = createNumber("priority", Integer.class);

    public final ComparablePath<java.util.UUID> ruleVersionId = createComparable("ruleVersionId", java.util.UUID.class);

    public QAccountCategoryEntity(String variable) {
        this(AccountCategoryEntity.class, forVariable(variable), INITS);
    }

    public QAccountCategoryEntity(Path<? extends AccountCategoryEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAccountCategoryEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAccountCategoryEntity(PathMetadata metadata, PathInits inits) {
        this(AccountCategoryEntity.class, metadata, inits);
    }

    public QAccountCategoryEntity(Class<? extends AccountCategoryEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.company = inits.isInitialized("company") ? new QCompanyEntity(forProperty("company")) : null;
    }

}

