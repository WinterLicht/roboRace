package gameLogic

sealed trait Trap {
  def position: Position
}

case class TurnRightTrap(position: Position) extends Trap
case class TurnLeftTrap(position: Position) extends Trap
case class StunTrap(position: Position) extends Trap
