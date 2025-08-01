package com.side.onnuri.accounting.infrastructure.persistence;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTransactionEventEntity is a Querydsl query type for TransactionEventEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTransactionEventEntity extends EntityPathBase<TransactionEventEntity> {

    private static final long serialVersionUID = 66722599L;

    public static final QTransactionEventEntity transactionEventEntity = new QTransactionEventEntity("transactionEventEntity");

    public final ComparablePath<java.util.UUID> categoryId = createComparable("categoryId", java.util.UUID.class);

    public final ComparablePath<java.util.UUID> companyId = createComparable("companyId", java.util.UUID.class);

    public final ComparablePath<java.util.UUID> eventId = createComparable("eventId", java.util.UUID.class);

    public final StringPath eventType = createString("eventType");

    public final StringPath matchedKeyword = createString("matchedKeyword");

    public final DateTimePath<java.time.LocalDateTime> occurredAt = createDateTime("occurredAt", java.time.LocalDateTime.class);

    public final EnumPath<com.side.onnuri.accounting.domain.enums.ClassificationStatus> status = createEnum("status", com.side.onnuri.accounting.domain.enums.ClassificationStatus.class);

    public final ComparablePath<java.util.UUID> transactionId = createComparable("transactionId", java.util.UUID.class);

    public QTransactionEventEntity(String variable) {
        super(TransactionEventEntity.class, forVariable(variable));
    }

    public QTransactionEventEntity(Path<? extends TransactionEventEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTransactionEventEntity(PathMetadata metadata) {
        super(TransactionEventEntity.class, metadata);
    }

}

