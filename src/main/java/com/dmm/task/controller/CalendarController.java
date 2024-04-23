package com.dmm.task.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.config.Task;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.dmm.task.data.entity.Tasks;
import com.dmm.task.data.repository.RegistRepository;

@Controller
public class CalendarController {
	
    @Autowired
    private RegistRepository registRepository; //タスクデータを取得するため。 RegistRepository の注入
    
    @GetMapping("/main")
    public String showCalendar(Model model) {
    	List<List<LocalDate>> monthList = new ArrayList<>();
        LocalDate today = LocalDate.now();
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
            LocalDate taskDate = task.getDate().toLocalDate(); // Regist に日付が LocalDateTime 型と仮定
            tasksMap.computeIfAbsent(taskDate, k -> new ArrayList<>()).add(task);
        }

        model.addAttribute("matrix", monthList);
        model.addAttribute("prev", today.minusMonths(1));
        model.addAttribute("next", today.plusMonths(1));
        Map<LocalDate, List<Task>> tasks = new HashMap<>();
        model.addAttribute("tasks", tasks);
        return "main";
    }
}