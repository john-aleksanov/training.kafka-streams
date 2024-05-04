package dev.marvel.kafkastreams.task2;

import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

import java.io.Serializable;

@Plugin(name = "TestAppender", category = "Core", elementType = "appender", printObject = true)
public class CountingAppender extends AbstractAppender {

    private int totalWordCount = 0;
    private int wordWithAWordCount = 0;

    protected CountingAppender(String name, Layout<? extends Serializable> layout) {
        super(name, null, layout, false, null);
    }

    @PluginFactory
    public static CountingAppender createAppender(@PluginAttribute("name") String name,
        @PluginElement("Layout") Layout<? extends Serializable> layout) {
        return new CountingAppender(name, layout);
    }

    @Override
    public void append(LogEvent event) {
        var formattedMessage = event.getMessage().getFormattedMessage();
        if (formattedMessage.startsWith("Yay")) {
            wordWithAWordCount++;
        } else if (formattedMessage.startsWith("Here's an intermediate result")) {
            totalWordCount++;
        }
    }

    public int getTotalWordCount() {
        return totalWordCount;
    }

    public int getWordWithAWordCount() {
        return wordWithAWordCount;
    }
}
