package com.droolSetup.drool.config;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.droolSetup.drool.utils.KieUtils;
import lombok.extern.slf4j.Slf4j;
import org.drools.decisiontable.DecisionTableProviderImpl;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.*;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.io.ResourceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

@Configuration
@Slf4j
public class DroolConfig {
 /*   @Autowired
    private AmazonS3 amazonS3Client;
*/
    /*@Value("${spring.receipt.bucket}")
    private String bucket;*/
    private KieServices kieServices = KieServices.Factory.get();

    @Bean
    @ConditionalOnMissingBean(KieFileSystem.class)
    public KieFileSystem getKieFileSystem() throws IOException {
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        Resource fileResource;
        try {
            //boolean fileExists = amazonS3Client.doesObjectExist(bucket, "FILE S3 PATH");
            //fileResource = ResourceFactory.newUrlResource(generateUrl("FILE S3 PATH", HttpMethod.GET));
            //fileResource = ResourceFactory.newFileResource("FILEPATH");// .newUrlResource(String.valueOf(Paths.get()));
            fileResource = ResourceFactory.newFileResource("/resources/DroolFile.xlsx");// .newUrlResource(String.valueOf(Paths.get()));
            kieFileSystem.write(fileResource);
            DecisionTableProviderImpl decisionTableProvider = new DecisionTableProviderImpl();
           // String file = decisionTableProvider.loadFromResource(fileResource, null);
        } catch (Exception e) {
            log.info("Exception while reading files from s3  ", e);
            e.printStackTrace();
        }
        return kieFileSystem;
    }

    @Bean
    @ConditionalOnMissingBean(KieContainer.class)
    public KieContainer kieContainer() throws IOException {
        KieServices kieServices = KieServices.Factory.get();
        final KieRepository kieRepository = kieServices.getRepository();

        kieRepository.addKieModule(new KieModule() {
            @Override
            public ReleaseId getReleaseId() {
                return kieRepository.getDefaultReleaseId();
            }
        });

        KieBuilder kieBuilder = kieServices.newKieBuilder(getKieFileSystem());
        Results results = kieBuilder.getResults();
        if (results.hasMessages(Message.Level.ERROR)) {
            log.info("File Reloading Issues" + results.getMessages());
            throw new IllegalStateException("### errors ###");
        }
        kieBuilder.buildAll();
        KieContainer kieContainer = kieServices.newKieContainer(kieRepository.getDefaultReleaseId());
        KieUtils.setKieContainer(kieContainer);
        return kieContainer;
    }

    @Bean
    @ConditionalOnMissingBean(KieBase.class)
    public KieBase kieBase() throws IOException {
        return kieContainer().getKieBase();
    }

/*    private String generateUrl(String fileName, HttpMethod httpMethod) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, 1);
        return amazonS3Client.generatePresignedUrl(bucket, fileName, calendar.getTime(), httpMethod).toString();
    }*/
}
