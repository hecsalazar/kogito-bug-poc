package org.acme.ugc.ugc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class UgcTag {
    private String tag;
    private double confidence;
}
