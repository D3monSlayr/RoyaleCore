package dev.royalcore.api.consumer;

import dev.royalcore.Main;
import dev.royalcore.api.template.Template;
import lombok.Getter;
import net.kyori.adventure.text.Component;

import java.rmi.AlreadyBoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TemplateConsumer {

    @Getter
    private final List<Template> templates = new ArrayList<>();

    public void useTemplates(Template... templates) {

        for (Template template : templates) {
            if(this.templates.contains(template)) {
                Main.getPlugin().getComponentLogger().error(Component.text("A template is already included in the registry!"), new AlreadyBoundException());
            } else {
                this.templates.add(template);
            }
        }
    }

}
