package com.shopme.export.UserExporter;

import com.shopme.dto.response.UserResponse;
import com.shopme.export.AbstractExporter;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import java.util.List;

public class UserPdfExporter extends AbstractExporter {

    public void export(List<UserResponse> list, HttpServletResponse response){
        super.setResponseHeader(response, ".pdf", "application/pdf");

        try {
            PDDocument document = new PDDocument();
            PDPage page = new PDPage();

            document.addPage(page);

            document.save("users.pdf");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
