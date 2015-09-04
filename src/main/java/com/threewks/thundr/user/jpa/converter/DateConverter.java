package com.threewks.thundr.user.jpa.converter;

import org.joda.time.DateTime;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Timestamp;

@Converter
public class DateConverter implements AttributeConverter<DateTime, Timestamp> {

    @Override
    public Timestamp convertToDatabaseColumn(DateTime attribute) {
        return attribute == null ? null : new Timestamp(attribute.getMillis());
    }

    @Override
    public DateTime convertToEntityAttribute(Timestamp dbData) {
        return dbData == null ? null : new DateTime(dbData.getTime());
    }
}
