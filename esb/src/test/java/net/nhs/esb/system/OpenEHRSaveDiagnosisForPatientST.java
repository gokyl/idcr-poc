package net.nhs.esb.system;

import static net.nhs.domain.openehr.constants.RouteConstants.*;

import java.util.Date;
import java.util.Properties;

import net.nhs.domain.openehr.model.Diagnoses;
import net.nhs.esb.aggregators.ListAddElementAggregator;
import net.nhs.esb.config.RestConfig;
import net.nhs.esb.enrichers.DiagnosesEnricher;
import net.nhs.esb.enrichers.FindDiagnosisIdEnricher;
import net.nhs.esb.enrichers.OpenEhrCreateDiagnosisParams;
import net.nhs.esb.enrichers.OpenEhrPatientParams;
import net.nhs.esb.rest.Patients;
import net.nhs.esb.routes.OpenEHRCommonRoute;
import net.nhs.esb.routes.OpenEHRGetDiagnosisForPatientRoute;
import net.nhs.esb.routes.OpenEHRSaveDiagnosisForPatientRoute;
import net.nhs.esb.util.PropertiesTestConfig;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.spring.SpringRouteBuilder;
import org.apache.camel.test.spring.CamelSpringDelegatingTestContextLoader;
import org.apache.camel.test.spring.CamelSpringJUnit4ClassRunner;
import org.apache.camel.test.spring.MockEndpoints;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;

@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
		OpenEHRSaveDiagnosisForPatientST.TestConfig.class,
		OpenEHRSaveDiagnosisForPatientRoute.class,
		OpenEhrPatientParams.class,
		RestConfig.class,
		OpenEHRCommonRoute.class,
		OpenEhrCreateDiagnosisParams.class,
		FindDiagnosisIdEnricher.class,
		OpenEHRGetDiagnosisForPatientRoute.class,
		ListAddElementAggregator.class, DiagnosesEnricher.class
		}, loader = CamelSpringDelegatingTestContextLoader.class)
@MockEndpoints
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class OpenEHRSaveDiagnosisForPatientST {

	@Produce(uri = DIRECT+START)
	private ProducerTemplate start;

	@EndpointInject(uri = MOCK+END)
	private MockEndpoint endMock;

    @Test
    public void testRoute() throws Exception {
    	//given
    	Integer patientId = 746;
    	Date nowDate = new Date();
    	
    	Diagnoses diagnoses = new Diagnoses();
		diagnoses.setProblemDiagnosis("ProblemDiagnosis");
		diagnoses.setDescription("Description");
		diagnoses.setSeverity("Severity");
		diagnoses.setDateOfOnset(nowDate);
		diagnoses.setAgeAtOnset(18);
		diagnoses.setBodySite("BodySite");
		diagnoses.setDateOfResolution(nowDate);
		diagnoses.setAgeAtResolution(18);
    	
        // when
        start.sendBodyAndHeader(diagnoses, Patients.PATIENT_ID_HEADER, patientId);

        // then
        assertEquals(1, endMock.getExchanges().size());
    }

    @Configuration
    public static class TestConfig extends PropertiesTestConfig {

        @Override
        protected void loadProperties(Properties properties) {
        	properties.put("openEHRSaveDiagnosisForPatientRoute", DIRECT+OPENEHR_SAVE_DIAGNOSIS_FOR_PATIENT_ROUTE);
        	properties.put("openEHRGetDiagnosisForPatientRoute", DIRECT+OPENEHR_GET_DIAGNOSIS_FOR_PATIENT_ROUTE);
        }
        
        @Bean
		public SpringRouteBuilder testRoute() {
			return new SpringRouteBuilder() {

				@Override
				public void configure() throws Exception {
                    from(DIRECT+START)
                    	.to(DIRECT+OPENEHR_SAVE_DIAGNOSIS_FOR_PATIENT_ROUTE)
                    	.to("log:end?showAll=true")
                        .to(MOCK+END)
                        ;
				}
			};
		}
    }
}
