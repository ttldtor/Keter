package ru.org.codingteam.keter.scenes.game

import org.scalajs.dom.KeyboardEvent
import ru.org.codingteam.keter.game.actions.{Action, MoveAction, WaitAction}
import ru.org.codingteam.keter.game.objects.Actor
import ru.org.codingteam.keter.game.{Engine, GameState, LocationMap}
import ru.org.codingteam.keter.scenes.Scene
import ru.org.codingteam.rotjs.interface.{Display, EventQueue, ROT}
import ru.org.codingteam.rotjs.wrapper.Wrappers._

class GameScene(display: Display, var state: GameState, var player: Actor) extends Scene(display) {

  override protected def onKeyDown(event: KeyboardEvent): Unit = {
    event.keyCode match {
      case x if x == ROT.VK_NUMPAD8 || x == ROT.VK_UP => processAction(MoveAction(player, 0, -1))
      case x if x == ROT.VK_NUMPAD9 => processAction(MoveAction(player, 1, -1))
      case x if x == ROT.VK_NUMPAD6 || x == ROT.VK_RIGHT => processAction(MoveAction(player, 1, 0))
      case x if x == ROT.VK_NUMPAD3 => processAction(MoveAction(player, 1, 1))
      case x if x == ROT.VK_NUMPAD2 || x == ROT.VK_DOWN => processAction(MoveAction(player, 0, 1))
      case x if x == ROT.VK_NUMPAD1 => processAction(MoveAction(player, -1, 1))
      case x if x == ROT.VK_NUMPAD4 || x == ROT.VK_LEFT => processAction(MoveAction(player, -1, 0))
      case x if x == ROT.VK_NUMPAD7 => processAction(MoveAction(player, -1, -1))
      case x if x == ROT.VK_NUMPAD5 => processAction(WaitAction(player))
      case _ =>
    }
  }

  override protected def render(): Unit = {
    display.clear()

    val GameState(messages, LocationMap(surfaces, objects), time) = state
    surfaces.zipWithIndex.foreach { case (row, y) =>
      row.zipWithIndex.foreach { case (surface, x) =>
        display.draw(x, y, surface.tile)
      }
    }

    objects foreach { case (obj, (x, y)) =>
      display.draw(x, y, obj.tile)
    }

    display.drawTextCentered(s"Time passed: $time", Some(display.height - 1))
  }

  private val queue = new EventQueue()

  private def processAction(action: Action): Unit = {
    queue.add(action, action.duration)

    val (newState, newPlayer) = Engine.processTurn(state, queue)
    state = newState
    player = newPlayer

    println(s"Messages: ${state.messages}")
  }

}
