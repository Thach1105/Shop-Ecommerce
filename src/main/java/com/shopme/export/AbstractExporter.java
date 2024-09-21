package com.shopme.export;

import jakarta.servlet.http.HttpServletResponse;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class AbstractExporter {
    public void setResponseHeader(HttpServletResponse response, String extension, String contentType){
        Date now = new Date();
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
        String timestamp = formatter.format(now);

        String fileName = "user_" + timestamp + extension;
        /*        System.out.println(fileName);*/

        //config header response
        response.setContentType(contentType);
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=" + fileName;
        response.setHeader(headerKey, headerValue);

    }
}
