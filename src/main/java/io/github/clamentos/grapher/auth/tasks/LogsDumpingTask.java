package io.github.clamentos.grapher.auth.tasks;

///
import io.github.clamentos.grapher.auth.persistence.entities.Log;

///..
import io.github.clamentos.grapher.auth.persistence.repositories.LogRepository;

///.
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

///.
import java.time.ZonedDateTime;

///..
import java.time.format.DateTimeFormatter;

///.
import java.util.ArrayList;
import java.util.List;

///..
import java.util.regex.Pattern;

///..
import java.util.stream.Stream;

///.
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

///.
import jakarta.transaction.Transactional;

///
public final class LogsDumpingTask implements Runnable {

    ///
    private final Logger logger;
    private final LogRepository repository;

    ///..
    private final Pattern linePattern;
    private final Pattern segmentPattern;

    ///
    public LogsDumpingTask(LogRepository repository) {

        logger = LogManager.getLogger(this.getClass());
        this.repository = repository;

        linePattern = Pattern.compile("]\n");
        segmentPattern = Pattern.compile(" ]");
    }

    ///
    @Override @Transactional
    public void run() {

        String directoryPath = "./logs/archived";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss.SSS XXX");
        Path folder = Paths.get(directoryPath);

        if(Files.exists(folder)) {

            try(Stream<Path> paths = Files.list(folder)) {

                paths.forEach(logFile -> process(formatter, logFile));
            }

            catch(Exception exc) {

                logger.error("Could not walk the directory: {}, because: {}", directoryPath, exc.getMessage());
            }
        }
    }

    ///.
    private void process(DateTimeFormatter formatter, Path logFile) {

        List<Log> logs = new ArrayList<>();

        try {

            String[] logLines = linePattern.split(Files.readString(logFile));

            for(String logLine : logLines) {

                String[] segments = segmentPattern.split(logLine);

                logs.add(new Log(

                    0,
                    ZonedDateTime.parse(clean(segments[0]), formatter),
                    clean(segments[1]),
                    clean(segments[2]),
                    clean(segments[3]),
                    clean(segments[4])
                ));
            }

            repository.saveAll(logs);
            Files.delete(logFile);
        }

        catch(Exception exc) {

            logger.warn("Could not process the log file: {}, because: {}", logFile.toString(), exc.getMessage());
        }
    }

    ///..
    private String clean(String value) {

        int endIdx = value.length() - 1;

        for(int i = endIdx; i >= 2; i--) {

            if(Character.isWhitespace(value.charAt(i))) endIdx--;
            else break;
        }

        return(value.substring(2, endIdx + 1));
    }

    ///
}
