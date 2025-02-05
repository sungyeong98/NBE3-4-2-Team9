package com.backend.domain.jobposting.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QSalary is a Querydsl query type for Salary
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QSalary extends BeanPath<Salary> {

    private static final long serialVersionUID = -746400721L;

    public static final QSalary salary = new QSalary("salary");

    public final NumberPath<Integer> code = createNumber("code", Integer.class);

    public final StringPath name = createString("name");

    public QSalary(String variable) {
        super(Salary.class, forVariable(variable));
    }

    public QSalary(Path<? extends Salary> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSalary(PathMetadata metadata) {
        super(Salary.class, metadata);
    }

}

