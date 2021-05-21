package org.acme.ugc.ugc;

import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.NoArgsConstructor;

@NoArgsConstructor
@ApplicationScoped
public class UgcImageTaggingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UgcImageTaggingService.class);

    public UgcTag ProcessImage(UgcTask ugcTask) {
        LOGGER.info("Processing ugc id: {}", ugcTask.getUgcId().toString());
        UgcTag result = UgcTag.builder().tag("safe").confidence(0.98).build();
        return result;
    }

    public String ProcessImageSingleTag(String ugcId) {
        LOGGER.info("Processing simple ugc id: {}", ugcId);
        return "safe";
    }
}
