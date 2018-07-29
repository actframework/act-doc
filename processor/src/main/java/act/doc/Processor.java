package act.doc;

import act.Act;
import act.cli.Command;
import act.cli.Required;
import act.inject.DefaultValue;
import act.inject.util.LoadResource;
import act.job.OnAppStart;
import org.osgl.exception.UnexpectedException;
import org.osgl.inject.annotation.Configuration;

import java.io.File;
import java.util.List;
import javax.inject.Inject;

@SuppressWarnings("unused")
public class Processor {

    @Configuration("doc.base")
    private String base;

    @Configuration("workspace")
    private String workspace;

    @LoadResource("chapters.list")
    private List<String> chapters;

    @Inject
    private Tags tags;

    public File base(String lang) {
        return new File(base, lang);
    }

    public File workspace() {
        return new File(workspace);
    }

    public List<String> chapters() {
        return chapters;
    }

    public String processTag(String line) {
        return tags.substitude(line);
    }

    @OnAppStart
    public void ensureWorkspace() {
        File file = workspace();
        if (!file.exists()) {
            if (!file.mkdir()) {
                throw new UnexpectedException("Workspace not ready for use: " + file.getAbsolutePath());
            }
        }
    }

    @Command(name = "process", help = "process a file")
    public void process(@Required("specify language suffix") @DefaultValue("cn") String lang) {
        new Book(lang, this).process();
    }

    public static void main(String[] args) throws Exception {
        Act.start();
    }

}
