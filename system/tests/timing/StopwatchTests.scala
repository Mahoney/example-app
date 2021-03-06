package uk.org.lidalia
package exampleapp.system.timing

import java.time.Duration.ofSeconds
import java.time.Instant

import org.scalatest.FunSuite
import uk.org.lidalia.exampleapp.system.timing.Stopwatch.{timeWithChildResults, time}

import util.Success

class StopwatchTests extends FunSuite {

  test("measures time spent") {

    val start = Instant.now()
    val clock = new MutableFixedClock(start)

    val result = time("some work", clock) {
      clock.fastForward(ofSeconds(1))
      "result"
    }

    assert(result == StopwatchResult(
      start = start,
      end = start.plusSeconds(1),
      input = "some work",
      output = Success("result")
    ))
  }

  test("measures sub periods") {

    val start = Instant.now()
    val clock = new MutableFixedClock(start)

    val result = timeWithChildResults("top work", clock) {
      clock.fastForward(ofSeconds(1))
      val child1 = time("child work 1", clock) {
        clock.fastForward(ofSeconds(1))
        "child result 1"
      }
      clock.fastForward(ofSeconds(1))
      val child2 = time("child work 2", clock) {
        clock.fastForward(ofSeconds(1))
        "child result 2"
      }
      clock.fastForward(ofSeconds(1))
      ("top result", List(child1, child2))
    }

    assert(result == StopwatchResult(
      start = start,
      end = start.plusSeconds(5),
      input = "top work",
      output = Success("top result"),
      List(
        StopwatchResult(
          start = start.plusSeconds(1),
          end = start.plusSeconds(2),
          input = "child work 1",
          output = Success("child result 1")
        ),
        StopwatchResult(
          start = start.plusSeconds(3),
          end = start.plusSeconds(4),
          input = "child work 2",
          output = Success("child result 2")
        )
      )
    ))
  }
}


