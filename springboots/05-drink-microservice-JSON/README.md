# RestApi Springboot drink microservice example

This is a WebMVC springboot restApi microservice example, it is a show case for Jackson

- @JsonProperty
- @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ssZ", shape=JsonFormat.Shape.STRING)
- @JsonSerialize(using = LocalDateSerializer.class)
```java
public class LocalDateSerializer extends JsonSerializer<LocalDate> {

    @Override
    public void serialize(LocalDate value, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {
        jsonGenerator.writeObject(value.format(DateTimeFormatter.BASIC_ISO_DATE));
    }
}
```

- @JsonDeserialize(using = LocalDateDeserializer.class)
```java
public class LocalDateDeserializer extends StdDeserializer<LocalDate> {

    public LocalDateDeserializer() {
        super(LocalDate.class);
    }

    @Override
    public LocalDate deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return LocalDate.parse(jsonParser.readValueAs(String.class), DateTimeFormatter.BASIC_ISO_DATE);
    }
}
```

- PropertyNamingStrategy: e.g. LOWER_CAMEL_CASE(default), SNAKE_CASE, KEBAB_CASE, see test dto's as an example


