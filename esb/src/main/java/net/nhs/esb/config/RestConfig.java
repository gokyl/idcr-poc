package net.nhs.esb.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import net.nhs.esb.patient.rest.PatientSearchController;
import net.nhs.esb.rest.OpenEhr;
import net.nhs.esb.rest.Patients;
import org.apache.camel.component.cxf.spring.SpringJAXRSClientFactoryBean;
import org.apache.camel.component.cxf.spring.SpringJAXRSServerFactoryBean;
import org.apache.camel.spring.javaconfig.CamelConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestConfig extends CamelConfiguration {

    @Value("${openehr.address}")
    private String openEhrAddress;

    @Autowired
    private Patients patients;

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new ISO8601DateFormat());
        return objectMapper;
    }

    @Bean
    public JacksonJsonProvider jacksonJsonProvider() {
        JacksonJsonProvider jacksonJsonProvider = new JacksonJsonProvider();
        jacksonJsonProvider.setMapper(objectMapper());
        return jacksonJsonProvider;
    }

    @Bean
    public SpringJAXRSServerFactoryBean rsPatients() {
        SpringJAXRSServerFactoryBean springJAXRSServerFactoryBean = new SpringJAXRSServerFactoryBean();
        springJAXRSServerFactoryBean.setServiceBean(patients);
        springJAXRSServerFactoryBean.setAddress("/patients/");
        springJAXRSServerFactoryBean.setLoggingFeatureEnabled(true);
        springJAXRSServerFactoryBean.setProvider(jacksonJsonProvider());
        return springJAXRSServerFactoryBean;
    }

    @Bean
    public SpringJAXRSClientFactoryBean rsOpenEhr() {
        SpringJAXRSClientFactoryBean springJAXRSClientFactoryBean = new SpringJAXRSClientFactoryBean();
        springJAXRSClientFactoryBean.setServiceClass(OpenEhr.class);
        springJAXRSClientFactoryBean.setAddress(openEhrAddress);
        springJAXRSClientFactoryBean.setLoggingFeatureEnabled(true);
        springJAXRSClientFactoryBean.setProvider(jacksonJsonProvider());
        return springJAXRSClientFactoryBean;
    }

    @Bean
    public SpringJAXRSServerFactoryBean rsPatientSearch() {
        SpringJAXRSServerFactoryBean factoryBean = new SpringJAXRSServerFactoryBean();
        factoryBean.setServiceClass(PatientSearchController.class);
        factoryBean.setLoggingFeatureEnabled(true);
        factoryBean.setProvider(jacksonJsonProvider());

        return factoryBean;
    }
}
