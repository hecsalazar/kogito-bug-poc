package org.acme.ugc.ugc;

import java.lang.String;

import lombok.Data;

@Data
public class UgcTask {
    /**
     * UGC unique identifier
     */
    private String ugcId;
    /**
     * UGC type as mime format
     */
    private String ugcType;
    /**
     * Process requested for the UGC treatment
     */
    private String ugcProcessName;
    /**
     * Request status
     */
    private String status;
    /**
     * Content owner identifier
     */
    private String ownerId;
    /**
     * Content owner type
     */
    private String ownerType;
    /**
     * Context for the UGC.
     */
    private String context;
    /**
     * UGC URI
     */
    private String sourceUri;

}
