package com.dmm.task.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.dmm.task.data.entity.Tasks;
import com.dmm.task.data.repository.RegistRepository;

@Controller
public class CalendarController {
	
    @Autowired
    private RegistRepository registRepository; //タスクデータを取得するため。 RegistRepository の注入
    
    @GetMapping("/main")
    public String showCalendar(@RequestParam Optional<String> date, Model model) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); //yyyy-mm-ddのパターンを指定
        LocalDate today = date.map(d -> LocalDate.parse(d, dateFormatter)).orElse(LocalDate.now()); //Optional→nullかもしれない時。値をLocalDateオブジェクトへ.
        
    	List<List<LocalDate>> monthList = new ArrayList<>();
        LocalDate firstDayOfMonth = today.withDayOfMonth(1);
        int daysFromSunday = firstDayOfMonth.getDayOfWeek().getValue() % 7;
        LocalDate calendarStart = firstDayOfMonth.minusDays(daysFromSunday);

        LocalDate currentDay = calendarStart;
        while (currentDay.getMonth() == today.getMonth() || currentDay.isBefore(firstDayOfMonth)) {
            List<LocalDate> weekRow = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                weekRow.add(currentDay);
                currentDay = currentDay.plusDays(1); //
            }
            monthList.add(weekRow);
        }
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();//Authenticationオブジェクト(今ログインしているアカウントのロールなどの情報)取得
        List<Tasks> tasks = new ArrayList<>();//タスクのリスト初期化して
        if (authentication != null && authentication.isAuthenticated()) {//Authenticationオブジェクトがnullじゃない(=セキュリティコンテキストの設定が正しい)&&ちゃんとログイン(=認証)されたユーザー
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();//認証プロセス中に設定されたプリンシパル(=ログインユーザー)の取得。
            String username = userDetails.getUsername();  // UserDetailsオブジェクトから、ログイン時に使用されるユーザー名を取得
            if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {//ユーザーに割り当てられた全てのロールを取得。ROLE_ADMINに合致する要素が一つでもないかチェック
            	System.out.println("###admin###");
            	tasks = registRepository.findAll();//ADMINの場合全てのタスクをデータベースから取得
            } else {
            	System.out.println("###user###");//①正しい方を通っているかわかる
                tasks = registRepository.findByName(username);  // ユーザー名に基づいてタスクをフィルタリング②通っていルナらば処理が間違えている
            }
        }
        
        // タスクの取得とマッピング
        Map<LocalDate, List<Tasks>> tasksMap = new HashMap<>();
        for (Tasks task : tasks) {
            LocalDate taskDate = task.getDate().toLocalDate(); // LocalDateTimeからLocalDateへ
            tasksMap.computeIfAbsent(taskDate, k -> new ArrayList<>()).add(task);
        }
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年 MM月");
        String formattedDate = today.format(formatter);

        model.addAttribute("matrix", monthList);
        model.addAttribute("prev", today.minusMonths(1));
        model.addAttribute("next", today.plusMonths(1));
        model.addAttribute("tasks", tasksMap);
        model.addAttribute("month", formattedDate);
        return "main";
    }
    @GetMapping("/main/create/{date}")//日付をクリックしたタスクregist画面に遷移するように
    public String createTaskForm(@PathVariable("date") String dateString, Model model) {
        LocalDate date;
        try {
            date = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (DateTimeParseException e) {
            return "redirect:/main"; // 日付の形式が正しくない場合、カレンダーメインページにリダイレクト
        }
        model.addAttribute("date", date);  // Date型として日付を追加
        return "create";
    }

}