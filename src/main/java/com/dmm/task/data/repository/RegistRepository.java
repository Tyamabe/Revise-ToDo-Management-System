package com.dmm.task.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dmm.task.data.entity.Regist;

public interface RegistRepository extends JpaRepository<Regist, Integer> {

}
