package io.github.clamentos.grapher.auth.logging;

///
import io.github.clamentos.grapher.auth.persistence.entities.Log;

///..
import io.github.clamentos.grapher.auth.persistence.repositories.LogRepository;

///.
import java.io.IOException;

///..
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

///..
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

///..
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

///..
import java.util.regex.Pattern;

///.
import lombok.extern.slf4j.Slf4j;

///.
import org.springframework.beans.factory.annotation.Autowired;

///..
import org.springframework.core.env.Environment;

///..
import org.springframework.dao.DataAccessException;

///..
import org.springframework.scheduling.annotation.Scheduled;

///..
import org.springframework.stereotype.Component;

///
/**
 * <h3>Database Logs Writer</h3>
 * Spring {@link Component} to periodically write the log files into the {@code LOG} database table.
*/

///
@Component
@Slf4j

///
public class DatabaseLogsWriter {

    ///
    private final LogRepository logRepository;
    private final String logsPath;

    ///..
    private final Pattern pattern;

    ///
    /** This class is a Spring bean and this constructor should never be called explicitly. */
    @Autowired
    public DatabaseLogsWriter(LogRepository logRepository, Environment environment) {

        this.logRepository = logRepository;
        logsPath = environment.getProperty("grapher-auth.logsPath", String.class);

        pattern = Pattern.compile("\\|");
    }

    ///
    @Scheduled(fixedRate = 60_000)
    private void dump() {

        try {

            List<Path> paths = Files

                .list(Paths.get(logsPath))
                .filter(path -> Files.isDirectory(path) == false)
                .toList()
            ;

            if(paths.size() > 1) {

                boolean[] halt = new boolean[]{false}; // Used as an "indirect" variable.

                paths.sort((first, second) -> {

                    try {

                        FileTime firstTime = Files.readAttributes(first, BasicFileAttributes.class).creationTime(); 
                        FileTime secondTime = Files.readAttributes(first, BasicFileAttributes.class).creationTime(); 

                        return(secondTime.compareTo(firstTime));
                    }

                    catch(IOException exc) {

                        log.error("Could not read file attributes, will abort the job. {}", exc);
                        halt[0] = true;

                        return(Integer.MAX_VALUE);
                    }
                });

                if(halt[0] == false) {

                    paths.remove(0);

                    for(Path path : paths) {

                        this.write(Files.readAllLines(path));
                        Files.delete(path);
                    }
                }
            }
        }

        catch(IOException | RuntimeException exc) {

            log.error("Could not process files, will abort the job. {}", exc);
        }
    }

    ///..
    private void write(Collection<String> lines) throws DataAccessException, NullPointerException, NumberFormatException {

        List<Log> logs = new ArrayList<>(lines.size());
        long now = System.currentTimeMillis();
        
        for(String line : lines) {

            String[] sections = pattern.split(line);
            logs.add(new Log(0, Long.parseLong(sections[0]), sections[1], sections[2], sections[3], sections[4], now));
        }

        logRepository.saveAll(logs);
    }

    ///
}
