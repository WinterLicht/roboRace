package gameLogic.eventLog

import gameLogic.{Direction, GameScenario, GameState, Position}
import gameLogic.action.Action
import gameLogic.command.Command

sealed trait EventLog
case class CommandAccepted(command: Command) extends EventLog
case class CommandRejected(command: Command, reason: RejectionReason) extends EventLog

case class GameStateTransition(oldState: GameState, newState: GameState) extends EventLog

case class GameScenarioDefined(scenario: GameScenario) extends EventLog

case class RobotAction(playerName: String, action: Action) extends EventLog
case class RobotPositionTransition(playerName: String, from: Position, to: Position) extends EventLog
case class RobotDirectionTransition(playerName: String, from: Direction, to: Direction) extends EventLog

case object AllPlayerDefinedActions extends EventLog
case class PlayerActionsExecuted(nextCycle: Int) extends EventLog

sealed trait RejectionReason
case object PlayerAlreadyRegistered extends RejectionReason
case object NoPlayersRegistered extends RejectionReason
case object TooMuchPlayersRegistered extends RejectionReason
case object WrongCycle extends RejectionReason
case object WrongState extends RejectionReason