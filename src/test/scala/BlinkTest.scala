import chisel3._
import chiseltest._
import chisel3.util.experimental.BoringUtils
import chiseltest.simulator.WriteVcdAnnotation
import org.scalatest.flatspec.AnyFlatSpec
import scala.math.pow

class HalfAdderTest extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "HalfAdder"

  it should "match the truth table" in {
    test(new HalfAdder()) { dut =>
      val half_adder_truth_table = List(
      /* a,b,c,s */
        (0,0,0,0),
        (0,1,0,1),
        (1,0,0,1),
        (1,1,1,0))

      half_adder_truth_table.foreach {
        case (a, b, c, s) =>
          dut.io.a.poke(a);dut.io.b.poke(b)
          dut.clock.step(1)
          assert(dut.io.c.peek().litValue == c)
          assert(dut.io.s.peek().litValue == s)
      }
    }
  }
}
class FullPGAdderTest extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "FullPGAdder"

  it should "match table of truth" in {
    test(new FullPGAdder()) { dut =>
      val full_adder_truth_table = List(
        /* a, b, cin,    s, p, g */
          (0, 0, 0,      0, 0, 0), 
          (0, 0, 1,      1, 0, 0),
          (0, 1, 0,      1, 1, 0),
          (0, 1, 1,      0, 1, 0),
          (1, 0, 0,      1, 1, 0),
          (1, 0, 1,      0, 1, 0),
          (1, 1, 0,      0, 0, 1),
          (1, 1, 1,      1, 0, 1))
      full_adder_truth_table.foreach {
        case (a, b, cin, s, p, g) =>
          dut.io.a.poke(a); dut.io.b.poke(b); dut.io.cin.poke(cin)
          dut.clock.step(1)
          assert(dut.io.s.peek().litValue == s);
          assert(dut.io.p.peek().litValue == p);
          assert(dut.io.g.peek().litValue == g);
      }
    }
  }
}


class FullAdderTest extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "FullAdder"

  it should "match table of truth" in {
    test(new FullAdder()) { dut =>
      val full_adder_truth_table = List(
        /* a, b, cin, cout, s */
          (0, 0, 0,      0, 0), 
          (0, 0, 1,      0, 1),
          (0, 1, 0,      0, 1),
          (0, 1, 1,      1, 0),
          (1, 0, 0,      0, 1),
          (1, 0, 1,      1, 0),
          (1, 1, 0,      1, 0),
          (1, 1, 1,      1, 1))
      full_adder_truth_table.foreach {
        case (a, b, cin, cout, s) =>
          dut.io.a.poke(a); dut.io.b.poke(b); dut.io.cin.poke(cin)
          dut.clock.step(1)
          assert(dut.io.cout.peek().litValue == cout);
          assert(dut.io.s.peek().litValue == s);
      }
    }
  }
}

class CarryLookAheadTest extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "CarryLookAhead"

  it should "compute carry correctly" in {
    test(new CarryLookAhead(2)) { dut =>
      dut.io.p(0).poke(false.B)
      dut.io.g(0).poke(true.B)

      assert(dut.io.c(1).peek().litValue == 1) 

      dut.io.p(1).poke(true.B)
      dut.io.g(1).poke(false.B)
      assert(dut.io.cout.peek().litValue == 1)
      dut.clock.step(1)
    }
  }
}

class FullAdderAdditionTest extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "FullAdderAddition"

  it should "make correct additions" in {
    test(new FullAdderAddition(5)) { dut =>
      val a = 2; val b = 3
      dut.io.a.poke(a)
      dut.io.b.poke(b)
      dut.clock.step(1)
      assert(dut.io.s.peek().litValue == (a + b))
    }
  }
}

class FullPGAdderAdditionTest extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "FullAdderAddition"

  it should "make correct additions" in {
    test(new FullPGAdderAddition(5)) { dut =>
      val a = 2; val b = 3
      dut.io.a.poke(a)
      dut.io.b.poke(b)
      dut.clock.step(1)
      assert(dut.io.s.peek().litValue == (a + b))
    }
  }
}

//class NaturalCountTest extends AnyFlatSpec with ChiselScalatestTester {
//  behavior of "NaturalCount"
//
//  it should "Count naturaly" in {
//    test(new NaturalCount(5)) { dut =>
//      for(i <- 0 until (1<<5)){
//        assert(dut.io.count.peek().litValue == i)
//        dut.clock.step(1)
//      }
//    }
//  }
//}

class FullAdderCountTest extends AnyFlatSpec with ChiselScalatestTester {

  val adderWidth = 9

  behavior of "FullAdderCount"
  it should "Count normally" in {
    test(new FullAdderCount(adderWidth)).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      for(i <- 0 until (1<<adderWidth)){
        assert(dut.io.count.peek().litValue == i)
        dut.clock.step(1)
      }
    }
  }

  behavior of "FullPGAdderCount"
  it should "Count normally" in {
    test(new FullAdderCount(adderWidth, usePG=true))
    //test(new FullAdderCount(2, usePG=true))
      .withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      val maxcount = (1<<adderWidth)
      println(s"Count until $maxcount")
      for(i <- 0 until (1<<adderWidth)){
        assert(dut.io.count.peek().litValue == i)
        dut.clock.step(1)
      }
    }
  }

  behavior of "NaturalCount"
  it should "Count naturally" in {
    test(new NaturalCount(adderWidth)).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      for(i <- 0 until (1<<adderWidth)){
        assert(dut.io.count.peek().litValue == i)
        dut.clock.step(1)
      }
    }
  }

}
//
//
//class BlinkTest extends AnyFlatSpec with ChiselScalatestTester {
//  behavior of "BlinkFullAdder"
//
//  it should "count correctly" in {
//    test(new BlinkFullAdder(true)) { dut =>
//      val rsize = dut.rsize
//      dut.io.leds.expect(0.U)
//      println("result size -> " + dut.rsize)
//      for(i <- 0 to 10) {
//        println("result ->" + dut.io.dbgresult.get.peek().litValue)
//        println("coutVec ->" + dut.io.dbgcoutVec.get.peek().litValue)
//        dut.io.dbgresult.get.expect(i)
//        dut.clock.step(1)
//      }
//      println("End leds value " + dut.io.leds.peek().litValue)
//    }
//  }
//}
//
//
//class PdChainTest extends AnyFlatSpec with ChiselScalatestTester {
//  behavior of "PdChainTest"
//
//  val nsize = 4 
//
//  def pcount_decode(n: Int, b: BigInt): BigInt = {
//    var y: BigInt = b
//    for(k: Int <- 1 until n) {
//      if((y & ((1<<k) - 1))  < k){
//        y = y ^ (1<<k)
//      }
//    }
//    y
//  }
//
//  it should " count strangely " in {
//    test(new PdChain(nsize)) { dut =>
//      for(i <- 0 until (1<<nsize)) {
//        val value = dut.io.count.peek().litValue
//        val naturalvalue = pcount_decode(nsize, value)
//        println("[" + i + "] raw -> " + value + " natural -> " + naturalvalue)
//        assert(i == naturalvalue)
//        dut.clock.step(1)
//      }
//      val value = dut.io.count.peek().litValue
//      val naturalvalue = pcount_decode(nsize, value)
//      assert(0 == naturalvalue)
//    }
//  }
//}
//
//class BlinkAddTest extends AnyFlatSpec with ChiselScalatestTester {
//  behavior of "BlinkAddTest"
//
//  it should "count correctly" in {
//    val rsize = 8 
//    test(new BlinkAdder(dbg=true, rsize=rsize)) { dut =>
//      val rsize = dut.rsize
//      val maxvalue = pow(2, rsize).toInt
//      dut.clock.setTimeout(maxvalue*2)
//      dut.io.leds.expect(0.U)
//      println("result size -> " + dut.rsize)
//      println("initial leds -> " + dut.io.leds.peek().litValue)
//      for(i <- 1 until maxvalue) {
//        dut.clock.step(1)
//        dut.io.leds.expect(i)
//      }
//      dut.clock.step(1)
//      println("step " + maxvalue + " leds -> " + dut.io.leds.peek().litValue)
//      dut.io.leds.expect(0)
//
//      println("End leds value " + dut.io.leds.peek().litValue)
//    }
//  }
//
//}
//
