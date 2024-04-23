package com.dmm.task.data.entity;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "tasks") //database01内のtasksテーブル(課題で作成したやつ)指定。ここに辿り着くのに非常に時間かかった、、、
public class Tasks {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String title;
	private String name;
	private String text;
	private LocalDateTime date;
	private boolean done;
}
