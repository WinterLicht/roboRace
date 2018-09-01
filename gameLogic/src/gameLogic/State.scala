package gameLogic

trait State {
  def sequence(fs: (Game => Game)*): Game => Game =
    game => fs.foldLeft(game)((s, f) => f(s))

  def conditional(cond: Game => Boolean)(f: Game => Game): Game => Game =
    game => if(cond(game)) f(game) else game
}

object State extends State