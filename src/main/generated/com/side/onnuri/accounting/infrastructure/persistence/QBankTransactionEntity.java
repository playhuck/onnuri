package com.side.onnuri.accounting.infrastructure.persistence;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QBankTransactionEntity is a Querydsl query type for BankTransactionEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBankTransactionEntity extends EntityPathBase<BankTransactionEntity> {

    private static final long serialVersionUID = -1455952195L;

    public static final QBankTransactionEntity bankTransactionEntity = new QBankTransactionEntity("bankTransactionEntity");

    public final MapPath<String, Object, SimplePath<Object>> attributes = this.<String, Object, SimplePath<Object>>createMap("attributes", String.class, Object.class, SimplePath.class);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public QBankTransactionEntity(String variable) {
        super(BankTransactionEntity.class, forVariable(variable));
    }

    public QBankTransactionEntity(Path<? extends BankTransactionEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBankTransactionEntity(PathMetadata metadata) {
        super(BankTransactionEntity.class, metadata);
    }

}

