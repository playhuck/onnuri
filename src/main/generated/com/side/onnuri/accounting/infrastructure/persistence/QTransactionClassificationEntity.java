package com.side.onnuri.accounting.infrastructure.persistence;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTransactionClassificationEntity is a Querydsl query type for TransactionClassificationEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTransactionClassificationEntity extends EntityPathBase<TransactionClassificationEntity> {

    private static final long serialVersionUID = -129091937L;

    public static final QTransactionClassificationEntity transactionClassificationEntity = new QTransactionClassificationEntity("transactionClassificationEntity");

    public final ComparablePath<java.util.UUID> categoryId = createComparable("categoryId", java.util.UUID.class);

    public final StringPath categoryName = createString("categoryName");

    public final DateTimePath<java.time.LocalDateTime> classifiedAt = createDateTime("classifiedAt", java.time.LocalDateTime.class);

    public final ComparablePath<java.util.UUID> companyId = createComparable("companyId", java.util.UUID.class);

    public final StringPath companyName = createString("companyName");

    public final NumberPath<Double> confidence = createNumber("confidence", Double.class);

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final StringPath matchedKeyword = createString("matchedKeyword");

    public final StringPath ruleVersion = createString("ruleVersion");

    public final ComparablePath<java.util.UUID> ruleVersionId = createComparable("ruleVersionId", java.util.UUID.class);

    public final EnumPath<com.side.onnuri.accounting.domain.enums.ClassificationStatus> status = createEnum("status", com.side.onnuri.accounting.domain.enums.ClassificationStatus.class);

    public final ComparablePath<java.util.UUID> transactionId = createComparable("transactionId", java.util.UUID.class);

    public QTransactionClassificationEntity(String variable) {
        super(TransactionClassificationEntity.class, forVariable(variable));
    }

    public QTransactionClassificationEntity(Path<? extends TransactionClassificationEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTransactionClassificationEntity(PathMetadata metadata) {
        super(TransactionClassificationEntity.class, metadata);
    }

}

