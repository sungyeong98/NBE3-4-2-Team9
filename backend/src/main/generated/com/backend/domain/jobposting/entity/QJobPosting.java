package com.backend.domain.jobposting.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QJobPosting is a Querydsl query type for JobPosting
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QJobPosting extends EntityPathBase<JobPosting> {

    private static final long serialVersionUID = -563754838L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QJobPosting jobPosting = new QJobPosting("jobPosting");

    public final NumberPath<Long> applyCnt = createNumber("applyCnt", Long.class);

    public final DateTimePath<java.time.ZonedDateTime> closeDate = createDateTime("closeDate", java.time.ZonedDateTime.class);

    public final StringPath companyLink = createString("companyLink");

    public final StringPath companyName = createString("companyName");

    public final QExperienceLevel experienceLevel;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final EnumPath<JobPostingStatus> jobPostingStatus = createEnum("jobPostingStatus", JobPostingStatus.class);

    public final ListPath<com.backend.domain.jobskill.entity.JobSkill, com.backend.domain.jobskill.entity.QJobSkill> jobSkillList = this.<com.backend.domain.jobskill.entity.JobSkill, com.backend.domain.jobskill.entity.QJobSkill>createList("jobSkillList", com.backend.domain.jobskill.entity.JobSkill.class, com.backend.domain.jobskill.entity.QJobSkill.class, PathInits.DIRECT2);

    public final DateTimePath<java.time.ZonedDateTime> openDate = createDateTime("openDate", java.time.ZonedDateTime.class);

    public final DateTimePath<java.time.ZonedDateTime> postDate = createDateTime("postDate", java.time.ZonedDateTime.class);

    public final QRequireEducate requireEducate;

    public final QSalary salary;

    public final StringPath subject = createString("subject");

    public final StringPath url = createString("url");

    public QJobPosting(String variable) {
        this(JobPosting.class, forVariable(variable), INITS);
    }

    public QJobPosting(Path<? extends JobPosting> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QJobPosting(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QJobPosting(PathMetadata metadata, PathInits inits) {
        this(JobPosting.class, metadata, inits);
    }

    public QJobPosting(Class<? extends JobPosting> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.experienceLevel = inits.isInitialized("experienceLevel") ? new QExperienceLevel(forProperty("experienceLevel")) : null;
        this.requireEducate = inits.isInitialized("requireEducate") ? new QRequireEducate(forProperty("requireEducate")) : null;
        this.salary = inits.isInitialized("salary") ? new QSalary(forProperty("salary")) : null;
    }

}

