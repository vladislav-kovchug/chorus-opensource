package com.infoclinika.mssharing.model.test.processing;

import com.google.common.collect.ImmutableSet;
import com.infoclinika.mssharing.model.helper.ExperimentSampleItem;
import com.infoclinika.mssharing.model.internal.read.ProcessingFileReader;
import com.infoclinika.mssharing.model.read.dto.details.ExperimentItem;
import com.infoclinika.mssharing.model.read.dto.details.FileItem;
import com.infoclinika.mssharing.platform.model.write.ExperimentManagementTemplate;
import org.testng.annotations.Test;
import org.testng.log4testng.Logger;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.ImmutableList.of;
import static org.testng.Assert.assertEquals;

public class ManagingProcessingFileTest extends AbstractProcessingTest{

    public static final Logger LOGGER = Logger.getLogger(ManagingProcessingFileTest.class);

    @Test
    public void checkProcessingFileIsUpload(){
        final long userId = uc.createLab3AndBob();
        final long experiment = createExperimentWithOneRawFile(userId, uc.getLab3());

        final FileItem file = (FileItem)detailsReader.readExperiment(userId, experiment).files.iterator().next();

        final long processingFileId = createProcessingFile(experiment, file);
        ProcessingFileReader.ProcessingFileInfo processingFileInfo = processingFileReader.readProcessingFileInfo(processingFileId);
        final long experimentId = processingFileInfo.abstractExperiment.getId();

        assertEquals(experiment, experimentId);
        assertEquals(file.name, processingFileInfo.name);
    }

    @Test
    public void associateProcessingFileWithExperimentFile(){

        final long userId = uc.createLab3AndBob();
        final long experiment = createExperimentWithOneRawFile(userId, uc.getLab3());

        final FileItem file = (FileItem)detailsReader.readExperiment(userId, experiment).files.iterator().next();
        final long processingFileId = createProcessingFile(experiment, file);

        Map<String, Collection<String>> fileToFileMap = createFileToFileMap(file, processingFileId);
        processingFileManagement.associateProcessingFileWithRawFile(fileToFileMap, new HashMap<>(),experiment, userId, "ProcessingRunTest");

        assertProcessingFilesIsAssociateExperimentFile(processingFileId, file.id);
    }


    @Test
    public void associateMultipartProcessingFilesWithExperimentFiles(){
        final long user = uc.createLab3AndBob();
        final long experiment = createExperimentWithOneRawFile(user, uc.getLab3());

        final long instrument = instrumentFromExperimentFile(user, experiment);
        final long file1 = uc.saveFile(user, instrument);
        final long file2 = uc.saveFile(user, instrument);

        final ExperimentSampleItem sample1 = sampleWithFactors(file1, of("1"));
        final ExperimentSampleItem sample2 = sampleWithFactors(file2, of("2"));
        addFilesToExperiment(user, experiment, of(factor(experiment)), of(
                new com.infoclinika.mssharing.model.write.FileItem(file1, false, 0, preparedSample(file1, ImmutableSet.of(sample1))),
                new com.infoclinika.mssharing.model.write.FileItem(file2, false, 0, preparedSample(file2, ImmutableSet.of(sample2)))), of("3"));

        final ExperimentItem experimentItem = detailsReader.readExperiment(user, experiment);
        List<Long> processingFilesList = createMultiProcessingFiles(experimentItem);
        Map<String, Collection<String>> map = createFileToFileMap(experimentItem, processingFilesList);

        processingFileManagement.associateProcessingFileWithRawFile(map, new HashMap<>(),experiment, user, "ProcessingRunTests");

        for(int i = 0; i < processingFilesList.size(); i++){
            assertMultipartProcessingFilesIsAssociateExperimentFile(processingFilesList.get(i), experimentItem.files.get(i).id, experimentItem.id);
        }
    }


    private ExperimentManagementTemplate.MetaFactorTemplate factor(long experimentId) {
        return new ExperimentManagementTemplate.MetaFactorTemplate(generateString(), "", false, experimentId);
    }





}
