// See LICENSE for license details.

package chiselTests

import Chisel._
import org.scalatest._
import org.scalatest.prop._
import Chisel.testers.BasicTester

class Tbl(w: Int, n: Int) extends Module {
  val io = new Bundle {
    val wi  = UInt(INPUT, log2Up(n))
    val ri  = UInt(INPUT, log2Up(n))
    val we  = Bool(INPUT)
    val  d  = UInt(INPUT, w)
    val  o  = UInt(OUTPUT, w)
  }
  val m = Mem(n, UInt(width = w))
  io.o := m(io.ri)
  when (io.we) {
    m(io.wi) := io.d
    when(io.ri === io.wi) {
      io.o := io.d
    }
  }
}

class TblTester(w: Int, n: Int, idxs: List[Int], values: List[Int]) extends BasicTester {
  val (cnt, wrap) = Counter(true.asBool, idxs.size)
  val dut = Module(new Tbl(w, n))
  val vvalues = Vec(values.map(_.asUInt))
  val vidxs = Vec(idxs.map(_.asUInt))
  val prev_idx = vidxs(cnt - 1.asUInt)
  val prev_value = vvalues(cnt - 1.asUInt)
  dut.io.wi := vidxs(cnt)
  dut.io.ri := prev_idx
  dut.io.we := true.asBool //TODO enSequence
  dut.io.d := vvalues(cnt)
  when (cnt > 0.asUInt) {
    when (prev_idx === vidxs(cnt)) {
      assert(dut.io.o === vvalues(cnt))
    } .otherwise {
      assert(dut.io.o === prev_value)
    }
  }
  when(wrap) {
    stop()
  }
}

class TblSpec extends ChiselPropSpec {
  property("All table reads should return the previous write") {
    forAll(safeUIntPairN(8)) { case(w: Int, pairs: List[(Int, Int)]) =>
      // Provide an appropriate whenever clause.
      // ScalaTest will try and shrink the values on error to determine the smallest values that cause the error.
      whenever(w > 0 && pairs.length > 0) {
        val (idxs, values) = pairs.unzip
        assertTesterPasses{ new TblTester(w, 1 << w, idxs, values) }
      }
    }
  }
}
