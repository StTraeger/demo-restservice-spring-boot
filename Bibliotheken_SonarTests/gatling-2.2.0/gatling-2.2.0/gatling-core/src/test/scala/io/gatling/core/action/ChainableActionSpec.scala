/**
 * Copyright 2011-2016 GatlingCorp (http://gatling.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gatling.core.action

import io.gatling.BaseSpec
import io.gatling.commons.util.ExceptionHelper._
import io.gatling.commons.validation._
import io.gatling.core.session.Session

class ChainableActionSpec extends BaseSpec {

  class ChainableTestAction(val next: Action, fail: Boolean) extends Action with ChainableAction {
    var hasRun = false

    override val name = "chainable"
    override def execute(session: Session): Unit =
      if (fail) throw new Exception("expected crash").noStackTrace()
      else hasRun = true
  }

  class NextTestAction extends Action {
    var message: Session = _
    override val name = "next"
    override def execute(session: Session): Unit = message = session
  }

  class FailableTestAction(val next: Action, fail: Boolean) extends Action with ChainableAction {
    var hasRun = false

    override val name = "test"
    override def execute(session: Session) = recover(session) {
      if (fail) "woops".failure
      else {
        hasRun = true
        "".success
      }
    }
  }

  "A Chainable Action" should "call the execute method when receiving a Session" in {
    val next = new NextTestAction
    val testAction = new ChainableTestAction(next, fail = false)

    testAction.hasRun shouldBe false
    testAction ! Session("scenario", 0)
    testAction.hasRun shouldBe true
  }

  it should "send the session, failed, to the next actor when crashing" in {
    val next = new NextTestAction
    val testAction = new ChainableTestAction(next, fail = true)
    val session = Session("scenario", 0)

    testAction ! session
    next.message shouldBe session.markAsFailed
  }

  "A Failable Action" should "call the execute method when receiving a Session" in {
    val next = new NextTestAction
    val testAction = new FailableTestAction(next, fail = false)

    testAction.hasRun shouldBe false

    testAction ! Session("scenario", 0)
    testAction.hasRun shouldBe true
  }

  it should "send the session, failed, to the next actor when recovering a Failure" in {
    val next = new NextTestAction
    val testAction = new FailableTestAction(next, fail = true)
    val session = Session("scenario", 0)

    testAction ! session
    next.message shouldBe session.markAsFailed
  }
}
