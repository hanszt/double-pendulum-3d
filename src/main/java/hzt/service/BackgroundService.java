package hzt.service;

import hzt.model.appearance.Resource;
import hzt.service.inteface.IBackgroundService;
import hzt.utils.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class BackgroundService implements IBackgroundService {

    public static final Resource NO_PICTURE = new Resource("No picture", new File(""));
    private static final String RELATIVE_BG_IMAGES_RESOURCE_DIR = "../../images/backgrounds";

    private static final Logger LOGGER = LogManager.getLogger(BackgroundService.class);

    private final Set<Resource> resources;

    public BackgroundService() {
        this.resources = scanForResourceImages();
    }

    private Set<Resource> scanForResourceImages() {
        return Optional.ofNullable(getClass().getResource(RELATIVE_BG_IMAGES_RESOURCE_DIR))
                .map(URL::getFile)
                .map(File::new)
                .filter(File::isDirectory)
                .map(File::listFiles)
                .stream()
                .flatMap(Arrays::stream)
                .map(file -> new Resource(extractName(file), file))
                .collect(Collectors.collectingAndThen(Collectors.toSet(), BackgroundService::checkNotEmpty));
    }

    private static Set<Resource> checkNotEmpty(Set<Resource> set) {
        if (set.isEmpty()) {
            LOGGER.error("Resource folder at " + RELATIVE_BG_IMAGES_RESOURCE_DIR + " not found...");
        }
        set.add(NO_PICTURE);
        return Collections.unmodifiableSet(set);
    }

    private static String extractName(File file) {
        return StringUtils.convertToCapitalizedSentence(file.getName(), ".jpg", ".png");
    }

    public Set<Resource> getResources() {
        return Collections.unmodifiableSet(resources);
    }

}
