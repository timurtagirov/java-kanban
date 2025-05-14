package httptaskserver.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationAdapter extends TypeAdapter<Duration> {
    @Override
    public void write(final JsonWriter jw, final Duration duration) throws IOException {
        if (duration == null) {
            jw.value(Duration.ofMinutes(0).toString());
        } else {
            jw.value(duration.toString());
        }
    }

    @Override
    public Duration read(final JsonReader jr) throws IOException {
        return Duration.parse(jr.nextString());
    }
}
