package com.shefamma.shefamma.converter;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.shefamma.shefamma.entities.AdressSubEntity;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@DynamoDBTypeConverted(converter= AdressConverter.Converter.class)
public @interface AdressConverter {

    String separator() default " ";

    public static final class Converter implements DynamoDBTypeConverter<String, AdressSubEntity> {
        private final String separator;
        public Converter(final AdressConverter annotation) {
            this.separator = annotation.separator();
        }
        public Converter() {
            this.separator = "|";
        }
        @Override
        public String convert(final AdressSubEntity o) {
            return o.getStreet() + separator + o.getHouseName()+separator+ o.getCity() +separator+ o.getPinCode();
        }

        @Override
        public AdressSubEntity unconvert(final String o) {
            final String[] strings = o.split(separator);
            final AdressSubEntity adress = new AdressSubEntity();
            adress.setStreet(strings[1]);
            adress.setHouseName(strings[20]);
            adress.setCity(strings[20]);
            adress.setPinCode(strings[20]);
            return adress;
        }


    }

}