package com.shopme.export.BrandExporter;

import com.shopme.dto.response.BrandResponse;
import com.shopme.export.AbstractExporter;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.List;

public class BrandExcelExporter extends AbstractExporter {

    public void export(List<BrandResponse> list, HttpServletResponse response) throws IOException {
        super.setResponseHeader(response,".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Brands");

        //Create header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Name");
        headerRow.createCell(2).setCellValue("Logo");
        headerRow.createCell(3).setCellValue("Categories");

        int rowNum = 1;
        for(var brand : list){
            Row row = sheet.createRow(rowNum);
            row.createCell(0).setCellValue(brand.getId());
            row.createCell(1).setCellValue(brand.getName());
            row.createCell(2).setCellValue(brand.getLogo());
            StringBuilder listCategory = new StringBuilder();
            for(String i : brand.getCategories()){
                listCategory.append("; ").append(i);
            }
            row.createCell(3).setCellValue(listCategory.substring(2));

            rowNum++;
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
