package com.shopme.export.UserExporter;
import com.shopme.export.AbstractExporter;
import org.apache.poi.ss.usermodel.*;

import com.shopme.dto.response.UserResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.List;

public class UserExcelExporter extends AbstractExporter {

    public void export(List<UserResponse> list, HttpServletResponse response) throws IOException {
        super.setResponseHeader(response, ".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Users");

        //Create header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Username");
        headerRow.createCell(2).setCellValue("First Name");
        headerRow.createCell(3).setCellValue("Last Name");
        headerRow.createCell(4).setCellValue("Email");
        headerRow.createCell(5).setCellValue("Role");
        headerRow.createCell(6).setCellValue("Enable");

        int rowNum = 1;
        for ( var user : list){
            Row row = sheet.createRow(rowNum);
            row.createCell(0).setCellValue(user.getId());
            row.createCell(1).setCellValue(user.getUsername());
            row.createCell(2).setCellValue(user.getFirstName());
            row.createCell(3).setCellValue(user.getLastName());
            row.createCell(4).setCellValue(user.getEmail());
            row.createCell(5).setCellValue(user.getRoles().replace(' ', ','));
            row.createCell(6).setCellValue(user.isEnabled());
            rowNum++;
        }

        //writing to output stream
        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
