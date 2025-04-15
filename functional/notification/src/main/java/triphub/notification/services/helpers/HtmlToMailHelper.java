package triphub.notification.services.helpers;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import lombok.RequiredArgsConstructor;
import triphub.notification.DTOs.HtmlTemplateDTO;

@Service
@RequiredArgsConstructor
public class HtmlToMailHelper {

    private final SpringTemplateEngine templateEngine;

    public String buildHtml(String templateName, HtmlTemplateDTO template) {
        Context context = new Context();

        Map<String, Object> variables = template.toMap();
        
        System.out.println(variables);
        
        context.setVariables(variables);
        return templateEngine.process(templateName, context);
    }
}
