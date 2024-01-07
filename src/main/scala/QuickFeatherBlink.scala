import chisel3._
import chisel3.util._
import fpgamacro.eos_s3.SysClk

class helloworldfpga extends RawModule {
  /* IO */
  val redled = IO(Output(Bool()))
  val greenled = IO(Output(Bool()))
  val blueled = IO(Output(Bool()))

  /* PLL */
  val clk = Wire(Clock())

  val clock_cell = Module(new SysClk)
  clk := clock_cell.io.sys_clk_0 

  /* Blink */
  withClockAndReset(clk, false.B){
      val blink = Module(new Blink(44,
                                  LED_WIDTH=3,
//                                  COUNTTYPE=CounterTypes.NaturalCount))
//                                  COUNTTYPE=CounterTypes.FullAdderCount))
                                  COUNTTYPE=CounterTypes.PdChain))
      redled := blink.io.leds(0)
      greenled := blink.io.leds(1)
      blueled := blink.io.leds(2)
  }
}

object helloworldfpga extends App {
  println("Generate helloworldfpga")
  (new chisel3.stage.ChiselStage).emitVerilog(new helloworldfpga(), args)
}
