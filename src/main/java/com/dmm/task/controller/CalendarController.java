package com.dmm.task.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
        
        // タスクの取得とマッピング
        List<Tasks> allTasks = registRepository.findAll();
        Map<LocalDate, List<Tasks>> tasksMap = new HashMap<>();
        for (Tasks task : allTasks) {
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
}