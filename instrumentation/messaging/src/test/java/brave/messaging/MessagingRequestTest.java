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

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MessagingRequestTest {
  @Test public void toString_mentionsDelegate() {
    class IceCreamRequest extends MessagingRequest {
      @Override public Object unwrap() {
        return "chocolate";
      }

      @Override public String operation() {
        return null;
      }

      @Override public String channelKind() {
        return null;
      }

      @Override public String channelName() {
        return null;
      }
    }
    assertThat(new IceCreamRequest())
      .hasToString("IceCreamRequest{chocolate}");
  }

  @Test public void toString_doesntStackoverflowWhenUnwrapIsNull() {
    class BuggyRequest extends MessagingRequest {
      @Override public Object unwrap() {
        return null;
      }

      @Override public String operation() {
        return null;
      }

      @Override public String channelKind() {
        return null;
      }

      @Override public String channelName() {
        return null;
      }
    }
    assertThat(new BuggyRequest())
      .hasToString("BuggyRequest");
  }
}
