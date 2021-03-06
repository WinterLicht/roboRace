package controller

import gameLogic.Scenario
import io.circe.generic.auto._
import io.circe.syntax._
import javax.inject.{Inject, Singleton}
import play.api.libs.circe.Circe
import play.api.mvc.InjectedController
import repo.{ScenarioRepository, ScenarioRow}

case class ScenarioPost(description: String, scenario: Scenario)

@Singleton
class ScenarioController @Inject()(repo: ScenarioRepository) extends InjectedController with Circe {

  def get() = Action {
    val list = repo.list().filter(_.scenario.isDefined)
    if (list.isEmpty) {
      val defaultRow = ScenarioRow(Utils.newShortId(), "system", "default", Some(Scenario.default))
      repo.save(defaultRow)
      Ok(List(defaultRow).asJson)
    } else {
      Ok(list.asJson)
    }
  }

  def getSingle(id: String) = Action {
    repo.get(id) match {
      case None => NotFound
      case Some(row) => Ok(row.asJson)
    }
  }

  def post() = Action(circe.tolerantJson[ScenarioPost]) { request =>
    Utils.playerName(request) match {
      case None => Unauthorized
      case _ if !Scenario.validation(request.body.scenario) => BadRequest
      case Some(player) =>
        val row = ScenarioRow(
          id = Utils.newShortId(),
          owner = player,
          description = request.body.description,
          scenario = Some(request.body.scenario))
        repo.save(row)
        Created(row.asJson)
    }
  }

  def put(id: String) = Action(circe.tolerantJson[ScenarioPost]) { request =>
    (Utils.playerName(request), repo.get(id)) match {
      case (None, _)                                                        => Unauthorized
      case _ if !Scenario.validation(request.body.scenario)                 => BadRequest
      case (Some(player), Some(scenarioRow)) if scenarioRow.owner != player => Forbidden
      case (Some(player), _)                                                =>
        val row = ScenarioRow(
          id = id,
          owner = player,
          description = request.body.description,
          scenario = Some(request.body.scenario))
        repo.save(row)
        Ok(row.asJson)
    }
  }

  def delete(id: String) = Action { request =>
    (repo.get(id), Utils.playerName(request)) match {
      case (None, _) => NotFound
      case (_, None) => Unauthorized
      case (Some(row), Some(player)) if row.owner != player => Unauthorized
      case (Some(row), Some(_)) => repo.delete(row.id)
        NoContent
    }
  }

  // todo: SSE!
}
