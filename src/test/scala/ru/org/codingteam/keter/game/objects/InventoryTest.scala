package ru.org.codingteam.keter.game.objects

import ru.org.codingteam.keter.game.objects.equipment.items.Knife
import utest._

import scala.collection.MultiSet

object InventoryTest extends TestSuite {
  val tests = Tests {
    test("shouldSetTwoKnifes") {
      val i = Inventory(Set(), Set(Knife("Knife")), Set(Knife("Knife")))
      println(i.allEquipment)
      assert(i.allEquipment.size == 2)
    }
  }
}
