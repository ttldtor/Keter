package ru.org.codingteam.keter.ui.views

import ru.org.codingteam.keter.ui.SimpleKeyMapView
import ru.org.codingteam.keter.ui.shape.Rectangle
import ru.org.codingteam.keter.ui.viewmodels.ItemsViewModel
import ru.org.codingteam.rotjs.interface.ROT.Display
import ru.org.codingteam.rotjs.interface.ROT

/**
 * A list of typed items.
 */
class ListView[T](shape: Rectangle,
                  model: ItemsViewModel[T],
                  override val keyMap: ListView.KeyMap) extends SimpleKeyMapView {

  /**
   * A pair of model item and its name.
   */
  protected type Item = (T, String)

  override def render(display: Display): Unit = {
    val margin = 1
    model.items.vector.zipWithIndex foreach { case (item, index) =>
      val y = shape.y + index
      if (index > shape.height) {
        return
      }

      renderItem(display, item, shape.x + margin, y, shape.width - 2 * margin)
    }
  }

  protected def renderItem(display: Display, item: Item, x: Int, y: Int, width: Int): Unit = {
    val (value, name) = item
    if (model.selectedItem.contains(value)) {
      display.draw(shape.x, y, ">")
    }

    display.drawText(x, y, name, width)
  }
}

object ListView {

  type KeyMap = Map[Int, () => Unit]

  def bracketKeyMap[T](model: ItemsViewModel[T]): KeyMap = Map(
    ROT.VK_OPEN_BRACKET -> model.up _,
    ROT.VK_CLOSE_BRACKET -> model.down _
  )

  def arrowKeyMap[T](model: ItemsViewModel[T]): KeyMap = Map(
    ROT.VK_UP -> model.up _,
    ROT.VK_DOWN -> model.down _
  )
}
