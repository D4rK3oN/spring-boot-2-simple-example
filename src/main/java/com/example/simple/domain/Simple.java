package com.example.simple.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.querydsl.core.annotations.QueryEntity;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder(toBuilder = true)
@QueryEntity
@Document(collection = "#{@collections.getSimpleObjects()}")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Simple {

    @Id
    @JsonIgnore
    private String id;

    @Indexed(unique = true, direction = IndexDirection.ASCENDING)
    @JsonProperty("id")
    private String simpleId;

    private String name;

    private Integer age;

    @JsonIgnore
    public boolean isEmpty() {
        return (id == null || id.isEmpty()) && (simpleId == null || simpleId.isEmpty()) && (name == null || name.isEmpty());
    }
}
