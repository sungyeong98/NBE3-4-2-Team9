package com.backend.global.timeconverter;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class UnixTimestampDeserializer extends JsonDeserializer<ZonedDateTime> {

    @Override
    public ZonedDateTime deserialize(JsonParser p, DeserializationContext ctxt)
        throws IOException, JacksonException {
        long timestamp = p.getLongValue();
        return Instant.ofEpochSecond(timestamp)
            .atZone(ZoneId.of("Asia/Seoul"));
    }

}
