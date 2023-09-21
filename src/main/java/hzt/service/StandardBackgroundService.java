package hzt.service;

import hzt.model.appearance.Resource;
import hzt.service.inteface.BackgroundService;
import hzt.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toSet;

public final class StandardBackgroundService implements BackgroundService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StandardBackgroundService.class);
    public static final Resource NO_PICTURE = new Resource("No picture", new File(""));
    private static final String RELATIVE_BG_IMAGES_RESOURCE_DIR = "/images/backgrounds";

    private final Set<Resource> resources;

    public StandardBackgroundService() {
        resources = scanForResourceImages();
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
                .collect(collectingAndThen(toSet(), StandardBackgroundService::logIfEmptyAndAddNoPicture));
    }

    private static Set<Resource> logIfEmptyAndAddNoPicture(Collection<Resource> collection) {
        if (collection.isEmpty()) {
            LOGGER.error("Resource folder at {} not found...", RELATIVE_BG_IMAGES_RESOURCE_DIR);
        }
        collection.add(NO_PICTURE);
        return Set.copyOf(collection);
    }

    private static String extractName(File file) {
        return StringUtils.convertToCapitalizedSentence(file.getName(), ".jpg", ".png");
    }

    public Set<Resource> getResources() {
        return Set.copyOf(resources);
    }

}
