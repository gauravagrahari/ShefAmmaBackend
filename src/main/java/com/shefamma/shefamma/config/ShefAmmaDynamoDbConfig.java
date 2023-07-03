package com.shefamma.shefamma.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ShefAmmaDynamoDbConfig {

    @Bean
    public AmazonDynamoDB amazonDynamoDB() {
        return AmazonDynamoDBClientBuilder
                .standard()
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(
                                "dynamodb.ap-southeast-2.amazonaws.com",
                                "ap-southeast-2"
                        )
                )
                .withCredentials(
                        new AWSStaticCredentialsProvider(
                                new BasicAWSCredentials(
                                        "AKIATDGNA7XLUHRU2K4O",
                                        "5FKad7I/6hCmBreMZ/SCZqyL1zJTWu2rJkdaby3T"
                                )
                        )
                )
                .build();
    }

    @Bean
    public DynamoDBMapper dynamoDBMapper() {
        return new DynamoDBMapper(amazonDynamoDB());
    }
}
//   "AKIATDGNA7XLUHRU2K4O",
//           "5FKad7I/6hCmBreMZ/SCZqyL1zJTWu2rJkdaby3