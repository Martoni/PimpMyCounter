import chisel3._
import chisel3.util._
import chisel3.experimental.Param
import fpgamacro.gatemate.CC_PLL

class GatemateBlink extends RawModule {
  /* IO */
  val clock = IO(Input(Clock()))
  val resetn = IO(Input(Bool()))
  val leds = IO(Output(UInt(8.W)))

  /* PLL */
  val clk100 = Wire(Clock())
  val pm: Map[String, Param] = Map(
    "REF_CLK"         -> "10.0",
    "OUT_CLK"         -> "100.0",
    "PERF_MD"         -> "SPEED",
    "LOW_JITTER"      -> 1,
    "CI_FILTER_CONST" -> 2,
    "CP_FILTER_CONST" -> 4)
  val gm_pll = Module(new CC_PLL(pm))
  gm_pll.io.CLK_REF := clock
  gm_pll.io.CLK_FEEDBACK := (false.B).asClock
  gm_pll.io.USR_CLK_REF := (false.B).asClock
  gm_pll.io.USR_LOCKED_STDY_RST := false.B
  clk100 := gm_pll.io.CLK0

  val reset = Wire(Bool())
  reset := !resetn

  /* Blink */
  withClockAndReset(clk100, reset){
      val blink = Module(new Blink(44,
                                  LED_WIDTH=8,
//                                  COUNTTYPE=CounterTypes.NaturalCount))
                                  COUNTTYPE=CounterTypes.FullAdderCount))
      leds := blink.io.leds
  }
}

object GatemateBlink extends App {
  println("Generate GatemateBlink")
  (new chisel3.stage.ChiselStage).emitVerilog(new GatemateBlink(), args)
}

