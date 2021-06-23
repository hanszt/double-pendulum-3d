package hzt;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

class LayeredArchitectureTest {

    @Test
    void servicesShouldOnlyBeAccessedByControllers() {
        JavaClasses importedClasses = new ClassFileImporter().importPackages("hzt");

        ArchRule myRule = classes()
                .that().resideInAPackage("..service..")
                .should().onlyBeAccessed().byAnyPackage("..controller..", "..service..");

        myRule.check(importedClasses);
    }

    @Test
    void controllersShouldOnlyBeAccessedByFrontend() {
        JavaClasses importedClasses = new ClassFileImporter().importPackages("hzt");

        ArchRule myRule = layeredArchitecture()
                .layer("controller").definedBy("..controller..")
                .layer("view").definedBy("..view..")
                .whereLayer("controller").mayOnlyBeAccessedByLayers("view");

        myRule.check(importedClasses);
    }

    @Test
    void viewLayerShouldNotBeAccessedByAnyOtherLayer() {
        JavaClasses importedClasses = new ClassFileImporter().importPackages("hzt");
        layeredArchitecture()
                .layer("view").definedBy("..view..")
                .whereLayer("view").mayNotBeAccessedByAnyLayer()
                .check(importedClasses);

    }

}

