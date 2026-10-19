package com.project.gl.helper;

import com.project.gl.model.entity.JournalId;
import com.project.gl.repo.JournalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JournalCodeGenerator {

    private final JournalRepository journalIdRepository;
    private static final SecureRandom random = new SecureRandom();
    private static final String PREFIX = "JR-";


    public String generateUniqueJournalCode() {
        String code;
        do {
            code = generateRandomCode();
        } while (journalIdRepository.existsByJournalCode(code));
        return code;
    }

    private String generateRandomCode2() {
        // First digit: 1-9 (not 0)
        int firstDigit = random.nextInt(9) + 1;
        
        // Remaining 4 digits: 0-9
        StringBuilder code = new StringBuilder();
        code.append(firstDigit);
        for (int i = 1; i < 5; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

     private String generateRandomCode3() {
        // Generate 5 digits: 0-9
        StringBuilder digits = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            digits.append(random.nextInt(10));
        }
        return PREFIX + digits.toString();
    }

    private String generateRandomCode() {
        // First digit: 1-9 (not 0)
        int firstDigit = random.nextInt(9) + 1;
        
        // Remaining 4 digits: 0-9
        StringBuilder digits = new StringBuilder();
        digits.append(firstDigit);
        for (int i = 1; i < 5; i++) {
            digits.append(random.nextInt(10));
        }
        return PREFIX + digits.toString();
    }
}