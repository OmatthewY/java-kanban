package manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import adapters.LocalDateTimeAdapter;
import http.HttpTaskManager;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
public class Managers {
    public static TaskManager getDefault() throws IOException, InterruptedException {
        return new HttpTaskManager(URI.create("http://localhost:6000"));
    }
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        return gsonBuilder.setPrettyPrinting()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }
}