/*
 *
 * This file is part of the Hesperides distribution.
 * (https://github.com/voyages-sncf-technologies/hesperides)
 * Copyright (c) 2016 VSCT.
 *
 * Hesperides is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, version 3.
 *
 * Hesperides is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */
package org.hesperides.tests.bdd.technos.scenarios;

import cucumber.api.java8.En;
import org.hesperides.presentation.io.templatecontainers.PartialTemplateIO;
import org.hesperides.presentation.io.templatecontainers.TemplateIO;
import org.hesperides.tests.bdd.CucumberSpringBean;
import org.hesperides.tests.bdd.technos.contexts.TechnoContext;
import org.hesperides.tests.bdd.templatecontainers.TemplateSamples;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class RetrieveTemplates extends CucumberSpringBean implements En {

    @Autowired
    private TechnoContext technoContext;

    private ResponseEntity<PartialTemplateIO[]> response;
    private List<TemplateIO> templateInputs = new ArrayList<>();

    public RetrieveTemplates() {

        Given("^multiple templates in this techno$", () -> {
            for (int i = 0; i < 6; i++) {
                String templateName = TemplateSamples.DEFAULT_NAME + i;
                ResponseEntity<TemplateIO> response = technoContext.addTemplateToExistingTechno(templateName);
                templateInputs.add(response.getBody());
            }
        });

        When("^retrieving the templates of this techno$", () -> {
            response = rest.getTestRest().getForEntity(technoContext.getTemplatesURI(), PartialTemplateIO[].class);
        });

        Then("^I get a list of all the templates of this techno$", () -> {
            assertEquals(HttpStatus.OK, response.getStatusCode());
            List<PartialTemplateIO> templateOutputs = Arrays.asList(response.getBody());
            assertEquals(templateInputs.size() + 1, templateOutputs.size());
            //TODO Vérifier le contenu de chaque template ?
        });
    }
}
