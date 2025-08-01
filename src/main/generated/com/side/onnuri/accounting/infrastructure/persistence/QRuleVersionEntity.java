package com.side.onnuri.accounting.infrastructure.persistence;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QRuleVersionEntity is a Querydsl query type for RuleVersionEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRuleVersionEntity extends EntityPathBase<RuleVersionEntity> {

    private static final long serialVersionUID = -2062775689L;

    public static final QRuleVersionEntity ruleVersionEntity = new QRuleVersionEntity("ruleVersionEntity");

    public final BooleanPath active = createBoolean("active");

    public final StringPath description = createString("description");

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final BooleanPath isConsumed = createBoolean("isConsumed");

    public final SimplePath<com.fasterxml.jackson.databind.JsonNode> ruleData = createSimple("ruleData", com.fasterxml.jackson.databind.JsonNode.class);

    public final DateTimePath<java.time.LocalDateTime> uploadedAt = createDateTime("uploadedAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> usedAt = createDateTime("usedAt", java.time.LocalDateTime.class);

    public final StringPath versionName = createString("versionName");

    public QRuleVersionEntity(String variable) {
        super(RuleVersionEntity.class, forVariable(variable));
    }

    public QRuleVersionEntity(Path<? extends RuleVersionEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRuleVersionEntity(PathMetadata metadata) {
        super(RuleVersionEntity.class, metadata);
    }

}

