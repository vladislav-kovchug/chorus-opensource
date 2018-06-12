package com.infoclinika.mssharing.model.test.processing;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.infoclinika.mssharing.model.helper.AbstractTest;
import com.infoclinika.mssharing.model.internal.read.ProcessingRunReader;
import com.infoclinika.mssharing.model.internal.read.ProcessingFileReader;
import com.infoclinika.mssharing.model.read.ExtendedShortExperimentFileItem;
import com.infoclinika.mssharing.model.read.dto.details.ExperimentItem;
import com.infoclinika.mssharing.model.read.dto.details.FileItem;
import com.infoclinika.mssharing.model.write.ProcessingFileManagement;
import com.infoclinika.mssharing.model.write.ProcessingRunManagement;
import com.infoclinika.mssharing.model.write.ProjectInfo;
import com.infoclinika.mssharing.platform.entity.restorable.FileMetaDataTemplate;
import com.infoclinika.mssharing.platform.model.read.DetailsReaderTemplate.*;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

import static com.infoclinika.mssharing.model.helper.Data.PROJECT_TITLE;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class AbstractProcessingTest extends AbstractTest{


    @Inject
    protected ProcessingFileReader processingFileReader;
    @Inject
    protected ProcessingFileManagement processingFileManagement;
    @Inject
    protected ProcessingRunManagement processingRunManagement;
    @Inject
    protected ProcessingRunReader processingRunReader;



    protected long createProcessingFile(long experiment, FileItem fileItem){
        ProcessingFileManagement.ProcessingFileShortInfo processingFileShortInfo = new ProcessingFileManagement.ProcessingFileShortInfo(fileItem.name, "processed-file/" + experiment + "/" + fileItem.name);
        return processingFileManagement.createProcessingFile(experiment, processingFileShortInfo);
    }


    protected Map<String, Collection<String>> createFileToFileMap(FileItem fileItem, long processingFileId){
        ProcessingFileReader.ProcessingFileInfo processingFileInfo = processingFileReader.readProcessingFileInfo(processingFileId);

        Map<String, Collection<String>> map = new HashMap<>();
        List<String> collection = new ArrayList<>();
        collection.add(fileItem.name);
        map.put(processingFileInfo.name, collection);

        return map;
    }

    protected void assertProcessingFilesIsAssociateExperimentFile(long processingFileId, long fileId){

        ProcessingFileReader.ProcessingFileInfo processingFileInfo = processingFileReader.readProcessingFileInfo(processingFileId);
        assertTrue(Iterables.any(processingFileInfo.fileMetaDataTemplateList, new Predicate<FileMetaDataTemplate>() {
            @Override
            public boolean apply(FileMetaDataTemplate input) {
                return input.getId() == fileId;
            }
        }));
        assertTrue(processingFileInfo.processingRuns.size() > 0);
        assertTrue(processingFileInfo.fileMetaDataTemplateList.size() == 1);
    }


    protected void assertMultipartProcessingFilesIsAssociateExperimentFile(long processingFileId, long fileId, long experiment){

        ProcessingFileReader.ProcessingFileInfo processingFileInfo = processingFileReader.readProcessingFileInfo(processingFileId);
        assertTrue(Iterables.any(processingFileInfo.fileMetaDataTemplateList, new Predicate<FileMetaDataTemplate>() {
            @Override
            public boolean apply(FileMetaDataTemplate input) {
                return input.getId() == fileId;
            }
        }));

        assertTrue(processingFileInfo.processingRuns.size() > 0);
        assertEquals(Optional.ofNullable(experiment).get(), processingFileInfo.abstractExperiment.getId());
    }


    protected List<Long> createMultiProcessingFiles(ExperimentItem experimentItem){

        List<Long> list = new ArrayList<>();
        for(int i = 0; i < experimentItem.files.size(); i++){
            String file = "file-test"+UUID.randomUUID().toString()+".RAW";
            ProcessingFileManagement.ProcessingFileShortInfo processingFileShortInfo = new ProcessingFileManagement.ProcessingFileShortInfo(file, "processed-file/" + experimentItem.id +"/"+file);
            long id = processingFileManagement.createProcessingFile(experimentItem.id, processingFileShortInfo);
            list.add(id);
        }
        return list;
    }

    protected Map<String, Collection<String>> createFileToFileMap(ExperimentItem experimentItem, List<Long> list){
        Map<String, Collection<String>> map = new HashMap<>();
        List<String> collection = new ArrayList<>();
        for(int i = 0; i<list.size(); i++){
            ProcessingFileReader.ProcessingFileInfo processingFileInfo = processingFileReader.readProcessingFileInfo(list.get(i));
            collection.add(experimentItem.files.get(i).name);
            map.put(processingFileInfo.name, collection);
        }

        return map;
    }

    public Map<String, Collection<String>> createSamplesToFileMap(ExperimentShortInfo shortInfo, Map<String, Collection<String>> fileToFileMap){

        Map<String, Collection<String>> sampleFilesMap = new HashMap();

        for(Map.Entry<String, Collection<String>> entry : fileToFileMap.entrySet()){
            String key = entry.getKey(); // processed file
            Collection<String> values = entry.getValue(); // experiment files
            Collection<String> processedFiles = new ArrayList<>();

            for(String experimentFiles: values){

                ImmutableList<ExtendedShortExperimentFileItem> files = (ImmutableList<ExtendedShortExperimentFileItem>) shortInfo.files;

                for(ExtendedShortExperimentFileItem fileItem : files){
                    if(fileItem.name == experimentFiles){

                        ImmutableList<ExtendedShortExperimentFileItem.ExperimentShortSampleItem> sampleItems = fileItem.samples;
                        ExtendedShortExperimentFileItem.ExperimentShortSampleItem sampleItem = sampleItems.get(0);

                        if(!sampleFilesMap.containsKey(sampleItem.name)){

                            if(!processedFiles.contains(key)){
                                processedFiles.add(key);
                                sampleFilesMap.put(sampleItem.name, processedFiles);
                            }
                        }
                    }
                }
            }
        }

        return sampleFilesMap;
    }


    protected long createExperimentWithOneRawFile(long user, long lab) {
        final long project = studyManagement.createProject(user, new ProjectInfo(PROJECT_TITLE, "DNA", "Some proj", lab));
        return createInstrumentAndExperimentWithOneFile(user, lab, project);
    }

}
