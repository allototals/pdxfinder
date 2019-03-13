package org.pdxfinder.services.dto;

import java.util.List;

/*
 * Created by csaba on 13/03/2019.
 */
public class MolecularDataEntryDTO {

    private String sampleId;
    private String sampleType;
    private String engraftedTumorPassage;
    private String dataAvailableLabel;
    private String dataAvailableUrl;
    private String platformUsedLabel;
    private String platformUsedUrl;
    private String rawDataLabel;
    private String rawDataUrl;

    private String histology;

    public MolecularDataEntryDTO(String sampleId, String sampleType, String engraftedTumorPassage, String dataAvailableLabel,
                                 String dataAvailableUrl, String platformUsedLabel, String platformUsedUrl, String rawDataLabel,
                                 String rawDataUrl) {

        this.sampleId = sampleId;
        this.sampleType = sampleType;
        this.engraftedTumorPassage = engraftedTumorPassage;
        this.dataAvailableLabel = dataAvailableLabel;
        this.dataAvailableUrl = dataAvailableUrl;
        this.platformUsedLabel = platformUsedLabel;
        this.platformUsedUrl = platformUsedUrl;
        this.rawDataLabel = rawDataLabel;
        this.rawDataUrl = rawDataUrl;
    }

    public MolecularDataEntryDTO() {
    }

    public String getSampleId() {
        return sampleId;
    }

    public void setSampleId(String sampleId) {
        this.sampleId = sampleId;
    }

    public String getSampleType() {
        return sampleType;
    }

    public void setSampleType(String sampleType) {
        this.sampleType = sampleType;
    }

    public String getEngraftedTumorPassage() {
        return engraftedTumorPassage;
    }

    public void setEngraftedTumorPassage(String engraftedTumorPassage) {
        this.engraftedTumorPassage = engraftedTumorPassage;
    }

    public String getDataAvailableLabel() {
        return dataAvailableLabel;
    }

    public void setDataAvailableLabel(String dataAvailableLabel) {
        this.dataAvailableLabel = dataAvailableLabel;
    }

    public String getDataAvailableUrl() {
        return dataAvailableUrl;
    }

    public void setDataAvailableUrl(String dataAvailableUrl) {
        this.dataAvailableUrl = dataAvailableUrl;
    }

    public String getPlatformUsedLabel() {
        return platformUsedLabel;
    }

    public void setPlatformUsedLabel(String platformUsedLabel) {
        this.platformUsedLabel = platformUsedLabel;
    }

    public String getPlatformUsedUrl() {
        return platformUsedUrl;
    }

    public void setPlatformUsedUrl(String platformUsedUrl) {
        this.platformUsedUrl = platformUsedUrl;
    }

    public String getRawDataLabel() {
        return rawDataLabel;
    }

    public void setRawDataLabel(String rawDataLabel) {
        this.rawDataLabel = rawDataLabel;
    }

    public String getRawDataUrl() {
        return rawDataUrl;
    }

    public void setRawDataUrl(String rawDataUrl) {
        this.rawDataUrl = rawDataUrl;
    }

    public String getHistology() {
        return histology;
    }

    public void setHistology(String histology) {
        this.histology = histology;
    }

}