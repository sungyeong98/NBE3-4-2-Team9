package com.backend.global.scheduler.timeconverter;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * 클래스 작성 목적 :
 * 사람인 open api의 응답 데이터 날짜타입 = Timestamp
 * jobPosting 엔티티의 (postDate, openDate, closeDate)필드 날짜 타입 = ZonedDateTime
 * 결론 : TimeStamp -> ZonedDateTime 으로 바꿔야하기 위해 생성.
 *
 * Unix 타임스탬프(Long)를 ZonedDateTime 객체로 변환하는 클래스입니다.
 */
public class UnixTimestampDeserializer extends JsonDeserializer<ZonedDateTime> {


    /**
     * dto 클래스인 Job에 응답 필드인 postDate, openDate, closeDate의 타입을 timeStamp -> ZonedDateTime 으로 변환하기 위한 메소드.
     *
     * @param p JSON 콘텐츠에서 타임스탬프를 읽는데 사용되는 JsonParser 객체.
     * @param ctxt 현재 역직렬화 작업에 대한 정보를 접근할 수 있는 DeserializationContext 객체.
     * @return 변환된 ZonedDateTime 객체 (Asia/Seoul 시간대 기준).
     * @throws IOException JSON 콘텐츠를 읽는 중에 발생할 수 있는 예외.
     * @throws JacksonException 역직렬화 중 발생할 수 있는 예외.
     */
    @Override
    public ZonedDateTime deserialize(JsonParser p, DeserializationContext ctxt)
        throws IOException, JacksonException {

        /**
         * JSON에서 읽은 텍스트를 Long 타입으로 변환하여 타임스탬프를 얻음.
         * 응답 JSON 데이터가 String일수도 OR Integer일수도 있음, 응답 JSON 데이터를 봐야함 (현재는 String으로 받음)
         * long timestamp = Long.parseLong(p.getText()); ==> JSON에서 문자열(String)으로 온 경우.
         * long timestamp = p.getLongValue(); ==> JSON에서 숫자(Integer)로 온 경우.
         */
        long timestamp = Long.parseLong(p.getText());

        /**
         * 타임스탬프를 Instant 객체로 변환하고, 이를 Asia/Seoul 시간대로 ZonedDateTime으로 변환.
         */
        return Instant.ofEpochSecond(timestamp)
            .atZone(ZoneId.of("Asia/Seoul"));
    }

}
