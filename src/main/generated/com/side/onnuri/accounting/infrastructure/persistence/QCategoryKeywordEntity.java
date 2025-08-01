package com.side.onnuri.accounting.infrastructure.persistence;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCategoryKeywordEntity is a Querydsl query type for CategoryKeywordEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCategoryKeywordEntity extends EntityPathBase<CategoryKeywordEntity> {

    private static final long serialVersionUID = 326405126L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCategoryKeywordEntity categoryKeywordEntity = new QCategoryKeywordEntity("categoryKeywordEntity");

    public final QAccountCategoryEntity category;

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final StringPath keyword = createString("keyword");

    public QCategoryKeywordEntity(String variable) {
        this(CategoryKeywordEntity.class, forVariable(variable), INITS);
    }

    public QCategoryKeywordEntity(Path<? extends CategoryKeywordEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCategoryKeywordEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCategoryKeywordEntity(PathMetadata metadata, PathInits inits) {
        this(CategoryKeywordEntity.class, metadata, inits);
    }

    public QCategoryKeywordEntity(Class<? extends CategoryKeywordEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.category = inits.isInitialized("category") ? new QAccountCategoryEntity(forProperty("category"), inits.get("category")) : null;
    }

}

