package utility.gson_adapter;

import com.google.gson.*;
import model.Epic;
import model.Subtask;
import model.Task;

import java.lang.reflect.Type;

public class JsonAdapter implements JsonDeserializer<Task> {
    @Override
    public Task deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String taskType = jsonObject.get("type").getAsString();
        return switch (taskType) {
            case "Epic" -> context.deserialize(jsonElement, Epic.class);
            case "Subtask" -> context.deserialize(jsonElement, Subtask.class);
            default -> context.deserialize(jsonElement, Task.class);
        };
    }
}
