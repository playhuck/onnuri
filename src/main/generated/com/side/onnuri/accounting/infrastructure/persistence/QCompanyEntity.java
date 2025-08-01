package com.side.onnuri.accounting.infrastructure.persistence;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCompanyEntity is a Querydsl query type for CompanyEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCompanyEntity extends EntityPathBase<CompanyEntity> {

    private static final long serialVersionUID = 259466232L;

    public static final QCompanyEntity companyEntity = new QCompanyEntity("companyEntity");

    public final ListPath<AccountCategoryEntity, QAccountCategoryEntity> categories = this.<AccountCategoryEntity, QAccountCategoryEntity>createList("categories", AccountCategoryEntity.class, QAccountCategoryEntity.class, PathInits.DIRECT2);

    public final StringPath companyId = createString("companyId");

    public final StringPath companyName = createString("companyName");

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public QCompanyEntity(String variable) {
        super(CompanyEntity.class, forVariable(variable));
    }

    public QCompanyEntity(Path<? extends CompanyEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCompanyEntity(PathMetadata metadata) {
        super(CompanyEntity.class, metadata);
    }

}

