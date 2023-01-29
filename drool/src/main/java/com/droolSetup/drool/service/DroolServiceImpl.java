package com.droolSetup.drool.service;

import com.droolSetup.drool.dto.DTO;
import com.droolSetup.drool.utils.KieUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.drools.decisiontable.DecisionTableProviderImpl;
import org.kie.api.KieServices;
import org.kie.api.builder.*;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

@Service
@Slf4j
public class DroolServiceImpl {

    public DTO getSeats(DTO dto) throws IOException {
        KieSession kieSession = KieUtils.getKieContainer().newKieSession();
        kieSession.insert(dto);
        kieSession.fireAllRules();
        if (Objects.nonNull(dto.getSeats())) {
        } else {
            log.info("seats not found");
        }
        kieSession.dispose();
        return dto;
    }

    public String reload(String fileType, int reload) {
        String message = null;
        Resource resource = null;
        Results results = null;
        boolean tempFileCorrupted = false;
        boolean mainFileCorrupted = false;
        KieServices kieServices = getKieServices();
        try {
            KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
            resource = ResourceFactory.newFileResource("/resources/DroolFile.xlsx");
            kieFileSystem.write(resource);
            KieRepository kieRepository = getKieServices().getRepository();
            KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem).buildAll();
            results = kieBuilder.getResults();
            message = "file is not corrupt";
            if (results.hasMessages(Message.Level.ERROR)) {
                message = "Temp file contains error File Reloading Issues";
                tempFileCorrupted = true;
            }
        } catch (Exception e) {
            message = "File does not exist or Corrupted";
            tempFileCorrupted = true;
        } if(reload == 1 ){
        }
        if (tempFileCorrupted) {
            try {
                KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
                resource = ResourceFactory.newFileResource("/resources/DroolFile.xlsx");
                kieFileSystem.write(resource);
                KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem).buildAll();
                results = kieBuilder.getResults();
                if (results.hasMessages(Message.Level.ERROR)) {
                    message = " main File Reloading Issues" + results.getMessages();
                    mainFileCorrupted = true;
                }
            } catch (Exception e) {
                message = " File is corrupted and main File also does not exist or Corrupted";
                mainFileCorrupted = true;
            }
        }
        if (!mainFileCorrupted) {
            KieUtils.setKieContainer(kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId()));
            DecisionTableProviderImpl decisionTableProvider
                    = new DecisionTableProviderImpl();
            String url = decisionTableProvider.loadFromResource(resource, null);
            message = "Reloading Succyessful::: tempFile corrupted::" + tempFileCorrupted;
        }
        return message;
    }

    private KieServices getKieServices() {
        return KieServices.Factory.get();
    }


    public boolean validatedDrool() {
        File file = new File("/resources/DroolFile.xlsx");
        try {
            FileInputStream inputStream = new FileInputStream(file);
            Workbook baeuldungWorkBook = new XSSFWorkbook(inputStream);
            Sheet sheet = baeuldungWorkBook.getSheetAt(0);
            int firstRow = sheet.getFirstRowNum();
            int lastRow = sheet.getLastRowNum();
            for (int index = firstRow + 1; index <= lastRow; index++) {
                Row row = sheet.getRow(index);
                int cellCount = row.getPhysicalNumberOfCells();
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
