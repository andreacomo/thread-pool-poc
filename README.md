# Thread pool playground

This project is a playground for understanding better how thread pools works

This playground is aimed to investigate the **Rejection Policies**: built in rejection policies include:

* `AbortPolicy`: This policy throws a RejectedExecutionException when the queue is full. This usually indicates that the system is overwhelmed, leading to request failure.
* `DiscardPolicy`: This policy silently discards the rejected task. This is generally not ideal, as it can lead to lost tasks without any indication of failure.
* `DiscardOldestPolicy`: This policy discards the oldest task in the queue to make room for the new one. This might be useful in certain scenarios but could lead to unexpected behavior.
* `CallerRunsPolicy`: This policy has the submitting thread (usually the client’s thread) execute the task instead of adding it to the queue. This can provide a way to process tasks even when the queue is full, but it may affect performance on the client side.

(source: https://medium.com/@raksmeykoung_19675/if-a-task-queue-reaches-full-capacity-and-a-task-is-rejected-what-happens-to-the-client-3c67dbf16756)