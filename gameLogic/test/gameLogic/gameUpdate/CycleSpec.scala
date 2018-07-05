package gameLogic
package gameUpdate

import helper.GameUpdateHelper
import org.scalatest.{FunSuite, Matchers}

class CycleSpec extends FunSuite with Matchers with GameUpdateHelper {
  test("should start game if all players are ready") {
    updateChain(InitialGame)(
      DefineScenario(GameScenario.default)(p1).accepted.anyEvents,
      RegisterForGame(p2).accepted.noEvents,
      RegisterForGame(p3).accepted.noEvents,
      ChooseInstructions(0, 0 until Constants.instructionsPerCycle)(p1).accepted.noEvents,
      ChooseInstructions(0, 0 until Constants.instructionsPerCycle)(p2).accepted.noEvents,
      ChooseInstructions(0, 0 until Constants.instructionsPerCycle)(p3).accepted
        .logged(_ should contain(StartNextCycle(1))),
      assertRunning{ game =>
        game.players.map(_.name) shouldBe List(p1, p2, p3)
        game.cycle shouldBe 1
        for (player <- game.players) {
          player.instructions shouldBe Seq()
          player.finished shouldBe None
          player.robot shouldBe GameScenario.default.initialRobots(player.index)
          player.instructionOptions.size shouldBe Constants.instructionOptionsPerCycle
        }
        succeed
      }
    )
  }

  test("should start the next cycle when all player choose their action") {
    updateChain(InitialGame)(
      DefineScenario(GameScenario.default)(p1).accepted.anyEvents,
      RegisterForGame(p2).accepted.noEvents,
      RegisterForGame(p3).accepted.noEvents,
      ChooseInstructions(0, 0 until Constants.instructionsPerCycle)(p1).accepted.noEvents,
      ChooseInstructions(0, 0 until Constants.instructionsPerCycle)(p2).accepted.noEvents,
      ChooseInstructions(0, 0 until Constants.instructionsPerCycle)(p3).accepted
        .logged(_ should contain(StartNextCycle(1))),
      ChooseInstructions(1, 0 until Constants.instructionsPerCycle)(p1).accepted.noEvents,
      ChooseInstructions(1, 0 until Constants.instructionsPerCycle)(p2).accepted.noEvents,
      ChooseInstructions(1, 0 until Constants.instructionsPerCycle)(p3).accepted
        .logged(_ should contain(StartNextCycle(2))),
      assertRunning(_ => succeed)
    )
  }
}
