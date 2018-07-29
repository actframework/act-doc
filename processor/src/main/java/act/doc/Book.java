package act.doc;

import act.app.conf.AutoConfig;
import act.util.SimpleBean;
import org.osgl.util.C;
import org.osgl.util.IO;
import org.osgl.util.S;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@AutoConfig
public class Book implements SimpleBean {

    public File base;
    private Processor processor;
    private String lang;

    public Book(String lang, Processor processor) {
        this.base = processor.base(lang);
        this.processor = processor;
        this.lang = lang;
    }

    public void process() {
        List<String> lines = new ArrayList<>();
        for (String chapter : processor.chapters()) {
            File src = new File(base, chapter);
            List<String> fileLines = IO.readLines(src);
            C.List<String> processedFileLines = C.newList();
            for (String line : fileLines) {
                if ("[返回目录](index.md)".equals(line.trim())) {
                    continue;
                }
                line = processor.processTag(line);
                processedFileLines.add(line);
            }
            if (!lines.isEmpty()) {
                processedFileLines.add("\\newpage");
            }
            lines.addAll(processedFileLines);
        }
        File target = new File(processor.workspace(), "act_doc-" + lang + ".md");
        IO.write(S.join(lines).by("\n").get()).to(target);
    }

}
