package httptaskserver.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public void write(final JsonWriter jw, final LocalDateTime dateTime) throws IOException {
        if (dateTime == null) {
            jw.value(LocalDateTime.now().format(dtf));
        } else {
            jw.value(dateTime.format(dtf));
        }
    }

    @Override
    public LocalDateTime read(final JsonReader jr) throws IOException {
        return LocalDateTime.parse(jr.nextString(), dtf);
    }
}
