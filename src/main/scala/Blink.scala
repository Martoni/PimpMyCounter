// Implementation naïve d'un blinker
import chisel3._
import chisel3.util._

import scala.math.BigInt
import chisel3.util.log2Ceil

object CounterTypes extends Enumeration {
  val NaturalCount = Value
  val FullAdderCount = Value
  val FullPGAdderCount = Value
  val PdChain = Value
}

class Blink(val COUNT_WIDTH: Int = 32,
            val LED_WIDTH: Int = 8,
            val COUNTTYPE: CounterTypes.Value = CounterTypes.NaturalCount) extends Module {
  val io = IO(new Bundle{
      val leds = Output(UInt(LED_WIDTH.W))
  })

  COUNTTYPE match  {
    case CounterTypes.NaturalCount => {
      println("Generate NaturalCount")
      val counter = Module(new NaturalCount(COUNT_WIDTH))
      io.leds := counter.io.count(COUNT_WIDTH-1, COUNT_WIDTH-LED_WIDTH)
    }
    case CounterTypes.PdChain => {
      println("Generate PdChain")
      val counter = Module(new PdChain(COUNT_WIDTH))
      io.leds := counter.io.count(COUNT_WIDTH-1, COUNT_WIDTH-LED_WIDTH)
    }
    case CounterTypes.FullAdderCount => {
      println("Generate FullAdderCount of " + COUNT_WIDTH + " bits")
      val counter = Module(new FullAdderCount(COUNT_WIDTH))
      io.leds := counter.io.count(COUNT_WIDTH-1, COUNT_WIDTH-LED_WIDTH)
    }
    case CounterTypes.FullPGAdderCount => {
      println("Generate Full PG Adder Count of " + COUNT_WIDTH + " bits")
      val counter = Module(new FullPGAdderCount(COUNT_WIDTH, usePG = true))
      io.leds := counter.io.count(COUNT_WIDTH-1, COUNT_WIDTH-LED_WIDTH)
    }
  }
}

// Natural counter with classic addition"
class NaturalCount(val COUNT_WIDTH: Int = 32) extends Module {
  val io = IO(new Bundle {
    val count = Output(UInt(COUNT_WIDTH.W))
  })

  val counterValue = RegInit(0.U(COUNT_WIDTH.W))
  counterValue := counterValue + 1.U
  io.count := counterValue 
}

/* FullAdder counter */
class FullAdderCount(val COUNT_WIDTH: Int = 44, val usePG: Boolean := false) extends Module {
  val io = IO(new Bundle {
    val count = Output(UInt(COUNT_WIDTH.W))
  })

  val counterValue = RegInit(0.U(COUNT_WIDTH.W))
  val addition =  if (usePG == true)
    Module(new FullPGAdderAddition(COUNT_WIDTH))
  else
    Module(new FullAdderAddition(COUNT_WIDTH))
  addition.io.a := counterValue
  addition.io.b := 1.U
  counterValue := addition.io.s

  io.count := counterValue
}


class FullPGAdderAddition(val COUNT_WIDTH: Int = 44) extends Module {
  val io = IO(new Bundle {
    val a   = Input(UInt(COUNT_WIDTH.W))
    val b   = Input(UInt(COUNT_WIDTH.W))
    val c   = Output(Bool())
    val s   = Output(UInt(COUNT_WIDTH.W))
  })

  val s_vec = Wire(Vec(COUNT_WIDTH, Bool()))

  /* Carry-LookAhead module */
  val cla_mod = Module(new CarryLookAhead(COUNT_WIDTH))
  cla_mod.io.c0 := false.B

  val fullPGAdders = for(i <- 0 until COUNT_WIDTH) yield {
    val full_pg_adder = Module(new FullPGAdder())
    full_pg_adder.io.a := io.a(i)
    full_pg_adder.io.b := io.b(i)
    if (i == 0) {
      full_adder.io.cin := false.B
    } else {
      full_adder.io.cin := cla_mod.io.c(i-1)
    }
    cla_mod.io.p(i) := full_pg_adder.io.p
    cla_mod.io.g(i) := full_pg_adder.io.g
    s_vec(i) := full_adder.io.s
    full_adder
  }

  io.c := cla_mod.io.c(COUNT_WIDTH-2)
  io.s := s_vec.asUInt
}


class CarryLookAhead(val COUNT_WIDTH: Int = 44) extends Module {
  val io = IO(new Bundle {
    val p =  Input(Vec(COUNT_WIDTH.W), Bool())
    val g =  Input(Vec(COUNT_WIDTH.W), Bool())
    val c = Output(Vec((COUNT_WIDTH - 1).W), Bool())
    val c0= Input(Bool())
  })

  for (i <- 0 until COUNT_WIDTH) {
    if (i == 0) {
      io.c(i+1) = io.g(i) | (io.p(i) & io.c0)
    } else {
      io.c(i+1) = io.g(i) | (io.p(i) & io.c(i-1))
    }
  }
}


/* Full Adder additionner */
class FullAdderAddition(val COUNT_WIDTH: Int = 32) extends Module {
  val io = IO(new Bundle {
    val a   = Input(UInt(COUNT_WIDTH.W))
    val b   = Input(UInt(COUNT_WIDTH.W))
    val c   = Output(Bool())
    val s   = Output(UInt(COUNT_WIDTH.W))
  })

  val a_vec = VecInit(io.a.asBools)
  val b_vec = VecInit(io.b.asBools)
  val c_vec = Wire(Vec(COUNT_WIDTH, Bool()))
  val s_vec = Wire(Vec(COUNT_WIDTH, Bool()))

  /* FullAdder vector with COUNT_WIDTH modules */
  val fullAdders = for(i <- 0 until COUNT_WIDTH) yield {
    val full_adder = Module(new FullAdder())
    full_adder.io.a := a_vec(i)
    full_adder.io.b := b_vec(i)
    c_vec(i) := full_adder.io.cout
    if (i == 0) {
      full_adder.io.cin := false.B
    } else {
      full_adder.io.cin := c_vec(i-1)
    }
    s_vec(i) := full_adder.io.s
    full_adder
  }

  io.c := c_vec(COUNT_WIDTH-1)
  io.s := s_vec.asUInt
}


object Blink extends App {
//  (new chisel3.stage.ChiselStage).emitVerilog(new Blink(COUNT_WIDTH=128), args)
  (new chisel3.stage.ChiselStage).emitVerilog(new Blink(), args)
}


class FullPGAdder extends Module {
  val io = IO(new Bundle {
    val a    = Input(Bool())
    val b    = Input(Bool())
    val cin  = Input(Bool())
    val s    = Output(Bool())
    val p    = Output(Bool())
    val g    = Output(Bool())
  })

  val ha0 = Module(new HalfAdder())
  val ha1 = Module(new HalfAdder())

  ha0.io.a := io.a
  ha0.io.b := io.b
  ha1.io.a := ha0.io.s
  ha1.io.b := io.cin

  io.s := ha1.io.s
  io.p := ha0.io.s
  io.g := ha0.io.c
}


class FullAdder extends Module {
  val io = IO(new Bundle {
    val a    = Input(Bool())
    val b    = Input(Bool())
    val cin  = Input(Bool())
    val s    = Output(Bool())
    val cout = Output(Bool())
  })

  val ha0 = Module(new HalfAdder())
  val ha1 = Module(new HalfAdder())
  val ha2 = Module(new HalfAdder())

  ha0.io.a := io.a
  ha0.io.b := io.b
  ha1.io.a := ha0.io.s
  ha1.io.b := io.cin
  ha2.io.a := ha0.io.c
  ha2.io.b := ha1.io.c

  io.s    := ha1.io.s
  io.cout := ha2.io.s
}

class HalfAdder extends Module {
  val io = IO(new Bundle {
      val a = Input(Bool())
      val b = Input(Bool())
      val s = Output(Bool())
      val c = Output(Bool())
  })

  io.s := io.a ^ io.b
  io.c := io.a & io.b
}

class PDivTwo(init: Boolean = false) extends Module {
  val io = IO(new Bundle {
      val en = Input(UInt(1.W))
      val q = Output(UInt(1.W))
      val p = Output(UInt(1.W))
  })

  val reg0 = RegInit(if(init) 0.U else 1.U)
  val reg1 = RegInit(if(init) 1.U else 0.U)

  when(io.en === 1.U) {
    reg0 := ~reg0
  }
  reg1 := io.en & reg0

  io.q := reg0
  io.p := reg1
}

class PdChain(n: Int = 4) extends Module {
  val io = IO(new Bundle {
    val count = Output(UInt(n.W))
  })
  // instantiate PdivTwo modules
  val pDivTwo = for (i <- 0 until n) yield {
    val pdivtwo = Module(new PDivTwo(i == 0))
    pdivtwo
  }
  val pDivTwo_io = VecInit(pDivTwo.map(_.io))

  // connect together
  pDivTwo_io(0).en := 1.U(1.W)
  for(i <- 1 until n) {
    pDivTwo_io(i).en := pDivTwo_io(i-1).p
  }

  val countValue = for (i <- 0 until n) yield pDivTwo_io(i).q

  io.count := countValue.reverse.reduce(_ ## _)
}

object PdChain extends App {
  (new chisel3.stage.ChiselStage).emitVerilog(new PdChain(), args)
}

class BlinkFullAdder(dbg: Boolean = false) extends Module {
  val rsize = 16
  val io = IO(new Bundle {
    val leds = Output(UInt(8.W))
    // DEBUGS
    val dbgresult = if(dbg) Some(Output(UInt(rsize.W))) else None
    val dbgcoutVec = if(dbg) Some(Output(UInt(rsize.W))) else None
  })

  def FullAdderS (a: Bool, b: Bool, cin: Bool) : Bool = {
    (a ## b ## cin).xorR
  }
  def FullAdderCout (a: Bool, b: Bool, cin: Bool) : Bool = {
    (a & b) ^ ((a ^ b) & cin)
  }


  val result = RegInit(0.U(rsize.W))
  if(dbg) {
    io.dbgresult.get := result
  }
  val one    = RegInit(1.U(rsize.W))

  val coutVec = Wire(Vec(rsize, Bool()))
  val sVec = Wire(Vec(rsize, Bool()))

  coutVec(0) := FullAdderCout(one(0), result(0), false.B)
  sVec(0) := FullAdderS(one(0), result(0), false.B)
  (1 until rsize) foreach { i =>
    coutVec(i) := FullAdderCout(one(i), result(i), coutVec(i-1))
    sVec(i) := FullAdderS(one(i), result(i), coutVec(i-1))
  }
  
  if(dbg) {
    io.dbgcoutVec.get := coutVec.asUInt
  }
  result := sVec.asUInt
  io.leds := result(rsize-1, rsize-8)
}

class BlinkAdder (dbg: Boolean = false,val rsize: Int = 16) extends Module {
  val io = IO(new Bundle {
    val leds = Output(UInt(rsize.W))
  })

  def ClassicAdder(a: Bool, b: Bool, cin: Bool): UInt = {
    val res = Wire(UInt(2.W))
    res := a +& b + cin
    res
  }

  val result = RegInit(0.U(rsize.W))
  val one = RegInit(1.U(rsize.W))

  val coutVec = Wire(Vec(rsize, Bool()))
  val sVec = Wire(Vec(rsize, Bool()))

  val tmp = ClassicAdder(one(0), result(0), false.B)
  coutVec(0) := tmp(1)
  sVec(0) := tmp(0)

  (1 until rsize) foreach { i =>
    val tmp = ClassicAdder(one(i), result(i), coutVec(i-1))
    coutVec(i) := tmp(1) 
    sVec(i)    := tmp(0) 
  }

  result := sVec.asUInt
  io.leds := result

}

