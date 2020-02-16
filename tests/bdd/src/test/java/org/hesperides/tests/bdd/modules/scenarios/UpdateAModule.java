package org.hesperides.tests.bdd.modules.scenarios;

import cucumber.api.java8.En;
import org.hesperides.presentation.io.ModuleIO;
import org.hesperides.presentation.io.templatecontainers.TemplateIO;
import org.hesperides.tests.bdd.CucumberSpringBean;
import org.hesperides.tests.bdd.modules.ModuleSamples;
import org.hesperides.tests.bdd.modules.contexts.ModuleContext;
import org.hesperides.tests.bdd.modules.contexts.TemplateContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertEquals;

public class UpdateAModule extends CucumberSpringBean implements En {

    @Autowired
    private ModuleContext moduleContext;
    @Autowired
    private TemplateContext templateContext;

    private ResponseEntity response;

    public UpdateAModule() {

        When("^updating this module$", () -> {
            ModuleIO moduleInput = ModuleSamples.getModuleInputWithVersionId(1);
            response = moduleContext.updateModule(moduleInput);
        });

        When("^updating the same version of the module alongside$", () -> {
            ModuleIO moduleInput = ModuleSamples.getModuleInputWithVersionId(1);
            response = failTryingToUpdateModule(moduleInput);
        });

        Then("^the module is successfully updated", () -> {
            assertEquals(HttpStatus.OK, response.getStatusCode());
            ModuleIO module = (ModuleIO) response.getBody();
            assertEquals(2L, module.getVersionId().longValue());
        });

        Then("^the module update is rejected$", () -> {
            assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        });

        Then("^the module contains the template$", () -> {
            ResponseEntity<TemplateIO> response = templateContext.retrieveExistingTemplate();
            assertEquals(HttpStatus.OK, response.getStatusCode());
        });
    }

    private ResponseEntity failTryingToUpdateModule(ModuleIO moduleInput) {
        return rest.doWithErrorHandlerDisabled(rest -> rest.exchange("/modules", HttpMethod.PUT, new HttpEntity<>(moduleInput), String.class));
    }

    // TODO Tester la mise à jour de technos
}
