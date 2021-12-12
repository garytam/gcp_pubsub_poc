package com.gtc.gcp_pubsub_poc.pubsub;

import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PubsubMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class MessageListener {

    @Value("${projectId}")
    private String projectId;

    @Value("${subscriptionId}")
    private String subscriptionId;

    @Value("${maxErrorCount}")
    private int maxErrorCount;

    @Async
    public void subscribeAsync() {
        ProjectSubscriptionName subscriptionName =
                ProjectSubscriptionName.of(projectId, subscriptionId);

        // Instantiate an asynchronous message receiver.
        MessageReceiver receiver =
                (PubsubMessage message, AckReplyConsumer consumer) -> {
                    // Handle incoming message, then ack the received message.
                    System.out.printf("Id: " + message.getMessageId());
                    System.out.printf("Data: " + message.getData().toStringUtf8() + "\n");
                    Map mapData = message.getAttributesMap();
                    mapData.keySet().forEach( key -> {
                        System.out.printf("[ key =" + key + " value=" + mapData.get(key) + "]\n");
                    });
                    consumer.ack();
                };

        int errorCount = 0;

        Subscriber subscriber = null;
        while( errorCount < maxErrorCount){
            try {
                subscriber = Subscriber.newBuilder(subscriptionName, receiver).build();
                // Start the subscriber.
                subscriber.startAsync().awaitRunning();
                System.out.printf("Listening for messages on %s:\n", subscriptionName.toString());
                // Allow the subscriber to run for 30s unless an unrecoverable error occurs.
                subscriber.awaitTerminated(30, TimeUnit.MINUTES);
            } catch (TimeoutException timeoutException) {
                // Shut down the subscriber after 30s. Stop receiving messages.
                subscriber.stopAsync();
                //restart the subscriber again
                System.out.printf("restart subscriber after 30 seconds");
                subscriber = Subscriber.newBuilder(subscriptionName, receiver).build();
                errorCount ++;
            }
        }

    }
}