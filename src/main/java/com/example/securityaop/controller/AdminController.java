package com.example.securityaop.controller;

import com.example.securityaop.annotation.AdminOnly;
import com.example.securityaop.model.SecurityLog;
import com.example.securityaop.model.Feedback;
import com.example.securityaop.repository.SecurityLogRepository;
import com.example.securityaop.repository.FeedbackRepository;
import com.example.securityaop.service.UserService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;

import java.io.ByteArrayOutputStream;
import java.util.*;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminController {

    @Autowired private UserService userService;
    @Autowired private SecurityLogRepository logRepo;
    @Autowired private FeedbackRepository feedbackRepo;

    // ✅ Dashboard stats
    @AdminOnly
    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        try {
            return ResponseEntity.ok(userService.getStats());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to load admin stats"));
        }
    }

    // ✅ Logs
    @AdminOnly
    @GetMapping("/securityLogs")
    public ResponseEntity<?> getSecurityLogs() {
        try {
            return ResponseEntity.ok(logRepo.findTop20ByOrderByTimestampDesc());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to load security logs"));
        }
    }

    // ✅ Export Logs to Excel
    @AdminOnly
    @GetMapping("/exportLogs")
    public void exportLogs(HttpServletResponse response) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Security Logs");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Timestamp");
            header.createCell(1).setCellValue("Email");
            header.createCell(2).setCellValue("Action");
            header.createCell(3).setCellValue("Details");
            header.createCell(4).setCellValue("Type");

            int rowIdx = 1;
            for (SecurityLog log : logRepo.findAll()) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(String.valueOf(log.getTimestamp()));
                row.createCell(1).setCellValue(log.getEmail() != null ? log.getEmail() : "N/A");
                row.createCell(2).setCellValue(log.getAction());
                row.createCell(3).setCellValue(log.getDetails());
                row.createCell(4).setCellValue(log.getType());
            }

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=security_logs.xlsx");
            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ✅ All users
    @AdminOnly
    @GetMapping("/users")
    public ResponseEntity<?> getUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // ✅ Delete user
    @AdminOnly
    @PostMapping("/deleteUser")
    public ResponseEntity<?> deleteUser(@RequestParam String email) {
        boolean result = userService.deleteByEmail(email);
        return ResponseEntity.ok(Map.of("message", result ? "User deleted" : "User not found"));
    }

    // ✅ Change role
    @AdminOnly
    @PostMapping("/updateRole")
    public ResponseEntity<?> updateRole(@RequestParam String email, @RequestParam String role) {
        boolean result = userService.updateUserRole(email, role);
        return ResponseEntity.ok(Map.of("message", result ? "Role updated" : "Failed to update role"));
    }

    // ✅ Activate/deactivate user
    @AdminOnly
    @PostMapping("/setActive")
    public ResponseEntity<?> setActive(@RequestParam String email, @RequestParam boolean active) {
        boolean result = userService.setUserActive(email, active);
        return ResponseEntity.ok(Map.of("message", result ? "User status updated" : "Failed to update status"));
    }

    // ✅ View all feedback
    @AdminOnly
    @GetMapping("/feedbacks")
    public ResponseEntity<?> getFeedbacks() {
        return ResponseEntity.ok(feedbackRepo.findAll());
    }

    // ✅ Export all users to Excel
    @AdminOnly
    @GetMapping("/exportUsers")
    public ResponseEntity<byte[]> exportUsers() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Users");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Email");
            header.createCell(1).setCellValue("Role");
            header.createCell(2).setCellValue("Active");
            header.createCell(3).setCellValue("Name");

            int rowIdx = 1;
            for (var user : userService.getAllUsers()) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(user.getEmail());
                row.createCell(1).setCellValue(user.getRole());
                row.createCell(2).setCellValue(user.isActive());
                row.createCell(3).setCellValue(user.getName());
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=users.xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(out.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(null);
        }
    }
}
