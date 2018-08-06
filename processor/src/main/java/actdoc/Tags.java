package actdoc;

import act.inject.util.LoadResource;
import org.osgl.util.Keyword;
import org.osgl.util.S;

import java.util.Map;
import javax.inject.Singleton;

@Singleton
public class Tags {

    @LoadResource("tags")
    private Map<Keyword, String> mapping;

    public String substitude(String line) {
        String key = line;
        if (line.startsWith("<")) {
            key = S.ensureStrippedOff(line, S.ANGLE_BRACKETS);
        }
        String substitute = mapping.get(Keyword.of(key));
        return null == substitute ? line : substitute;
    }

}
