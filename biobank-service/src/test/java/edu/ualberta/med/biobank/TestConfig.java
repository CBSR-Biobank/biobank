package edu.ualberta.med.biobank;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import edu.ualberta.med.biobank.model.provider.CollectionEventProvider;
import edu.ualberta.med.biobank.model.provider.CollectionEventTypeProvider;
import edu.ualberta.med.biobank.model.provider.Mother;
import edu.ualberta.med.biobank.model.provider.PatientProvider;
import edu.ualberta.med.biobank.model.provider.StudyProvider;

@Configuration
@Profile("dev")
public class TestConfig {

    @Bean
    public Mother getMother() {
        return new Mother();
    }

    @Bean
    public StudyProvider getStudyProvider() {
        return new StudyProvider(getMother());
    }

    @Bean
    public PatientProvider getPatientProvider() {
        return new PatientProvider(getMother());
    }

    @Bean
    public CollectionEventProvider getCollectionEventProvider() {
        return new CollectionEventProvider(getMother());
    }

    @Bean
    public CollectionEventTypeProvider getCollectionEventTypeProvider() {
        return new CollectionEventTypeProvider(getMother());
    }

}
