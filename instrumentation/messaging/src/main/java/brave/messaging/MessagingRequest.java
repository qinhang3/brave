/*
 * Copyright 2013-2019 The OpenZipkin Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package brave.messaging;

import brave.internal.Nullable;

/**
 * Abstract request type used for parsing and sampling of messaging producers and consumers.
 *
 * @see ProducerRequest
 * @see ConsumerRequest
 * @since 5.9
 */
public abstract class MessagingRequest {
  /**
   * Returns the underlying messaging request object. Ex. {@code javax.jms.Message}
   *
   * <p>Note: Some implementations are composed of multiple types, such as a message and a
   * destination. Moreover, an implementation may change the type returned due to refactoring.
   * Unless you control the implementation, cast carefully (ex using {@code instance of}) instead of
   * presuming a specific type will always be returned.
   *
   * @since 5.9
   */
  public abstract Object unwrap();

  /**
   * The unqualified, case-sensitive semantic message operation name. The currently defined names
   * are "send", "receive", "bulk-send" and "bulk-receive".
   *
   * <p>Examples:
   * <pre><ul>
   *   <li>Amazon SQS - {@code AmazonSQS.sendMessageBatch()} returns "bulk-send" as it can send many messages</li>
   *   <li>JMS - {@code MessageProducer.send()} returns "send" as it only sends one message</li>
   *   <li>Kafka - {@code Consumer.poll()} returns "bulk-receive" as it can receive many messages</li>
   *   <li>RabbitMQ - {@code Consumer.handleDelivery()} returns "receive" as the callback receives only one message</li>
   * </ul></pre>
   *
   * <p>Note: There is no constant set of operations, yet. Even when there is a constant set, there
   * may be operations such as "browse" or "purge" which aren't defined. Once implementation
   * matures, a constant file will be defined, with potentially more names.
   *
   * @return the messaging operation or null if unreadable.
   * @since 5.9
   */
  @Nullable public abstract String operation();

  /**
   * Type of channel, e.g. "queue" or "topic". {@code null} if unreadable.
   *
   * <p>Conventionally associated with the key "messaging.channel_kind"
   *
   * @see #channelName()
   * @since 5.9
   */
  // Naming matches conventions for Span
  @Nullable public abstract String channelKind();

  /**
   * Messaging channel name, e.g. "hooks" or "complaints". {@code null} if unreadable.
   *
   * <p>Conventionally associated with the key "messaging.channel_name"
   *
   * @see #channelKind()
   * @since 5.9
   */
  @Nullable public abstract String channelName();

  @Override public String toString() {
    Object unwrapped = unwrap();
    // unwrap() returning null is a bug. It could also return this. don't NPE or stack overflow!
    if (unwrapped == null || unwrapped == this) return getClass().getSimpleName();
    return getClass().getSimpleName() + "{" + unwrapped + "}";
  }

  MessagingRequest() { // sealed type: only producer and consumer
  }
}
