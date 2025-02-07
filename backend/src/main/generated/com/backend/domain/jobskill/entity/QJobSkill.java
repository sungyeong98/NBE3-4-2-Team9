package com.backend.domain.jobskill.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QJobSkill is a Querydsl query type for JobSkill
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QJobSkill extends EntityPathBase<JobSkill> {

    private static final long serialVersionUID = -2133332920L;

    public static final QJobSkill jobSkill = new QJobSkill("jobSkill");

    public final NumberPath<Integer> code = createNumber("code", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public QJobSkill(String variable) {
        super(JobSkill.class, forVariable(variable));
    }

    public QJobSkill(Path<? extends JobSkill> path) {
        super(path.getType(), path.getMetadata());
    }

    public QJobSkill(PathMetadata metadata) {
        super(JobSkill.class, metadata);
    }

}

