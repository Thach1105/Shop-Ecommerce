package com.shopme.export.UserExporter;

import com.shopme.dto.response.UserResponse;
import com.shopme.export.AbstractExporter;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;



import java.io.IOException;
import java.util.List;

public class UserCsvExporter extends AbstractExporter {

    public void export(List<UserResponse> list, HttpServletResponse response){

        super.setResponseHeader(response, ".csv", "text/csv");

        //Create CSVPrinter to write CSV
        try {
            CSVPrinter csvPrinter = new CSVPrinter(response.getWriter(),
                    CSVFormat.DEFAULT.withHeader("ID", "Username", "First Name", "Last Name", "Email", "Role", "Enabled" ));

            for (UserResponse user : list) {
                try {
                    csvPrinter.printRecord(
                            user.getId(),
                            user.getUsername(),
                            user.getFirstName(),
                            user.getLastName(),
                            user.getEmail(),
                            user.getRoles().replace(" ", ","),
                            user.isEnabled());
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
            csvPrinter.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }
}
