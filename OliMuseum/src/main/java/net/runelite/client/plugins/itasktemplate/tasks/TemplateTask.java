package net.runelite.client.plugins.olimuseum.tasks;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.olimuseum.Task;

@Slf4j
public class TemplateTask extends Task {

    @Override
    public boolean validate() {
        return true; //replace with your condition
    }

    @Override
    public String getTaskDescription() {
        return "Template task";
    }

    @Override
    public void run() {
        log.info("Running template task");
        game.tick();
    }
}