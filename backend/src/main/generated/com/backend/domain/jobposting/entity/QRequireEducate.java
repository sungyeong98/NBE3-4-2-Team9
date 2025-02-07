package com.backend.domain.jobposting.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QRequireEducate is a Querydsl query type for RequireEducate
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QRequireEducate extends BeanPath<RequireEducate> {

    private static final long serialVersionUID = 20461637L;

    public static final QRequireEducate requireEducate = new QRequireEducate("requireEducate");

    public final NumberPath<Integer> code = createNumber("code", Integer.class);

    public final StringPath name = createString("name");

    public QRequireEducate(String variable) {
        super(RequireEducate.class, forVariable(variable));
    }

    public QRequireEducate(Path<? extends RequireEducate> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRequireEducate(PathMetadata metadata) {
        super(RequireEducate.class, metadata);
    }

}

