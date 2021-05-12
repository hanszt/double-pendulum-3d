package hzt.service;

import javafx.beans.property.SimpleStringProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.lang.String.format;

public class AboutService implements IAboutService {

    private static final Logger LOGGER = LogManager.getLogger(AboutService.class);
    private static final String RELATIVE_TEXT_RESOURCE_DIR = "../../about";

    public List<AboutText> loadContent() {
        List<AboutText> aboutTexts = new ArrayList<>();
       var url = getClass().getResource(RELATIVE_TEXT_RESOURCE_DIR);
        if (url != null) {
            var fileDir = new File(url.getFile());
            if (fileDir.isDirectory()) {
                for (String fileName : fileDir.list()) {
                    String name = fileName.replace(".txt", "").replace("_", " ");
                    var aboutText = new AboutText(name, new SimpleStringProperty(loadTextContent(fileName)));
                    aboutTexts.add(aboutText);
                }
            }
        } else  LOGGER.error("about folder not found at "+ RELATIVE_TEXT_RESOURCE_DIR + "...");
        if (aboutTexts.isEmpty()) aboutTexts.add(new AboutText("no content", new SimpleStringProperty()));
        return aboutTexts;
    }

    private String loadTextContent(String fileName) {
        var sb = new StringBuilder();
        List<String> fileTextContent = readInputFileByLine(getClass().getResource(RELATIVE_TEXT_RESOURCE_DIR + "/" + fileName).getFile());
        fileTextContent.forEach(str -> sb.append(str).append(format("%n")));
        return sb.toString();
    }

    private static List<String> readInputFileByLine(String path) {
        List<String> inputList = new ArrayList<>();
        var file = new File(path);
        try (var input = new Scanner(file)) {
            while (input.hasNextLine()) {
                inputList.add(input.nextLine());
            }
        } catch (FileNotFoundException e) {
            LOGGER.error(() -> "File with path " + path + " not found...");
        }
        return inputList;
    }


}
