/*******************************************************************************
 * Copyright 2012 One Platform Foundation
 * 
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 ******************************************************************************/
package org.onepf.appdf.parser;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipException;

import org.junit.Before;
import org.junit.Test;
import org.onepf.appdf.model.Application;
import org.onepf.appdf.model.Availability;
import org.onepf.appdf.model.Categorisation;
import org.onepf.appdf.model.Categorisation.ApplicationType;
import org.onepf.appdf.model.Consent;
import org.onepf.appdf.model.ContentDescription;
import org.onepf.appdf.model.ContentDescriptor;
import org.onepf.appdf.model.ContentDescriptor.DescriptorValue;

/**
 * Test covers top level functionality of AppdfFileParser class
 * @author nivanov
 *
 */
public class ParserTest {
	
	private File resource;
	
	@Before
	public void initResource() throws URISyntaxException{
		URL resourceUrl = ParserTest.class.getResource("yshell.appdf");
		resource = new File(resourceUrl.toURI());
	}
	
	@Test
	public void parserCreation() throws IOException{
		@SuppressWarnings("unused")
		AppdfFileParser parser = new AppdfFileParser(resource);
	}
	
	@Test
	public void parseDontFail() throws IOException {
		Application application = parseApplication();
		assertNotEquals(null, application);
	}
	@Test(expected=ZipException.class)
	public void failsOnNonZip() throws IOException, URISyntaxException{
		URL nonZipResourceURL = ParserTest.class.getResource("not.appdf");
		File file = new File(nonZipResourceURL.toURI());
		@SuppressWarnings("unused")
		AppdfFileParser parser = new AppdfFileParser(file);
	}
	
	@Test
	public void checkPackage() throws IOException{
	    Application application = parseApplication();
        assertThat(application.getPackageName(), is("ru.yandex.shell"));
	}

    public Application parseApplication() throws IOException, ParsingException {
        AppdfFileParser parser = new AppdfFileParser(resource);
        Application application = parser.parse().getApplication();
        return application;
    }
	@Test
	public void checkCategorisation() throws ParsingException, IOException{
	    Application app = parseApplication();
	    Categorisation categorisation = app.getCategorisation();
        assertThat(categorisation,notNullValue());
        assertThat(categorisation.getApplicationType(),is(ApplicationType.APPLICATION));
	    assertThat(categorisation.getCategory(),is("personalization"));
	    assertThat(categorisation.getSubCategory(),is("personalization"));
	}
	@Test
	public void checkAvalibility() throws ParsingException,IOException{
	    Application app = parseApplication();
	    Availability avalability = app.getAvalability();
	    assertThat(avalability.getIncludeContries().size(),is(1));
	    assertThat(avalability.getIncludeContries().get(0),is("ru"));
	    List<String> excludeContries = avalability.getExcludeContries();
        assertThat(excludeContries.size(),is(4));
	    Collections.sort(excludeContries);
	    assertThat(excludeContries.get(0),is("by"));
	    assertThat(excludeContries.get(1),is("kz"));
	    assertThat(excludeContries.get(2),is("tr"));
	    assertThat(excludeContries.get(3),is("ua"));
	}
	@Test
	public void checkConsent() throws ParsingException,IOException{
	    Application app = parseApplication();
	    Consent consent = app.getConsent();
	    assertThat(consent.isFreeFromThirdPartyCopytightedContent(), is(true));
	    assertThat(consent.isGoogleAndroidContentGuidelines(), is(true));
	    assertThat(consent.isSlidemeAgreement(), is(true));
	    assertThat(consent.isUsExportLaws(),is(true));	    
	}
	
	@Test
	public void checkContentDescription() throws ParsingException, IOException{
	    Application app = parseApplication();	    
	    ContentDescription contentDescription = app.getContentDescription();
	    ContentDescriptor contentDescriptor = contentDescription.getContentDescriptor();
	    assertThat(contentDescriptor.getAlcohol(),is(DescriptorValue.NO));
	    assertThat(contentDescriptor.getBadLanguage(),is(DescriptorValue.NO));
	    assertThat(contentDescriptor.getCartoonViolence(),is(DescriptorValue.NO));
	    assertThat(contentDescriptor.getDiscrimination(),is(DescriptorValue.NO));
	    assertThat(contentDescriptor.getDrugs(),is(DescriptorValue.NO));
	}
	
}
