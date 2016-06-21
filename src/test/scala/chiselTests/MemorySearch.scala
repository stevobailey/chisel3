// See LICENSE for license details.

package chiselTests
import Chisel._
import Chisel.testers.BasicTester

class MemorySearch extends Module {
  val io = new Bundle {
    val target  = UInt(INPUT,  4)
    val en      = Bool(INPUT)
    val done    = Bool(OUTPUT)
    val address = UInt(OUTPUT, 3)
  }
  val vals  = Array(0, 4, 15, 14, 2, 5, 13)
  val index = Reg(init = 0.asUInt(3))
  val elts  = Vec(vals.map(_.asUInt(4)))
  // val elts  = Mem(UInt(width = 32), 8) TODO ????
  val elt  = elts(index)
  val end  = !io.en && ((elt === io.target) || (index === 7.asUInt))
  when (io.en) {
    index := 0.asUInt
  } .elsewhen (!end) {
    index := index +% 1.asUInt
  }
  io.done    := end
  io.address := index
}

/*
class MemorySearchTester(c: MemorySearch) extends Tester(c) {
  val list = c.vals
  val n = 8
  val maxT = n * (list.length + 3)
  for (k <- 0 until n) {
    val target = rnd.nextInt(16)
    poke(c.io.en,     1)
    poke(c.io.target, target)
    step(1)
    poke(c.io.en,     0)
    do {
      step(1)
    } while (peek(c.io.done) == 0 && t < maxT)
    val addr = peek(c.io.address).toInt
    expect(addr == list.length || list(addr) == target,
           "LOOKING FOR " + target + " FOUND " + addr)
  }
}
*/

class MemorySearchSpec extends ChiselPropSpec {

  property("MemorySearch should elaborate") {
    elaborate { new EnableShiftRegister }
  }

  ignore("MemorySearch should return the correct result") { }
}
