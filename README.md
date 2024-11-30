# Thread pool playground

This project is a playground for understanding better how thread pools works

This playground is aimed to investigate the **Rejection Policies**: built in rejection policies include:

* `AbortPolicy`: This policy throws a RejectedExecutionException when the queue is full. This usually indicates that the system is overwhelmed, leading to request failure.
* `DiscardPolicy`: This policy silently discards the rejected task. This is generally not ideal, as it can lead to lost tasks without any indication of failure.
* `DiscardOldestPolicy`: This policy discards the oldest task in the queue to make room for the new one. This might be useful in certain scenarios but could lead to unexpected behavior.
* `CallerRunsPolicy`: This policy has the submitting thread (usually the client’s thread) execute the task instead of adding it to the queue. This can provide a way to process tasks even when the queue is full, but it may affect performance on the client side. The caller-runs policy makes it easy to implement a simple form of throttling: that is, **a slow consumer can slow down a fast producer to control the task submission flow**.

See comments on the classes for details

## References

* [Processing more than 10 SQS messages concurrently with Spring Cloud AWS Messaging](https://medium.com/conductor-r-d/processing-more-than-10-sqs-messages-concurrently-with-spring-cloud-aws-messaging-5d09ebd94abd)
* [If a task queue reaches full capacity and a task is rejected, what happens to the client? Will the client see a failed request, or does the system try to handle it through a retry mechanism?](https://medium.com/@raksmeykoung_19675/if-a-task-queue-reaches-full-capacity-and-a-task-is-rejected-what-happens-to-the-client-3c67dbf16756)
* [Introduction to Thread Pools in Java](https://www.baeldung.com/thread-pool-java-and-guava)
* [Guide to `RejectedExecutionHandler`](https://www.baeldung.com/java-rejectedexecutionhandler)
