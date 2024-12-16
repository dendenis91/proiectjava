package ro.unitbv.tvzier.orar;

import com.opencsv.CSVReader;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileReader;
import java.util.List;
import java.nio.file.Paths;
import java.time.LocalDate;

@RestController
public class OrarController {
    @GetMapping(path="getView",produces = MediaType.TEXT_HTML_VALUE)
    public String render(
            @RequestParam("filePath") String filePath,
            @RequestParam(value = "sala", required = false) String sala,
            @RequestParam(value = "partId", required = false) Integer partId,
            @RequestParam(value = "nod", required = false) String nod)
    {
        StringBuilder htmlBuilder = new StringBuilder();

        try {
            System.out.println("Attempting to open file: " + filePath);

            String safePath = Paths.get("./data", filePath).toString();

            //String currentDay = LocalDate.now().getDayOfWeek().name();
            String currentDay = "LUNI";
            try (CSVReader csvReader = new CSVReader(new FileReader(safePath))) {
                List<String[]> rows = csvReader.readAll();
                htmlBuilder.append("<table border='1' style='border-collapse: collapse;'>");
                boolean inSalaSection = false;  // Flag to track when we're in the sala section
                for (String[] row : rows) {
                    boolean matchFound = false;
                    for (String cell : row) {
                        if (cell.equalsIgnoreCase(sala)) {
                            matchFound = true;
                            break;
                        }
                    }

                    if (matchFound) {
                        inSalaSection = true;
                    }

                    if (inSalaSection) {
                        boolean isToday = false;
                        for (String cell : row) {
                            if (cell.equalsIgnoreCase(currentDay)) {
                                isToday = true;
                                break;
                            }
                        }

                        htmlBuilder.append("<tr ");
                        if (isToday) {
                            htmlBuilder.append("style='background-color: #FFD700;'");
                        }
                        htmlBuilder.append(">");

                        for (String cell : row) {
                            htmlBuilder.append("<td>").append(cell).append("</td>");
                        }
                        htmlBuilder.append("</tr>");
                    }

                    if (inSalaSection && row[0].equalsIgnoreCase("SAMBATA")) {
                        break;
                    }
                }
                htmlBuilder.append("</table>");
            }

        } catch (Exception e) {
            e.printStackTrace(); // Log the error for debugging
            return "<p>Error reading the CSV file: " + e.getMessage() + "</p>";
        }

        return htmlBuilder.toString();


        // http://localhost:8080/getView?filePath=Oglinzi_sali_2024.csv&sala=VIII16

    }
}
