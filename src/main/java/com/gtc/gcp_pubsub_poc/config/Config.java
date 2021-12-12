package com.gtc.gcp_pubsub_poc.config;

import com.gtc.gcp_pubsub_poc.pubsub.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import javax.annotation.PostConstruct;

@Configuration
public class Config {
    @PostConstruct
    public void postConstruct() {
        System.out.println("Started after Spring boot application !");
    }

    @Autowired
    MessageListener messageListener;

    @Bean
    CommandLineRunner runner() {
        return args -> {
            System.out.println("CommandLineRunner running in the UnsplashApplication class...");
        };
    }

    @EventListener(ApplicationReadyEvent.class)
    public void doSomethingAfterStartup() {
        System.out.println("hello world, I have just started up");
        String projectId = "mypubsub-334701";
        String subscriptionId = "subscript_pub1_filter";
        subscriptionId = "subscript_pub1_filter";
        messageListener.subscribeAsync();
    }
}
