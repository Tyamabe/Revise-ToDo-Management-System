package com.dmm.task.form;

import java.time.LocalDate;

import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class RegistForm {
	// titleへのバリデーション設定を追加
	@Size(min = 1, max = 200)
	private String title;
	// textへのバリデーション設定を追加
	@Size(min = 1, max = 200)
	private String text;
 //   @NotNull(message = "日付を入力してください。") // 日付がちゃんと入力されるようにしたい
    @DateTimeFormat(pattern = "yyyy-MM-dd") // 日付フォーマットをHTMLに合わせてハイフンにする
    private LocalDate date;  // 日付フィールド。指定した日付でタスク登録できるように
    private boolean done; // タスクの完了状態を追加
}
