package ru.org.codingteam.keter.scenes.game

import org.scalajs.dom.KeyboardEvent
import ru.org.codingteam.keter.game.actions._
import ru.org.codingteam.keter.game.objects._
import ru.org.codingteam.keter.game.objects.behaviors.PlayerBehavior
import ru.org.codingteam.keter.game.{Engine, GameState, LocationMap}
import ru.org.codingteam.keter.scenes.Scene
import ru.org.codingteam.keter.util.Logging
import ru.org.codingteam.rotjs.interface.{Display, ROT}
import ru.org.codingteam.rotjs.wrapper.Wrappers._

class GameScene(display: Display, engine: Engine) extends Scene(display) with Logging {

  setGameState(engine.gameState)
  engine.registerCallback(setGameState)

  override protected def onKeyDown(event: KeyboardEvent): Unit = {
    if (event.keyCode == ROT.VK_NUMPAD5) {
      processAction(WaitAction, player.position)
    } else {
      val target = event.keyCode match {
        case x if x == ROT.VK_NUMPAD8 || x == ROT.VK_UP => Some(playerVector(0, -1))
        case x if x == ROT.VK_NUMPAD9 => Some(playerVector(1, -1))
        case x if x == ROT.VK_NUMPAD6 || x == ROT.VK_RIGHT => Some(playerVector(1, 0))
        case x if x == ROT.VK_NUMPAD3 => Some(playerVector(1, 1))
        case x if x == ROT.VK_NUMPAD2 || x == ROT.VK_DOWN => Some(playerVector(0, 1))
        case x if x == ROT.VK_NUMPAD1 => Some(playerVector(-1, 1))
        case x if x == ROT.VK_NUMPAD4 || x == ROT.VK_LEFT => Some(playerVector(-1, 0))
        case x if x == ROT.VK_NUMPAD7 => Some(playerVector(-1, -1))
        case _ => None
      }

      target match {
        case None =>
        case Some(t) =>
          gameState.map.actorAt(player.position + t) match {
            case None => processAction(WalkAction, ObjectPosition(t.x, t.y))
            case Some(a) => processAction(MeleeAttackAction, a.position)
          }
      }
    }

    render()
  }

  override protected def render(): Unit = {
    display.clear()

    val GameState(messages, locationMap@LocationMap(surfaces, actors, objects, _), time) = gameState
    val player = locationMap.player

    log.debug("Drawing surfaces")
    surfaces.zipWithIndex.foreach { case (row, y) =>
      row.zipWithIndex.foreach { case (surface, x) =>
        display.draw(x, y, surface.tile)
      }
    }

    log.debug("Drawing objects")
    objects.zipWithIndex.foreach { case (row, y) =>
      row.zipWithIndex.foreach { case (obj, x) =>
        if (obj.length > 0) {
          log.debug(s"Drawing object $obj(0).name at $x, $y")
          display.draw(x, y, obj(0).tile)
        }
      }
    }

    log.debug(s"Drawing ${actors.size} actors")
    actors.values foreach (actor => display.draw(actor.position.x, actor.position.y, actor.tile, getColor(actor)))
    // display stats.
    display.drawTextCentered(s"Faction/Name: ${player.faction.name}/${player.name}", Some(display.height - 2))
    display.drawTextCentered(s"Health: ${player.stats.health} Time passed: $time", Some(display.height - 1))
  }

  private def player = gameState.map.actors.values.filter(_.behavior.isInstanceOf[PlayerBehavior]).head

  private def playerVector(dx: Int, dy: Int) = player.position + ObjectPosition(dx, dy)

  private var gameState: GameState = engine.gameState

  private def setGameState(state: GameState): Unit = {
    gameState = state
  }

  private def processAction(action: IActionDefinition, target: ObjectPosition): Unit = {
    log.debug(s"Scheduling player action: $action, target: $target")
    val behavior = player.behavior.asInstanceOf[PlayerBehavior]
    behavior.nextAction.success(Action(player, action, target))
  }

  private def getColor(actor: Actor) = {
    actor.state match {
      case ActorActive => null
      case ActorInactive => "#aaa"
    }
  }

}
