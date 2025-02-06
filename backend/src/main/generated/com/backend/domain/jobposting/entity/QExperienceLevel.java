package com.backend.domain.jobposting.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QExperienceLevel is a Querydsl query type for ExperienceLevel
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QExperienceLevel extends BeanPath<ExperienceLevel> {

    private static final long serialVersionUID = -426607755L;

    public static final QExperienceLevel experienceLevel = new QExperienceLevel("experienceLevel");

    public final NumberPath<Integer> code = createNumber("code", Integer.class);

    public final NumberPath<Integer> max = createNumber("max", Integer.class);

    public final NumberPath<Integer> min = createNumber("min", Integer.class);

    public final StringPath name = createString("name");

    public QExperienceLevel(String variable) {
        super(ExperienceLevel.class, forVariable(variable));
    }

    public QExperienceLevel(Path<? extends ExperienceLevel> path) {
        super(path.getType(), path.getMetadata());
    }

    public QExperienceLevel(PathMetadata metadata) {
        super(ExperienceLevel.class, metadata);
    }

}

