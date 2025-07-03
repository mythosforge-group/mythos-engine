package mythosengine.core.template;

import java.util.List;

import lombok.Data;

@Data
public class RpgTemplate {
    private String system;
    private String generationType;
    private List<String> rules;
    private String prompt;
}