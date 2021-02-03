@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.Type)
public @interface JsonSerializable {
}



@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface JsonElement {
    public String key() default "";
}


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Init {
}


@JsonSerializable
public class Person {

    @JsonElement
    private String firstName;

    @JsonElement
    private String lastName;

    @JsonElement(key = "personAge")
    private String age;

    private String address;

    @Init
    private void initNames() {
        this.firstName = this.firstName.substring(0, 1).toUpperCase() 
          + this.firstName.substring(1);
        this.lastName = this.lastName.substring(0, 1).toUpperCase() 
          + this.lastName.substring(1);
    }

    // Standard getters and setters
}


private void checkIfSerializable(Object object) {
    if (Objects.isNull(object)) {
        throw new JsonSerializationException("The object to serialize is null");
    }
        
    Class<?> clazz = object.getClass();
    if (!clazz.isAnnotationPresent(JsonSerializable.class)) {
        throw new JsonSerializationException("The class " 
          + clazz.getSimpleName() 
          + " is not annotated with JsonSerializable");
    }
}


private void initializeObject(Object object) throws Exception {
    Class<?> clazz = object.getClass();
    for (Method method : clazz.getDeclaredMethods()) {
        if (method.isAnnotationPresent(Init.class)) {
            method.setAccessible(true);
            method.invoke(object);
        }
    }
 }
 
 
private String getJsonString(Object object) throws Exception {	
    Class<?> clazz = object.getClass();
    Map<String, String> jsonElementsMap = new HashMap<>();
    for (Field field : clazz.getDeclaredFields()) {
        field.setAccessible(true);
        if (field.isAnnotationPresent(JsonElement.class)) {
            jsonElementsMap.put(getKey(field), (String) field.get(object));
        }
    }		
     
    String jsonString = jsonElementsMap.entrySet()
        .stream()
        .map(entry -> "\"" + entry.getKey() + "\":\"" 
          + entry.getValue() + "\"")
        .collect(Collectors.joining(","));
    return "{" + jsonString + "}";
}

public class ObjectToJsonConverter {
    public String convertToJson(Object object) throws JsonSerializationException {
        try {
            checkIfSerializable(object);
            initializeObject(object);
            return getJsonString(object);
        } catch (Exception e) {
            throw new JsonSerializationException(e.getMessage());
        }
    }
}


 @Test
    public void givenObjectSerializedThenTrueReturned() throws JsonSerializationException {
        Person person = new Person("soufiane", "cheouati", "34");
        ObjectToJsonConverter serializer = new ObjectToJsonConverter();
        String jsonString = serializer.convertToJson(person);
        assertEquals("{\"personAge\":\"34\",\"firstName\":\"Soufiane\",\"lastName\":\"Cheouati\"}", jsonString);
    }