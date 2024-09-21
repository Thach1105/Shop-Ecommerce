package com.shopme.export.CategoryExporter;

import com.shopme.dto.response.CategoryResponse;
import com.shopme.dto.response.UserResponse;
import com.shopme.export.AbstractExporter;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.List;

public class CategoryExcelExporter extends AbstractExporter {

    public void export(List<CategoryResponse> list, HttpServletResponse response) throws IOException {
        super.setResponseHeader(response, ".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Categories");

        //Create header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Name");
        headerRow.createCell(2).setCellValue("Alias");
        headerRow.createCell(3).setCellValue("Description");
        headerRow.createCell(4).setCellValue("Enable");
        headerRow.createCell(5).setCellValue("Parent ID");
        //headerRow.createCell(6).setCellValue("Enable");

        int rowNum = 1;
        for ( var category : list){

            Row row = sheet.createRow(rowNum);
            row.createCell(0).setCellValue(category.getId());
            row.createCell(1).setCellValue(category.getName());
            row.createCell(2).setCellValue(category.getAlias());
            row.createCell(3).setCellValue(category.getDescription());
            row.createCell(4).setCellValue(category.isEnabled());
            if(!category.getChildren().isEmpty()){
                int rowChildNum = rowNum + 1;
                for (var child : category.getChildren()){
                    Row rowChild = sheet.createRow(rowChildNum);
                    rowChild.createCell(0).setCellValue(child.getId());
                    rowChild.createCell(1).setCellValue(child.getName());
                    rowChild.createCell(2).setCellValue(child.getAlias());
                    rowChild.createCell(3).setCellValue(child.getDescription());
                    rowChild.createCell(4).setCellValue(child.isEnabled());
                    rowChild.createCell(5).setCellValue(child.getId());

                    rowChildNum++;
                }
                rowNum = rowChildNum;
            } else rowNum++;
        }

        //writing to output stream
        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
