package com.wheretoeat.gtech.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

@NoRepositoryBean
public interface BaseRepo<T, ID extends Serializable> extends JpaRepository<T, ID> {
    String ACTIVE = " e.deleted = false ";
    String CREATED_BY = " and e.createdBy.id = :userId ";
    String COUNT = "select count(e) ";

}