package com.elearning.sbf.jmssample.model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
//for text message (json, xml) we DO NOT need Serializable,
//but if we used a Java Object JMS exchange we
//DO NEED Serializable.
public class HelloWorldMessage implements Serializable {

    //if not Java will create one for us,
    //needed for serialization to keep track of serialized object see Serializable class.
    static final long serialVersionUID = -14472175156460933L;

    private UUID id;
    private String message;
}
