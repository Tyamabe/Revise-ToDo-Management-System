package com.dmm.task.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dmm.task.data.entity.Tasks;

public interface RegistRepository extends JpaRepository<Tasks, Integer> {//これだけでタスクの表示はできる
    List<Tasks> findByName(String name); //権限ごとにタスク表示を分けるために追加。Tasksクラスのname属性に基づいてタスク検索
}
