/*
 * C O P Y R I G H T   N O T I C E
 * -----------------------------------------------------------------------
 * Copyright (c) 2011-2012 InfoClinika, Inc. 5901 152nd Ave SE, Bellevue, WA 98006,
 * United States of America.  (425) 442-8058.  http://www.infoclinika.com.
 * All Rights Reserved.  Reproduction, adaptation, or translation without prior written permission of InfoClinika, Inc. is prohibited.
 * Unpublished--rights reserved under the copyright laws of the United States.  RESTRICTED RIGHTS LEGEND Use, duplication or disclosure by the
 */
package com.infoclinika.mssharing.model;

import com.infoclinika.mssharing.model.helper.ColumnViewHelper;
import com.infoclinika.mssharing.model.read.DetailsReader;
import com.infoclinika.mssharing.platform.model.common.items.FileExtensionItem;

import java.util.List;
import java.util.Set;

/**
 * Predefines application start data. Now expose admins ids.
 * But probably should allow to create admins.
 *
 * @author Stanislav Kurilin
 */
public interface PredefinedDataCreator {
    long admin(String firstName, String lastName, String email, String passwordHash);

    long instrumentModel(String vendor, String type, String studyType, String name, boolean isFolderArchiveSupport, boolean multipleFiles, Set<FileExtensionItem> extensions);

    void experimentType(String name, boolean allowed2DLC, boolean allowLabels);

    void species(String... name);

    long proteinDatabase(long actor, String dbName, String contentUrl, String specie);

    long cdfDatabase(long user, String dbName, String contentUrl, String specie);

    List<Long> createColumnsDefinitions(List<ColumnViewHelper.Column> info, ColumnViewHelper.ColumnViewType type);

    long defaultColumnsView(Set<ColumnViewHelper.ColumnInfo> view, ColumnViewHelper.ColumnViewType type);

    void allUsersGroup();

    long createExperimentLabelType(String name, int maxSamples);

    long createExperimentLabel(long experimentLabelType, String aminoAcid, String name);
    
    void billingProperties();

}
