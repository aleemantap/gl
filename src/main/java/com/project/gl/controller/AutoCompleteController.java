package com.project.gl.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.project.gl.helper.AutoCompleteHelper;
import com.project.gl.model.entity.NomorGlAccount;

@Controller
@RequestMapping("/auto-complete")
public class AutoCompleteController {

    @Autowired
    private AutoCompleteHelper autoCompleteHelper;
  
    @GetMapping("/gl-debit-kredit-for-select-2-jquery/{debet_or_kredit}")
    @ResponseBody
    public List<Map<String, Object>> getGlDebitKredit(
        @PathVariable("debet_or_kredit") String debetOrKredit,
        @RequestParam(value = "q", defaultValue = "") String query) {

    List<Map<String, Object>> results = new ArrayList<>();

    if (query != null && query.trim().length() >= 2) {

        List<Object[]> data = autoCompleteHelper.getGELDebet(query,debetOrKredit);

        for (Object[] row : data) {

            Map<String, Object> item = new HashMap<>();

            item.put("id", row[0]);
            item.put("nomorGl", row[1]);
            item.put("kategori", row[2]);
            item.put("cabang", row[3]);
            item.put("nama", row[4]);

            results.add(item);
        }
    }

    return results;
}
    
}
