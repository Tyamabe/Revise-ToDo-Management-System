package com.dmm.task.controller;

import java.time.LocalDate;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.dmm.task.data.entity.Tasks;
import com.dmm.task.data.repository.RegistRepository;
import com.dmm.task.form.RegistForm;
import com.dmm.task.service.AccountUserDetails;

@Controller
public class RegistController {

//	private RegistRepository repo; //repoをCalendarControllerから取得できるように。掲示板課題との違いは、表示を別のクラスにすること
//	public RegistController(RegistRepository repo) {
//		this.repo = repo;
//	}
//	public RegistRepository getRepo() {
//		return repo; 
//	}
	@Autowired
	private RegistRepository repo;
	
	/**
	 * 投稿の一覧表示.
	 * 
	 * @param model モデル
	 * @return 遷移先
	 */
	@GetMapping("/create")
	public String regist(Model model) {
		// 逆順で投稿をすべて取得する
		List<Tasks> list = repo.findAll(Sort.by(Sort.Direction.DESC, "id"));
//    Collections.reverse(list); //普通に取得してこちらの処理でもOK
		model.addAttribute("main", list);
		RegistForm registForm = new RegistForm();
	    registForm.setDate(LocalDate.now()); //★試し。初期値として今の日付を入れる
		model.addAttribute("registForm", registForm);
		return "/create";
	}

	/**
	 * 投稿を作成.
	 * 
	 * @param postForm 送信データ
	 * @param user     ユーザー情報
	 * @return 遷移先
	 */
	@PostMapping("/main/create")
	public String create(@Validated RegistForm registForm, BindingResult bindingResult,
			@AuthenticationPrincipal AccountUserDetails user, Model model) {
	    System.out.println("#####Received date: " + registForm.getDate()); //どの日付がサーバに渡されてる
	 
	    if (registForm.getDate() == null) {  //日付がnullだったらエラー出す
	        model.addAttribute("errorMessage", "日付が入力されていません。");
	        return "create";
	    }
	    
		// バリデーションの結果、エラーがあるかどうかチェック
		//if (bindingResult.hasErrors()) {
			// エラーがある場合は投稿登録画面を返す
		//	List<Regist> list = repo.findAll(Sort.by(Sort.Direction.DESC, "id"));
		//	model.addAttribute("regist", list);
		//	model.addAttribute("registForm", registForm);
		//	return "redirect:/main";
		//}
	//    if (bindingResult.hasErrors()) {
	//    	// バリデーションエラーがある場合、エラー情報を持ってフォームを再表示
	//    	model.addAttribute("tasks", repo.findAll(Sort.by(Sort.Direction.DESC, "id")));
	//    	model.addAttribute("registForm", registForm);
	//    	System.out.println("###日付nullチェック###");
	//    	return "create";
	//    	}
		
	    if (registForm.getDate() == null) {
	        model.addAttribute("errorMessage", "日付が入力されていません。");
	        return "create"; // 日付が null の場合はエラーメッセージを表示してフォーム画面に戻る
	    }

		Tasks tasks = new Tasks();
		tasks.setName(user.getUsername());//ここがgetNameだったことが問題だったっぽい
		tasks.setTitle(registForm.getTitle());
		tasks.setText(registForm.getText());
		tasks.setDate(registForm.getDate().atStartOfDay());

		repo.save(tasks);

		return "redirect:/main";
	}
	
	//投稿を編集する
	@PostMapping("/main/edit/{id}")
	public String updateTask(@PathVariable("id") Integer id, @Valid RegistForm form, BindingResult bindingResult, Model model) {
	    if (bindingResult.hasErrors()) {
	        // フォームにエラーがある場合、再度編集フォームを表示
	        model.addAttribute("task", form);
	        return "edit";
	    }
	    // データベースからタスクを検索
	    Tasks task = repo.findById(id).orElse(null);
	    if (task != null) {
	        task.setTitle(form.getTitle());//タイトルの更新
	        task.setText(form.getText());//内容の更新
	        task.setDone(form.isDone()); // 完了フラグ更新
	        
	        //タスクが完了されている場合、タイトルの先頭にチェックマークを追加
	      //  if (form.isDone()) {
	        //    task.setTitle("✅ " + task.getTitle());
	        //}
	        
	        
	        repo.save(task);//
	    } else {
	        // タスクが見つからない場合はエラーメッセージを表示
	        model.addAttribute("errorMessage", "タスクが見つかりませんでした。");
	        return "redirect:/main";
	    }
	    // 更新後はカレンダーページにリダイレクト
	    return "redirect:/main";
	}

	

	/**
	 * 投稿を削除する
	 * 
	 * @param id 投稿ID
	 * @return 遷移先
	 */
	@PostMapping("/main/delete/{id}")//editから、遷移後のuRLであるこちらへ変更→/main/delete/{id}
	public String delete(@PathVariable("id") Integer id) {
		repo.deleteById(id);
		return "redirect:/main";
	}
	
	//タスクがクリックされたらeditに遷移するようにするための設定
	@GetMapping("/main/edit/{id}")
	public String editTaskForm(@PathVariable("id") Integer id, Model model) {
	    // タスクをIDで検索
	    Tasks task = repo.findById(id).orElse(null);
	    if (task == null) {
	        return "redirect:/main"; // タスクが見つからない場合はメインページにリダイレクト
	    }
	    model.addAttribute("task", task);
	    return "edit"; // edit.htmlを表示
	}
}