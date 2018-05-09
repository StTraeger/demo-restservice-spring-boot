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
package io.gatling.core.structure

import io.gatling.core.action.builder.{ ActionBuilder, SessionHookBuilder }
import io.gatling.core.session.{ Expression, Session }

trait Execs[B] {

  private[core] def actionBuilders: List[ActionBuilder]
  private[core] def newInstance(actionBuilders: List[ActionBuilder]): B

  def exec(sessionFunction: Expression[Session]): B = exec(new SessionHookBuilder(sessionFunction, true))
  def exec(actionBuilder: ActionBuilder): B = chain(List(actionBuilder))
  def exec(chains: ChainBuilder*): B = exec(chains.toIterable)
  def exec(chains: Iterator[ChainBuilder]): B = exec(chains.toIterable)
  def exec(chains: Iterable[ChainBuilder]): B = chain(chains.toList.reverse.flatMap(_.actionBuilders))
  def exec(scenario: ScenarioBuilder): B = chain(scenario.actionBuilders)

  private[core] def chain(newActionBuilders: Seq[ActionBuilder]): B = newInstance(newActionBuilders.toList ::: actionBuilders)
}
