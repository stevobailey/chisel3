// See LICENSE for license details.

package cookbook

import chisel3._
import chisel3.util._
import chisel3.testers.BasicTester

import chiselTests.ChiselFlatSpec

/** Tester for concise cookbook tests
  *
  * Provides a length of test after which the test will pass 
  */
abstract class CookbookTester(length: Int) extends BasicTester {
  require(length >= 0, "Simulation length must be non-negative!")

  // No IO allowed, cookbook tests must be self-contained
  override final val io = new Bundle { }

  val (cycle, done) = Counter(Bool(true), length)
  when (done) { stop() }
}

abstract class CookbookSpec extends ChiselFlatSpec
